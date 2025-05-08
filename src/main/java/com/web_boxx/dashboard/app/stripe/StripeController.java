package com.web_boxx.dashboard.app.stripe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.web_boxx.dashboard.app.models.User;
import com.web_boxx.dashboard.app.repositories.UserRepository;
import com.web_boxx.dashboard.app.security.JwtHelper;

@RestController
@RequestMapping("/api/payment")
public class StripeController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;

    // 🟢 1. Checkout-Session erstellen
    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession() {
        String userId = jwtHelper.getUserIdFromToken();
        Long amount = stripeService.calculatePricingAmount();

        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }

            User user = optionalUser.get();
            String stripeCustomerId = user.getStripeCustomerId();

            // Verhindere doppelte offene Zahlungen
            if (user.getPurchaseHistory() != null) {
                boolean hasPending = user.getPurchaseHistory().stream()
                        .anyMatch(entry -> "pending".equals(entry.get("status")));
                if (hasPending) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("error", "Offene Zahlung existiert bereits."));
                }
            }

            // Stripe Checkout-Session erstellen
            Session session = stripeService.createCheckoutSession(stripeCustomerId, amount);

            // Kaufvorgang speichern
            Map<String, String> purchase = new HashMap<>();
            purchase.put("amount", String.valueOf(amount));
            purchase.put("status", "pending");
            purchase.put("url", session.getUrl());

            if (user.getPurchaseHistory() == null) {
                user.setPurchaseHistory(new ArrayList<>());
            }
            user.getPurchaseHistory().add(purchase);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("checkoutUrl", session.getUrl()));

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Stripe error: " + e.getMessage()));
        }
    }

    // 🟡 2. Webhook zum Verarbeiten von Stripe-Events
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) {
        String payload;
        try (Scanner scanner = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A")) {
            payload = scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }

        String sigHeader = request.getHeader("Stripe-Signature");
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        String eventType = event.getType();

        if ("checkout.session.completed".equals(eventType) || "checkout.session.expired".equals(eventType)) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return ResponseEntity.ok("Ignored (no session)");
        
            String customerId = session.getCustomer();
            long amount = session.getAmountTotal();
            String url = session.getUrl();
        
            Optional<User> optionalUser = userRepository.findByStripeCustomerId(customerId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                List<Map<String, String>> history = user.getPurchaseHistory();
                if (history != null) {
                    if ("checkout.session.completed".equals(eventType)) {
                        // ✅ Markiere als bezahlt
                        for (Map<String, String> entry : history) {
                            if ("pending".equals(entry.get("status")) &&
                                String.valueOf(amount).equals(entry.get("amount")) &&
                                url.equals(entry.get("url"))) {
                                entry.put("status", "paid");
                                break;
                            }
                        }
                    } else if ("checkout.session.expired".equals(eventType)) {
                        // ❌ Entferne abgelaufene Zahlung
                        history.removeIf(entry ->
                            "pending".equals(entry.get("status")) &&
                            String.valueOf(amount).equals(entry.get("amount")) &&
                            url.equals(entry.get("url"))
                        );
                    }
                    userRepository.save(user);
                }
            }
        }
        

        return ResponseEntity.ok("Webhook handled");
    }

}

package com.web_boxx.dashboard.app.stripe;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.web_boxx.dashboard.app.models.User;
import com.web_boxx.dashboard.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final UserRepository userRepository;

    public StripeWebhookController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ungültige Signatur");
        }

        switch (event.getType()) {
            case "customer.subscription.created" -> handleSubscriptionCreated(event);
            case "customer.subscription.deleted" -> handleSubscriptionDeleted(event);
            case "invoice.created" -> handleInvoiceCreated(event);
            case "invoice.paid" -> handleInvoicePaid(event);
            case "invoice.payment_failed" -> handleInvoicePaymentFailed(event);
            default -> System.out.println("Unhandled event: " + event.getType());
        }

        return ResponseEntity.ok("Webhook verarbeitet");
    }

    private void handleSubscriptionCreated(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().get();
        String customerId = subscription.getCustomer();

        Optional<User> optionalUser = userRepository.findByStripeCustomerId(customerId);
        optionalUser.ifPresent(user -> {
            user.setSubscriptionStatus("active");
            user.setIsActive(true);
            userRepository.save(user);
        });
    }

    private void handleSubscriptionDeleted(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().get();
        String customerId = subscription.getCustomer();

        Optional<User> optionalUser = userRepository.findByStripeCustomerId(customerId);
        optionalUser.ifPresent(user -> {
            user.setSubscriptionStatus("canceled");
            user.setIsActive(false);
            userRepository.save(user);
        });
    }

    private void handleInvoiceCreated(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().get();
        System.out.println("Rechnung erstellt: " + invoice.getId());
    }

    private void handleInvoicePaid(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().get();
        String customerId = invoice.getCustomer();

        Optional<User> optionalUser = userRepository.findByStripeCustomerId(customerId);
        optionalUser.ifPresent(user -> {
            user.setSubscriptionStatus("paid");
            userRepository.save(user);
        });
    }

    private void handleInvoicePaymentFailed(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().get();
        String customerId = invoice.getCustomer();

        Optional<User> optionalUser = userRepository.findByStripeCustomerId(customerId);
        optionalUser.ifPresent(user -> {
            user.setSubscriptionStatus("payment_failed");
            user.setIsActive(false);
            userRepository.save(user);
        });
    }
}

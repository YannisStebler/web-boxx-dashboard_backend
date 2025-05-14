package com.web_boxx.dashboard.app.services;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.web_boxx.dashboard.app.models.User;
import com.web_boxx.dashboard.app.repositories.UserRepository;
import com.web_boxx.dashboard.app.security.JwtHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeSubscriptionService {

    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final SubscriptionService subscriptionService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public String createSubscriptionCheckoutSession() throws StripeException {
        Stripe.apiKey = stripeApiKey;

        String userId = jwtHelper.getUserIdFromToken();
        User user = userRepository.findById(userId).orElseThrow();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(user.getStripeCustomerId())
                .setSuccessUrl("http://localhost:4200/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:4200/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice("price_1ROdf0KcxI7Mq20Dz4t2Vvf6") // Pauschalpreis (monatlich)
                                .build()
                )
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice("price_1ROdf0KcxI7Mq20Da5ATJYxq") // createdProducts (nutzungsbasiert)
                                .build()
                )
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice("price_1ROdf0KcxI7Mq20DyHT3aEgS") // completedOrders (nutzungsbasiert)
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    public void reportBasePrice(String stripeCustomerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(stripeApiKey, "");
        headers.set("Stripe-Version", "2025-03-31.basil");

        Map<String, Object> payload = new HashMap<>();
        payload.put("stripe_customer_id", stripeCustomerId);
        payload.put("value", "1");

        Map<String, Object> body = new HashMap<>();
        body.put("event_name", "baseprice");
        body.put("payload", payload);
        body.put("timestamp", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.stripe.com/v2/billing/meter_events", request, String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Stripe usage report failed: " + response.getBody());
        }
    }

    public void reportUsage(String eventName, long quantity) {
        Stripe.apiKey = stripeApiKey;

        String userId = jwtHelper.getUserIdFromToken();
        User user = userRepository.findById(userId).orElseThrow();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(stripeApiKey, "");
        headers.set("Stripe-Version", "2025-03-31.basil");

        Map<String, Object> payload = new HashMap<>();
        payload.put("stripe_customer_id", user.getStripeCustomerId());
        payload.put("value", String.valueOf(quantity));

        Map<String, Object> body = new HashMap<>();
        body.put("event_name", eventName);
        body.put("payload", payload);

        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        body.put("timestamp", timestamp);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = new RestTemplate().postForEntity(
                "https://api.stripe.com/v2/billing/meter_events", request, String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Stripe usage report failed: " + response.getBody());
        }
    }

    public String cancelSubscription() throws StripeException {
        Stripe.apiKey = stripeApiKey;

        String userId = jwtHelper.getUserIdFromToken();
        User user = userRepository.findById(userId).orElseThrow();

        List<Subscription> subscriptions = Subscription.list(
                Map.of("customer", user.getStripeCustomerId(), "status", "active")
        ).getData();

        if (subscriptions.isEmpty()) {
            return "Keine aktive Subscription gefunden.";
        }

        Subscription sub = subscriptions.get(0); 

        Map<String, Object> cancelParams = new HashMap<>();
        cancelParams.put("invoice_now", true);
        cancelParams.put("prorate", false);

        sub.cancel(cancelParams);

        subscriptionService.cancelSubscription();

        return "Subscription wurde sofort gekündigt und finale Rechnung gestellt.";
    }

}

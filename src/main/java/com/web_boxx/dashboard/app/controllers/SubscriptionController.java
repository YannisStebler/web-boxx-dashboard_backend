package com.web_boxx.dashboard.app.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;
import com.web_boxx.dashboard.app.services.StripeSubscriptionService;
import com.web_boxx.dashboard.app.services.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final StripeSubscriptionService stripeSubscriptionService;

    private final SubscriptionService subscriptionService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/start-checkout")
    public ResponseEntity<String> startCheckout() {
        try {
            String url = stripeSubscriptionService.createSubscriptionCheckoutSession();
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Starten des Checkouts: " + e.getMessage());
        }
    }

    @PostMapping("/usage/{type}/{count}")
    public ResponseEntity<String> reportUsage(@PathVariable String type, @PathVariable int count) {
        try {
            stripeSubscriptionService.reportUsage(type, count);
            return ResponseEntity.ok("Usage reported");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Melden: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancel() {
        try {
            String result = stripeSubscriptionService.cancelSubscription();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Kündigen: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("customer.subscription.created".equalsIgnoreCase(event.getType())) {
                Subscription subscription = (Subscription) event.getData().getObject();
                System.out.println("Made event: " + event.getType() + " - Customer ID: " + subscription.getCustomer());

                stripeSubscriptionService.reportBasePrice(subscription.getCustomer());
                subscriptionService.createSubscription(subscription.getCustomer());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
    }

}

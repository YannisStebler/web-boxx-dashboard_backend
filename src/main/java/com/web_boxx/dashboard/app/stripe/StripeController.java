package com.web_boxx.dashboard.app.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.UsageRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe/test")
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/subscribe")
    public String startSubscription(@RequestParam String customerId, @RequestParam String priceId) throws StripeException {
        Subscription subscription = stripeService.createMeteredSubscription(customerId, priceId);
        return "Subscription ID: " + subscription.getId();
    }

    @PostMapping("/report")
    public String reportUsage(@RequestParam String subscriptionItemId, @RequestParam long units) throws StripeException {
        UsageRecord record = stripeService.reportUsage(subscriptionItemId, units);
        return "Usage ID: " + record.getId();
    }
}

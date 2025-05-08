package com.web_boxx.dashboard.app.stripe;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.web_boxx.dashboard.app.models.UsageRecord;
import com.web_boxx.dashboard.app.repositories.UsageRecordRepository;
import com.web_boxx.dashboard.app.security.JwtHelper;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    public Session createCheckoutSession(String stripeCustomerId, long amountInCents) throws StripeException {
        Stripe.apiKey = stripeApiKey;
    
        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setCustomer(stripeCustomerId)
            .setSuccessUrl("https://www.quikcashier.com/payment/success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("https://www.quikcashier.com/payment/cancel")
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("eur")
                            .setUnitAmount(amountInCents)
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Benutzerdefinierter Kauf")
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build();
    
        return Session.create(params);
    }

    public Long calculatePricingAmount() {

        Long base = 1495L; // 14,95 Fr in Cent

        String userId = jwtHelper.getUserIdFromToken(); 
        List<UsageRecord> usageRecords = usageRecordRepository.findByUserId(userId);

        Long totalAmount = base;
        if (usageRecords != null && !usageRecords.isEmpty()) {
            for (UsageRecord record : usageRecords) {
            totalAmount += record.getPrice() != null ? Long.parseLong(record.getPrice()) : 0L;
            }
        }
        return totalAmount;
    }
        
}

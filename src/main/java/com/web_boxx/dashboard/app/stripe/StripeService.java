package com.web_boxx.dashboard.app.stripe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

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
    
        return Session.create(params); // gib die ganze Session zurück
    }
    
}

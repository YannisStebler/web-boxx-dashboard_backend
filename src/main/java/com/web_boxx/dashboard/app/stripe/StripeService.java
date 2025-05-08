package com.web_boxx.dashboard.app.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.UsageRecord;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.UsageRecordCreateOnSubscriptionItemParams;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StripeService {

    public Subscription createMeteredSubscription(String customerId, String priceId) throws StripeException {
        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
            .setCustomer(customerId)
            .addItem(SubscriptionCreateParams.Item.builder().setPrice(priceId).build())
            .setCollectionMethod(SubscriptionCreateParams.CollectionMethod.CHARGE_AUTOMATICALLY)
            .build();

        return Subscription.create(params);
    }

    public UsageRecord reportUsage(String subscriptionItemId, long quantity) throws StripeException {
        UsageRecordCreateOnSubscriptionItemParams params =
            UsageRecordCreateOnSubscriptionItemParams.builder()
                .setQuantity(quantity)
                .setTimestamp(Instant.now().getEpochSecond())
                .setAction(UsageRecordCreateOnSubscriptionItemParams.Action.INCREMENT)
                .build();

        return UsageRecord.createOnSubscriptionItem(subscriptionItemId, params, null);
    }
}

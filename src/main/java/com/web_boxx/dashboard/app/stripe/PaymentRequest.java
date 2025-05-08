package com.web_boxx.dashboard.app.stripe;

import lombok.Data;

@Data
public class PaymentRequest {
    private String userId;
    private long amount;

}

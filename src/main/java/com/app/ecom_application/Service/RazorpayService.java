package com.app.ecom_application.Service;

import com.app.ecom_application.Dto.PaymentOrderResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.util.UUID;

@Service
public class RazorpayService {

    @Value("${app.payment.mode:mock}")
    private String paymentMode;

    @Value("${app.razorpay.key_id:rzp_test_demo}")
    private String keyId;

    @Value("${app.razorpay.key_secret:demo_secret}")
    private String keySecret;

    public PaymentOrderResponse createOrder(long amountInPaise, String currency, String receipt) {
        if ("mock".equalsIgnoreCase(paymentMode)) {
            return createMockOrder(amountInPaise, currency, receipt);
        }

        return createRealOrder(amountInPaise, currency, receipt);
    }

    public PaymentOrderResponse createMockOrder(long amountInPaise, String currency, String receipt) {
        String orderId = "order_" + UUID.randomUUID().toString().substring(0, 12);
        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setOrderId(orderId);
        response.setCurrency(currency == null || currency.isBlank() ? "INR" : currency);
        response.setAmount((int) amountInPaise);
        response.setKey(keyId);
        response.setReceipt(receipt == null || receipt.isBlank() ? "demo-receipt" : receipt);
        response.setStatus("created");
        response.setMessage("Mock Razorpay order created successfully.");
        return response;
    }

    public PaymentOrderResponse createRealOrder(long amountInPaise, String currency, String receipt) {
        String resolvedCurrency = currency == null || currency.isBlank() ? "INR" : currency;
        String resolvedReceipt = receipt == null || receipt.isBlank() ? "order_receipt" : receipt;
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", resolvedCurrency);
            options.put("receipt", resolvedReceipt);
            options.put("payment_capture", 1);

            Order order = client.orders.create(options);
            PaymentOrderResponse response = new PaymentOrderResponse();
            response.setOrderId(order.get("id"));
            response.setCurrency(order.get("currency"));
            response.setAmount(order.get("amount"));
            response.setKey(keyId);
            response.setReceipt(order.get("receipt"));
            response.setStatus(order.get("status"));
            response.setMessage("Razorpay order created successfully.");
            return response;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to create Razorpay order.", exception);
        }
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        if (orderId == null || paymentId == null || signature == null) {
            return false;
        }

        if ("mock".equalsIgnoreCase(paymentMode)) {
            return paymentId.startsWith("pay_")
                    && signature.startsWith("sig_")
                    && orderId.startsWith("order_");
        }

        try {
            return keySecret != null && !keySecret.isBlank()
                    && Utils.verifyPaymentSignature(
                    new JSONObject()
                            .put("razorpay_order_id", orderId)
                            .put("razorpay_payment_id", paymentId)
                            .put("razorpay_signature", signature), keySecret);
        } catch (Exception exception) {
            return false;
        }
    }
}

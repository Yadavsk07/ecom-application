package com.app.ecom_application;

import com.app.ecom_application.Service.RazorpayService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    @Test
    void createPaymentOrderReturnsReceiptAndAmount() {
        RazorpayService service = new RazorpayService();

        var response = service.createMockOrder(2500, "INR", "demo-order");

        assertNotNull(response);
        assertEquals("INR", response.getCurrency());
        assertEquals(2500, response.getAmount());
        assertNotNull(response.getOrderId());
    }
}

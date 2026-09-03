package com.app.ecom_application;

import com.app.ecom_application.Dto.AuthRequest;
import com.app.ecom_application.Dto.RegisterRequest;
//import com.app.ecom_application.Service.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthFlowTest {

    @Test
    void registerRequestShouldContainRequiredFields() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setEmail("alice@example.com");
        request.setPhone("9999999999");
        request.setPassword("admin123");

        assertEquals("Alice", request.getFirstName());
        assertEquals("admin123", request.getPassword());
    }

    @Test
    void authRequestShouldAcceptEmailAndPassword() {
        AuthRequest request = new AuthRequest();
        request.setEmail("admin@example.com");
        request.setPassword("secret");

        assertEquals("admin@example.com", request.getEmail());
        assertEquals("secret", request.getPassword());
    }
}

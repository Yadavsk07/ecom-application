package com.app.ecom_application.Dto;

import com.app.ecom_application.Model.UserRole;
import lombok.Data;

@Data
public class UserRequest {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private AddressDto address;
}

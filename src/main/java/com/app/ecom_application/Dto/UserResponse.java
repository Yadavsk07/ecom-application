package com.app.ecom_application.Dto;

import com.app.ecom_application.Model.UserRole;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonPropertyOrder({
        "id",
        "firstName",
        "lastName",
        "email",
        "phone",
        "role",
        "address"
})
@Data
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole role;

    private AddressDto address;

}

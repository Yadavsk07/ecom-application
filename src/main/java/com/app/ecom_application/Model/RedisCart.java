package com.app.ecom_application.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisCart {

    private Long userId;

    private List<RedisCartItem> items = new ArrayList<>();
}
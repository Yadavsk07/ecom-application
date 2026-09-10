package com.app.ecom_application.Repository;

import com.app.ecom_application.Model.RedisCart;
import com.app.ecom_application.Model.RedisCartItem;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisCartRepository {

    private static final String CART_KEY_PREFIX = "cart:user:";

    private static final long CART_TTL_HOURS = 24;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCartRepository(
            RedisTemplate<String, Object> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    private String getKey(Long userId) {

        return CART_KEY_PREFIX + userId;
    }

    public RedisCart getCart(Long userId) {

        Object value = redisTemplate.opsForValue()
                .get(getKey(userId));

        if (value == null) {
            return null;
        }

        if (value instanceof RedisCart redisCart) {
            return redisCart;
        }

        return null;
    }

    public void saveCart(RedisCart cart) {

        redisTemplate.opsForValue()
                .set(
                        getKey(cart.getUserId()),
                        cart,
                        CART_TTL_HOURS,
                        TimeUnit.HOURS
                );
    }

    public void deleteCart(Long userId) {

        redisTemplate.delete(getKey(userId));
    }

    public boolean exists(Long userId) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(getKey(userId))
        );
    }
}
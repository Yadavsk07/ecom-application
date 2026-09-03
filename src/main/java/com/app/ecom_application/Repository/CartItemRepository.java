package com.app.ecom_application.Repository;


import com.app.ecom_application.Model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, String> {

    CartItem findByUserIdAndProductId(Long userId, Long productId);

    long deleteByUserIdAndProductId(Long userId, Long productId);

    List<CartItem> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}

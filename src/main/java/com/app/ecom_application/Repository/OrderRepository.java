package com.app.ecom_application.Repository;

import com.app.ecom_application.Model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
	List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
	boolean existsByPaymentOrderId(String paymentOrderId);
}

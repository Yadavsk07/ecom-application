package com.app.ecom_application.Repository;

import com.app.ecom_application.Dto.ProductResponse;
import com.app.ecom_application.Model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, Long>
{

    List<Product> findByActiveTrue();

    @Query("{ 'active': true, 'stockQuantity': { $gt: 0 }, 'name': { $regex: ?0, $options: 'i' } }")
    List<Product> searchProducts(String keyword);
}

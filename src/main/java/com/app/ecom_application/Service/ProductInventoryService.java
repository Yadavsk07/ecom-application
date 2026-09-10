package com.app.ecom_application.Service;

import com.app.ecom_application.Model.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ProductInventoryService {

    private final MongoTemplate mongoTemplate;

    public ProductInventoryService(
            MongoTemplate mongoTemplate) {

        this.mongoTemplate = mongoTemplate;
    }

    public boolean decreaseStock(
            Long productId,
            int quantity) {

        Query query = new Query();

        query.addCriteria(
                Criteria.where("_id")
                        .is(productId)
        );

        query.addCriteria(
                Criteria.where("stockQuantity")
                        .gte(quantity)
        );

        Update update = new Update();

        update.inc(
                "stockQuantity",
                -quantity
        );

        var result =
                mongoTemplate.updateFirst(
                        query,
                        update,
                        Product.class
                );

        return result.getModifiedCount() == 1;
    }
}
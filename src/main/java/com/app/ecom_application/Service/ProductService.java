package com.app.ecom_application.Service;


import com.app.ecom_application.Dto.ProductRequest;
import com.app.ecom_application.Dto.ProductResponse;
import com.app.ecom_application.Dto.UserResponse;
import com.app.ecom_application.Model.Product;
import com.app.ecom_application.Repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @CacheEvict(value = {"products", "productSearch"}, allEntries = true)
    public ProductResponse createProduct(ProductRequest productRequest) {

        Product product = new Product();
        updateProductFromRequest(product , productRequest);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    private ProductResponse mapToProductResponse(Product savedProduct) {
        ProductResponse response = new ProductResponse();

        response.setId(savedProduct.getId());
        response.setName(savedProduct.getName());
        response.setDescription(savedProduct.getDescription());
        response.setCategory(savedProduct.getCategory());
        response.setActive(savedProduct.getActive());
        response.setImageUrl(savedProduct.getImageUrl());
        response.setPrice(savedProduct.getPrice());
        response.setStockQuantity(savedProduct.getStockQuantity());

        return response;
    }

    private void updateProductFromRequest(Product product, ProductRequest productRequest)
    {

        product.setId(productRequest.getId());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());

    }

    @CacheEvict(value = {"products", "productSearch"}, allEntries = true)
    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest)
    {

        return productRepository.findById(id)
                .map(existingProduct ->
                {
                    updateProductFromRequest(existingProduct , productRequest);
                    Product savedProduct = productRepository.save(existingProduct);
                    return mapToProductResponse(savedProduct);
                });
    }


    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponse> fetchAllProducts()
    {
        System.out.println("Fetching products from MongoDB...");

        return productRepository.findByActiveTrue().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> fetchAllProductsForAdmin() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"products", "productSearch"}, allEntries = true)
    public boolean deleteProduct(Long id) {

        return productRepository.findById(id)
                .map(product -> {product.setActive(false);
                    productRepository.save(product);
                     return true;
                }).orElse(false);

    }

    @Cacheable(value = "productSearch", key = "#keyword.toLowerCase()")
    public List<ProductResponse> searchProduct(String keyword) {

        System.out.println("Searching products from MongoDB...");

        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }
}

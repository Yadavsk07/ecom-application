package com.app.ecom_application.Service;

import com.app.ecom_application.Dto.CartItemRequest;
import com.app.ecom_application.Dto.CartItemResponse;
import com.app.ecom_application.Dto.ProductResponse;
import com.app.ecom_application.Dto.UserResponse;
import com.app.ecom_application.Model.CartItem;
import com.app.ecom_application.Model.Product;
import com.app.ecom_application.Model.User;
import com.app.ecom_application.Repository.CartItemRepository;
import com.app.ecom_application.Repository.ProductRepository;
import com.app.ecom_application.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Data
@Service
public class CartService {


    private UserRepository userRepository;
    private ProductRepository productRepository;
    private CartItemRepository cartItemRepository;

    public CartService(UserRepository userRepository, ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    private CartItemResponse mapToCartItemResponse(CartItem Item) {
        CartItemResponse response = new CartItemResponse();

        response.setId(Item.getId());
        response.setUserId(Item.getUserId());
        response.setProductId(Item.getProductId());
        response.setPrice(Item.getPrice());
        response.setQuantity(Item.getQuantity());

        Optional<User> userOpt =
                userRepository.findById(Item.getUserId());

        if (userOpt.isPresent()) {

            User user = userOpt.get();

            UserResponse userResponse = new UserResponse();

            userResponse.setId(user.getId());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setEmail(user.getEmail());
            userResponse.setPhone(user.getPhone());
            userResponse.setRole(user.getRole());

            response.setUser(userResponse);
        }


        Optional<Product> productOpt =
                productRepository.findById(Item.getProductId());

        if (productOpt.isPresent()) {

            Product product = productOpt.get();

            ProductResponse productResponse =
                    new ProductResponse();

            productResponse.setId(product.getId());
            productResponse.setName(product.getName());
            productResponse.setDescription(product.getDescription());
            productResponse.setPrice(product.getPrice());
            productResponse.setStockQuantity(product.getStockQuantity());
            productResponse.setCategory(product.getCategory());
            productResponse.setImageUrl(product.getImageUrl());
            productResponse.setActive(product.getActive());

            response.setProduct(productResponse);
        }


        return response;
    }



    public boolean addToCart(Long userId, CartItemRequest request) {

        System.out.println("===== ADD TO CART =====");
        System.out.println("User ID: " + userId);
        System.out.println("Product ID: " + request.getProductId());
        System.out.println("Quantity: " + request.getQuantity());

        Optional<Product> productOpt = productRepository.findById(request.getProductId());

        if(productOpt.isEmpty())
            return false;

        Product product = productOpt.get();

        if (!Boolean.TRUE.equals(product.getActive()) || request.getQuantity() <= 0) {
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);

        if(userOpt.isEmpty())
            return false;

        User user = userOpt.get();

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(user.getId() , product.getId());

        if(existingCartItem != null)
        {
            //Update the quantity
            int newQuantity = existingCartItem.getQuantity() + request.getQuantity();
            if (product.getStockQuantity() < newQuantity) {
                return false;
            }
            existingCartItem.setQuantity(newQuantity);
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            cartItemRepository.save(existingCartItem);

        }
        else
        {
            CartItem cartItem = new CartItem();

            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
        }

        return true;
    }

    public boolean deleteItemFromCart(Long userId, Long productId) {

        Optional<Product> productOpt = productRepository.findById(productId);

        if(productOpt.isEmpty()) {
            //System.out.println("Product Not found");
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);

        if(userOpt.isEmpty())
        {
            //System.out.println("User not found");
            return false;
        }


        long deletedCount =
                cartItemRepository.deleteByUserIdAndProductId(userId, productId);

        if (deletedCount == 0) {
            //System.out.println("Product is not present in the cart");
            return false;
        }



        return true;
    }


    public List<CartItemResponse> getCart(Long userId) {

        if (userRepository.findById(userId).isEmpty()) {
            return Collections.emptyList();
        }

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(userId);

        return cartItems.stream()
                .map(this::mapToCartItemResponse)
                .toList();
    }

    public BigDecimal getCartTotal(Long userId) {
        return getCart(userId).stream()
                .map(CartItemResponse::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public void clearCart(Long userId) {

        userRepository.findById(userId).ifPresent(user ->
                cartItemRepository.deleteByUserId(userId));
    }
}

package com.app.ecom_application.Service;

import com.app.ecom_application.Dto.*;
import com.app.ecom_application.Model.CartItem;
import com.app.ecom_application.Model.Product;
import com.app.ecom_application.Model.RedisCart;
import com.app.ecom_application.Model.RedisCartItem;
import com.app.ecom_application.Model.User;
import com.app.ecom_application.Repository.CartItemRepository;
import com.app.ecom_application.Repository.ProductRepository;
import com.app.ecom_application.Repository.RedisCartRepository;
import com.app.ecom_application.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final RedisCartRepository redisCartRepository;

    public CartService(
            UserRepository userRepository,
            ProductRepository productRepository,
            CartItemRepository cartItemRepository,
            RedisCartRepository redisCartRepository) {

        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.redisCartRepository = redisCartRepository;
    }

    // =========================================================
    // ADD TO CART
    // =========================================================

    public boolean addToCart(
            Long userId,
            CartItemRequest request) {

        System.out.println("===== ADD TO CART =====");

        Optional<User> userOpt =
                userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return false;
        }

        Optional<Product> productOpt =
                productRepository.findById(request.getProductId());

        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();

        if (!Boolean.TRUE.equals(product.getActive())) {
            return false;
        }

        if (request.getQuantity() <= 0) {
            return false;
        }

        // -----------------------------------------------------
        // Get cart from Redis
        // -----------------------------------------------------

        RedisCart cart =
                redisCartRepository.getCart(userId);

        // -----------------------------------------------------
        // Redis MISS
        // Load MongoDB cart
        // -----------------------------------------------------

        if (cart == null) {

            System.out.println(
                    "Redis MISS - Loading cart from MongoDB"
            );

            cart = loadCartFromMongoDB(userId);
        }

        // -----------------------------------------------------
        // Find existing product
        // -----------------------------------------------------

        Optional<RedisCartItem> existingItem =
                cart.getItems()
                        .stream()
                        .filter(item ->
                                item.getProductId()
                                        .equals(product.getId()))
                        .findFirst();

        if (existingItem.isPresent()) {

            RedisCartItem item = existingItem.get();

            int newQuantity =
                    item.getQuantity()
                            + request.getQuantity();

            if (product.getStockQuantity() < newQuantity) {
                return false;
            }

            item.setQuantity(newQuantity);

            item.setUnitPrice(
                    product.getPrice()
            );

        } else {

            if (product.getStockQuantity()
                    < request.getQuantity()) {

                return false;
            }

            RedisCartItem newItem =
                    new RedisCartItem(
                            product.getId(),
                            request.getQuantity(),
                            product.getPrice()
                    );

            cart.getItems().add(newItem);
        }

        // -----------------------------------------------------
        // Save Redis
        // -----------------------------------------------------

        redisCartRepository.saveCart(cart);

        // -----------------------------------------------------
        // Synchronize MongoDB
        // -----------------------------------------------------

        saveCartToMongoDB(cart);

        return true;
    }

    // =========================================================
    // GET CART
    // =========================================================

    public List<CartItemResponse> getCart(Long userId) {

        if (userRepository.findById(userId).isEmpty()) {
            return Collections.emptyList();
        }

        // -----------------------------------------------------
        // First check Redis
        // -----------------------------------------------------

        RedisCart redisCart =
                redisCartRepository.getCart(userId);

        if (redisCart != null) {

            System.out.println(
                    "===== CART REDIS HIT ====="
            );

            return mapRedisCartToResponse(redisCart);
        }

        // -----------------------------------------------------
        // Redis MISS
        // -----------------------------------------------------

        System.out.println(
                "===== CART REDIS MISS ====="
        );

        RedisCart cart =
                loadCartFromMongoDB(userId);

        if (cart.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        // Store MongoDB cart in Redis

        redisCartRepository.saveCart(cart);

        return mapRedisCartToResponse(cart);
    }

    // =========================================================
    // REMOVE ITEM
    // =========================================================

    public boolean deleteItemFromCart(
            Long userId,
            Long productId) {

        if (userRepository.findById(userId).isEmpty()) {
            return false;
        }

        // Load cart from Redis first

        RedisCart cart =
                redisCartRepository.getCart(userId);

        if (cart == null) {

            cart = loadCartFromMongoDB(userId);
        }

        boolean removed =
                cart.getItems()
                        .removeIf(item ->
                                item.getProductId()
                                        .equals(productId));

        if (!removed) {
            return false;
        }

        // Save updated Redis cart

        if (cart.getItems().isEmpty()) {

            redisCartRepository.deleteCart(userId);

        } else {

            redisCartRepository.saveCart(cart);
        }

        // Remove from MongoDB

        cartItemRepository
                .deleteByUserIdAndProductId(
                        userId,
                        productId
                );

        return true;
    }

    // =========================================================
    // CART TOTAL
    // =========================================================

    public BigDecimal getCartTotal(Long userId) {

        return getCart(userId)
                .stream()
                .map(CartItemResponse::getPrice)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    // =========================================================
    // CLEAR CART
    // =========================================================

    public void clearCart(Long userId) {

        // MongoDB

        cartItemRepository
                .deleteByUserId(userId);

        // Redis

        redisCartRepository
                .deleteCart(userId);

        System.out.println(
                "Cart cleared from Redis and MongoDB"
        );
    }

    // =========================================================
    // LOAD CART FROM MONGODB
    // =========================================================

    private RedisCart loadCartFromMongoDB(
            Long userId) {

        List<CartItem> mongoItems =
                cartItemRepository
                        .findByUserId(userId);

        RedisCart cart =
                new RedisCart();

        cart.setUserId(userId);

        List<RedisCartItem> redisItems =
                new ArrayList<>();

        for (CartItem item : mongoItems) {

            BigDecimal unitPrice;

            if (item.getQuantity() != null
                    && item.getQuantity() > 0) {

                unitPrice =
                        item.getPrice()
                                .divide(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        ),
                                        2,
                                        java.math.RoundingMode
                                                .HALF_UP
                                );

            } else {

                unitPrice = item.getPrice();
            }

            RedisCartItem redisItem =
                    new RedisCartItem(
                            item.getProductId(),
                            item.getQuantity(),
                            unitPrice
                    );

            redisItems.add(redisItem);
        }

        cart.setItems(redisItems);

        return cart;
    }

    // =========================================================
    // SAVE CART TO MONGODB
    // =========================================================

    private void saveCartToMongoDB(
            RedisCart redisCart) {

        // Delete existing MongoDB cart

        cartItemRepository
                .deleteByUserId(
                        redisCart.getUserId()
                );

        // Save Redis cart items into MongoDB

        for (RedisCartItem redisItem :
                redisCart.getItems()) {

            CartItem cartItem =
                    new CartItem();

            cartItem.setUserId(
                    redisCart.getUserId()
            );

            cartItem.setProductId(
                    redisItem.getProductId()
            );

            cartItem.setQuantity(
                    redisItem.getQuantity()
            );

            BigDecimal subtotal =
                    redisItem.getUnitPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            redisItem.getQuantity()
                                    )
                            );

            cartItem.setPrice(subtotal);

            cartItemRepository.save(cartItem);
        }
    }

    // =========================================================
    // REDIS CART → RESPONSE
    // =========================================================

    private List<CartItemResponse> mapRedisCartToResponse(
            RedisCart cart) {

        if (cart == null || cart.getItems() == null
                || cart.getItems().isEmpty()) {

            return Collections.emptyList();
        }

        // ---------------------------------------------------------
        // Fetch USER only once
        // ---------------------------------------------------------

        User user = userRepository
                .findById(cart.getUserId())
                .orElse(null);

        if (user == null) {
            return Collections.emptyList();
        }

        UserResponse userResponse =
                new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setRole(user.getRole());

        // ---------------------------------------------------------
        // Collect all product IDs
        // ---------------------------------------------------------

        List<Long> productIds =
                cart.getItems()
                        .stream()
                        .map(RedisCartItem::getProductId)
                        .toList();

        // ---------------------------------------------------------
        // Fetch ALL products in ONE MongoDB query
        // ---------------------------------------------------------

        List<Product> products =
                productRepository.findByIdIn(productIds);

        Map<Long, Product> productMap =
                products.stream()
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        Product::getId,
                                        product -> product
                                )
                        );

        // ---------------------------------------------------------
        // Build response
        // ---------------------------------------------------------

        return cart.getItems()
                .stream()
                .map(item -> {

                    Product product =
                            productMap.get(item.getProductId());

                    if (product == null) {
                        return null;
                    }

                    CartItemResponse response =
                            new CartItemResponse();

                    response.setUserId(
                            cart.getUserId()
                    );

                    response.setProductId(
                            item.getProductId()
                    );

                    response.setQuantity(
                            item.getQuantity()
                    );

                    BigDecimal subtotal =
                            item.getUnitPrice()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    item.getQuantity()
                                            )
                                    );

                    response.setPrice(subtotal);

                    response.setUser(userResponse);

                    ProductResponse productResponse =
                            new ProductResponse();

                    productResponse.setId(
                            product.getId()
                    );

                    productResponse.setName(
                            product.getName()
                    );

                    productResponse.setDescription(
                            product.getDescription()
                    );

                    productResponse.setPrice(
                            product.getPrice()
                    );

                    productResponse.setStockQuantity(
                            product.getStockQuantity()
                    );

                    productResponse.setCategory(
                            product.getCategory()
                    );

                    productResponse.setImageUrl(
                            product.getImageUrl()
                    );

                    productResponse.setActive(
                            product.getActive()
                    );

                    response.setProduct(
                            productResponse
                    );

                    return response;

                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public CartValidationResult validateCartForCheckout(
            Long userId) {

        RedisCart cart =
                redisCartRepository.getCart(userId);

        // ---------------------------------------------------------
        // Redis MISS
        // ---------------------------------------------------------

        if (cart == null) {

            cart = loadCartFromMongoDB(userId);

            if (cart.getItems().isEmpty()) {

                return new CartValidationResult(
                        false,
                        "Cart is empty",
                        BigDecimal.ZERO
                );
            }

            redisCartRepository.saveCart(cart);
        }

        // ---------------------------------------------------------
        // Empty cart
        // ---------------------------------------------------------

        if (cart.getItems() == null
                || cart.getItems().isEmpty()) {

            return new CartValidationResult(
                    false,
                    "Cart is empty",
                    BigDecimal.ZERO
            );
        }

        // ---------------------------------------------------------
        // Fetch products in ONE query
        // ---------------------------------------------------------

        List<Long> productIds =
                cart.getItems()
                        .stream()
                        .map(RedisCartItem::getProductId)
                        .toList();

        List<Product> products =
                productRepository.findByIdIn(productIds);

        Map<Long, Product> productMap =
                products.stream()
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        Product::getId,
                                        product -> product
                                )
                        );

        BigDecimal total =
                BigDecimal.ZERO;

        // ---------------------------------------------------------
        // Validate every item
        // ---------------------------------------------------------

        for (RedisCartItem item :
                cart.getItems()) {

            Product product =
                    productMap.get(item.getProductId());

            if (product == null) {

                return new CartValidationResult(
                        false,
                        "Product " + item.getProductId()
                                + " no longer exists",
                        BigDecimal.ZERO
                );
            }

            // Product active?

            if (!Boolean.TRUE.equals(
                    product.getActive())) {

                return new CartValidationResult(
                        false,
                        "Product '" +
                                product.getName() +
                                "' is no longer available",
                        BigDecimal.ZERO
                );
            }

            // Quantity valid?

            if (item.getQuantity() == null
                    || item.getQuantity() <= 0) {

                return new CartValidationResult(
                        false,
                        "Invalid quantity for product '"
                                + product.getName() + "'",
                        BigDecimal.ZERO
                );
            }

            // Stock validation

            if (product.getStockQuantity() == null
                    || product.getStockQuantity()
                    < item.getQuantity()) {

                return new CartValidationResult(
                        false,
                        "Insufficient stock for product '"
                                + product.getName() + "'",
                        BigDecimal.ZERO
                );
            }

            // -----------------------------------------------------
            // PRICE VALIDATION
            // -----------------------------------------------------

            BigDecimal currentPrice =
                    product.getPrice();

            BigDecimal cartPrice =
                    item.getUnitPrice();

            if (cartPrice == null
                    || currentPrice.compareTo(cartPrice) != 0) {

                return new CartValidationResult(
                        false,
                        "Price changed for product '"
                                + product.getName()
                                + "'. Please review your cart.",
                        BigDecimal.ZERO
                );
            }

            // -----------------------------------------------------
            // Calculate current total
            // -----------------------------------------------------

            BigDecimal subtotal =
                    currentPrice.multiply(
                            BigDecimal.valueOf(
                                    item.getQuantity()
                            )
                    );

            total = total.add(subtotal);
        }

        return new CartValidationResult(
                true,
                "Cart is valid",
                total
        );
    }

    public void clearMongoCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    public void clearRedisCart(Long userId) {
        redisCartRepository.deleteCart(userId);
    }

    public List<CartItemResponse> getCartFromMongoDB(Long userId) {

        RedisCart cart = loadCartFromMongoDB(userId);

        if (cart == null ||
                cart.getItems() == null ||
                cart.getItems().isEmpty()) {

            return List.of();
        }

        return mapRedisCartToResponse(cart);
    }

    public CartValidationResult validateCartForCheckoutFromMongoDB(
            Long userId) {

        RedisCart cart = loadCartFromMongoDB(userId);

        if (cart == null ||
                cart.getItems() == null ||
                cart.getItems().isEmpty()) {

            return new CartValidationResult(
                    false,
                    "Cart is empty",
                    BigDecimal.ZERO
            );
        }

        List<Long> productIds =
                cart.getItems()
                        .stream()
                        .map(RedisCartItem::getProductId)
                        .toList();

        List<Product> products =
                productRepository.findByIdIn(productIds);

        Map<Long, Product> productMap =
                products.stream()
                        .collect(Collectors.toMap(
                                Product::getId,
                                product -> product
                        ));

        BigDecimal total =
                BigDecimal.ZERO;

        for (RedisCartItem item : cart.getItems()) {

            Product product =
                    productMap.get(item.getProductId());

            if (product == null) {

                return new CartValidationResult(
                        false,
                        "Product "
                                + item.getProductId()
                                + " no longer exists",
                        BigDecimal.ZERO
                );
            }

            if (!Boolean.TRUE.equals(product.getActive())) {

                return new CartValidationResult(
                        false,
                        "Product '"
                                + product.getName()
                                + "' is no longer available",
                        BigDecimal.ZERO
                );
            }

            if (item.getQuantity() == null ||
                    item.getQuantity() <= 0) {

                return new CartValidationResult(
                        false,
                        "Invalid quantity for product '"
                                + product.getName()
                                + "'",
                        BigDecimal.ZERO
                );
            }

            if (product.getStockQuantity() == null ||
                    product.getStockQuantity()
                            < item.getQuantity()) {

                return new CartValidationResult(
                        false,
                        "Insufficient stock for product '"
                                + product.getName()
                                + "'",
                        BigDecimal.ZERO
                );
            }

            BigDecimal currentPrice =
                    product.getPrice();

            BigDecimal cartPrice =
                    item.getUnitPrice();

            if (cartPrice == null ||
                    currentPrice.compareTo(cartPrice) != 0) {

                return new CartValidationResult(
                        false,
                        "Price changed for product '"
                                + product.getName()
                                + "'. Please review your cart.",
                        BigDecimal.ZERO
                );
            }

            BigDecimal subtotal =
                    currentPrice.multiply(
                            BigDecimal.valueOf(
                                    item.getQuantity()
                            )
                    );

            total = total.add(subtotal);
        }

        return new CartValidationResult(
                true,
                "Cart is valid",
                total
        );
    }
}
package com.app.ecom_application.Controller;


import com.app.ecom_application.Dto.CartItemRequest;
import com.app.ecom_application.Dto.CartItemResponse;
import com.app.ecom_application.Dto.CartValidationResult;
import com.app.ecom_application.Model.CartItem;
import com.app.ecom_application.Service.CartService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService) {

        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody CartItemRequest request)
    {
        System.out.println("Inside CartController");
        if(!cartService.addToCart(userId , request))
        {
            return ResponseEntity.badRequest().body("Product out of Stock or User Not found or Product not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/item/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long productId)
    {
        boolean deleted = cartService.deleteItemFromCart(userId , productId);


        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<CartValidationResult>
    validateCartForCheckout(
            @RequestAttribute("userId") Long userId) {

        return ResponseEntity.ok(
                cartService.validateCartForCheckout(userId)
        );
    }

//    @GetMapping("/item/{userId}")
//    public ResponseEntity<List<CartItemResponse>> getAllItems(@PathVariable Long userId)
//    {
//
//        return ResponseEntity.ok(cartService.fetchAllItems(userId));
//    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(@RequestAttribute("userId") Long userId)
    {

        return ResponseEntity.ok(cartService.getCart(userId));
    }
}

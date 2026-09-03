package com.app.ecom_application.Controller;

import com.app.ecom_application.Dto.UserRequest;
import com.app.ecom_application.Dto.UserResponse;
import com.app.ecom_application.Model.User;
import com.app.ecom_application.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers()
    {
        return new ResponseEntity<>(userService.fetchAllUsers() , HttpStatus.OK);

        //return ResponseEntity.ok(userService.fetchAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id)
    {
//        User user = userService.fetchUser(id);
//        if(user == null)
//        {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(user);

        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest)
    {
        userService.addUser(userRequest);
        //userList.clear();
        return ResponseEntity.ok("User added");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable Long id,
                                             @RequestBody UserRequest userRequest)
    {
        boolean updated = userService.updateUser(id , userRequest);

        if(updated)
            return ResponseEntity.ok("User Updated");
        return ResponseEntity.notFound().build();
    }


}

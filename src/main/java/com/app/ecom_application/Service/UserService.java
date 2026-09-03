package com.app.ecom_application.Service;

import com.app.ecom_application.Dto.AuthRequest;
import com.app.ecom_application.Dto.AuthResponse;
import com.app.ecom_application.Dto.AddressDto;
import com.app.ecom_application.Dto.RegisterRequest;
import com.app.ecom_application.Dto.UserRequest;
import com.app.ecom_application.Dto.UserResponse;
import com.app.ecom_application.Model.Address;
import com.app.ecom_application.Model.User;
import com.app.ecom_application.Model.UserRole;
import com.app.ecom_application.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail() == null ? "" : registerRequest.getEmail().trim();

        if (email.isEmpty() || registerRequest.getPassword() == null || registerRequest.getPassword().isBlank()) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        User user = new User();
        user.setId(System.currentTimeMillis());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(email);
        user.setPhone(registerRequest.getPhone());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(UserRole.CUSTOMER);

        if(registerRequest.getAddress() != null)
        {
            Address address = new Address();

            address.setStreet(registerRequest.getAddress().getStreet());
            address.setCity(registerRequest.getAddress().getCity());
            address.setState(registerRequest.getAddress().getState());
            address.setCountry(registerRequest.getAddress().getCountry());
            address.setZipcode(registerRequest.getAddress().getZipcode());

            user.setAddress(address);
        }

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser , "Registration Successful");
    }

    public AuthResponse login(AuthRequest authRequest) {
        String email = authRequest.getEmail() == null ? "" : authRequest.getEmail().trim();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        return buildAuthResponse(user , "User Logged in Successfully");
    }

    public List<UserResponse> fetchAllUsers()
    {

        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }


    public void addUser(UserRequest userRequest)
    {
        User user = new User();
        updateUserFromRequest(user , userRequest);

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode("welcome123"));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(user);
    }



    public Optional<UserResponse> fetchUser(Long id)
    {

        return userRepository.findById(id)
                .map(this::mapToUserResponse);

    }

    public boolean updateUser(Long id , UserRequest updatedUserRequest)
    {
        return userRepository.findById(id)
                .map(existingUser -> {
                    updateUserFromRequest(existingUser , updatedUserRequest);
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest)
    {
        if (userRequest.getId() != null) {
            user.setId(userRequest.getId());
        }
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        if(userRequest.getAddress() != null)
        {
            Address address = new Address();

            address.setStreet(userRequest.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getCity());
            address.setState(userRequest.getAddress().getState());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode());

            user.setAddress(address);
        }

    }

    public UserResponse mapToUserResponse(User user)
    {
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole() == null ? UserRole.CUSTOMER : user.getRole());

        if(user.getAddress() != null)
        {
            AddressDto addressDto = new AddressDto();

            addressDto.setStreet(user.getAddress().getStreet());
            addressDto.setCity(user.getAddress().getCity());
            addressDto.setState(user.getAddress().getState());
            addressDto.setCountry(user.getAddress().getCountry());
            addressDto.setZipcode(user.getAddress().getZipcode());

            response.setAddress(addressDto);
        }

        return response;
    }

    private AuthResponse buildAuthResponse(User user , String message) {
        String token = jwtService.generateToken(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setMessage(message);
        authResponse.setUser(mapToUserResponse(user));
        return authResponse;
    }

}

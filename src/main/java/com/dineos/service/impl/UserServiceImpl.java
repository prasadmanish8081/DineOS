package com.dineos.service.impl;

import com.dineos.dto.request.KitchenUserCreateRequest;
import com.dineos.dto.response.UserResponse;
import com.dineos.entity.Restaurant;
import com.dineos.entity.User;
import com.dineos.enums.Role;
import com.dineos.exception.ResourceAlreadyExistsException;
import com.dineos.exception.RestaurantForbiddenException;
import com.dineos.exception.RestaurantNotFoundException;
import com.dineos.exception.ResourceNotFoundException;
import com.dineos.repository.RestaurantRepository;
import com.dineos.repository.UserRepository;
import com.dineos.service.UserService;
import com.dineos.util.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse createKitchenUser(Long restaurantId, String ownerEmail, KitchenUserCreateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
        User owner = userRepository.findByEmailIgnoreCase(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (owner.getRole() != Role.ADMIN && (owner.getRole() != Role.OWNER
                || restaurant.getOwner() == null
                || !restaurant.getOwner().getEmail().equalsIgnoreCase(ownerEmail))) {
            throw new RestaurantForbiddenException("You can only create kitchen users for your own restaurant");
        }

        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResourceAlreadyExistsException("Email is already registered");
        }

        User kitchenUser = new User();
        kitchenUser.setName(request.name().trim());
        kitchenUser.setEmail(request.email().trim().toLowerCase());
        kitchenUser.setPassword(passwordEncoder.encode(request.password()));
        kitchenUser.setRole(Role.KITCHEN);
        kitchenUser.setAssignedRestaurant(restaurant);
        return UserMapper.toResponse(userRepository.save(kitchenUser));
    }
}

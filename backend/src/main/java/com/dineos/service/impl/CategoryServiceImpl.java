package com.dineos.service.impl;

import com.dineos.dto.request.CategoryCreateRequest;
import com.dineos.dto.response.CategoryResponse;
import com.dineos.entity.Category;
import com.dineos.entity.Restaurant;
import com.dineos.entity.User;
import com.dineos.enums.Role;
import com.dineos.exception.CategoryAlreadyExistsException;
import com.dineos.exception.CategoryNotFoundException;
import com.dineos.exception.RestaurantForbiddenException;
import com.dineos.exception.RestaurantNotFoundException;
import com.dineos.exception.ResourceNotFoundException;
import com.dineos.repository.CategoryRepository;
import com.dineos.repository.RestaurantRepository;
import com.dineos.repository.UserRepository;
import com.dineos.service.CategoryService;
import com.dineos.util.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(Long restaurantId, String actorEmail, CategoryCreateRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        User actor = findUser(actorEmail);
        ensureCanManageRestaurant(actor, restaurant);

        if (categoryRepository.existsByNameIgnoreCaseAndRestaurant_Id(request.name().trim(), restaurantId)) {
            throw new CategoryAlreadyExistsException("Category already exists for this restaurant");
        }

        Category category = new Category();
        category.setName(request.name().trim());
        category.setRestaurant(restaurant);
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByRestaurant(Long restaurantId) {
        findRestaurant(restaurantId);
        return categoryRepository.findByRestaurant_Id(restaurantId)
                .stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void ensureCanManageRestaurant(User actor, Restaurant restaurant) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() != Role.OWNER || restaurant.getOwner() == null ||
                !restaurant.getOwner().getEmail().equalsIgnoreCase(actor.getEmail())) {
            throw new RestaurantForbiddenException("You are not allowed to manage this restaurant");
        }
    }
}

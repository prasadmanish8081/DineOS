package com.dineos.service.impl;

import com.dineos.dto.request.MenuItemCreateRequest;
import com.dineos.dto.request.MenuItemUpdateRequest;
import com.dineos.dto.response.MenuItemResponse;
import com.dineos.dto.response.MenuPageResponse;
import com.dineos.entity.Category;
import com.dineos.entity.MenuItem;
import com.dineos.entity.Restaurant;
import com.dineos.entity.User;
import com.dineos.enums.Role;
import com.dineos.exception.CategoryNotFoundException;
import com.dineos.exception.MenuItemAlreadyExistsException;
import com.dineos.exception.MenuItemNotFoundException;
import com.dineos.exception.RestaurantForbiddenException;
import com.dineos.exception.RestaurantNotFoundException;
import com.dineos.exception.ResourceNotFoundException;
import com.dineos.repository.CategoryRepository;
import com.dineos.repository.MenuItemRepository;
import com.dineos.repository.RestaurantRepository;
import com.dineos.repository.UserRepository;
import com.dineos.service.MenuItemService;
import com.dineos.util.MenuItemMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public MenuItemServiceImpl(
            MenuItemRepository menuItemRepository,
            CategoryRepository categoryRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository
    ) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MenuItemResponse addMenuItem(Long restaurantId, String actorEmail, MenuItemCreateRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        User actor = findUser(actorEmail);
        ensureCanManageRestaurant(actor, restaurant);

        Category category = findCategory(request.categoryId(), restaurantId);
        ensureCategoryBelongsToRestaurant(category, restaurantId);

        if (menuItemRepository.existsByNameIgnoreCaseAndRestaurant_Id(request.name().trim(), restaurantId)) {
            throw new MenuItemAlreadyExistsException("Menu item already exists for this restaurant");
        }

        MenuItem item = new MenuItem();
        applyRequest(item, request.name(), request.description(), request.price(), request.imageUrl(),
                request.isVeg(), request.isAvailable(), request.spicyLevel(), request.preparationTime());
        item.setRestaurant(restaurant);
        item.setCategory(category);
        return MenuItemMapper.toResponse(menuItemRepository.save(item));
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long restaurantId, Long menuItemId, String actorEmail, MenuItemUpdateRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        User actor = findUser(actorEmail);
        ensureCanManageRestaurant(actor, restaurant);

        MenuItem item = menuItemRepository.findByIdAndRestaurant_Id(menuItemId, restaurantId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found"));

        Category category = findCategory(request.categoryId(), restaurantId);
        ensureCategoryBelongsToRestaurant(category, restaurantId);

        if (menuItemRepository.existsByNameIgnoreCaseAndRestaurant_IdAndIdNot(request.name().trim(), restaurantId, menuItemId)) {
            throw new MenuItemAlreadyExistsException("Menu item already exists for this restaurant");
        }

        applyRequest(item, request.name(), request.description(), request.price(), request.imageUrl(),
                request.isVeg(), request.isAvailable(), request.spicyLevel(), request.preparationTime());
        item.setCategory(category);
        item.setRestaurant(restaurant);
        return MenuItemMapper.toResponse(menuItemRepository.save(item));
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long restaurantId, Long menuItemId, String actorEmail) {
        Restaurant restaurant = findRestaurant(restaurantId);
        User actor = findUser(actorEmail);
        ensureCanManageRestaurant(actor, restaurant);

        MenuItem item = menuItemRepository.findByIdAndRestaurant_Id(menuItemId, restaurantId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found"));
        menuItemRepository.delete(item);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuPageResponse<MenuItemResponse> getMenuByRestaurant(Long restaurantId, Boolean veg, Pageable pageable) {
        findRestaurant(restaurantId);

        Page<MenuItem> page = veg == null
                ? menuItemRepository.findByRestaurant_Id(restaurantId, pageable)
                : menuItemRepository.findByRestaurant_IdAndIsVeg(restaurantId, veg, pageable);

        return new MenuPageResponse<>(
                page.getContent().stream().map(MenuItemMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Category findCategory(Long categoryId, Long restaurantId) {
        return categoryRepository.findByIdAndRestaurant_Id(categoryId, restaurantId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found for this restaurant"));
    }

    private void ensureCategoryBelongsToRestaurant(Category category, Long restaurantId) {
        if (category.getRestaurant() == null || category.getRestaurant().getId() == null ||
                !category.getRestaurant().getId().equals(restaurantId)) {
            throw new CategoryNotFoundException("Category not found for this restaurant");
        }
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

    private void applyRequest(
            MenuItem item,
            String name,
            String description,
            java.math.BigDecimal price,
            String imageUrl,
            Boolean isVeg,
            Boolean isAvailable,
            Integer spicyLevel,
            Integer preparationTime
    ) {
        item.setName(name.trim());
        item.setDescription(description == null || description.isBlank() ? null : description.trim());
        item.setPrice(price);
        item.setImageUrl(imageUrl == null || imageUrl.isBlank() ? null : imageUrl.trim());
        item.setIsVeg(isVeg);
        item.setIsAvailable(isAvailable);
        item.setSpicyLevel(spicyLevel);
        item.setPreparationTime(preparationTime);
    }
}

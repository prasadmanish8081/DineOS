package com.dineos.service.impl;

import com.dineos.dto.request.RestaurantCreateRequest;
import com.dineos.dto.request.RestaurantUpdateRequest;
import com.dineos.dto.response.RestaurantResponse;
import com.dineos.entity.Restaurant;
import com.dineos.entity.User;
import com.dineos.enums.Role;
import com.dineos.exception.RestaurantAlreadyExistsException;
import com.dineos.exception.RestaurantForbiddenException;
import com.dineos.exception.RestaurantNotFoundException;
import com.dineos.exception.ResourceNotFoundException;
import com.dineos.repository.RestaurantRepository;
import com.dineos.repository.UserRepository;
import com.dineos.service.RestaurantService;
import com.dineos.util.RestaurantMapper;
import com.dineos.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public RestaurantResponse createRestaurant(String ownerEmail, RestaurantCreateRequest request) {
        User owner = findUser(ownerEmail);
        ensureOwnerCanManageRestaurant(owner);

        if (restaurantRepository.existsByOwner_EmailIgnoreCase(ownerEmail)) {
            throw new RestaurantAlreadyExistsException("This owner already manages a restaurant");
        }

        String slug = generateUniqueSlug(request.name(), null);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.name().trim());
        restaurant.setSlug(slug);
        restaurant.setAddress(request.address().trim());
        restaurant.setPhone(request.phone().trim());
        restaurant.setGstNumber(request.gstNumber().trim());
        restaurant.setLogoUrl(request.logoUrl() == null || request.logoUrl().isBlank() ? null : request.logoUrl().trim());
        restaurant.setOwner(owner);
        owner.setRestaurant(restaurant);

        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    @Transactional
    public RestaurantResponse updateRestaurant(Long restaurantId, String ownerEmail, RestaurantUpdateRequest request) {
        User actor = findUser(ownerEmail);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));

        if (!isAdmin(actor) && !restaurant.getOwner().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new RestaurantForbiddenException("You can only update your own restaurant");
        }

        restaurant.setName(request.name().trim());
        restaurant.setAddress(request.address().trim());
        restaurant.setPhone(request.phone().trim());
        restaurant.setGstNumber(request.gstNumber().trim());
        restaurant.setLogoUrl(request.logoUrl() == null || request.logoUrl().isBlank() ? null : request.logoUrl().trim());

        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long restaurantId, String actorEmail) {
        User actor = findUser(actorEmail);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
        if (!isAdmin(actor) && (restaurant.getOwner() == null ||
                !restaurant.getOwner().getEmail().equalsIgnoreCase(actorEmail))) {
            throw new RestaurantForbiddenException("You can only access your own restaurant");
        }
        return RestaurantMapper.toResponse(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantBySlug(String slug) {
        Restaurant restaurant = restaurantRepository.findBySlugIgnoreCase(slug)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
        return RestaurantMapper.toResponse(restaurant);
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void ensureOwnerCanManageRestaurant(User user) {
        if (user.getRole() != Role.OWNER && user.getRole() != Role.ADMIN) {
            throw new RestaurantForbiddenException("Only OWNER or ADMIN users can manage restaurants");
        }
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private String generateUniqueSlug(String name, Long currentRestaurantId) {
        String baseSlug = SlugUtil.slugify(name);
        String slug = baseSlug;
        int suffix = 1;

        while (restaurantRepository.existsBySlugIgnoreCase(slug)) {
            Restaurant existing = restaurantRepository.findBySlugIgnoreCase(slug).orElse(null);
            if (existing != null && currentRestaurantId != null && existing.getId().equals(currentRestaurantId)) {
                break;
            }
            if (existing != null && currentRestaurantId == null) {
                slug = baseSlug + "-" + suffix++;
                continue;
            }
            if (existing != null && !existing.getId().equals(currentRestaurantId)) {
                slug = baseSlug + "-" + suffix++;
                continue;
            }
            break;
        }

        return slug;
    }
}

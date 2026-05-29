package com.dineos.service.impl;

import com.dineos.dto.response.CategoryMenuResponse;
import com.dineos.dto.response.PublicMenuResponse;
import com.dineos.entity.Category;
import com.dineos.entity.MenuItem;
import com.dineos.entity.Restaurant;
import com.dineos.entity.RestaurantTable;
import com.dineos.exception.RestaurantNotFoundException;
import com.dineos.exception.TableNotFoundException;
import com.dineos.repository.CategoryRepository;
import com.dineos.repository.MenuItemRepository;
import com.dineos.repository.RestaurantRepository;
import com.dineos.repository.RestaurantTableRepository;
import com.dineos.service.PublicMenuService;
import com.dineos.util.MenuItemMapper;
import com.dineos.util.RestaurantMapper;
import com.dineos.util.TableMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicMenuServiceImpl implements PublicMenuService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    public PublicMenuServiceImpl(
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository,
            CategoryRepository categoryRepository,
            MenuItemRepository menuItemRepository
    ) {
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PublicMenuResponse getMenuForQr(String restaurantSlug, String tableNumber) {
        Restaurant restaurant = restaurantRepository.findBySlugIgnoreCase(restaurantSlug)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
        RestaurantTable table = tableRepository.findByRestaurant_IdAndTableNumberIgnoreCase(restaurant.getId(), tableNumber.trim())
                .orElseThrow(() -> new TableNotFoundException("Table not found"));

        List<Category> categories = categoryRepository.findByRestaurant_IdOrderByNameAsc(restaurant.getId());
        Map<Long, List<MenuItem>> itemsByCategory = menuItemRepository
                .findByRestaurant_IdAndIsAvailableTrueOrderByCategory_NameAscNameAsc(restaurant.getId())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getCategory().getId()));

        List<CategoryMenuResponse> categoryMenus = categories.stream()
                .map(category -> new CategoryMenuResponse(
                        category.getId(),
                        category.getName(),
                        restaurant.getId(),
                        category.getCreatedAt(),
                        itemsByCategory.getOrDefault(category.getId(), List.of())
                                .stream()
                                .map(MenuItemMapper::toResponse)
                                .toList()
                ))
                .toList();

        return new PublicMenuResponse(
                RestaurantMapper.toResponse(restaurant),
                TableMapper.toResponse(table),
                categoryMenus
        );
    }
}

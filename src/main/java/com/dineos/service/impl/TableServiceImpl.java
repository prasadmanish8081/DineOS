package com.dineos.service.impl;

import com.dineos.dto.request.TableCreateRequest;
import com.dineos.dto.response.TableResponse;
import com.dineos.entity.Restaurant;
import com.dineos.entity.RestaurantTable;
import com.dineos.entity.User;
import com.dineos.enums.Role;
import com.dineos.exception.RestaurantNotFoundException;
import com.dineos.exception.ResourceNotFoundException;
import com.dineos.exception.TableAlreadyExistsException;
import com.dineos.exception.TableForbiddenException;
import com.dineos.exception.TableNotFoundException;
import com.dineos.repository.RestaurantRepository;
import com.dineos.repository.RestaurantTableRepository;
import com.dineos.repository.UserRepository;
import com.dineos.service.TableService;
import com.dineos.service.QrCodeService;
import com.dineos.util.TableMapper;
import com.dineos.config.MenuProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;

@Service
public class TableServiceImpl implements TableService {

    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final QrCodeService qrCodeService;
    private final MenuProperties menuProperties;

    public TableServiceImpl(
            RestaurantTableRepository tableRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            QrCodeService qrCodeService,
            MenuProperties menuProperties
    ) {
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.qrCodeService = qrCodeService;
        this.menuProperties = menuProperties;
    }

    @Override
    @Transactional
    public TableResponse createTable(Long restaurantId, String actorEmail, TableCreateRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        User actor = findUser(actorEmail);
        ensureCanManageRestaurant(actor, restaurant);

        String normalizedTableNumber = normalizeTableNumber(request.tableNumber());
        if (tableRepository.existsByRestaurant_IdAndTableNumberIgnoreCase(restaurantId, normalizedTableNumber)) {
            throw new TableAlreadyExistsException("Table number already exists for this restaurant");
        }

        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(normalizedTableNumber);
        table.setQrCodeUrl(buildPublicMenuPath(restaurant.getSlug(), normalizedTableNumber));
        table.setRestaurant(restaurant);

        return TableMapper.toResponse(tableRepository.save(table));
    }

    @Override
    @Transactional(readOnly = true)
    public TableResponse getTableDetails(Long restaurantId, Long tableId, String actorEmail) {
        Restaurant restaurant = findRestaurant(restaurantId);
        ensureCanManageRestaurant(findUser(actorEmail), restaurant);
        RestaurantTable table = tableRepository.findByIdAndRestaurant_Id(tableId, restaurantId)
                .orElseThrow(() -> new TableNotFoundException("Table not found"));
        return TableMapper.toResponse(table);
    }

    @Override
    @Transactional(readOnly = true)
    public TableResponse getTableByNumber(Long restaurantId, String tableNumber) {
        RestaurantTable table = tableRepository.findByRestaurant_IdAndTableNumberIgnoreCase(restaurantId, normalizeTableNumber(tableNumber))
                .orElseThrow(() -> new TableNotFoundException("Table not found"));
        return TableMapper.toResponse(table);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateQrCode(Long restaurantId, Long tableId, String actorEmail) {
        Restaurant restaurant = findRestaurant(restaurantId);
        ensureCanManageRestaurant(findUser(actorEmail), restaurant);
        RestaurantTable table = tableRepository.findByIdAndRestaurant_Id(tableId, restaurantId)
                .orElseThrow(() -> new TableNotFoundException("Table not found"));

        String baseUrl = normalizeBaseUrl(menuProperties.getBaseUrl());
        if (isLocalHostUrl(baseUrl)) {
            baseUrl = detectLocalNetworkBaseUrl().orElse(baseUrl);
        }

        String absoluteMenuUrl = baseUrl + buildPublicMenuPath(restaurant.getSlug(), table.getTableNumber());
        return qrCodeService.generatePng(absoluteMenuUrl, 320, 320);
    }

    private boolean isLocalHostUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return true;
        }
        try {
            URI uri = new URI(baseUrl);
            String host = uri.getHost();
            return host == null || host.equals("localhost") || host.equals("127.0.0.1") || host.equals("0.0.0.0");
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    private Optional<String> detectLocalNetworkBaseUrl() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface networkInterface : Collections.list(interfaces)) {
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                for (InetAddress address : Collections.list(addresses)) {
                    if (address instanceof Inet4Address && !address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        return Optional.of("http://" + address.getHostAddress() + ":3000");
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
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
            throw new TableForbiddenException("You are not allowed to manage tables for this restaurant");
        }
    }

    private String normalizeTableNumber(String tableNumber) {
        return tableNumber.trim();
    }

    private String normalizeBaseUrl(String baseUrl) {
        String normalized = baseUrl == null ? "" : baseUrl.trim();
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private String buildPublicMenuPath(String restaurantSlug, String tableNumber) {
        return "/#/menu/" + restaurantSlug + "/table/" + tableNumber;
    }
}

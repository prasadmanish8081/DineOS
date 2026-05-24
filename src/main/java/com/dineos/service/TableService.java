package com.dineos.service;

import com.dineos.dto.request.TableCreateRequest;
import com.dineos.dto.response.TableResponse;

public interface TableService {

    TableResponse createTable(Long restaurantId, String actorEmail, TableCreateRequest request);

    TableResponse getTableDetails(Long restaurantId, Long tableId, String actorEmail);

    TableResponse getTableByNumber(Long restaurantId, String tableNumber);

    byte[] generateQrCode(Long restaurantId, Long tableId, String actorEmail);
}

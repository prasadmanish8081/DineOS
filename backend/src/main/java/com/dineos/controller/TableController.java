package com.dineos.controller;

import com.dineos.dto.request.TableCreateRequest;
import com.dineos.dto.response.TableResponse;
import com.dineos.security.UserPrincipal;
import com.dineos.service.TableService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<TableResponse> createTable(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @Valid @RequestBody TableCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(restaurantId, principal.getUsername(), request));
    }

    @GetMapping("/{tableId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<TableResponse> getTableDetails(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long tableId
    ) {
        return ResponseEntity.ok(tableService.getTableDetails(restaurantId, tableId, principal.getUsername()));
    }

    @GetMapping("/by-number/{tableNumber}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TableResponse> getTableByNumber(
            @PathVariable Long restaurantId,
            @PathVariable String tableNumber
    ) {
        return ResponseEntity.ok(tableService.getTableByNumber(restaurantId, tableNumber));
    }

    @GetMapping("/{tableId}/qr-code")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<byte[]> generateQrCode(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long tableId
    ) {
        byte[] png = tableService.generateQrCode(restaurantId, tableId, principal.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"table-" + tableId + "-qr.png\"")
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)))
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }
}

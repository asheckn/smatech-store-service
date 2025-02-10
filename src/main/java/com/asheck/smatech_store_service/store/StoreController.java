package com.asheck.smatech_store_service.store;

import com.asheck.smatech_store_service.helper.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/promoter")
@RequiredArgsConstructor
public class StoreController {


    private final StoreService storeService;

    @GetMapping("/getStore")
    public ResponseEntity<?> getStore() {
        Store store = storeService.getStore();
        return ResponseEntity.ok(new ApiResponse<>(true, "Store retrieved successfully", store));
    }

    @PostMapping("/create-store")
    public ResponseEntity<?> createStore(@RequestBody CreateStoreDto storeDetails) {
        Store createdStore = storeService.createStore(storeDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Store created successfully", createdStore));
    }

    @PutMapping("/update-store")
    public ResponseEntity<?> updateStore(@RequestBody CreateStoreDto storeDetails, @RequestParam long storeId) {
        Store createdStore = storeService.updateStore(storeDetails, storeId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Store created successfully", createdStore));
    }


}

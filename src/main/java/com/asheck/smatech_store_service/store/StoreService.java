package com.asheck.smatech_store_service.store;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public Store getStore() {
        List<Store> stores = storeRepository.findAll();

        if (stores.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No store found");
        }

        return stores.get(0); // Safe since we checked it's not empty
    }


    public Store createStore(CreateStoreDto storeDto) {




        List<Store> stores = storeRepository.findAll();


        if(!stores.isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Store already setup");
        }

        Store store = new Store();
        store.setStoreName(storeDto.storeName());
        store.setStoreCode(UUID.randomUUID());
        store.setAddress(storeDto.address());
        store.setClosingTime(storeDto.closingTime());
        store.setOpeningTime(storeDto.openingTime());
        store.setContactNumbers(storeDto.contactNumbers());
        store.setEmail(storeDto.email());
        store.setDescription(storeDto.description());
        store.setRegistrationNumber(storeDto.registrationNumber());
        store.setTaxNumber(store.getTaxNumber());

       return  storeRepository.save(store);

    }

    @Transactional
    public Store updateStore(CreateStoreDto storeDetails, long storeId) {
        // Find existing store
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store with ID " + storeId + " not found"));

        // Update store fields
        if (storeDetails.storeName() != null) {
            store.setStoreName(storeDetails.storeName());
        }
        if (storeDetails.address() != null) {
            store.setAddress(storeDetails.address());
        }
        if (storeDetails.closingTime() != null) {
            store.setClosingTime(storeDetails.closingTime());
        }
        if (storeDetails.openingTime() != null) {
            store.setOpeningTime(storeDetails.openingTime());
        }
        if (storeDetails.contactNumbers() != null && !storeDetails.contactNumbers().isEmpty()) {
            store.setContactNumbers(storeDetails.contactNumbers());
        }
        if (storeDetails.email() != null) {
            store.setEmail(storeDetails.email());
        }
        if (storeDetails.description() != null) {
            store.setDescription(storeDetails.description());
        }
        if (storeDetails.registrationNumber() != null) {
            store.setRegistrationNumber(storeDetails.registrationNumber());
        }
        if (storeDetails.taxNumber() != null) {
            store.setTaxNumber(storeDetails.taxNumber());
        }

        // Save and return updated store
        return storeRepository.save(store);
    }
}

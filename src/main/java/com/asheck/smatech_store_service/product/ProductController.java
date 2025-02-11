package com.asheck.smatech_store_service.product;


import com.asheck.smatech_store_service.helper.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping(path = "api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    // create a new product
    @PostMapping(path = "/create-product", consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(
            @RequestParam String name,
            @RequestParam long categoryId,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam BigDecimal vatRate,
            @RequestParam String currencyCode,
            @RequestParam Integer stock,
            @RequestParam ProductStatus status,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        ProductDto productDto = new ProductDto(
                name,
                categoryId,
                description,
                price,
                vatRate,
                currencyCode,
                stock,
                status
        );


        Product createdProduct = productService.createProduct(productDto, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Product created successfully", createdProduct));
    }

    // get product by id
    @GetMapping("/getProductById/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable long productId ) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product retrieved successfully", product));
    }

    // get product by code
    @GetMapping("/getProductByCode/{productCode}")
    public ResponseEntity<?> getProductByCode(@PathVariable String productCode ) {
        Product product = productService.getProductByCode(productCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product retrieved successfully", product));
    }

    // get all products

    @GetMapping("/getProducts")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getAllProducts(name, categoryId, price, status, page, size);

        return ResponseEntity.ok(new ApiResponse<>(true, "Products retrieved successfully", products));
    }

    // update product
    @PutMapping(path ="/updateProduct/{productId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) String description,
                                           @RequestParam(required = false) BigDecimal price,
                                           @RequestParam(required = false) BigDecimal vatRate,
                                           @RequestParam(required = false) String currencyCode,
                                           @RequestParam(required = false) Integer stock,
                                           @RequestParam(required = false) ProductStatus status,
                                           @RequestParam(value = "image",required = false) MultipartFile image) throws IOException {



        Product updatedProduct = productService.updateProduct(name,description,categoryId, price, vatRate,currencyCode,stock, status, image, productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product updated successfully", updatedProduct));
    }

    // delete product
    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable long productId) {
        productService.softDeleteProduct(productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product deleted successfully", null));
    }

    // Add Stock
    @PutMapping("/addStock/{productCode}")
    public ResponseEntity<?> addStock(@PathVariable String productCode, @RequestParam int stock) {
        Product product = productService.addStock(productCode, stock);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stock added successfully", product));
    }

    // Remove Stock
    @PutMapping("/removeStock/{productCode}")
    public ResponseEntity<?> removeStock(@PathVariable String productCode, @RequestParam int stock) {
        Product product = productService.removeStock(productCode, stock);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stock removed successfully", product));
    }





}

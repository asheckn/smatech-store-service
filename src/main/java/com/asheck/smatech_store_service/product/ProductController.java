package com.asheck.smatech_store_service.product;


import com.asheck.smatech_store_service.helper.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    // create a new product
    @PostMapping(path = "/create-product", consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(@RequestParam("image") MultipartFile image, @RequestBody ProductDto productDto) throws IOException, IOException {
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




}

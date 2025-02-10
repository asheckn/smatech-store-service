package com.asheck.smatech_store_service.category;

import com.asheck.smatech_store_service.helper.ApiResponse;
import com.asheck.smatech_store_service.store.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/category")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;

    @GetMapping("/getCategories")
    public ResponseEntity<?> getCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>(true, "Categories retrieved successfully", categories));
    }

    @GetMapping("/getCategoryById/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable long categoryId ) {
        Category category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category retrieved successfully", category));
    }

    @PostMapping(path = "/add-category", consumes = "multipart/form-data")
    public ResponseEntity<?> addCategory(@RequestParam("image") MultipartFile image, @RequestParam String name) throws IOException {
        Category createdCategory = categoryService.createCategory(name, image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Category created successfully", createdCategory));
    }

    @PutMapping(path = "/update-category", consumes = "multipart/form-data")
    public ResponseEntity<?> updateStore(@RequestParam("image") MultipartFile image, @RequestParam String name, @RequestParam long storeId) throws IOException {
        Category updateCategory = categoryService.updateCategory(name, image, storeId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Category updated successfully", updateCategory));
    }

    @GetMapping(path ="downloadImage/{filePath}")
    public ResponseEntity<?> downloadImages(@PathVariable("filePath") String  filePath) throws IOException {
        return ResponseEntity.ok(categoryService.downloadImage(filePath));
    }






}

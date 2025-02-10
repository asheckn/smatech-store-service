package com.asheck.smatech_store_service.category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Value("${folder-path}")
    private  String FOLDER_PATH;

    //Get all categories
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No categories found");
        }
        return categories;
    }

    //Get Category by Id
    public Category getCategoryById(long id){
        return categoryRepository.findById(id)
                .orElseThrow(
                        () ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "No categories found")
                );
    }


    //CreateCategory
    public Category createCategory(String name, MultipartFile image) throws IOException {

    //Create category

        Category category  = new Category();

        String imagePath = image.getOriginalFilename().trim().replace(' ', '_');

        category.setCategoryCode(UUID.randomUUID());
        category.setName(name);
        category.setDeleted(false);
        category.setStatus(true);
        category.setImage(imagePath);

        categoryRepository.save(category);

        Path uploadPath = Path.of(FOLDER_PATH);
        Path filepath = uploadPath.resolve(imagePath);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        Files.copy(image.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);


        return category;


    }

    @Transactional
    public Category updateCategory(String name, MultipartFile image, long categoryId) throws IOException {
        // Find existing category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with ID " + categoryId + " not found"));

        // Update category fields
        if (name != null) {
            category.setName(name);
        }
        if (image != null) {
            String imagePath = image.getOriginalFilename().trim().replace(' ', '_');
            category.setImage(imagePath);
            Path uploadPath = Path.of(FOLDER_PATH);
            Path filepath = uploadPath.resolve(imagePath);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            Files.copy(image.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
        }


        // Save and return updated category
        return categoryRepository.save(category);
    }

    public byte[] downloadImage(String filePath) throws IOException {
        Path imagePath = Path.of(FOLDER_PATH,filePath);
        if(Files.exists(imagePath)){
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return imageBytes;
        }else {
            return null; // handle missing images
        }
    }
}

package com.asheck.smatech_store_service.product;


import com.asheck.smatech_store_service.category.Category;
import com.asheck.smatech_store_service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {


    private final CategoryService categoryService;

    private final ProductRepository productRepository;

    @Value("${folder-path}")
    private String FOLDER_PATH;

    //Create Product
    public Product createProduct(ProductDto productDto, MultipartFile image) throws IOException {
        Product product = new Product();

        Category category = categoryService.getCategoryById(productDto.categoryId());



        product.setName(productDto.name());
        product.setCategory(category);

        product.setDescription(productDto.description());
        product.setProductCode(UUID.randomUUID());

        // Verify if the price is greater than 0
        if(productDto.price().compareTo(BigDecimal.valueOf(0.0)) < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Price cannot be less than 0");
        }
        product.setPrice(productDto.price());

        // Verify if the vat rate is greater than 0
        if(productDto.vatRate().compareTo(BigDecimal.valueOf(0.0)) < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Vat rate cannot be less than 0");
        }
        product.setVatRate(productDto.vatRate());
        product.setCurrencyCode(productDto.currencyCode());
        String imagePath = "product_"+product.getProductCode()+image.getOriginalFilename().trim().replace(' ', '_');
        product.setImage(imagePath);
        // Verify if the stock is greater than 0
        if(productDto.stock() < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Stock cannot be less than 0");
        }
        product.setStock(productDto.stock());
        product.setStatus(productDto.status());

        productRepository.save(product);

        Path uploadPath = Path.of(FOLDER_PATH);
        Path filepath = uploadPath.resolve(imagePath);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        Files.copy(image.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

        return product;

    }

    public Product getProductById(long productId){
        return productRepository.findById(productId)
                .orElseThrow(
                        () ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "No product found")
                );
    }

    public Product getProductByCode(String productCode){
        return productRepository.findProductByProductCode(UUID.fromString(productCode))
                .orElseThrow(
                        () ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "No product found")
                );
    }

    public Product addStock(String productCode, int stock){
        Product product = productRepository.findProductByProductCode(UUID.fromString(productCode))
                .orElseThrow(
                        () ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "No product found")
                );
        product.setStock(product.getStock() + stock);
        return productRepository.save(product);
    }

    @Transactional
    public Product removeStock(String productCode, int stock){
        Product product = productRepository.findProductByProductCode(UUID.fromString(productCode))
                .orElseThrow(
                        () ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "No product found")
                );
        if(product.getStock() < stock){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, product.getName()+" stock is less than the requested amount");
        }
        product.setStock(product.getStock() - stock);
        return productRepository.save(product);
    }

    public Page<Product> getAllProducts(String name, Long categoryId, BigDecimal price, ProductStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Product> products = productRepository.findByFilters(name, categoryId, price, status, pageable);

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found");
        }

        return products;
    }

    @Transactional
    public void softDeleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setDeleted(true);
        productRepository.save(product); // Update instead of delete
    }

    @Transactional
    public Product updateProduct(String name, String description,Long categoryId, BigDecimal price, BigDecimal vatRate, String currencyCode, Integer stock, ProductStatus status, MultipartFile image, long productId) throws IOException {
        // Find existing product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + productId + " not found"));




        // Update product fields
        if (name!= null) {
            product.setName(name);
        }
        if (description != null) {
            product.setDescription(description);
        }
        if (price != null) {
            // Verify if the price is greater than 0
            if(price.compareTo(BigDecimal.valueOf(0.0)) < 0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Price cannot be less than 0");
            }
            product.setPrice(price);
        }
        if (vatRate != null) {
            // Verify if the vat rate is greater than 0
            if(vatRate.compareTo(BigDecimal.valueOf(0.0)) < 0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Vat rate cannot be less than 0");
            }
            product.setVatRate(vatRate);
        }
        if (currencyCode != null) {
            product.setCurrencyCode(currencyCode);
        }
        if (stock != null) {
            // Verify if the stock is greater than 0
            if(stock < 0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Stock cannot be less than 0");
            }
            product.setStock(stock);
        }
        if (status != null) {
            product.setStatus(status);
        }
        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            product.setCategory(category);
        }

        if (image != null) {
            String imagePath = "product_"+product.getProductCode()+image.getOriginalFilename().trim().replace(' ', '_');
            product.setImage(imagePath);
            Path uploadPath = Path.of(FOLDER_PATH);
            Path filepath = uploadPath.resolve(imagePath);
            Files.copy(image.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
        }

        return product;
    }



}

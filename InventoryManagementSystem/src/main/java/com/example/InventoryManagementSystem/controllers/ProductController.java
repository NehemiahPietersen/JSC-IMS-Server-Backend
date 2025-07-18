package com.example.InventoryManagementSystem.controllers;

import com.example.InventoryManagementSystem.dtos.ProductDTO;
import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.SupplierDTO;
import com.example.InventoryManagementSystem.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> saveProduct(
            @RequestParam("imageFile")MultipartFile imageFile,
            @RequestParam("name")String name,
            @RequestParam("sku")String sku,
            @RequestParam("price")BigDecimal price,
            @RequestParam("stockQuantity")Integer stockQuantity,
            @RequestParam("categoryId")Long categoryId,
            @RequestParam(value = "description", required = false)String description
            ) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setName(name);
        productDTO.setSku(sku);
        productDTO.setPrice(price);
        productDTO.setStockQuantity(stockQuantity);
        productDTO.setCategoryId(categoryId);
        productDTO.setDescription(description);

        return ResponseEntity.ok(productService.saveProduct(productDTO, imageFile));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateProduct(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "imageFile", required = false)MultipartFile imageFile,
            @RequestParam(value = "name", required = false)String name,
            @RequestParam(value = "sku", required = false)String sku,
            @RequestParam(value = "price", required = false)BigDecimal price,
            @RequestParam(value = "stockQuantity", required = false)Integer stockQuantity,
            @RequestParam(value = "categoryId", required = false)Long categoryId,
            @RequestParam(value = "description", required = false)String description
    ) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setProductId(productId);
        productDTO.setName(name);
        productDTO.setSku(sku);
        productDTO.setPrice(price);
        productDTO.setStockQuantity(stockQuantity);
        productDTO.setCategoryId(categoryId);
        productDTO.setDescription(description);

        return ResponseEntity.ok(productService.updateProduct(productId, productDTO, imageFile));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteProduct(@PathVariable @Valid Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchProduct(@RequestParam String input) {
        return ResponseEntity.ok(productService.searchProduct(input));
    }

}

package com.example.InventoryManagementSystem.services;

import com.example.InventoryManagementSystem.dtos.ProductDTO;
import com.example.InventoryManagementSystem.dtos.Response;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    Response saveProduct(ProductDTO productDTO, MultipartFile imageFile);

    Response getAllProducts();

    Response getProductById(Long id);

    Response updateProduct(Long id, ProductDTO productDTO, MultipartFile imageFile);

    Response deleteProduct(Long id);

    Response searchProduct(String name);

}

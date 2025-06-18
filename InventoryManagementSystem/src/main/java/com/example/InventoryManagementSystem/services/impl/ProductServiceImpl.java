package com.example.InventoryManagementSystem.services.impl;

import com.example.InventoryManagementSystem.dtos.ProductDTO;
import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.exceptions.NotFoundException;
import com.example.InventoryManagementSystem.models.Category;
import com.example.InventoryManagementSystem.models.Product;
import com.example.InventoryManagementSystem.repositories.CategoryRepository;
import com.example.InventoryManagementSystem.repositories.ProductRepository;
import com.example.InventoryManagementSystem.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/product-images/";

    @Override
    public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()-> new NotFoundException("Category is not found"));

        //map DTO to product entity
        Product productToSave = Product.builder()
                .name(productDTO.getName())
                .sku(productDTO.getSku())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .expiryDate(productDTO.getExpiryDate())
                .stockQuantity(productDTO.getStockQuantity())
                .category(category)
                .build();

        //set image
        if(imageFile != null && !imageFile.isEmpty()) {
            log.info("Image file Exists");

            String imagePath = saveImage(imageFile); //saves image
            productToSave.setImageUrl(imagePath); //add image to product entity
        }

        //save product entity
        productRepository.save(productToSave);

        return Response.builder()
                .status(200)
                .message("Product successfully CREATED")
                .build();
    }

    private String saveImage(MultipartFile imageFile) {
        //validate image and check  if it is greater than 1GB
        if(!imageFile.getContentType().startsWith("image/") || imageFile.getSize() > 1024 * 1024 *1024) {
            throw new IllegalArgumentException("Only Image files under 1GB allowed");
        }

        //create directory if not exist
        File directory = new File(IMAGE_DIRECTORY);
        if(!directory.exists()) {
            directory.mkdir();
            log.info("Directory has been CREATED");
        }

        //generate unique file name for image
        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        //get absolute path of image
        String imagePath = IMAGE_DIRECTORY + uniqueFileName;

        try{
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile); //writing image to destination folder
        } catch (Exception e) {
            throw new IllegalArgumentException("Error Saving Image: " + e.getMessage());
        }

        return imagePath;
    }

    @Override
    public Response getAllProducts() {
        List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<ProductDTO> productDTOList = modelMapper.map(productList, new TypeToken<List<ProductDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .productList(productDTOList)
                .build();
    }

    @Override
    public Response getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));

        return Response.builder()
                .status(200)
                .message("success")
                .product(modelMapper.map(product, ProductDTO.class))
                .build();
    }

    @Override
    public Response updateProduct(Long id, ProductDTO productDTO, MultipartFile imageFile) {
        //checks if product exists
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product does not exist"));

        //checks if image file has been changed
        if(imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            existingProduct.setImageUrl(imagePath);
        }

        //checks if category has been changed
        if(productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category does not exist"));

            existingProduct.setCategory(category);
        }

        //check if product fields has been changed
        if(productDTO.getName() != null && !productDTO.getName().isBlank()) {
            existingProduct.setName(productDTO.getName());
        }
        if(productDTO.getSku() != null && !productDTO.getSku().isBlank()) {
            existingProduct.setSku(productDTO.getSku());
        }
        if(productDTO.getDescription() != null && !productDTO.getDescription().isBlank()) {
            existingProduct.setDescription(productDTO.getDescription());
        }
        if(productDTO.getPrice() != null && productDTO.getPrice().compareTo(BigDecimal.ZERO) >= 0) {
            existingProduct.setPrice(productDTO.getPrice());
        }
        if(productDTO.getStockQuantity() != null && productDTO.getStockQuantity() >= 0)  {
            existingProduct.setStockQuantity(productDTO.getStockQuantity());
        }

        productRepository.save(existingProduct);

        return Response.builder()
                .status(200)
                .message("Product successfully UPDATED")
                .build();
    }

    @Override
    public Response deleteProduct(Long id) {
        productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        productRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Product successfully DELETED")
                .build();
    }

    @Override
    public Response searchProduct(String input) {
        List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(input, input);

        if(products.isEmpty()){
            throw new NotFoundException("Product not found");
        }

        List<ProductDTO> productDTOList = modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .productList(productDTOList)
                .build();
    }
}

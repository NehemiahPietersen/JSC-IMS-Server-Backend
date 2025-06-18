package com.example.InventoryManagementSystem.services.impl;

import com.example.InventoryManagementSystem.dtos.CategoryDTO;
import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.exceptions.NotFoundException;
import com.example.InventoryManagementSystem.models.Category;
import com.example.InventoryManagementSystem.repositories.CategoryRepository;
import com.example.InventoryManagementSystem.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    //TODO: FIX THE CATEGORY SERVICE - NOT WORKING

    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    @Override
    public Response createCategory(CategoryDTO categoryDTO) {
        log.info("CATEGORY IS: {}", categoryDTO);

        Category categoryToSave = modelMapper.map(categoryDTO, Category.class);

        categoryRepository.save(categoryToSave);

        return Response.builder()
                .status(200)
                .message("category saved successfully")
                .build();
    }

    @Override
    public Response getAllCategories() {
        List<Category> categoryList = categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id")); //sort by id
        categoryList.forEach(category -> category.setProducts(null)); //only the categories no products need to be shown

        List<CategoryDTO> categoryDTOList = modelMapper.map(categoryList,
                new TypeToken<List<CategoryDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .categoryList(categoryDTOList)
                .build();
    }

    @Override
    public Response getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not Found"));

        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .category(categoryDTO)
                .build();
    }

    @Override
    public Response updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));

        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);

        return Response.builder()
                .status(200)
                .message("Category successfully UPDATED")
                .build();
    }

    @Override
    public Response deleteCategory(Long id) {
        categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Category successfully DELETED")
                .build();
    }
}

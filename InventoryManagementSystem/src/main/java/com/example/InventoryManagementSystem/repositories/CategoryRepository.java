package com.example.InventoryManagementSystem.repositories;

import com.example.InventoryManagementSystem.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCategoryName(String category);
}

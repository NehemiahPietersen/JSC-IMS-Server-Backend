package com.example.InventoryManagementSystem.repositories;

import com.example.InventoryManagementSystem.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByName(String category);
}

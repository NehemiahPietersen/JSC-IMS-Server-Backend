package com.example.InventoryManagementSystem.repositories;

import com.example.InventoryManagementSystem.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}

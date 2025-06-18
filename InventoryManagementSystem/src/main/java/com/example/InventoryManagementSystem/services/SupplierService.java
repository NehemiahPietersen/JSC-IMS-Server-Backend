package com.example.InventoryManagementSystem.services;

import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.SupplierDTO;

public interface SupplierService {

    Response createSupplier(SupplierDTO supplierDTO);

    Response getAllSuppliers();

    Response getSupplierById(Long id);

    Response updateSupplier(Long id, SupplierDTO supplierDTO);

    Response deleteSupplier(Long id);
}

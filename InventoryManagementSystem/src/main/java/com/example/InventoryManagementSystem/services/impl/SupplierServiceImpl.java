package com.example.InventoryManagementSystem.services.impl;

import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.SupplierDTO;
import com.example.InventoryManagementSystem.exceptions.NotFoundException;
import com.example.InventoryManagementSystem.models.Supplier;
import com.example.InventoryManagementSystem.repositories.SupplierRepository;
import com.example.InventoryManagementSystem.services.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response createSupplier(SupplierDTO supplierDTO) {
        Supplier supplierToSave = modelMapper.map(supplierDTO, Supplier.class);

        supplierRepository.save(supplierToSave);

        return Response.builder()
                .status(200)
                .message("supplier saved successfully")
                .build();
    }

    @Override
    public Response getAllSuppliers() {
        List<Supplier> supplierList = supplierRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<SupplierDTO> supplierDTOList = modelMapper.map(supplierList, new TypeToken<List<SupplierDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .supplierList(supplierDTOList)
                .build();
    }

    @Override
    public Response getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new NotFoundException("Supplier not Found"));

        SupplierDTO supplierDTO = modelMapper.map(supplier, SupplierDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .supplier(supplierDTO)
                .build();
    }

    @Override
    public Response updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier existingSupplier = supplierRepository.findById(id).orElseThrow(() -> new NotFoundException("Supplier not found"));

        if(supplierDTO.getName() != null ) existingSupplier.setName(supplierDTO.getName());
        if(supplierDTO.getContactNumber() != null ) existingSupplier.setContactNumber(supplierDTO.getContactNumber());
        if(supplierDTO.getEmail() != null ) existingSupplier.setEmail(supplierDTO.getEmail());
        if(supplierDTO.getAddress() != null ) existingSupplier.setAddress(supplierDTO.getAddress());

        supplierRepository.save(existingSupplier);

        return Response.builder()
                .status(200)
                .message("Supplier successfully UPDATED")
                .build();
    }

    @Override
    public Response deleteSupplier(Long id) {
        supplierRepository.findById(id).orElseThrow(() -> new NotFoundException("Supplier not found"));
        supplierRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Supplier successfully DELETED")
                .build();
    }
}

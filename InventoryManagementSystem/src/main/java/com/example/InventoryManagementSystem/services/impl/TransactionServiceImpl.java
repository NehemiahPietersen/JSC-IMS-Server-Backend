package com.example.InventoryManagementSystem.services.impl;

import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.TransactionRequest;
import com.example.InventoryManagementSystem.enums.TransactionStatus;
import com.example.InventoryManagementSystem.repositories.ProductRepository;
import com.example.InventoryManagementSystem.repositories.SupplierRepository;
import com.example.InventoryManagementSystem.repositories.TransactionRepository;
import com.example.InventoryManagementSystem.services.TransactionService;
import com.example.InventoryManagementSystem.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    //TODO: https://www.youtube.com/watch?v=2l8PrJhJgw0 finish watching

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response purchase(TransactionRequest transactionRequest) {
        return null;
    }

    @Override
    public Response sell(TransactionRequest transactionRequest) {
        return null;
    }

    @Override
    public Response returnToSupplier(TransactionRequest transactionRequest) {
        return null;
    }

    @Override
    public Response getAllTransactions(int page, int size, String searchValue) {
        return null;
    }

    @Override
    public Response getByTransactionId(Long id) {
        return null;
    }

    @Override
    public Response getTransactionByMonthAndYear(int month, int year) {
        return null;
    }

    @Override
    public Response updateTransactionStatus(Long transactionId, TransactionStatus transactionStatus) {
        return null;
    }
}

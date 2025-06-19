package com.example.InventoryManagementSystem.services.impl;

import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.TransactionDTO;
import com.example.InventoryManagementSystem.dtos.TransactionRequest;
import com.example.InventoryManagementSystem.enums.TransactionStatus;
import com.example.InventoryManagementSystem.enums.TransactionType;
import com.example.InventoryManagementSystem.exceptions.NameValueRequiredException;
import com.example.InventoryManagementSystem.exceptions.NotFoundException;
import com.example.InventoryManagementSystem.models.Product;
import com.example.InventoryManagementSystem.models.Supplier;
import com.example.InventoryManagementSystem.models.Transaction;
import com.example.InventoryManagementSystem.models.User;
import com.example.InventoryManagementSystem.repositories.ProductRepository;
import com.example.InventoryManagementSystem.repositories.SupplierRepository;
import com.example.InventoryManagementSystem.repositories.TransactionRepository;
import com.example.InventoryManagementSystem.services.TransactionService;
import com.example.InventoryManagementSystem.services.UserService;
import com.example.InventoryManagementSystem.specifications.TransactionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response purchase(TransactionRequest transactionRequest) {
        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        //validations
        if(supplierId == null) throw new NameValueRequiredException("Supplier ID is required");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        //when operations are performed current user is logged makes requests
        User user = userService.getCurrentLoggedInUser();

        //update stock quantity and save
        product.setStockQuantity(product.getStockQuantity() + quantity); //US PURCHASING PRODUCTS
        productRepository.save(product);

        //create transaction
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.PURCHASE)
                .transactionStatus(TransactionStatus.COMPLETED)
                .products(product)
                .user(user)
                .supplier(supplier)
                .totalProducts(quantity)
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .build();

        //save the transaction
        transactionRepository.save(transaction);

        //RESPONSE
        return Response.builder()
                .status(200)
                .message("PURCHASE made Successfully")
                .build();
    }

    @Override
    public Response sell(TransactionRequest transactionRequest) {

        Long productId = transactionRequest.getProductId();
        Integer quantity = transactionRequest.getQuantity();

        //validation of product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product does not exist"));

        //get current logged in user
        User user = userService.getCurrentLoggedInUser();

        //update and re-save
        product.setStockQuantity(product.getStockQuantity() - quantity); //US SELLING OUR PRODUCTS
        productRepository.save(product);

        //create transaction
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.SALE)
                .transactionStatus(TransactionStatus.COMPLETED)
                .products(product)
                .user(user)
                .totalProducts(quantity)
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .build();

        //save the transaction
        transactionRepository.save(transaction);

        //RESPONSE
        return Response.builder()
                .status(200)
                .message("SALE made Successfully")
                .build();
    }

    @Override
    public Response returnToSupplier(TransactionRequest transactionRequest) {

        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        //validations
        if(supplierId == null) throw new NameValueRequiredException("Supplier ID is required");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        //when operations are performed current user is logged makes requests
        User user = userService.getCurrentLoggedInUser();

        //update stock quantity and re-save
        product.setStockQuantity(product.getStockQuantity() - quantity); //US SENDING BACK STOCK - fault, problems
        productRepository.save(product);

        //create transaction
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.RETURN_TO_SUPPLIER)
                .transactionStatus(TransactionStatus.PROCESSING)
                .products(product)
                .user(user)
                .supplier(supplier)
                .totalProducts(quantity)
                .totalPrice(BigDecimal.ZERO)
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .build();

        //save the transaction
        transactionRepository.save(transaction);

        return Response.builder()
                .status(200)
                .message("RETURN in Progress")
                .build();
    }

    @Override
    public Response getAllTransactions(int page, int size, String searchValue) {
        //page is the page number we want ot see
        //size is the number of items we want on the page
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        //User uses the Transaction Filter specification
        Specification<Transaction> spec = TransactionFilter.byFilter(searchValue);
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        List<TransactionDTO> transactionDTOList = modelMapper.map(transactionPage.getContent(),
                new TypeToken<List<TransactionDTO>>() {}.getType());

        //
        transactionDTOList.forEach(transactionDTO -> {
            transactionDTO.setUser(null);
            transactionDTO.setProducts(null);
            transactionDTO.setSupplier(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .transactionList(transactionDTOList)
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .build();
    }

    @Override
    public Response getByTransactionId(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction does not exist"));

        TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);

        transactionDTO.setUser(null);

        return Response.builder()
                .status(200)
                .message("success")
                .transaction(transactionDTO)
                .build();
    }

    @Override
    public Response getTransactionByMonthAndYear(int month, int year) {
        List<Transaction> transactions = transactionRepository.findAll(TransactionFilter.byMonthAndYear(month, year));

        List<TransactionDTO> transactionDTOS = modelMapper.map(transactions,
                new TypeToken<List<TransactionDTO>>() {}.getType());

        transactionDTOS.forEach(transactionDTO -> {
            transactionDTO.setUser(null);
            transactionDTO.setProducts(null);
            transactionDTO.setSupplier(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .transactionList(transactionDTOS)
                .build();
    }

    @Override
    public Response updateTransactionStatus(Long transactionId, TransactionStatus transactionStatus) {
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction does not exist"));

        existingTransaction.setTransactionStatus(transactionStatus);
        existingTransaction.setUpdatedAt(LocalDateTime.now());

        //save the transaction
        transactionRepository.save(existingTransaction);

        return Response.builder()
                .status(200)
                .message("Transaction Status has been successfully UPDATED")
                .build();
    }
}

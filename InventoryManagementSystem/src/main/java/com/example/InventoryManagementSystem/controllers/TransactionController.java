package com.example.InventoryManagementSystem.controllers;

import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.TransactionRequest;
import com.example.InventoryManagementSystem.enums.TransactionStatus;
import com.example.InventoryManagementSystem.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/purchase")
    public ResponseEntity<Response> purchaseInventory(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.purchase(transactionRequest));
    }

    @PostMapping("/sell")
    public ResponseEntity<Response> sellInventory(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.sell(transactionRequest));
    }

    @PostMapping("/return")
    public ResponseEntity<Response> returnInventory(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.returnToSupplier(transactionRequest));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String searchValue
    ) {
        return ResponseEntity.ok(transactionService.getAllTransactions(page, size, searchValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getByTransactionId(id));
    }

    @GetMapping("/by-month-year")
    public ResponseEntity<Response> getTransactionByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(transactionService.getTransactionByMonthAndYear(month, year));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Response> updateTransactionStatus(
            @PathVariable Long transactionId,
            @RequestBody TransactionStatus status
            ) {
        return ResponseEntity.ok(transactionService.updateTransactionStatus(transactionId, status));
    }

}

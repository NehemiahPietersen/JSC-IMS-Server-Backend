package com.example.InventoryManagementSystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //GENERIC
    private int status;
    private String message;

    //For Login
    private String token;
    private String role;
    private String expirationTime;

    //For pagination
    private Integer totalPages;
    private Long totalElements;

    //Data output optionals
    private UserDTO user;
    private List<UserDTO> userList;

    private SupplierDTO supplier;
    private List<SupplierDTO> supplierList;

    private CategoryDTO category;
    private List<CategoryDTO> categoryList;

    private ProductDTO product;
    private List<ProductDTO> productList;

    private TransactionDTO transaction;
    private List<TransactionDTO> transactionList;

    private final LocalDateTime timestamp = LocalDateTime.now();
}

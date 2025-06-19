package com.example.InventoryManagementSystem.dtos;

import com.example.InventoryManagementSystem.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest {

    //for every transaction we need part of the following

    @Positive(message = "product id is required")
    private Long productId;

    @Positive(message = "quantity is required")
    private Integer quantity;

    private Long supplierId;
    private String description;
    private String note;

}

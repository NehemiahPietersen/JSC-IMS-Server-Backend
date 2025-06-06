package com.example.InventoryManagementSystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "suppliers")
@Data
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Supplier name is required")
    private String name;

    @NotBlank(message = "Supplier email address is required")
    @Column(name = "email_address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Column(name = "supplier_number")
    private String contactNumber;

    @NotBlank(message = "Supplier address required")
    private String address;

    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();
}

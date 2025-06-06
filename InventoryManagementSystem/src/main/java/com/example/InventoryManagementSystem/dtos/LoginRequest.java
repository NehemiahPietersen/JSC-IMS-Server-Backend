package com.example.InventoryManagementSystem.dtos;

import com.example.InventoryManagementSystem.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Name is required to login")
    private String name;

    @NotBlank(message = "Email is required to login")
    private String email;

    @NotBlank(message = "Password is required to login")
    private String password;

    private UserRole role;
}

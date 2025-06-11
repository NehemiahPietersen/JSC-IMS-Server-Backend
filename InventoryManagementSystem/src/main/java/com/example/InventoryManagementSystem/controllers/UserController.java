package com.example.InventoryManagementSystem.controllers;

import com.example.InventoryManagementSystem.dtos.LoginRequest;
import com.example.InventoryManagementSystem.dtos.RegisterRequest;
import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.UserDTO;
import com.example.InventoryManagementSystem.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')") //only ADMIN role can access
    public ResponseEntity<Response> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    @PutMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") //only ADMIN role can access
    public ResponseEntity<Response> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @PutMapping("/transactions/{userId}")
    public ResponseEntity<Response> getUserTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserTransactions(userId));
    }
}

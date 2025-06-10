package com.example.InventoryManagementSystem.services;

import com.example.InventoryManagementSystem.dtos.LoginRequest;
import com.example.InventoryManagementSystem.dtos.RegisterRequest;
import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.UserDTO;
import com.example.InventoryManagementSystem.models.User;

public interface UserService {

    Response registerUser(RegisterRequest registerRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    User getCurrentLoggedInUser();
    Response getUserById(Long id);
    Response updateUser(Long id, UserDTO userDTO);
    Response deleteUser(Long id);
    Response getUserTransactions(Long id);
}

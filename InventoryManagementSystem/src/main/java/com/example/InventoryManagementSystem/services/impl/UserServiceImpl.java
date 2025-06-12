package com.example.InventoryManagementSystem.services.impl;

import com.example.InventoryManagementSystem.dtos.LoginRequest;
import com.example.InventoryManagementSystem.dtos.RegisterRequest;
import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.UserDTO;
import com.example.InventoryManagementSystem.enums.UserRole;
import com.example.InventoryManagementSystem.exceptions.InvalidCredentialsException;
import com.example.InventoryManagementSystem.exceptions.NotFoundException;
import com.example.InventoryManagementSystem.models.User;
import com.example.InventoryManagementSystem.repositories.UserRepository;
import com.example.InventoryManagementSystem.security.JwtUtils;
import com.example.InventoryManagementSystem.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;


    @Override
    public Response registerUser(RegisterRequest registerRequest) {
        UserRole uRole = UserRole.MANAGER;

        if(registerRequest.getUserRole() != null) {
            uRole = registerRequest.getUserRole();
        }

        User userToSave = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .contactNumber(registerRequest.getContactNumber())
                .role(uRole) //todo: Fix the user role not working as it should from Requests being sent
                .build();

        userRepository.save(userToSave);

        return Response.builder()
                .status(200)
                .message("User was successfully registered")
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Email Not Found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Password does not Match");
        }

        String token = jwtUtils.generateToken(user.getEmail()); //create token

        return Response.builder()
                .status(200)
                .message("User logged in")
                .role(String.valueOf(user.getRole()))
                .token(token)
                .expirationTime("6 months")
                .build();
    }

    @Override
    public Response getAllUsers() {
        List<User> userList = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        userList.forEach(user -> user.setTransactions(null)); //don't include transactions

        List<UserDTO> userDTO = modelMapper.map(userList, new TypeToken<List<UserDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .userList(userDTO)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not Found"));
        user.setTransactions(null); //don't want transactions with the user here
        return user;
    }

    @Override
    public Response getUserById(Long id) {
        User existingUser = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));

        UserDTO userDTO = modelMapper.map(existingUser, UserDTO.class);
        userDTO.setTransactions(null); //no transactions on the ID

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDTO)
                .build();
    }

    @Override
    public Response updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));

        if(userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if(userDTO.getContactNumber() != null) existingUser.setContactNumber(userDTO.getContactNumber());
        if(userDTO.getName() != null) existingUser.setName(userDTO.getName());
        if(userDTO.getRole() != null) existingUser.setRole(userDTO.getRole());

        if(userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(existingUser);

        return Response.builder()
                .status(200)
                .message("User successfully updated")
                .build();
    }

    @Override
    public Response deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("User successfully deleted")
                .build();
    }

    @Override
    public Response getUserTransactions(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.getTransactions().forEach(transactionDTO -> { //don't want to return user details
            transactionDTO.setUser(null);
            transactionDTO.setSupplier(null);
        });

        return Response.builder()
                .status(200)
                .message("Transactions retrieved")
                .user(userDTO)
                .build();
    }
}

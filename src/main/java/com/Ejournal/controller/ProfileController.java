package com.Ejournal.controller;

import com.Ejournal.DTO.UserDTO;
import com.Ejournal.entity.User;
import com.Ejournal.entity.Role;
import com.Ejournal.exception.AppError;
import com.Ejournal.repo.UserRepository;
import com.Ejournal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final UserService userService;


    @Value("${upload.img}")
    protected String uploadImg;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(){
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUser)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "name", user.getName(),
                "phone", user.getPhone(),
                "enabled", user.isEnabled(),
                "roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
        ));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editProfile(@RequestBody UserDTO userDTO){
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUser)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (userDTO.getName() != null && !userDTO.getName().isEmpty()) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
            user.setPhone(userDTO.getPhone());
        }
        if (userService.findByEmail(userDTO.getEmail()).isPresent()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с таким email уже существует"), HttpStatus.BAD_REQUEST);
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            user.setEmail(userDTO.getEmail());
        }

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}

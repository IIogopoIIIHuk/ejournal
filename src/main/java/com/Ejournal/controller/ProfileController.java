package com.Ejournal.controller;

import com.Ejournal.DTO.ChangePasswordRequest;
import com.Ejournal.entity.Role;
import com.Ejournal.entity.Subject;
import com.Ejournal.entity.User;
import com.Ejournal.repo.SubjectRepository;
import com.Ejournal.repo.UserRepository;
import com.Ejournal.service.UserService;
import com.Ejournal.exception.AppError;
import com.Ejournal.DTO.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;



    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        Map<String, Object> response = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "name", user.getName(),
                "phone", user.getPhone(),
                "enabled", user.isEnabled(),
                "roles", roles
        );

        // Добавим доп. поля в зависимости от роли
        if (roles.contains("ROLE_USER") && user.getGroup() != null) {
            response = new java.util.HashMap<>(response);
            ((Map<String, Object>) response).put("groupName", user.getGroup().getName());
        }

        if (roles.contains("ROLE_TEACHER")) {
            List<String> subjects = subjectRepository.findAllByOwner(user).stream()
                    .map(Subject::getName)
                    .distinct()
                    .toList();
            response = new java.util.HashMap<>(response);
            ((Map<String, Object>) response).put("subjects", subjects);
        }


        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editProfile(@RequestBody UserDTO userDTO) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
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
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с таким email уже существует"), HttpStatus.BAD_REQUEST);
            }
            user.setEmail(userDTO.getEmail());
        }

        userRepository.save(user);
        return ResponseEntity.ok("Профиль обновлён");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем старый пароль
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Неверный старый пароль");
        }

        // Устанавливаем новый пароль
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Пароль успешно изменён");
    }
}

package com.Ejournal.controller;

import com.Ejournal.DTO.UserDTO;
import com.Ejournal.entity.User;
import com.Ejournal.entity.Role;
import com.Ejournal.entity.Subject;
import com.Ejournal.repo.RoleRepository;
import com.Ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserDTO> userDTOs = users.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setName(user.getName());
            userDTO.setPhone(user.getPhone());
            userDTO.setEnabled(user.isEnabled());

            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
            userDTO.setRoles(roles);

            // Вот это добавляем: если есть предметы, маппим их
            if (user.getSubjects() != null && !user.getSubjects().isEmpty()) {
                List<String> subjects = user.getSubjects().stream()
                        .map(Subject::getName)
                        .collect(Collectors.toList());
                userDTO.setSubjects(subjects);
            } else {
                userDTO.setSubjects(new ArrayList<>()); // Чтобы не было null
            }

            return userDTO;
        }).toList();
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/editRole/{id}")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestParam String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        user.setRoles(new ArrayList<>(List.of(role)));

        userRepository.save(user);

        return ResponseEntity.ok(toUserDTO(user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        userRepository.delete(user);

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.isEnabled(),
                roles,
                user.getSubjects() != null ? user.getSubjects().stream().map(Subject::getName).toList() : new ArrayList<>()
        );

        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/enable/{id}")
    public ResponseEntity<?> enableUser(@PathVariable Long id, @RequestParam boolean enable) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(enable);
        userRepository.save(user);

        return ResponseEntity.ok(toUserDTO(user));
    }

    private UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPhone(user.getPhone());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        if (user.getSubjects() != null && !user.getSubjects().isEmpty()) {
            userDTO.setSubjects(user.getSubjects().stream()
                    .map(Subject::getName)
                    .collect(Collectors.toList()));
        } else {
            userDTO.setSubjects(new ArrayList<>());
        }

        return userDTO;
    }
}

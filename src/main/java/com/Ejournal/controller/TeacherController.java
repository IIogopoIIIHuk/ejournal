package com.Ejournal.controller;

import com.Ejournal.DTO.UserDTO;
import com.Ejournal.entity.Role;
import com.Ejournal.entity.User;
import com.Ejournal.repo.AbsenceRepository;
import com.Ejournal.repo.NoteRepository;
import com.Ejournal.repo.RoleRepository;
import com.Ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NoteRepository noteRepository;
    private final AbsenceRepository absenceRepository;


    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseThrow(() -> new RuntimeException("Роль 'ROLE_TEACHER' не найдена"));

        List<User> teachers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(teacherRole))
                .collect(Collectors.toList());

        List<UserDTO> teacherDTOs = teachers.stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(teacherDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchStudents(@RequestParam String name) {
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseThrow(() -> new RuntimeException("Роль 'ROLE_TEACHER' не найдена"));

        List<User> teachers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(teacherRole))
                .filter(user -> user.getName() != null && user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());

        List<UserDTO> teacherDTOs = teachers.stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(teacherDTOs);
    }


    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setEnabled(user.isEnabled());
        dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        return dto;
    }
}

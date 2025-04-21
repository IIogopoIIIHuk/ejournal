package com.Ejournal.controller;

import com.Ejournal.DTO.AbsenceDTO;
import com.Ejournal.DTO.NoteDTO;
import com.Ejournal.DTO.StudentDetailsDTO;
import com.Ejournal.DTO.UserDTO;
import com.Ejournal.entity.Role;
import com.Ejournal.entity.User;
import com.Ejournal.entity.Absence;
import com.Ejournal.entity.Subject;
import com.Ejournal.repo.AbsenceRepository;
import com.Ejournal.repo.NoteRepository;
import com.Ejournal.repo.RoleRepository;
import com.Ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NoteRepository noteRepository;
    private final AbsenceRepository absenceRepository;

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        Role studentRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль 'ROLE_USER' не найдена"));

        List<User> students = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(studentRole))
                .collect(Collectors.toList());

        List<UserDTO> studentDTOs = students.stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchStudents(@RequestParam String name) {
        Role studentRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль 'ROLE_USER' не найдена"));

        List<User> students = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(studentRole))
                .filter(user -> user.getName() != null && user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());

        List<UserDTO> studentDTOs = students.stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean isStudent = student.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_USER"));

        if (!isStudent) {
            return ResponseEntity.status(403).body("Пользователь не является студентом");
        }

        UserDTO studentDTO = mapToDTO(student);
        String attendance = calculateAttendanceStatus(student);
        List<NoteDTO> notes = noteRepository.findByOwner(student).stream()
                .map(note -> {
                    NoteDTO dto = new NoteDTO();
                    dto.setId(note.getId());
                    dto.setFile(note.getFile());
                    dto.setDateWith(note.getDateWith());
                    dto.setDateBy(note.getDateBy());
                    return dto;
                }).toList();

        List<AbsenceDTO> absences = absenceRepository.findByUser(student).stream()
                .map(abs -> {
                    AbsenceDTO dto = new AbsenceDTO();
                    dto.setId(abs.getId());
                    dto.setDate(abs.getDate());
                    dto.setCount(abs.getCount());
                    dto.setReason(abs.getReason());
                    dto.setSubjectName(abs.getSubject() != null ? abs.getSubject().getName() : ""); // safe
                    return dto;
                }).toList();


        Map<String, Object> response = new HashMap<>();
        response.put("student", studentDTO);
        response.put("attendanceStatus", attendance);
        response.put("notes", notes);
        response.put("absences", absences);

        return ResponseEntity.ok(response);
    }


    private String calculateAttendanceStatus(User student) {
        List<Absence> absences = absenceRepository.findByUser(student);
        int disrespectful = absences.stream()
                .filter(abs -> abs.getReason().name().equals("DISRESPECTFUL"))
                .mapToInt(Absence::getCount)
                .sum();

        if (disrespectful < 10) return "Хорошая посещаемость";
        if (disrespectful < 30) return "Посещаемость в пределах нормы";
        return "Плохая посещаемость";
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

package com.Ejournal.controller;

import com.Ejournal.DTO.AbsenceDTO;
import com.Ejournal.entity.User;
import com.Ejournal.repo.AbsenceRepository;
import com.Ejournal.repo.RoleRepository;
import com.Ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/absences")
public class AbsenceController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AbsenceRepository absenceRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<AbsenceDTO>> getAbsencesForStudent(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        List<AbsenceDTO> absences = absenceRepository.findByUser(user).stream().map(abs -> {
            AbsenceDTO dto = new AbsenceDTO();
            dto.setId(abs.getId());
            dto.setDate(abs.getDate());
            dto.setCount(abs.getCount());
            dto.setReason(abs.getReason());
            dto.setSubjectName(abs.getSubject() != null ? abs.getSubject().getName() : "");
            dto.setStudentId(user.getId());
            dto.setStudentName(user.getName());
            return dto;
        }).toList();

        return ResponseEntity.ok(absences);
    }

    @GetMapping
    public ResponseEntity<?> getAllAbsences() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<AbsenceDTO> absences = absenceRepository.findByUser(currentUser).stream()
                .map(abs -> {
                    AbsenceDTO dto = new AbsenceDTO();
                    dto.setId(abs.getId());
                    dto.setDate(abs.getDate());
                    dto.setCount(abs.getCount());
                    dto.setReason(abs.getReason());
                    dto.setSubjectName(abs.getSubject() != null ? abs.getSubject().getName() : "");
                    dto.setStudentId(abs.getUser().getId());
                    dto.setStudentName(abs.getUser().getName());
                    return dto;
                }).toList();

        int disrespectfulAbsences = absenceRepository.findByUser(currentUser).stream()
                .filter(abs -> abs.getReason().name().equals("DISRESPECTFUL"))
                .mapToInt(abs -> abs.getCount())
                .sum();

        String attendanceStatus;
        if (disrespectfulAbsences < 10) {
            attendanceStatus = "Хорошая посещаемость";
        } else if (disrespectfulAbsences < 30) {
            attendanceStatus = "Посещаемость в пределах нормы";
        } else {
            attendanceStatus = "Плохая посещаемость";
        }

        return ResponseEntity.ok(
                Map.of(
                        "absences", absences,
                        "attendanceStatus", attendanceStatus
                )
        );
    }


}

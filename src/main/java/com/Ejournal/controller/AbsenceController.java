package com.Ejournal.controller;

import com.Ejournal.DTO.AbsenceDTO;
import com.Ejournal.entity.User;
import com.Ejournal.repo.AbsenceRepository;
import com.Ejournal.repo.RoleRepository;
import com.Ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/absences")
public class AbsenceController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AbsenceRepository absenceRepository;


    @GetMapping("/{userId}/absences")
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



}

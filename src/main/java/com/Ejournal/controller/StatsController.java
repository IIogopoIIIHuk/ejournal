package com.Ejournal.controller;

import com.Ejournal.DTO.AbsenceDTO;
import com.Ejournal.entity.Absence;
import com.Ejournal.enums.Reason;
import com.Ejournal.repo.AbsenceRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsController {

    private final AbsenceRepository absenceRepository;

    @GetMapping
    public ResponseEntity<StatsResponse> getAllStats() {
        List<Absence> absences = absenceRepository.findAll();
        absences.sort(Comparator.comparing(Absence::getDate));
        Collections.reverse(absences);

        int absencesDisrespectful = 0;
        int absencesRespectful = 0;

        List<AbsenceDTO> absenceDTOs = absences.stream()
                .map(this::mapToDTO) // Здесь добавляется groupName
                .collect(Collectors.toList());

        for (Absence absence : absences) {
            if (absence.getReason() == Reason.DISRESPECTFUL) {
                absencesDisrespectful += absence.getCount();
            }
            if (absence.getReason() == Reason.RESPECTFUL) {
                absencesRespectful += absence.getCount();
            }
        }

        StatsResponse response = new StatsResponse();
        response.setAbsences(absenceDTOs);
        response.setAbsencesDisrespectful(absencesDisrespectful);
        response.setAbsencesRespectful(absencesRespectful);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<StatsResponse> searchStats(@RequestParam Long groupId, @RequestParam int year, @RequestParam int month) {
        List<Absence> absences = absenceRepository.findAll();

        String searchDatePrefix = (month < 10) ? year + "-0" + month : year + "-" + month;

        absences = absences.stream()
                .filter(a -> a.getSubject() != null && a.getSubject().getGroup() != null)
                .filter(a -> a.getSubject().getGroup().getId().equals(groupId))
                .filter(a -> a.getDate() != null && a.getDate().startsWith(searchDatePrefix))
                .sorted(Comparator.comparing(Absence::getDate).reversed())
                .collect(Collectors.toList());

        int absencesDisrespectful = 0;
        int absencesRespectful = 0;

        List<AbsenceDTO> absenceDTOs = absences.stream()
                .map(this::mapToDTO) // Здесь добавляется groupName
                .collect(Collectors.toList());

        for (Absence absence : absences) {
            if (absence.getReason() == Reason.DISRESPECTFUL) {
                absencesDisrespectful += absence.getCount();
            }
            if (absence.getReason() == Reason.RESPECTFUL) {
                absencesRespectful += absence.getCount();
            }
        }

        StatsResponse response = new StatsResponse();
        response.setAbsences(absenceDTOs);
        response.setAbsencesDisrespectful(absencesDisrespectful);
        response.setAbsencesRespectful(absencesRespectful);

        if (absences.isEmpty()) {
            response.setMessage("Элементов удовлетворяющих поиску нет");
        }

        return ResponseEntity.ok(response);
    }


    private AbsenceDTO mapToDTO(Absence absence) {
        AbsenceDTO dto = new AbsenceDTO();
        dto.setId(absence.getId());
        dto.setDate(absence.getDate());
        dto.setCount(absence.getCount());
        dto.setReason(absence.getReason());

        if (absence.getSubject() != null) {
            dto.setSubjectName(absence.getSubject().getName());
            if (absence.getSubject().getGroup() != null) {
                dto.setGroupName(absence.getSubject().getGroup().getName()); // Добавляем groupName
            } else {
                dto.setGroupName(null);
            }
        } else {
            dto.setSubjectName(null);
            dto.setGroupName(null); // Если нет subject, то groupName тоже null
        }

        if (absence.getUser() != null) {
            dto.setStudentId(absence.getUser().getId());
            dto.setStudentName(absence.getUser().getName());
        }

        return dto;
    }


    @Data
    public static class StatsResponse {
        private List<AbsenceDTO> absences;
        private int absencesDisrespectful;
        private int absencesRespectful;
        private String message;
    }
}

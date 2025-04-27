package com.Ejournal.controller;

import com.Ejournal.entity.*;
import com.Ejournal.enums.Reason;
import com.Ejournal.repo.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final AbsenceRepository absenceRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllSubjects() {
        List<Map<String, Object>> subjects = subjectRepository.findAll().stream()
                .map(subject -> Map.<String, Object>of(
                        "id", subject.getId(),
                        "name", subject.getName(),
                        "groupId", subject.getGroup().getId(),
                        "groupName", subject.getGroup().getName()
                ))
                .toList();
        return ResponseEntity.ok(subjects);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addSubject(@RequestBody SubjectAddRequest request) {
        // Получение текущего пользователя
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        UserGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        Subject subject = new Subject();
        subject.setName(request.getName());
        subject.setGroup(group);
        subject.setOwner(owner);

        subjectRepository.save(subject);

        return ResponseEntity.ok(Map.of(
                "id", subject.getId(),
                "name", subject.getName(),
                "groupId", group.getId(),
                "groupName", group.getName()
        ));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSubject(@PathVariable Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));

        List<Map<String, Object>> absences = subject.getAbsencesSorted().stream()
                .map(abs -> Map.<String, Object>of(
                        "id", abs.getId(),
                        "date", abs.getDate(),
                        "count", abs.getCount(),
                        "reason", abs.getReason().name(),
                        "studentId", abs.getUser().getId(),
                        "studentName", abs.getUser().getName()
                )).toList();

        return ResponseEntity.ok(Map.of(
                "id", subject.getId(),
                "name", subject.getName(),
                "groupId", subject.getGroup().getId(),
                "groupName", subject.getGroup().getName(),
                "absences", absences
        ));
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<Map<String, Object>> editSubject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));
        subject.setName(body.get("name"));
        subjectRepository.save(subject);
        return ResponseEntity.ok(Map.of(
                "id", subject.getId(),
                "name", subject.getName(),
                "groupId", subject.getGroup().getId(),
                "groupName", subject.getGroup().getName()
        ));
    }

    @PostMapping("/{subjectId}/absences/add")
    public ResponseEntity<Map<String, Object>> addAbsence(@PathVariable Long subjectId, @RequestBody AbsenceAddRequest request) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Предмет не найден"));
        User student = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        Absence absence = new Absence();
        absence.setDate(request.getDate());
        absence.setCount(request.getCount());
        absence.setReason(request.getReason());
        absence.setSubject(subject);
        absence.setUser(student);

        absenceRepository.save(absence);

        return ResponseEntity.ok(Map.of(
                "id", absence.getId(),
                "date", absence.getDate(),
                "count", absence.getCount(),
                "reason", absence.getReason().name(),
                "studentId", student.getId(),
                "studentName", student.getName(),
                "studentSubject", subject.getName()
        ));
    }

    @PutMapping("/{subjectId}/absences/{absenceId}/edit")
    public ResponseEntity<Map<String, Object>> editAbsence(@PathVariable Long absenceId, @RequestBody AbsenceEditRequest request) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new RuntimeException("Пропуск не найден"));

        absence.setDate(request.getDate());
        absence.setCount(request.getCount());
        absence.setReason(request.getReason());

        absenceRepository.save(absence);

        return ResponseEntity.ok(Map.of(
                "id", absence.getId(),
                "date", absence.getDate(),
                "count", absence.getCount(),
                "reason", absence.getReason().name(),
                "studentId", absence.getUser().getId(),
                "studentName", absence.getUser().getName()
        ));
    }

    @DeleteMapping("/{subjectId}/absences/{absenceId}/delete")
    public ResponseEntity<Map<String, String>> deleteAbsence(@PathVariable Long absenceId) {
        absenceRepository.deleteById(absenceId);
        return ResponseEntity.ok(Map.of("message", "Пропуск удалён"));
    }

    @Data
    public static class SubjectAddRequest {
        private String name;
        private Long groupId;
    }

    @Data
    public static class AbsenceAddRequest {
        private Long userId;
        private String date;
        private int count;
        private Reason reason;
    }

    @Data
    public static class AbsenceEditRequest {
        private String date;
        private int count;
        private Reason reason;
    }
}

package com.Ejournal.controller;

import com.Ejournal.DTO.NoteDTO;
import com.Ejournal.entity.Note;
import com.Ejournal.entity.User;
import com.Ejournal.repo.NoteRepository;
import com.Ejournal.repo.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;


    @Value("${upload.notes.path}")
    private String uploadFileDir;

    @Value("${upload.notes.baseurl}")
    private String uploadFileBaseUrl;


    @GetMapping
    public ResponseEntity<List<NoteDTO>> getAllNotes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<NoteDTO> notes = noteRepository.findByOwner(user).stream()
                .map(this::mapToDTO)
                .toList();

        return ResponseEntity.ok(notes);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<NoteDTO>> getAllNotesByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<NoteDTO> notes = noteRepository.findByOwner(user).stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/add")
    public ResponseEntity<NoteDTO> addNote(
            @RequestPart(value = "note") String noteJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            String uploadedFileName = null;

            if (file != null && !file.getOriginalFilename().isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                String fileName = uuidFile + "_" + file.getOriginalFilename();

                Path uploadPath = Paths.get(uploadFileDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                uploadedFileName = fileName;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            NoteRequest request = objectMapper.readValue(noteJson, NoteRequest.class);

            // >>> Получаем текущего пользователя через SecurityContextHolder
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            Note note = new Note();
            note.setFile(uploadedFileName);
            note.setDateWith(request.getDateWith());
            note.setDateBy(request.getDateBy());
            note.setOwner(user);

            noteRepository.save(note);

            NoteDTO dto = new NoteDTO();
            dto.setId(note.getId());
            dto.setFile(uploadFileBaseUrl + note.getFile());
            dto.setDateWith(note.getDateWith());
            dto.setDateBy(note.getDateBy());

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла: " + e.getMessage());
        }
    }




    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объяснительная не найдена"));

        noteRepository.delete(note);

        return ResponseEntity.ok(mapToDTO(note));
    }

    private NoteDTO mapToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        if (note.getFile() != null) {
            dto.setFile(uploadFileBaseUrl + note.getFile());
        } else {
            dto.setFile(null);
        }
        dto.setDateWith(note.getDateWith());
        dto.setDateBy(note.getDateBy());
        return dto;
    }


    @Data
    public static class NoteRequest {
        private Long userId;
        private String dateWith;
        private String dateBy;
    }
}

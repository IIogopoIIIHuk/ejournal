package com.Ejournal.controller;

import com.Ejournal.DTO.AbsenceDTO;
import com.Ejournal.DTO.NoteDTO;
import com.Ejournal.entity.Note;
import com.Ejournal.entity.User;
import com.Ejournal.repo.NoteRepository;
import com.Ejournal.repo.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;


    @GetMapping()
    public ResponseEntity<List<NoteDTO>> getAllNotes() {
        List<NoteDTO> notes = noteRepository.findAll().stream().map(note -> {
            NoteDTO dto = new NoteDTO();
            dto.setId(note.getId());
            dto.setFile(note.getFile());
            dto.setDateWith(note.getDateWith());
            dto.setDateBy(note.getDateBy());
            return dto;
        }).toList();

        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NoteDTO>> getAllNotes(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<NoteDTO> notes = noteRepository.findByOwner(user).stream().map(note -> {
            NoteDTO dto = new NoteDTO();
            dto.setId(note.getId());
            dto.setFile(note.getFile());
            dto.setDateWith(note.getDateWith());
            dto.setDateBy(note.getDateBy());
            return dto;
        }).toList();

        return ResponseEntity.ok(notes);
    }


    @PostMapping("/add")
    public ResponseEntity<NoteDTO> addNote(@RequestBody NoteRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Note note = new Note();
        note.setFile(request.getFile());
        note.setDateWith(request.getDateWith());
        note.setDateBy(request.getDateBy());
        note.setOwner(user);

        noteRepository.save(note);

        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setFile(note.getFile());
        dto.setDateWith(note.getDateWith());
        dto.setDateBy(note.getDateBy());

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<NoteDTO> deleteNote(@PathVariable Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RuntimeException("Объяснительная не найдена"));
        noteRepository.delete(note);

        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setFile(note.getFile());
        dto.setDateWith(note.getDateWith());
        dto.setDateBy(note.getDateBy());

        return ResponseEntity.ok(dto);
    }

    @Data
    public static class NoteRequest {
        private Long userId;
        private String file;
        private String dateWith;
        private String dateBy;
    }
}

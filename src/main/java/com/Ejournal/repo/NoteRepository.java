package com.Ejournal.repo;

import com.Ejournal.entity.Note;
import com.Ejournal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwner(User owner);
}

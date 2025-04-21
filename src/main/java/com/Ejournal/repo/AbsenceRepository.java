package com.Ejournal.repo;

import com.Ejournal.entity.Absence;
import com.Ejournal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByUser(User user);
}

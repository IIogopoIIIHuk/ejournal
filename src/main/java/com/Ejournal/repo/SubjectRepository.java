package com.Ejournal.repo;

import com.Ejournal.entity.Subject;
import com.Ejournal.entity.UserGroup;
import com.Ejournal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByGroup(UserGroup group);
    List<Subject> findAllByOwner(User owner);
}

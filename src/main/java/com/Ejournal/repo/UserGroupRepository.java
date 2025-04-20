package com.Ejournal.repo;

import com.Ejournal.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findByNameContainingIgnoreCase(String name);
}

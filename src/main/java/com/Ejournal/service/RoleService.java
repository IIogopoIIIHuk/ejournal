package com.Ejournal.service;

import com.Ejournal.entity.Role;
import com.Ejournal.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getRole_User(){
        return getUserRole("ROLE_USER");
    }

    public Role getRole_Teacher(){
        return getUserRole("ROLE_TEACHER");
    }

    public Role getUserRole(String roleName){
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NoSuchElementException("Role '" + roleName + "' not found in database"));
    }


}
package com.Ejournal.controller;

import com.Ejournal.DTO.GroupDTO;
import com.Ejournal.DTO.UserDTO;
import com.Ejournal.entity.User;
import com.Ejournal.entity.UserGroup;
import com.Ejournal.repo.UserGroupRepository;
import com.Ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

    private final UserGroupRepository groupRepo;
    private final UserRepository userRepo;

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        List<GroupDTO> groups = groupRepo.findAll().stream().map(group -> {
            List<UserDTO> users = group.getUsers().stream().map(UserDTO::fromEntity).toList();
            return new GroupDTO(group.getId(), group.getName(), users);
        }).toList();

        return ResponseEntity.ok(groups);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GroupDTO>> searchGroups(@RequestParam String name) {
        List<UserGroup> found = groupRepo.findByNameContainingIgnoreCase(name);
        List<GroupDTO> groups = found.stream().map(group -> {
            List<UserDTO> users = group.getUsers().stream().map(UserDTO::fromEntity).toList();
            return new GroupDTO(group.getId(), group.getName(), users);
        }).toList();

        return ResponseEntity.ok(groups);
    }

    @PostMapping("/addGroup")
    public ResponseEntity<GroupDTO> addGroup(@RequestParam String name) {
        UserGroup group = new UserGroup();
        group.setName(name);
        groupRepo.save(group);

        return ResponseEntity.ok(new GroupDTO(
                group.getId(),
                group.getName(),
                List.of())
        );
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<GroupDTO> editGroup(@PathVariable Long id, @RequestParam String name) {
        UserGroup group = groupRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));
        group.setName(name);
        groupRepo.save(group);

        List<UserDTO> users = group.getUsers().stream().map(UserDTO::fromEntity).toList();
        return ResponseEntity.ok(new GroupDTO(group.getId(), group.getName(), users));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        UserGroup group = groupRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        for (User u : group.getUsers()) {
            u.setGroup(null);
            userRepo.save(u);
        }

        groupRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDTO> getGroupUsers(@PathVariable Long groupId) {
        UserGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        List<UserDTO> users = group.getUsers().stream().map(UserDTO::fromEntity).toList();
        return ResponseEntity.ok(new GroupDTO(group.getId(), group.getName(), users));
    }

    @PutMapping("/{groupId}/addUser")
    public ResponseEntity<?> addUserToGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        UserGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        user.setGroup(group);
        userRepo.save(user);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}/removeUser")
    public ResponseEntity<?> removeUserFromGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getGroup() == null || !user.getGroup().getId().equals(groupId)) {
            return ResponseEntity.badRequest().body("Пользователь не состоит в этой группе");
        }

        user.setGroup(null);
        userRepo.save(user);

        return ResponseEntity.ok().build();
    }


}

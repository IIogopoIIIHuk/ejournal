package com.Ejournal.DTO;


import com.Ejournal.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.Ejournal.entity.User;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String name;
    private String phone;
    private boolean enabled;
    private List<String> roles;

    public UserDTO() {

    }

    public UserDTO(Long id, String username, String email, String name, String phone) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public UserDTO(Long id, String username, String email, String name, String phone, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.enabled = enabled;
    }

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setEnabled(user.isEnabled());
        dto.setRoles(user.getRoles().stream().map(r -> r.getName()).toList());
        return dto;
    }



}
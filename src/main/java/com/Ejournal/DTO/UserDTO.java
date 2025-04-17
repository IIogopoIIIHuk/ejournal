package com.Ejournal.DTO;


import com.Ejournal.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
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


}
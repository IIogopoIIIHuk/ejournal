package com.Ejournal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private Long id;
    private String name;
    private Long groupId;
    private String groupName;
    private List<AbsenceDTO> absences;

    public SubjectDTO(Long id, String name, Long groupId, String groupName) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.groupName = groupName;
    }
}

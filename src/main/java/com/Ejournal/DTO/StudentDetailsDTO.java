package com.Ejournal.DTO;

import com.Ejournal.entity.Absence;
import com.Ejournal.entity.Note;
import lombok.Data;

import java.util.List;

@Data
public class StudentDetailsDTO {

    private UserDTO student;
    private String attendanceStatus;
    private List<Note> notes;
    private List<Absence> absences;
}

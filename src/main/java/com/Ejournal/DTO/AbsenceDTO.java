package com.Ejournal.DTO;

import com.Ejournal.enums.Reason;
import lombok.Data;

@Data
public class AbsenceDTO {
    private Long id;
    private String date;
    private int count;
    private Reason reason;
    private String subjectName;
    private Long studentId;
    private String studentName;
}

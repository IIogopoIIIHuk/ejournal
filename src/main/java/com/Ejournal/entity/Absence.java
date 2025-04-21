package com.Ejournal.entity;

import com.Ejournal.enums.Reason;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "absences")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private int count;

    @Enumerated(EnumType.STRING)
    private Reason reason;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

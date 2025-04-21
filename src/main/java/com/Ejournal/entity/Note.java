package com.Ejournal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String file;
    private String dateWith;
    private String dateBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}

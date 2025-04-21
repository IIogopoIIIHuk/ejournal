package com.Ejournal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Data
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Absence> absences = new ArrayList<>();

    public List<Absence> getAbsencesSorted() {
        absences.sort(Comparator.comparing(Absence::getDate));
        Collections.reverse(absences);
        return absences;
    }
}
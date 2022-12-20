package com.project.analytics.database.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "visitor")
public class Visitor {
    @Id
    @Column(name = "asid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ASID;

    @Column(name = "unit_id")
    private Long unitId;
    @Column(name = "container_id")
    private Long containerId;

    @Column(name = "msisdn")
    private Long MSISDN;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "patronymic")
    private String patronymic;

    public Visitor() {
    }

    public Visitor(Long unit_id, Long container_id, Long number) {
        unitId = unit_id;
        containerId = container_id;
        MSISDN = number;
    }

    public Visitor(Long unit_id, Long container_id) {
        unitId = unit_id;
        containerId = container_id;
    }
}
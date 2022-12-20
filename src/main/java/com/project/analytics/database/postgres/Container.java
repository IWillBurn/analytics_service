package com.project.analytics.database.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
@Entity
@Table(name = "container")
public class Container {
    @Id
    @Column(name = "container_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long containerId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "container_name")
    private String containerName;

    public Container() {
    }

    public Container(Long user, Long unit, String name) {
        this.userId = user;
        this.unitId = unit;
        this.containerName = name;
    }
}
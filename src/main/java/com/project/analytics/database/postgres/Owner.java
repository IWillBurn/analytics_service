package com.project.analytics.database.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@Entity
@AllArgsConstructor
@Table(name = "owner")
public class Owner {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    public Owner() {
    }

    public Owner(String name) {
        userName = name;
    }
}

package com.project.analytics.database;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "MSISDN")
    private Long MSISDN;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "patronymic")
    private String patronymic;

    public User() {
    }

    public User(Long number, String fName, String lName, String patron) {
        MSISDN = number;
        firstName = fName;
        lastName = lName;
        patronymic = patron;
    }
}
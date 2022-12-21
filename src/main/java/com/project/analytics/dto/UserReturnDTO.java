package com.project.analytics.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserReturnDTO {
    private String firstName;

    private String lastName;

    private String patronymic;
}

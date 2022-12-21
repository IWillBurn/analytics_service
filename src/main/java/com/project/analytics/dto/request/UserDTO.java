package com.project.analytics.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    @JsonProperty
    private Long userId;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private String patronymic;
    @JsonProperty
    private Long msisdn;
    UserDTO(){}
}

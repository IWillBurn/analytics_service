package com.project.analytics.dto.postgres;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ElementDTO {
    String name;
    String id;
    String className;
}

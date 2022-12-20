package com.project.analytics.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnitPageDTO {
    private Long userId;
    private String unitName;
}

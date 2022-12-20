package com.project.analytics.dto.request;

import com.project.analytics.dto.postgres.TriggerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TriggerResultDTO {
    private Long unitId;

    private Long containerId;

    private Long triggerId;

    private Long ASID;

    private String event;
}

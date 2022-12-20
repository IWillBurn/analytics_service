package com.project.analytics.dto.request;

import com.project.analytics.database.postgres.Container;
import com.project.analytics.dto.postgres.TriggerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ContainerPageDTO {
    private Long userId;

    private Long unitId;

    private String containerName;

    private List<TriggerDTO> triggers;
}

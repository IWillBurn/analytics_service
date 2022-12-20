package com.project.analytics.dto.postgres;

import com.project.analytics.database.postgres.Container;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ContainerDTO {
    private Long containerId;

    private Long userId;

    private Long unitId;

    private String containerName;

    private List<TriggerDTO> triggers;

    public ContainerDTO(Container container, List<TriggerDTO> trigger){
        containerId = container.getContainerId();
        userId = container.getUserId();
        unitId = container.getUnitId();
        containerName = container.getContainerName();
        triggers = trigger;
    }
}

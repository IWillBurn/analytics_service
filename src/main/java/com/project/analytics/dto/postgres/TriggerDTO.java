package com.project.analytics.dto.postgres;

import com.project.analytics.database.postgres.Trigger;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TriggerDTO {
    private ElementDTO element;
    private Long triggerId;
    private String event;

    public TriggerDTO(Trigger trigger){
        element = new ElementDTO(trigger.getWebName(), trigger.getWebId(), trigger.getWebClass());
        event = trigger.getEvent();
        triggerId = trigger.getTriggerId();
    }
}

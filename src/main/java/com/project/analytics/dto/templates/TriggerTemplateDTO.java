package com.project.analytics.dto.templates;

import com.project.analytics.dto.postgres.ElementDTO;
import com.project.analytics.dto.postgres.TriggerDTO;
import lombok.Data;

@Data
public class TriggerTemplateDTO {
    String querySelector;
    Long unitId;
    Long containerId;
    Long triggerId;
    Long ASID;
    String event;

    public TriggerTemplateDTO(Long unit, Long container, Long id, Long analyticsId, String eventType, ElementDTO el){
        querySelector = el.getName()+"#"+el.getId()+"."+el.getClassName();
        unitId = unit;
        containerId = container;
        triggerId = id;
        ASID = analyticsId;
        event = eventType;
    }

    public TriggerTemplateDTO(TriggerDTO trigger, Long unit, Long container, Long id, Long analyticsId){
        querySelector = trigger.getElement().getName()+"#"+trigger.getElement().getId()+"."+trigger.getElement().getClassName();
        unitId = unit;
        containerId = container;
        triggerId = id;
        ASID = analyticsId;
        event = trigger.getEvent();
    }
}

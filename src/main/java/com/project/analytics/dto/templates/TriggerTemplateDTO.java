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
        if (el.getId() != "") {
            querySelector = el.getName() + "#" + el.getId() + "." + el.getClassName();
        }
        else{
            querySelector = el.getName() + "." + el.getClassName();
        }
        unitId = unit;
        containerId = container;
        triggerId = id;
        ASID = analyticsId;
        event = eventType;
    }

    public TriggerTemplateDTO(TriggerDTO trigger, Long unit, Long container, Long id, Long analyticsId){
        if (trigger.getElement().getId() != "") {
            if (trigger.getElement().getClassName() != "") {
                querySelector = trigger.getElement().getName() + "#" + trigger.getElement().getId() + "." + trigger.getElement().getClassName();
            }
            else{
                querySelector = trigger.getElement().getName() + "#" + trigger.getElement().getId();
            }
        }
        else{
            if (trigger.getElement().getClassName() != "") {
                querySelector = trigger.getElement().getName() + "." + trigger.getElement().getClassName();
            }
            else{
                querySelector = trigger.getElement().getName();
            }
        }
        unitId = unit;
        containerId = container;
        triggerId = id;
        ASID = analyticsId;
        event = trigger.getEvent();
    }
}

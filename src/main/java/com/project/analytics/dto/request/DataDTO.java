package com.project.analytics.dto.request;

import com.project.analytics.dto.postgres.ElementDTO;
import com.project.analytics.dto.postgres.TriggerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DataDTO {
    private Long dataId;
    private String containerName;
    private ElementDTO element;
    private String event;
    private Long ASID;
    private Long MSISDN;
    private Boolean isEnrichment;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Long date;
    public DataDTO(Long data, String name, ElementDTO el, String ev, Long id, Long d){
        dataId = data;
        containerName = name;
        element = el;
        event = ev;
        ASID = id;
        date = d;
        isEnrichment = false;
    }
}

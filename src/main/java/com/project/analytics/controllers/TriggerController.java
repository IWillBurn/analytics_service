package com.project.analytics.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.analytics.database.minio.MinioController;
import com.project.analytics.database.postgres.*;
import com.project.analytics.dto.postgres.UnitDTO;
import com.project.analytics.dto.request.ASIDDTO;
import com.project.analytics.dto.request.TriggerResultDTO;
import com.project.analytics.dto.request.UnitPageDTO;
import com.project.analytics.dto.templates.TriggerTemplateDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trigger")
@RequiredArgsConstructor
public class TriggerController {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @PostMapping("")
    public String getDataFromTrigger(@RequestBody TriggerResultDTO trigger) throws IOException {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Data data;
        ASIDDTO asiddto = new ASIDDTO(0L);
        if (trigger.getMSISDN() == null){
            if (trigger.getASID() == null){
                Visitor visitor = new Visitor(trigger.getUnitId(), trigger.getContainerId());
                visitor = visitorRepository.save(visitor);
                data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), visitor.getASID(), trigger.getEvent(), date);
                asiddto.setASID(visitor.getASID());
            }
            else{
                Optional<Visitor> visitorsWithThisASID = visitorRepository.findById(trigger.getASID());
                if (visitorsWithThisASID.isEmpty()){
                    Visitor visitor = new Visitor(trigger.getUnitId(), trigger.getContainerId());
                    visitor = visitorRepository.save(visitor);
                    data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), visitor.getASID(), trigger.getEvent(), date);
                    asiddto.setASID(visitor.getASID());
                }
                else {
                    data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), trigger.getASID(), trigger.getEvent(), date);
                    asiddto.setASID(trigger.getASID());
                }
            }
        }
        else{
            if (trigger.getASID() == null){
                List<Visitor> visitors = visitorRepository.findByMSISDN(trigger.getMSISDN());
                Visitor visitor;
                if (visitors.isEmpty()){
                    visitor = new Visitor(trigger.getUnitId(), trigger.getContainerId(), trigger.getMSISDN());
                    visitor = visitorRepository.save(visitor);
                }
                else{ visitor = visitors.get(0); }
                data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), visitor.getASID(), trigger.getEvent(), date);
                asiddto.setASID(visitor.getASID());
            }
            else{
                List<Visitor> visitorsWithThisMSISDN = visitorRepository.findByMSISDN(trigger.getMSISDN());
                Optional<Visitor> visitorsWithThisASID = visitorRepository.findById(trigger.getASID());
                Visitor visitor;
                Visitor visitorWithThisASID;
                if (visitorsWithThisASID.isEmpty()){
                    if (visitorsWithThisMSISDN.isEmpty()){
                        visitor = new Visitor(trigger.getUnitId(), trigger.getContainerId(), trigger.getMSISDN());
                        visitor = visitorRepository.save(visitor);
                    }
                    else{
                        visitor = visitorsWithThisMSISDN.get(0);
                    }
                    data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), visitor.getASID(), trigger.getEvent(), date);
                    asiddto.setASID(visitor.getASID());
                }
                else{
                    if (visitorsWithThisMSISDN.isEmpty()){
                        visitorWithThisASID = visitorsWithThisASID.get();
                        visitorWithThisASID.setMSISDN(trigger.getMSISDN());
                        // Здесь должна быть отправка на обогощение
                        visitor = visitorRepository.save(visitorWithThisASID);
                        data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), visitor.getASID(), trigger.getEvent(), date);
                        asiddto.setASID(visitor.getASID());
                    }
                    else{
                        visitor = visitorsWithThisMSISDN.get(0);
                        data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), visitor.getASID(), trigger.getEvent(), date);
                        asiddto.setASID(visitor.getASID());
                    }
                }
            }
        }
        dataRepository.save(data);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{}";
        try {
            json = objectMapper.writeValueAsString(asiddto);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }
        return json;
    }
}

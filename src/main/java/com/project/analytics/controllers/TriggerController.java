package com.project.analytics.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.analytics.database.minio.MinioController;
import com.project.analytics.database.postgres.*;
import com.project.analytics.dto.postgres.UnitDTO;
import com.project.analytics.dto.request.ASIDDTO;
import com.project.analytics.dto.request.TriggerResultDTO;
import com.project.analytics.dto.request.UnitPageDTO;
import com.project.analytics.dto.request.UserDTO;
import com.project.analytics.dto.templates.TriggerTemplateDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/trigger")
@RequiredArgsConstructor
public class TriggerController {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @PostMapping("")
    public String getDataFromTrigger(@RequestBody TriggerResultDTO trigger) throws IOException, JSONException {
        System.out.println(trigger);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
        Data data;
        ASIDDTO asiddto = new ASIDDTO(0L);
        if (trigger.getMSISDN() == null || trigger.getMSISDN() == -1){
            if (trigger.getASID() == null || trigger.getASID() == -1){
                System.out.println("here2");
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
                    System.out.println("here5");
                    data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), trigger.getASID(), trigger.getEvent(), date);
                    asiddto.setASID(trigger.getASID());
                }
            }
        }
        else{
            List<Visitor> visitors = visitorRepository.findByMSISDN(trigger.getMSISDN());
            if (trigger.getASID() == null || trigger.getASID() == -1) {
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
                Optional<Visitor> visitorsWithThisASID = visitorRepository.findById(trigger.getASID());
                Visitor visitor;
                Visitor visitorWithThisASID;
                if (visitorsWithThisASID.isEmpty()){
                    if (visitors.isEmpty()){
                        visitor = new Visitor(trigger.getUnitId(), trigger.getContainerId(), trigger.getMSISDN());
                        String url = "http://127.0.0.1:8090/api/users" + "?MSISDN="+trigger.getMSISDN().toString();
                        RestTemplate restTemplate = new RestTemplate();
                        UserDTO user = restTemplate.getForObject(url, UserDTO.class);
                        if (user.getFirstName() != null){ visitor.setFirstName(user.getFirstName()); }
                        if (user.getLastName() != null){ visitor.setLastName(user.getLastName()); }
                        if (user.getPatronymic() != null){ visitor.setPatronymic(user.getPatronymic()); }
                        visitor = visitorRepository.save(visitor);
                    }
                    else{
                        visitor = visitors.get(0);
                    }
                }
                else{
                    if (visitors.isEmpty()){
                        visitorWithThisASID = visitorsWithThisASID.get();
                        visitorWithThisASID.setMSISDN(trigger.getMSISDN());
                        String url = "http://127.0.0.1:8090/api/users" + "?MSISDN="+trigger.getMSISDN().toString();
                        RestTemplate restTemplate = new RestTemplate();
                        UserDTO user = restTemplate.getForObject(url, UserDTO.class);
                        if (user.getFirstName() != null){ visitorWithThisASID.setFirstName(user.getFirstName()); }
                        if (user.getLastName() != null){ visitorWithThisASID.setLastName(user.getLastName()); }
                        if (user.getPatronymic() != null){ visitorWithThisASID.setPatronymic(user.getPatronymic()); }
                        visitor = visitorRepository.save(visitorWithThisASID);
                    }
                    else{
                        visitor = visitors.get(0);
                    }
                }
                data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), visitor.getASID(), trigger.getEvent(), date);
                asiddto.setASID(visitor.getASID());
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

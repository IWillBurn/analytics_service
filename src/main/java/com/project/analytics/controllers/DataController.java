package com.project.analytics.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.analytics.database.minio.MinioController;
import com.project.analytics.database.postgres.*;
import com.project.analytics.dto.postgres.ContainerDTO;
import com.project.analytics.dto.postgres.ElementDTO;
import com.project.analytics.dto.postgres.TriggerDTO;
import com.project.analytics.dto.request.DataDTO;
import com.project.analytics.dto.request.DataListDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    @Autowired
    private DataRepository dataRepository;
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private VisitorRepository visitorRepository;
    @Autowired
    private TriggerRepository triggerRepository;

    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public String getPageOfData(@RequestParam(name = "unitId") Long unitId,
                                @RequestParam(name = "page", required = false) Integer page,
                                @RequestParam(name = "pageSize", required = false) Integer pageSize) throws JsonProcessingException {
        List<Data> searchResult = dataRepository.findByUnitIdOrderByDateDesc(unitId);
        Long countOfPages = searchResult.size()/pageSize + 1L;
        if (page != null){
            List<Data> bufferResult = new ArrayList<>(searchResult);
            searchResult.clear();
            for (int i = (pageSize * page); i < Math.min(bufferResult.size(), pageSize * (page + 1)); i++){
                searchResult.add(bufferResult.get(i));
            }
        }

        List<DataDTO> result = new ArrayList<>();
        for (Data d : searchResult){
            Container container = containerRepository.findById(d.getContainerId()).get();
            Visitor visitor = visitorRepository.findById(d.getASID()).get();
            Trigger trigger = triggerRepository.findById(d.getTriggerId()).get();

            DataDTO data = new DataDTO(d.getDataId(), container.getContainerName(), new ElementDTO(trigger.getWebName(), trigger.getWebId(), trigger.getWebClass()), trigger.getEvent(), d.getASID(), d.getDate().getTime());
            if (visitor.getFirstName() != null){ data.setIsEnrichment(true); }
            if (visitor.getMSISDN() != null){ data.setMSISDN(visitor.getMSISDN()); }
            if (visitor.getFirstName() != null){ data.setFirstName(visitor.getFirstName()); }
            if (visitor.getLastName() != null){ data.setLastName(visitor.getLastName()); }
            if (visitor.getPatronymic() != null){ data.setPatronymic(visitor.getPatronymic()); }
            result.add(data);
        }

        String json = "{}";
        ObjectMapper objectMapper = new ObjectMapper();
        json = objectMapper.writeValueAsString(new DataListDTO(result, countOfPages));
        return json;
    }
}

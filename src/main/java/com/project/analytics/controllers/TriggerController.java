package com.project.analytics.controllers;

import com.project.analytics.database.minio.MinioController;
import com.project.analytics.database.postgres.Data;
import com.project.analytics.database.postgres.DataRepository;
import com.project.analytics.database.postgres.TriggerRepository;
import com.project.analytics.database.postgres.UnitRepository;
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

@RestController
@RequestMapping("/api/trigger")
@RequiredArgsConstructor
public class TriggerController {

    @Autowired
    private DataRepository dataRepository;

    @PostMapping("")
    public void getDataFromTrigger(@RequestBody TriggerResultDTO trigger) throws IOException {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Data data = new Data(trigger.getTriggerId(), trigger.getUnitId(), trigger.getContainerId(), trigger.getASID(), trigger.getEvent(), date);
        dataRepository.save(data);
    }
}

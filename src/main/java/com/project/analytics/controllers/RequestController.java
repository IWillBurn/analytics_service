package com.project.analytics.controllers;

import com.project.analytics.database.minio.MinioController;
import com.project.analytics.database.postgres.Data;
import com.project.analytics.database.postgres.DataRepository;
import com.project.analytics.database.postgres.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    @Autowired
    DataRepository dataRepository;

    @GetMapping(
            value = "/triggered",
            produces = "application/json"
    )
    public String getCountOfTriggered(@RequestParam(name = "triggerId") Long triggerId,
                                      @RequestParam(name = "unique", required = false) Boolean unique,
                                      @RequestParam(name = "dateFrom", required = false) Long dateFrom,
                                      @RequestParam(name = "dateTo", required = false) Long dateTo) {
        Long result = 0L;
        if (dateFrom == null){ dateFrom = 0L; }
        if (dateTo == null){ dateTo = 6311520000000L; }
        if (unique == null){ unique = true; }
        if (unique) {
            result = dataRepository.countTriggeredDistinct(triggerId, new Date(dateFrom), new Date(dateTo));
        }
        else{
            result = dataRepository.countByTrigger(triggerId, new Date(dateFrom), new Date(dateTo));
        }
        return "{ \"result\": " + result.toString() + " }";
    }
    @GetMapping(
            value = "/sequences",
            produces = "application/json"
    )
    public String getUsersUsedSequenceOfEvents(@RequestParam(name = "triggerIdA") Long triggerIdA,
                                               @RequestParam(name = "triggerIdB") Long triggerIdB,
                                               @RequestParam(name = "deltaTimeMin", required = false) Long deltaTimeMin,
                                               @RequestParam(name = "deltaTimeMax", required = false) Long deltaTimeMax,
                                               @RequestParam(name = "dateFrom", required = false) Long dateFrom,
                                               @RequestParam(name = "dateTo", required = false) Long dateTo) {
        List<Long> resultASID = new ArrayList<>();
        if (deltaTimeMin == null){ deltaTimeMin = 0L; }
        if (deltaTimeMax == null){ deltaTimeMax = 6311520000000L; }
        if (dateFrom == null){ dateFrom = 0L; }
        if (dateTo == null){ dateTo = 6311520000000L; }
        resultASID = dataRepository.dataEventBAfterEventA(triggerIdA, triggerIdB, new Date(dateFrom), new Date(dateTo), deltaTimeMin*1000000, deltaTimeMax*1000000);
        System.out.println(resultASID);
        return "{ \"result\": " + resultASID.toString() + " }";
    }
}

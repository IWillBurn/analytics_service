package com.project.analytics.controllers;

import com.project.analytics.database.minio.MinioController;
import com.project.analytics.database.postgres.Data;
import com.project.analytics.database.postgres.UnitRepository;
import com.project.analytics.dto.templates.TriggerTemplateDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;

@CrossOrigin
@RestController
@RequestMapping("/api/scripts")
@RequiredArgsConstructor
public class ScriptsController {
    @Autowired
    private MinioController minioController;
    @Autowired
    private UnitRepository unitRepository;

    @GetMapping(
            value = "/{id}.js",
            produces = "application/javascript"
    )
    public byte[] getScript(@PathVariable("id") String id) throws IOException {
        return IOUtils.toByteArray(minioController.getObject("scripts", "scripts_"+id));
    }
}

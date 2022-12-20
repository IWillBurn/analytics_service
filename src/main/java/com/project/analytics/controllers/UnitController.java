package com.project.analytics.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.analytics.database.minio.MinioController;
import com.project.analytics.database.postgres.*;
import com.project.analytics.dto.postgres.UnitDTO;
import com.project.analytics.dto.request.UnitPageDTO;
import io.minio.errors.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private MinioController minioController;

    // Получение юнита
    @GetMapping(
            value = "/{unit_id}",
            produces = "application/json"
    )
    public String getUnit(@PathVariable("unit_id") Long unitId){
        List<Unit> searchResult = unitRepository.findByUnitId(unitId);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{}";
        try {
            json = objectMapper.writeValueAsString(new UnitDTO(searchResult.get(0)));
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }
        return json;
    }

    // Получение страницы юнитов
    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public String getPageOfUnit(@RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "pageSize", required = false) Integer pageSize, @RequestParam(name = "userId") Long userId){
        String json = "{}";
        ObjectMapper objectMapper = new ObjectMapper();
        List<Unit> searchResult = unitRepository.findByUserId(userId);
        if (page != null){
            List<Unit> bufferResult = new ArrayList<>(searchResult);
            searchResult.clear();
            for (int i = (pageSize * page); i < Math.min(bufferResult.size(), pageSize * (page + 1)); i++){
                searchResult.add(bufferResult.get(i));
            }
        }
        List<UnitDTO> outputs = new ArrayList<>();
        for (Unit u : searchResult){
            outputs.add(new UnitDTO(u));
        }
        try {
            json = objectMapper.writeValueAsString(outputs);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }
        return json;
    }

    // Добавление юнита
    @PostMapping(
            value = "",
            produces = "application/json"
    )
    public String addUnit(@RequestBody UnitPageDTO requestBody){
        ObjectMapper objectMapper = new ObjectMapper();
        Unit newUnit = new Unit(requestBody.getUserId(), requestBody.getUnitName());
        unitRepository.save(newUnit);
        String json = "{}";
        try {
            json = objectMapper.writeValueAsString(new UnitDTO(newUnit));
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }
        return json;
    }

    // Переименование юнита
    @PatchMapping(
            value = "/{unit_id}",
            produces = "application/json"
    )
    public String changeUnit(@PathVariable("unit_id") Long unitId, @RequestBody UnitPageDTO requestBody){
        Optional<Unit> searchResult = unitRepository.findById(unitId);
        Unit result = searchResult.get();
        result.changeUnit(requestBody);
        unitRepository.save(result);
        ObjectMapper objectMapper = new ObjectMapper();
        unitRepository.save(result);
        String json = "{}";
        try {
            json = objectMapper.writeValueAsString(new UnitDTO(result));
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }
        return json;
    }

    // Удаление юнита
    @DeleteMapping(
            value = "/{unit_id}",
            produces = "application/json"
    )
    @Transactional
    public void deleteUnit(@PathVariable("unit_id") Long unitId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        triggerRepository.deleteByUnitId(unitId);
        List<Container> deletedContainers = containerRepository.deleteByUnitId(unitId);
        unitRepository.deleteById(unitId);
        minioController.deleteMinIOScripts(deletedContainers, "scripts", "scripts_");
    }
}

package com.project.analytics.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.analytics.database.minio.MinioController;
import com.project.analytics.database.postgres.*;
import com.project.analytics.dto.postgres.ContainerDTO;
import com.project.analytics.dto.postgres.TriggerDTO;
import com.project.analytics.dto.request.ContainerPageDTO;
import com.project.analytics.dto.templates.TriggerTemplateDTO;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/containers")
@RequiredArgsConstructor
public class ContainerController {
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private DataRepository dataRepository;
    @Autowired
    private MinioController minioController;

    // Получение контейнера
    @GetMapping(
            value = "/{container_id}",
            produces = "application/json"
    )
    public String getContainer(@PathVariable("container_id") Long containerId){
        Optional<Container> searchResult = containerRepository.findById(containerId);
        List<Trigger> triggers = triggerRepository.findByContainerId(containerId);
        List<TriggerDTO> triggersReturn = new ArrayList<>();
        for (Trigger t : triggers){
            if (!(t.getEvent().equals("online") || t.getEvent().equals("init"))) {
                System.out.println(t.getEvent());
                triggersReturn.add(new TriggerDTO(t));
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{}";
        try {
            json = objectMapper.writeValueAsString(new ContainerDTO(searchResult.get(), triggersReturn));
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }
        return json;
    }

    // Получение страницы контейнеров
    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public String getPageOfContainer(@RequestParam(name = "page", required = false) Integer page,
                                     @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                     @RequestParam(name = "unitId") Long unitId){
        String json = "{}";
        ObjectMapper objectMapper = new ObjectMapper();
        List<Container> searchResult = containerRepository.findByUnitId(unitId);
        if (page != null){
            List<Container> bufferResult = new ArrayList<>(searchResult);
            searchResult.clear();
            for (int i = (pageSize * page); i < Math.min(bufferResult.size(), pageSize * (page + 1)); i++){
                searchResult.add(bufferResult.get(i));
            }
        }
        List<ContainerDTO> outputs = new ArrayList<>();
        for (Container c : searchResult){
            List<Trigger> triggers = triggerRepository.findByContainerId(c.getContainerId());
            List<TriggerDTO> triggersReturn = new ArrayList<>();
            for (Trigger t : triggers){
                if (!(t.getEvent().equals("online") || t.getEvent().equals("init"))) {
                    triggersReturn.add(new TriggerDTO(t));
                }
            }
            outputs.add(new ContainerDTO(c, triggersReturn));
        }
        try {
            json = objectMapper.writeValueAsString(outputs);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
        }
        return json;
    }

    // Добавление контейнера
    @PostMapping(
            value = "",
            produces = "application/json"
    )
    @Transactional
    public String addContainer(@RequestBody ContainerPageDTO requestBody) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        Container newContainer = new Container(requestBody.getUserId(), requestBody.getUnitId(), requestBody.getContainerName());
        newContainer = containerRepository.save(newContainer);
        List<TriggerTemplateDTO> triggers = new ArrayList<>();
        List<TriggerDTO> resultTriggers = new ArrayList<>();

        for (TriggerDTO t : requestBody.getTriggers()){
            Trigger newTrigger = new Trigger(newContainer.getContainerId(), newContainer.getUnitId(), newContainer.getUserId(), t.getElement().getName(), t.getElement().getId(), t.getElement().getClassName(), t.getEvent());
            newTrigger = triggerRepository.save(newTrigger);
            triggers.add(new TriggerTemplateDTO(t, requestBody.getUnitId(), newContainer.getContainerId(), newTrigger.getTriggerId(), 0L));
            resultTriggers.add(new TriggerDTO(newTrigger));
        }

        Trigger newTriggerInit = new Trigger(newContainer.getContainerId(), newContainer.getUnitId(), newContainer.getUserId(), "", "", "", "init");
        newTriggerInit = triggerRepository.save(newTriggerInit);
        Long initId = newTriggerInit.getTriggerId();
        Trigger newTriggerOnline = new Trigger(newContainer.getContainerId(), newContainer.getUnitId(), newContainer.getUserId(), "", "", "", "online");
        newTriggerOnline = triggerRepository.save(newTriggerOnline);
        Long onlineId = newTriggerOnline.getTriggerId();

        String filePath = "src/main/resources/static/trigger" + newContainer.getContainerId().toString() + ".js";
        createLocalScripts(filePath, triggers, requestBody.getUnitId(), newContainer.getContainerId(), initId, onlineId);
        minioController.sendScripts(filePath, "scripts", "scripts_"+newContainer.getContainerId().toString(), true);
        deleteLocalScripts(filePath);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{}";
        json = objectMapper.writeValueAsString(new ContainerDTO(newContainer, resultTriggers));
        return json;
    }

    // Изменение контейнера
    @PutMapping(
            value = "/{container_id}",
            produces = "application/json"
    )
    @Transactional
    public String changeContainer(@PathVariable("container_id") Long containerId, @RequestBody ContainerPageDTO requestBody) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Optional<Container> searchResult = containerRepository.findById(containerId);
        Container result = searchResult.get();
        result.setContainerName(requestBody.getContainerName());
        containerRepository.save(result);
        List<TriggerTemplateDTO> triggers = new ArrayList<>();
        List<TriggerDTO> resultTriggers = new ArrayList<>();
        Set<Long> changedIds = new HashSet<>();

        Long initId = 0L;
        Long onlineId = 0L;

        for (TriggerDTO t : requestBody.getTriggers()){
            Trigger newTrigger;
            if (t.getTriggerId() != null){
                newTrigger = new Trigger(t.getTriggerId(), containerId, result.getUnitId(), result.getUserId(), t.getElement().getName(), t.getElement().getId(), t.getElement().getClassName(), t.getEvent());
            }
            else {
                newTrigger = new Trigger(containerId, result.getUnitId(), result.getUserId(), t.getElement().getName(), t.getElement().getId(), t.getElement().getClassName(), t.getEvent());
            }
            newTrigger = triggerRepository.save(newTrigger);
            changedIds.add(newTrigger.getTriggerId());
            resultTriggers.add(new TriggerDTO(newTrigger));
            if (!(newTrigger.getEvent().equals("online") || newTrigger.getEvent().equals("init"))) {
                triggers.add(new TriggerTemplateDTO(t, result.getUnitId(), containerId, newTrigger.getTriggerId(), 0L));
            }
        }

        List<Trigger> allTriggers = triggerRepository.findByContainerId(containerId);
        for (Trigger t : allTriggers){
            if (!changedIds.contains(t.getTriggerId())){
                if (!(t.getEvent().equals("online") || t.getEvent().equals("init"))) {
                    dataRepository.deleteByTriggerId(t.getTriggerId());
                    triggerRepository.deleteById(t.getTriggerId());
                }
                else{
                    if (t.getEvent().equals("init")){ initId = t.getTriggerId(); }
                    else{ onlineId = t.getTriggerId(); }
                }
            }
        }

        String filePath = "src/main/resources/static/trigger" + containerId.toString() + ".js";
        createLocalScripts(filePath, triggers, result.getUnitId(), containerId, initId, onlineId);
        minioController.sendScripts(filePath, "scripts", "scripts_"+containerId.toString(), true);
        deleteLocalScripts(filePath);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{}";
        json = objectMapper.writeValueAsString(new ContainerDTO(result, resultTriggers));
        return json;
    }

    // Удаление контейнера
    @DeleteMapping(
            value = "/{container_id}",
            produces = "application/json"
    )
    @Transactional
    public String deleteContainer(@PathVariable("container_id") Long containerId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        dataRepository.deleteByContainerId(containerId);
        triggerRepository.deleteByContainerId(containerId);
        Container container = containerRepository.findById(containerId).get();
        List<Container> containers = new LinkedList<>();
        containers.add(container);
        minioController.deleteMinIOScripts(containers, "scripts", "scripts_");
        containerRepository.deleteById(containerId);
        return "{ \"containerId\": " + containerId + " }";
    }

    // Создание файла скриптов для контейнера
    private void createLocalScripts(String filePath, List<TriggerTemplateDTO> triggers, Long unitId, Long containerId, Long initId, Long onlineId) throws IOException {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        Template template = velocityEngine.getTemplate("src/main/resources/templates/trigger_template.js");

        VelocityContext context = new VelocityContext();
        context.put("triggers", triggers);
        context.put("unitId", unitId);
        context.put("containerId", containerId);
        context.put("initId", initId);
        context.put("onlineId", onlineId);

        File saveFile = new File(filePath);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        FileOutputStream outStream = new FileOutputStream(saveFile);
        OutputStreamWriter writer = new OutputStreamWriter(outStream);
        BufferedWriter bufferWriter = new BufferedWriter(writer);

        template.merge(context, bufferWriter);

        bufferWriter.flush();
        outStream.close();
        bufferWriter.close();
    }

    // Удаление файла скриптов для контейнера
    private void deleteLocalScripts(String filePath){
        File file = new File(filePath);
        file.delete();
    }
}

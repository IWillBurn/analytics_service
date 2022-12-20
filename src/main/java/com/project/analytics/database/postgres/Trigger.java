package com.project.analytics.database.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.analytics.dto.request.UnitPageDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

@Data
@Entity
@AllArgsConstructor
@Table(name = "trigger")
public class Trigger {
    @Id
    @Column(name = "trigger_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long triggerId;

    @Column(name = "container_id")
    private Long containerId;
    @Column(name = "unit_id")
    private Long unitId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "web_name")
    private String webName;
    @Column(name = "web_id")
    private String webId;
    @Column(name = "web_class")
    private String webClass;
    @Column(name = "event")
    private String event;

    public Trigger() {
    }

    public Trigger(Long container, Long unit, Long user, String web_name, String web_id, String web_class, String web_event) {
        containerId = container;
        unitId = unit;
        userId = user;
        webName = web_name;
        webId = web_id;
        webClass = web_class;
        event = web_event;
    }
}
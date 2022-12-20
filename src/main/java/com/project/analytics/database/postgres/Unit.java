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
@Table(name = "unit")
@AllArgsConstructor
public class Unit {
    @Id
    @Column(name = "unit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long unitId;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "unit_name")
    private String unitName;

    public Unit() {
    }
    public Unit(Long user, String name) {
        userId = user;
        unitName = name;
    }

    public void changeUnit(UnitPageDTO dto){
        if (dto.getUnitName() != null){
            setUnitName(dto.getUnitName());
        }
    }
}


package com.project.analytics.dto.postgres;

import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import com.project.analytics.database.postgres.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnitDTO {
    private String unitName;

    private Long countOfContainers;
    private Long unitId;
    public UnitDTO(Unit unit, Long count){
        unitName = unit.getUnitName();
        unitId = unit.getUnitId();
        countOfContainers = count;
    }
}

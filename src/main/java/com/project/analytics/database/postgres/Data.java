package com.project.analytics.database.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@lombok.Data
@AllArgsConstructor
@Entity
@Table(name = "data")
public class Data {
    @Id
    @Column(name = "data_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dataId;

    @Column(name = "trigger_id")
    private Long triggerId;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "container_id")
    private Long containerId;

    @Column(name = "ASID")
    private Long ASID;

    @Column(name = "event")
    private String event;

    @Column(name = "date")
    private Timestamp date;

    public Data(Long tId, Long uId, Long cId, Long aId, String e, Timestamp d) {
        triggerId = tId;
        unitId = uId;
        containerId = cId;
        ASID = aId;
        event = e;
        date = d;
    }

    public Data() {
    }
}
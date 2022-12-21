package com.project.analytics.database.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<Data, Long>{

    void deleteByContainerId(Long containerId);
    void deleteByTriggerId(Long triggerId);
    void deleteByUnitId(Long unitId);
    List<Data> findByUnitIdOrderByDateDesc(Long unitId);
    @Query("SELECT COUNT(DISTINCT d.ASID) FROM Data d WHERE d.triggerId=?1 AND d.date>=?2 AND d.date<=?3")
    long countTriggeredDistinct(Long triggerId, Date dateFrom, Date dateTo);
    @Query("SELECT COUNT(d.ASID) FROM Data d WHERE d.triggerId=?1 AND d.date>=?2 AND d.date<=?3")
    long countByTrigger(Long triggerId, Date dateFrom, Date dateTo);

    @Query("SELECT DISTINCT d.ASID FROM Data d WHERE d.triggerId=?1 AND d.date>=?3 AND d.date<=?4 AND EXISTS (SELECT dd FROM Data dd WHERE dd.triggerId=?2 AND dd.date>=?3 AND dd.date<=?4 AND dd.date >= DATEADD(nanosecond, ?5, d.date) AND dd.date <= DATEADD(nanosecond, ?6, d.date) AND d.ASID = dd.ASID AND NOT (d.dataId = dd.dataId))")
    List<Long> dataEventBAfterEventA(Long triggerIdA, Long triggerIdB, Date dateFrom, Date dateTo, Long DeltaTimeMin, Long DeltaTimeMax);

    @Query("SELECT COUNT(DISTINCT d.ASID) FROM Data d WHERE d.triggerId =?1 AND DATEADD(nanosecond, 10000000000, d.date) >= ?2")
    Long countOnline(Long triggerId, Date currentDate);

    @Query("SELECT COUNT(DISTINCT d.ASID) FROM Data d WHERE d.unitId =?1 AND d.date >= ?2 AND d.date <= ?3")
    Long countAllInUnitData(Long unitId, Date dateFrom, Date DateTo);

    @Query("SELECT COUNT(DISTINCT d.ASID) FROM Data d WHERE d.unitId =?1 AND d.date >= ?2 AND d.date <= ?3 AND EXISTS (SELECT v FROM Visitor v WHERE v.ASID = d.ASID AND NOT v.firstName = null)")
    Long countEnrichedInUnitData(Long unitId, Date dateFrom, Date DateTo);
    @Query("SELECT COUNT(DISTINCT d.ASID) FROM Data d WHERE d.containerId =?1 AND d.date >= ?2 AND d.date <= ?3")
    Long countAllInContainerData(Long containerId, Date dateFrom, Date DateTo);

    @Query("SELECT COUNT(DISTINCT d.ASID) FROM Data d WHERE d.containerId =?1 AND d.date >= ?2 AND d.date <= ?3 AND EXISTS (SELECT v FROM Visitor v WHERE v.ASID = d.ASID AND NOT v.firstName = null)")
    Long countEnrichedInContainerData(Long containerId, Date dateFrom, Date DateTo);
}

package com.project.analytics.database.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByUnitName(String unitName);
    List<Unit> findByUserId(Long userId);
    List<Unit> findByUnitId(Long unitId);
}

package com.project.analytics.database.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriggerRepository extends JpaRepository<Trigger, Long> {
    List<Trigger> deleteByUnitId(Long id);
    List<Trigger> deleteByContainerId(Long id);
    List<Trigger> findByContainerId(Long id);
}

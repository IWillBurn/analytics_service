package com.project.analytics.database.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {
    List<Container> deleteByUnitId(Long id);
    List<Container> findByUserId(Long userId);
    List<Container> findByUnitId(Long unitId);

    Long countByUnitId(Long unitId);
}

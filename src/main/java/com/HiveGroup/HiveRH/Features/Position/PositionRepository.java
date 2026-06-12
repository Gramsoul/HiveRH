package com.HiveGroup.HiveRH.Features.Position;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionRepository extends JpaRepository<@NonNull PositionEntity, @NonNull Long> {
    @Query("""
            SELECT DISTINCT p
            FROM PositionEntity p
            JOIN p.assignments assignment
            WHERE assignment.department.id_department = :idDepartment
            """)
    List<PositionEntity> findDistinctByDepartmentId(@Param("idDepartment") Long idDepartment);
}

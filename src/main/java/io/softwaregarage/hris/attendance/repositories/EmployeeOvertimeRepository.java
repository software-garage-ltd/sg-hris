package io.softwaregarage.hris.attendance.repositories;

import io.softwaregarage.hris.attendance.entities.EmployeeOvertime;
import io.softwaregarage.hris.profile.entities.EmployeeProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EmployeeOvertimeRepository extends JpaRepository<EmployeeOvertime, UUID> {
    @Query("SELECT eo FROM EmployeeOvertime eo WHERE eo.employeeProfile = :param")
    List<EmployeeOvertime> findByEmployee(@Param("param") EmployeeProfile employeeProfile);

    @Query("SELECT eo FROM EmployeeOvertime eo WHERE eo.assignedApproverEmployee = :param")
    List<EmployeeOvertime> findByAssignedApproverEmployee(@Param("param") EmployeeProfile assignedApproverEmployee);

    @Query("""
           SELECT eo FROM EmployeeOvertime eo
           WHERE LOWER(eo.employeeProfile.firstName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(eo.employeeProfile.lastName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(eo.status) LIKE LOWER(CONCAT('%', :param, '%'))
           """)
    List<EmployeeOvertime> findByStringParameter(@Param("param") String parameter);
}

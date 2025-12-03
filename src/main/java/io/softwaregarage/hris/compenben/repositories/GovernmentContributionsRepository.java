package io.softwaregarage.hris.compenben.repositories;

import io.softwaregarage.hris.compenben.entities.GovernmentContributions;

import io.softwaregarage.hris.profile.entities.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GovernmentContributionsRepository extends JpaRepository<GovernmentContributions, UUID> {
    @Query("""
           SELECT gc FROM GovernmentContributions gc
           WHERE LOWER(gc.employeeProfile.firstName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(gc.employeeProfile.middleName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(gc.employeeProfile.lastName) LIKE LOWER(CONCAT('%', :param, '%'))
           """)
    List<GovernmentContributions> findByStringParameter(@Param("param") String param);

    @Query("""
           SELECT gc FROM GovernmentContributions gc
           WHERE gc.employeeProfile = :employee
           """)
    GovernmentContributions findByEmployee(@Param("employee") EmployeeProfile employeeProfile);
}

package io.softwaregarage.hris.compenben.repositories;

import io.softwaregarage.hris.compenben.entities.Allowance;
import io.softwaregarage.hris.profile.entities.EmployeeProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AllowanceRepository extends JpaRepository<Allowance, UUID> {
    @Query("""
           SELECT a FROM Allowance a
           WHERE LOWER(a.allowanceCode) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(a.allowanceType) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(a.employeeProfile.firstName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(a.employeeProfile.middleName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(a.employeeProfile.lastName) LIKE LOWER(CONCAT('%', :param, '%'))
           """)
    List<Allowance> findByStringParameter(@Param("param") String parameter);

    @Query("SELECT a FROM Allowance a WHERE a.employeeProfile = :param")
    List<Allowance> findAllowanceByEmployee(@Param("param") EmployeeProfile employeeProfile);
}

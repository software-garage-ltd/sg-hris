package io.softwaregarage.hris.payroll.repositories;

import io.softwaregarage.hris.payroll.entities.Payroll;
import io.softwaregarage.hris.profile.entities.EmployeeProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PayrollRepository extends JpaRepository<Payroll, UUID> {
    @Query("""
           SELECT p FROM Payroll p
           WHERE p.cutOffFromDate = :startDate
             AND p.cutOffToDate = :endDate
           ORDER BY p.employeeProfile.lastName, p.cutOffFromDate ASC
           """)
    List<Payroll> findPayrollByCutOffDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
           SELECT p FROM Payroll p
           WHERE p.employeeProfile = :employeeProfile
             AND p.cutOffFromDate = :startDate
             AND p.cutOffToDate = :endDate
           ORDER BY p.employeeProfile.lastName, p.cutOffFromDate ASC
           """)
    List<Payroll> findPayrollByEmployeeAndCutOffDates(@Param("employeeProfile") EmployeeProfile employeeProfile,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);
}

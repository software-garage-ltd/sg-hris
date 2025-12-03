package io.softwaregarage.hris.payroll.services;

import io.softwaregarage.hris.payroll.dtos.PayrollDTO;
import io.softwaregarage.hris.commons.BaseService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PayrollService extends BaseService<PayrollDTO> {
    @Transactional
    List<PayrollDTO> findPayrollDTOByCutOffDates(LocalDate startDate, LocalDate endDate);

    @Transactional
    List<PayrollDTO> findPayrollDTOByEmployeeProfileDTOAndCutOffFromDate(EmployeeProfileDTO employeeProfileDTO,
                                                                         LocalDate startDate,
                                                                         LocalDate endDate);
}

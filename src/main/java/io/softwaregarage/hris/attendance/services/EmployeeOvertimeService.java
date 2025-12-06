package io.softwaregarage.hris.attendance.services;

import io.softwaregarage.hris.attendance.dtos.EmployeeOvertimeDTO;
import io.softwaregarage.hris.commons.BaseService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeOvertimeService extends BaseService<EmployeeOvertimeDTO> {
    @Transactional
    List<EmployeeOvertimeDTO> findByEmployeeDTO(EmployeeProfileDTO employeeProfileDTO);

    @Transactional
    List<EmployeeOvertimeDTO> findByAssignedApproverEmployeeDTO(EmployeeProfileDTO assignedApproverEmployeeDTO);
}

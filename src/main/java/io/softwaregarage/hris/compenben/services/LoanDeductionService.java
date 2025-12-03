package io.softwaregarage.hris.compenben.services;

import io.softwaregarage.hris.compenben.dtos.LoanDeductionDTO;
import io.softwaregarage.hris.commons.BaseService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import java.util.List;

public interface LoanDeductionService extends BaseService<LoanDeductionDTO> {
    List<LoanDeductionDTO> findByEmployeeProfileDTO(EmployeeProfileDTO employeeProfileDTO);
}

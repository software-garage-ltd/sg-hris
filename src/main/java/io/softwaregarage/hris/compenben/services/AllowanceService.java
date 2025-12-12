package io.softwaregarage.hris.compenben.services;

import io.softwaregarage.hris.compenben.dtos.AllowanceDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.commons.BaseService;

import java.math.BigDecimal;

public interface AllowanceService extends BaseService<AllowanceDTO> {
    BigDecimal getSumOfTaxableAllowanceByEmployeeDTO(EmployeeProfileDTO employeeProfileDTO, int cutOffNumber);
    BigDecimal getSumOfNonTaxableAllowanceByEmployeeDTO(EmployeeProfileDTO employeeProfileDTO, int cutOffNumber);
}

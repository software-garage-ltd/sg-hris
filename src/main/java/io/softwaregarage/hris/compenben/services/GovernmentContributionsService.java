package io.softwaregarage.hris.compenben.services;

import io.softwaregarage.hris.compenben.dtos.GovernmentContributionsDTO;
import io.softwaregarage.hris.commons.BaseService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

public interface GovernmentContributionsService extends BaseService<GovernmentContributionsDTO> {
    GovernmentContributionsDTO findByEmployeeProfileDTO(EmployeeProfileDTO employeeProfileDTO);
}

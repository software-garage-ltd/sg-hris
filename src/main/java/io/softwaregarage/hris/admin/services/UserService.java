package io.softwaregarage.hris.admin.services;

import io.softwaregarage.hris.admin.dtos.UserDTO;
import io.softwaregarage.hris.commons.BaseService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import org.springframework.transaction.annotation.Transactional;

public interface UserService extends BaseService<UserDTO> {
    @Transactional
    UserDTO getByUsername(String username);

    @Transactional
    UserDTO getByEmployeeProfileDTO(EmployeeProfileDTO employeeProfileDTO);
}

package io.softwaregarage.hris.admin.services.impls;

import io.softwaregarage.hris.admin.dtos.UserDTO;
import io.softwaregarage.hris.admin.entities.User;
import io.softwaregarage.hris.admin.repositories.UserRepository;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.profile.repositories.EmployeeProfileRepository;
import io.softwaregarage.hris.admin.services.UserService;
import io.softwaregarage.hris.profile.services.impls.EmployeeProfileServiceImpl;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public UserServiceImpl(UserRepository userRepository,
                           EmployeeProfileRepository employeeProfileRepository) {
        this.userRepository = userRepository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    @Override
    public void saveOrUpdate(UserDTO object) {
        User user;
        String logMessage;

        if (object.getId() != null) {
            user = userRepository.getReferenceById(object.getId());
            logMessage = "User record with id ".concat(object.getId().toString()).concat(" is successfully updated.");
        } else {
            user = new User();
            user.setCreatedBy(object.getCreatedBy());
            user.setDateAndTimeCreated(LocalDateTime.now(ZoneId.of("Asia/Manila")));
            logMessage = "User record is successfully created.";
        }

        user.setEmployee(employeeProfileRepository.getReferenceById(object.getEmployeeDTO().getId()));
        user.setUsername(object.getUsername());
        user.setPassword(object.getPassword());
        user.setRole(object.getRole());
        user.setEmailAddress(object.getEmailAddress());
        user.setAccountActive(object.isAccountActive());
        user.setPasswordChanged(object.isPasswordChanged());
        user.setUpdatedBy(object.getUpdatedBy());
        user.setDateAndTimeUpdated(LocalDateTime.now(ZoneId.of("Asia/Manila")));

        userRepository.save(user);
        logger.info(logMessage);
    }

    @Override
    public UserDTO getById(UUID id) {
        logger.info("Retrieving user record with UUID ".concat(id.toString()));

        User user = userRepository.getReferenceById(id);
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setEmployeeDTO(new EmployeeProfileServiceImpl(employeeProfileRepository).getById(user.getEmployee().getId()));
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setEmailAddress(user.getEmailAddress());
        userDTO.setAccountActive(user.isAccountActive());
        userDTO.setPasswordChanged(user.isPasswordChanged());
        userDTO.setCreatedBy(user.getCreatedBy());
        userDTO.setDateAndTimeCreated(user.getDateAndTimeCreated());
        userDTO.setUpdatedBy(user.getUpdatedBy());
        userDTO.setDateAndTimeUpdated(user.getDateAndTimeUpdated());

        logger.info("User record with id ".concat(id.toString()).concat(" is successfully retrieved."));

        return userDTO;
    }

    @Override
    public void delete(UserDTO object) {
        if (object != null) {
            logger.warn("You are about to delete a user record permanently.");

            String id = object.getId().toString();
            User user = userRepository.getReferenceById(object.getId());
            userRepository.delete(user);

            logger.info("User record with id ".concat(id).concat(" is successfully deleted."));
        }
    }

    @Override
    public List<UserDTO> getAll(int page, int pageSize) {
        logger.info("Retrieving user records from the database.");
        List<User> userList = userRepository.findAll(PageRequest.of(page, pageSize)).stream().toList();

        logger.info("User records successfully retrieved.");
        List<UserDTO> userDTOList = new ArrayList<>();

        if (!userList.isEmpty()) {
            EmployeeProfileService employeeProfileService = new EmployeeProfileServiceImpl(employeeProfileRepository);

            for (User user : userList) {
                UserDTO userDTO = new UserDTO();

                userDTO.setId(user.getId());
                userDTO.setEmployeeDTO(employeeProfileService.getById(user.getEmployee().getId()));
                userDTO.setUsername(user.getUsername());
                userDTO.setPassword(user.getPassword());
                userDTO.setRole(user.getRole());
                userDTO.setEmailAddress(user.getEmailAddress());
                userDTO.setAccountActive(user.isAccountActive());
                userDTO.setPasswordChanged(user.isPasswordChanged());
                userDTO.setCreatedBy(user.getCreatedBy());
                userDTO.setDateAndTimeCreated(user.getDateAndTimeCreated());
                userDTO.setUpdatedBy(user.getUpdatedBy());
                userDTO.setDateAndTimeUpdated(user.getDateAndTimeUpdated());

                userDTOList.add(userDTO);
            }

            logger.info(String.valueOf(userList.size()).concat(" record(s) found."));
        }

        return userDTOList;
    }

    @Override
    public List<UserDTO> findByParameter(String param) {
        List<UserDTO> userDTOList = new ArrayList<>();
        List<User> userList = null;

        logger.info("Retrieving user records with search parameter '%".concat(param).concat("%' from the database."));

        if (param.equalsIgnoreCase("Yes") || param.equalsIgnoreCase("No")) {
            userList = userRepository.findByBooleanParameter(param.equalsIgnoreCase("Yes"));
        } else {
            userList = userRepository.findByStringParameter(param);
        }

        if (!userList.isEmpty()) {
            logger.info("User records with parameter '%".concat(param).concat("%' has successfully retrieved."));

            EmployeeProfileService employeeProfileService = new EmployeeProfileServiceImpl(employeeProfileRepository);

            for (User user : userList) {
                UserDTO userDTO = new UserDTO();

                userDTO.setId(user.getId());
                userDTO.setEmployeeDTO(employeeProfileService.getById(user.getEmployee().getId()));
                userDTO.setUsername(user.getUsername());
                userDTO.setPassword(user.getPassword());
                userDTO.setRole(user.getRole());
                userDTO.setEmailAddress(user.getEmailAddress());
                userDTO.setAccountActive(user.isAccountActive());
                userDTO.setPasswordChanged(user.isPasswordChanged());
                userDTO.setCreatedBy(user.getCreatedBy());
                userDTO.setDateAndTimeCreated(user.getDateAndTimeCreated());
                userDTO.setUpdatedBy(user.getUpdatedBy());
                userDTO.setDateAndTimeUpdated(user.getDateAndTimeUpdated());

                userDTOList.add(userDTO);
            }
        }

        return userDTOList;
    }

    @Override
    public UserDTO getByUsername(String username) {
        logger.info("Retrieving user record with username ".concat(username));

        User user = userRepository.findByUsername(username);
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setEmployeeDTO(new EmployeeProfileServiceImpl(employeeProfileRepository).getById(user.getEmployee().getId()));
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setEmailAddress(user.getEmailAddress());
        userDTO.setAccountActive(user.isAccountActive());
        userDTO.setPasswordChanged(user.isPasswordChanged());
        userDTO.setCreatedBy(user.getCreatedBy());
        userDTO.setDateAndTimeCreated(user.getDateAndTimeCreated());
        userDTO.setUpdatedBy(user.getUpdatedBy());
        userDTO.setDateAndTimeUpdated(user.getDateAndTimeUpdated());

        logger.info("User record with username ".concat(username).concat(" is successfully retrieved."));

        return userDTO;
    }

    @Override
    public UserDTO getByEmployeeProfileDTO(EmployeeProfileDTO employeeProfileDTO) {
        logger.info("Retrieving user record with employee ID ".concat(employeeProfileDTO.getEmployeeNumber()));

        User user = userRepository.findByEmployee(employeeProfileRepository
                .getReferenceById(employeeProfileDTO.getId()));
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setEmployeeDTO(new EmployeeProfileServiceImpl(employeeProfileRepository).getById(user.getEmployee().getId()));
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setEmailAddress(user.getEmailAddress());
        userDTO.setAccountActive(user.isAccountActive());
        userDTO.setPasswordChanged(user.isPasswordChanged());
        userDTO.setCreatedBy(user.getCreatedBy());
        userDTO.setDateAndTimeCreated(user.getDateAndTimeCreated());
        userDTO.setUpdatedBy(user.getUpdatedBy());
        userDTO.setDateAndTimeUpdated(user.getDateAndTimeUpdated());

        logger.info("User record with employee id ".concat(employeeProfileDTO.getEmployeeNumber().toString()).concat(" is successfully retrieved."));

        return userDTO;
    }
}

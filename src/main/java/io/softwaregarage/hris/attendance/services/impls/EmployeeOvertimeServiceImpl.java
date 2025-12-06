package io.softwaregarage.hris.attendance.services.impls;

import io.softwaregarage.hris.attendance.dtos.EmployeeOvertimeDTO;
import io.softwaregarage.hris.attendance.entities.EmployeeOvertime;
import io.softwaregarage.hris.attendance.repositories.EmployeeOvertimeRepository;
import io.softwaregarage.hris.attendance.services.EmployeeOvertimeService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.profile.repositories.EmployeeProfileRepository;
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
public class EmployeeOvertimeServiceImpl implements EmployeeOvertimeService {
    private final Logger logger = LoggerFactory.getLogger(EmployeeOvertimeService.class);
    private final EmployeeOvertimeRepository employeeOvertimeRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeProfileService employeeProfileService;

    public EmployeeOvertimeServiceImpl(EmployeeOvertimeRepository employeeOvertimeRepository,
                                       EmployeeProfileRepository employeeProfileRepository,
                                       EmployeeProfileService employeeProfileService) {
        this.employeeOvertimeRepository = employeeOvertimeRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeProfileService = employeeProfileService;
    }

    @Override
    public List<EmployeeOvertimeDTO> findByEmployeeDTO(EmployeeProfileDTO employeeProfileDTO) {
        logger.info("Retrieving employee overtime filings with UUID ".concat(employeeProfileDTO.getId().toString()).concat(" from the database."));

        List<EmployeeOvertimeDTO> employeeOvertimeDTOList = new ArrayList<>();
        List<EmployeeOvertime> employeeOvertimeList = employeeOvertimeRepository.findByEmployee(employeeProfileRepository.getReferenceById(employeeProfileDTO.getId()));

        if (!employeeOvertimeList.isEmpty()) {
            logger.info("Employee overtime filings with UUID ".concat(employeeProfileDTO.getId().toString()).concat(" has successfully retrieved."));

            for (EmployeeOvertime employeeOvertime : employeeOvertimeList) {
                EmployeeOvertimeDTO employeeOvertimeDTO = new EmployeeOvertimeDTO();

                employeeOvertimeDTO.setId(employeeOvertime.getId());
                employeeOvertimeDTO.setEmployeeProfile(employeeProfileService.getById(employeeOvertime.getEmployeeProfile().getId()));
                employeeOvertimeDTO.setOvertimeDate(employeeOvertime.getOvertimeDate());
                employeeOvertimeDTO.setOvertimeStartTime(employeeOvertime.getOvertimeStartTime());
                employeeOvertimeDTO.setOvertimeEndTime(employeeOvertime.getOvertimeEndTime());
                employeeOvertimeDTO.setOvertimeNumberOfHours(employeeOvertime.getOvertimeNumberOfHours());
                employeeOvertimeDTO.setStatus(employeeOvertime.getStatus());
                employeeOvertimeDTO.setAssignedApproverEmployeeDTO(employeeProfileService.getById(employeeOvertime.getAssignedApproverEmployee().getId()));
                employeeOvertimeDTO.setCreatedBy(employeeOvertime.getCreatedBy());
                employeeOvertimeDTO.setDateAndTimeCreated(employeeOvertime.getDateAndTimeCreated());
                employeeOvertimeDTO.setUpdatedBy(employeeOvertime.getUpdatedBy());
                employeeOvertimeDTO.setDateAndTimeUpdated(employeeOvertime.getDateAndTimeUpdated());

                employeeOvertimeDTOList.add(employeeOvertimeDTO);
            }

            logger.info(String.valueOf(employeeOvertimeList.size()).concat(" record(s) found."));
        }

        return employeeOvertimeDTOList;
    }

    @Override
    public List<EmployeeOvertimeDTO> findByAssignedApproverEmployeeDTO(EmployeeProfileDTO assignedApproverEmployeeDTO) {
        logger.info("Retrieving overtime filings for employee approver with UUID ".concat(assignedApproverEmployeeDTO.getId().toString()).concat(" from the database."));

        List<EmployeeOvertimeDTO> employeeOvertimeDTOList = new ArrayList<>();
        List<EmployeeOvertime> employeeOvertimeList = employeeOvertimeRepository.findByAssignedApproverEmployee(employeeProfileRepository.getReferenceById(assignedApproverEmployeeDTO.getId()));

        if (!employeeOvertimeList.isEmpty()) {
            logger.info("Overtime filings for employee approver with UUID ".concat(assignedApproverEmployeeDTO.getId().toString()).concat(" has successfully retrieved."));

            for (EmployeeOvertime employeeOvertime : employeeOvertimeList) {
                EmployeeOvertimeDTO employeeOvertimeDTO = new EmployeeOvertimeDTO();

                employeeOvertimeDTO.setId(employeeOvertime.getId());
                employeeOvertimeDTO.setEmployeeProfile(employeeProfileService.getById(employeeOvertime.getEmployeeProfile().getId()));
                employeeOvertimeDTO.setOvertimeDate(employeeOvertime.getOvertimeDate());
                employeeOvertimeDTO.setOvertimeStartTime(employeeOvertime.getOvertimeStartTime());
                employeeOvertimeDTO.setOvertimeEndTime(employeeOvertime.getOvertimeEndTime());
                employeeOvertimeDTO.setOvertimeNumberOfHours(employeeOvertime.getOvertimeNumberOfHours());
                employeeOvertimeDTO.setStatus(employeeOvertime.getStatus());
                employeeOvertimeDTO.setAssignedApproverEmployeeDTO(employeeProfileService.getById(employeeOvertime.getAssignedApproverEmployee().getId()));
                employeeOvertimeDTO.setCreatedBy(employeeOvertime.getCreatedBy());
                employeeOvertimeDTO.setDateAndTimeCreated(employeeOvertime.getDateAndTimeCreated());
                employeeOvertimeDTO.setUpdatedBy(employeeOvertime.getUpdatedBy());
                employeeOvertimeDTO.setDateAndTimeUpdated(employeeOvertime.getDateAndTimeUpdated());

                employeeOvertimeDTOList.add(employeeOvertimeDTO);
            }

            logger.info(String.valueOf(employeeOvertimeList.size()).concat(" record(s) found."));
        }

        return employeeOvertimeDTOList;
    }

    @Override
    public void saveOrUpdate(EmployeeOvertimeDTO object) {
        EmployeeOvertime employeeOvertime;
        String logMessage;

        if (object.getId() != null) {
            employeeOvertime = employeeOvertimeRepository.getReferenceById(object.getId());
            logMessage = "Employee's overtime filing record with id ".concat(object.getId().toString()).concat(" is successfully updated.");
        } else {
            employeeOvertime = new EmployeeOvertime();
            employeeOvertime.setCreatedBy(object.getCreatedBy());
            employeeOvertime.setDateAndTimeCreated(LocalDateTime.now(ZoneId.of("Asia/Manila")));
            logMessage = "Employee's overtime filing record is successfully created.";
        }

        employeeOvertime.setEmployeeProfile(employeeProfileRepository.getReferenceById(object.getEmployeeProfile().getId()));
        employeeOvertime.setAssignedApproverEmployee(employeeProfileRepository.getReferenceById(object.getAssignedApproverEmployeeDTO().getId()));
        employeeOvertime.setOvertimeDate(object.getOvertimeDate());
        employeeOvertime.setOvertimeStartTime(object.getOvertimeStartTime());
        employeeOvertime.setOvertimeEndTime(object.getOvertimeEndTime());
        employeeOvertime.setOvertimeNumberOfHours(object.getOvertimeNumberOfHours());
        employeeOvertime.setStatus(object.getStatus());
        employeeOvertime.setUpdatedBy(object.getUpdatedBy());
        employeeOvertime.setDateAndTimeUpdated(LocalDateTime.now(ZoneId.of("Asia/Manila")));

        employeeOvertimeRepository.save(employeeOvertime);
        logger.info(logMessage);
    }

    @Override
    public EmployeeOvertimeDTO getById(UUID id) {
        logger.info("Retrieving employee's overtime filing record with UUID ".concat(id.toString()));

        EmployeeOvertime employeeOvertime = employeeOvertimeRepository.getReferenceById(id);
        EmployeeOvertimeDTO employeeOvertimeDTO = new EmployeeOvertimeDTO();

        employeeOvertimeDTO.setId(employeeOvertime.getId());
        employeeOvertimeDTO.setEmployeeProfile(employeeProfileService.getById(employeeOvertime.getEmployeeProfile().getId()));
        employeeOvertimeDTO.setOvertimeDate(employeeOvertime.getOvertimeDate());
        employeeOvertimeDTO.setOvertimeStartTime(employeeOvertime.getOvertimeStartTime());
        employeeOvertimeDTO.setOvertimeEndTime(employeeOvertime.getOvertimeEndTime());
        employeeOvertimeDTO.setOvertimeNumberOfHours(employeeOvertime.getOvertimeNumberOfHours());
        employeeOvertimeDTO.setStatus(employeeOvertime.getStatus());
        employeeOvertimeDTO.setAssignedApproverEmployeeDTO(employeeProfileService.getById(employeeOvertime.getAssignedApproverEmployee().getId()));
        employeeOvertimeDTO.setCreatedBy(employeeOvertime.getCreatedBy());
        employeeOvertimeDTO.setDateAndTimeCreated(employeeOvertime.getDateAndTimeCreated());
        employeeOvertimeDTO.setUpdatedBy(employeeOvertime.getUpdatedBy());
        employeeOvertimeDTO.setDateAndTimeUpdated(employeeOvertime.getDateAndTimeUpdated());

        logger.info("Employee's overtime filing record with id ".concat(id.toString()).concat(" is successfully retrieved."));
        return employeeOvertimeDTO;
    }

    @Override
    public void delete(EmployeeOvertimeDTO object) {
        if (object != null) {
            logger.warn("You are about to delete the employee's overtime filing record permanently.");

            String id = object.getId().toString();
            EmployeeOvertime employeeOvertime = employeeOvertimeRepository.getReferenceById(object.getId());
            employeeOvertimeRepository.delete(employeeOvertime);

            logger.info("Employee's overtime filing record with id ".concat(id).concat(" is successfully deleted."));
        }
    }

    @Override
    public List<EmployeeOvertimeDTO> getAll(int page, int pageSize) {
        logger.info("Retrieving employee overtime filings from the database.");
        List<EmployeeOvertime> employeeOvertimeList = employeeOvertimeRepository.findAll(PageRequest.of(page, pageSize)).stream().toList();

        logger.info("Employee overtime filings successfully retrieved.");
        List<EmployeeOvertimeDTO> employeeOvertimeDTOList = new ArrayList<>();

        if (!employeeOvertimeList.isEmpty()) {
            for (EmployeeOvertime employeeOvertime : employeeOvertimeList) {
                EmployeeOvertimeDTO employeeOvertimeDTO = new EmployeeOvertimeDTO();

                employeeOvertimeDTO.setId(employeeOvertime.getId());
                employeeOvertimeDTO.setEmployeeProfile(employeeProfileService.getById(employeeOvertime.getEmployeeProfile().getId()));
                employeeOvertimeDTO.setOvertimeDate(employeeOvertime.getOvertimeDate());
                employeeOvertimeDTO.setOvertimeStartTime(employeeOvertime.getOvertimeStartTime());
                employeeOvertimeDTO.setOvertimeEndTime(employeeOvertime.getOvertimeEndTime());
                employeeOvertimeDTO.setOvertimeNumberOfHours(employeeOvertime.getOvertimeNumberOfHours());
                employeeOvertimeDTO.setStatus(employeeOvertime.getStatus());
                employeeOvertimeDTO.setAssignedApproverEmployeeDTO(employeeProfileService.getById(employeeOvertime.getAssignedApproverEmployee().getId()));
                employeeOvertimeDTO.setCreatedBy(employeeOvertime.getCreatedBy());
                employeeOvertimeDTO.setDateAndTimeCreated(employeeOvertime.getDateAndTimeCreated());
                employeeOvertimeDTO.setUpdatedBy(employeeOvertime.getUpdatedBy());
                employeeOvertimeDTO.setDateAndTimeUpdated(employeeOvertime.getDateAndTimeUpdated());

                employeeOvertimeDTOList.add(employeeOvertimeDTO);
            }

            logger.info(String.valueOf(employeeOvertimeList.size()).concat(" record(s) found."));
        }

        return employeeOvertimeDTOList;
    }

    @Override
    public List<EmployeeOvertimeDTO> findByParameter(String param) {
        logger.info("Retrieving employee overtime filings with search parameter '%".concat(param).concat("%' from the database."));

        List<EmployeeOvertimeDTO> employeeOvertimeDTOList = new ArrayList<>();
        List<EmployeeOvertime> employeeOvertimeList = employeeOvertimeRepository.findByStringParameter(param);

        if (!employeeOvertimeList.isEmpty()) {
            logger.info("Employee overtime filings with parameter '%".concat(param).concat("%' has successfully retrieved."));

            for (EmployeeOvertime employeeOvertime : employeeOvertimeList) {
                EmployeeOvertimeDTO employeeOvertimeDTO = new EmployeeOvertimeDTO();

                employeeOvertimeDTO.setId(employeeOvertime.getId());
                employeeOvertimeDTO.setEmployeeProfile(employeeProfileService.getById(employeeOvertime.getEmployeeProfile().getId()));
                employeeOvertimeDTO.setOvertimeDate(employeeOvertime.getOvertimeDate());
                employeeOvertimeDTO.setOvertimeStartTime(employeeOvertime.getOvertimeStartTime());
                employeeOvertimeDTO.setOvertimeEndTime(employeeOvertime.getOvertimeEndTime());
                employeeOvertimeDTO.setOvertimeNumberOfHours(employeeOvertime.getOvertimeNumberOfHours());
                employeeOvertimeDTO.setStatus(employeeOvertime.getStatus());
                employeeOvertimeDTO.setAssignedApproverEmployeeDTO(employeeProfileService.getById(employeeOvertime.getAssignedApproverEmployee().getId()));
                employeeOvertimeDTO.setCreatedBy(employeeOvertime.getCreatedBy());
                employeeOvertimeDTO.setDateAndTimeCreated(employeeOvertime.getDateAndTimeCreated());
                employeeOvertimeDTO.setUpdatedBy(employeeOvertime.getUpdatedBy());
                employeeOvertimeDTO.setDateAndTimeUpdated(employeeOvertime.getDateAndTimeUpdated());

                employeeOvertimeDTOList.add(employeeOvertimeDTO);
            }

            logger.info(String.valueOf(employeeOvertimeList.size()).concat(" record(s) found."));
        }

        return employeeOvertimeDTOList;
    }
}

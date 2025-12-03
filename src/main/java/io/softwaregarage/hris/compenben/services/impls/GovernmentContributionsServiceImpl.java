package io.softwaregarage.hris.compenben.services.impls;

import io.softwaregarage.hris.compenben.dtos.AllowanceDTO;
import io.softwaregarage.hris.compenben.dtos.GovernmentContributionsDTO;
import io.softwaregarage.hris.compenben.entities.GovernmentContributions;
import io.softwaregarage.hris.compenben.repositories.GovernmentContributionsRepository;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.profile.repositories.EmployeeProfileRepository;
import io.softwaregarage.hris.compenben.services.GovernmentContributionsService;
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
public class GovernmentContributionsServiceImpl implements GovernmentContributionsService {
    private final Logger logger = LoggerFactory.getLogger(GovernmentContributionsServiceImpl.class);

    private final GovernmentContributionsRepository governmentContributionsRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeProfileService employeeProfileService;

    public GovernmentContributionsServiceImpl(GovernmentContributionsRepository governmentContributionsRepository,
                                              EmployeeProfileRepository employeeProfileRepository,
                                              EmployeeProfileService employeeProfileService) {
        this.governmentContributionsRepository = governmentContributionsRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeProfileService = employeeProfileService;
    }


    @Override
    public void saveOrUpdate(GovernmentContributionsDTO object) {
        GovernmentContributions governmentContributions;
        String logMessage;

        if (object.getId() != null) {
            governmentContributions = governmentContributionsRepository.getReferenceById(object.getId());
            logMessage = "Employee's government contributions record with id ".concat(object.getId().toString()).concat(" is successfully updated.");
        } else {
            governmentContributions = new GovernmentContributions();
            governmentContributions.setCreatedBy(object.getCreatedBy());
            governmentContributions.setDateAndTimeCreated(LocalDateTime.now(ZoneId.of("Asia/Manila")));
            logMessage = "Employee's government contributions record is successfully created.";
        }

        governmentContributions.setSssContrbutionAmount(object.getSssContributionAmount());
        governmentContributions.setHdmfContrbutionAmount(object.getHdmfContributionAmount());
        governmentContributions.setPhilhealthContributionAmount(object.getPhilhealthContributionAmount());
        governmentContributions.setEmployee(employeeProfileRepository.getReferenceById(object.getEmployeeDTO().getId()));
        governmentContributions.setUpdatedBy(object.getUpdatedBy());
        governmentContributions.setDateAndTimeUpdated(LocalDateTime.now(ZoneId.of("Asia/Manila")));

        governmentContributionsRepository.save(governmentContributions);
        logger.info(logMessage);
    }

    @Override
    public GovernmentContributionsDTO getById(UUID id) {
        logger.info("Retrieving employee's government contributions record with UUID ".concat(id.toString()));

        GovernmentContributions governmentContributions = governmentContributionsRepository.getById(id);
        GovernmentContributionsDTO governmentContributionsDTO = new GovernmentContributionsDTO();

        governmentContributionsDTO.setId(governmentContributions.getId());
        governmentContributionsDTO.setSssContributionAmount(governmentContributions.getSssContrbutionAmount());
        governmentContributionsDTO.setHdmfContributionAmount(governmentContributions.getHdmfContrbutionAmount());
        governmentContributionsDTO.setPhilhealthContributionAmount(governmentContributions.getPhilhealthContributionAmount());
        governmentContributionsDTO.setEmployeeDTO(employeeProfileService.getById(governmentContributions.getEmployee().getId()));
        governmentContributionsDTO.setCreatedBy(governmentContributions.getCreatedBy());
        governmentContributionsDTO.setDateAndTimeCreated(governmentContributions.getDateAndTimeCreated());
        governmentContributionsDTO.setUpdatedBy(governmentContributions.getUpdatedBy());
        governmentContributionsDTO.setDateAndTimeUpdated(governmentContributions.getDateAndTimeUpdated());

        logger.info("Employee's government contriibutions record with id ".concat(id.toString()).concat(" is successfully retrieved."));
        return governmentContributionsDTO;
    }

    @Override
    public void delete(GovernmentContributionsDTO object) {
        if (object != null) {
            logger.warn("You are about to delete the employee's government contributions record permanently.");

            String id = object.getId().toString();
            GovernmentContributions governmentContributions = governmentContributionsRepository.getReferenceById(object.getId());
            governmentContributionsRepository.delete(governmentContributions);

            logger.info("Employee's allowance record with id ".concat(id).concat(" is successfully deleted."));
        }
    }

    @Override
    public List<GovernmentContributionsDTO> getAll(int page, int pageSize) {
        logger.info("Retrieving employee's government contributions from the database.");
        List<GovernmentContributions> governmentContributionsList = governmentContributionsRepository.findAll(PageRequest.of(page, pageSize)).stream().toList();

        logger.info("Employee's government contributions successfully retrieved.");
        List<GovernmentContributionsDTO> governmentContributionsDTOList = new ArrayList<>();

        if (!governmentContributionsList.isEmpty()) {
            for (GovernmentContributions governmentContributions : governmentContributionsList) {
                GovernmentContributionsDTO governmentContributionsDTO = new GovernmentContributionsDTO();

                governmentContributionsDTO.setId(governmentContributions.getId());
                governmentContributionsDTO.setSssContributionAmount(governmentContributions.getSssContrbutionAmount());
                governmentContributionsDTO.setHdmfContributionAmount(governmentContributions.getHdmfContrbutionAmount());
                governmentContributionsDTO.setPhilhealthContributionAmount(governmentContributions.getPhilhealthContributionAmount());
                governmentContributionsDTO.setEmployeeDTO(employeeProfileService.getById(governmentContributions.getEmployee().getId()));
                governmentContributionsDTO.setCreatedBy(governmentContributions.getCreatedBy());
                governmentContributionsDTO.setDateAndTimeCreated(governmentContributions.getDateAndTimeCreated());
                governmentContributionsDTO.setUpdatedBy(governmentContributions.getUpdatedBy());
                governmentContributionsDTO.setDateAndTimeUpdated(governmentContributions.getDateAndTimeUpdated());

                governmentContributionsDTOList.add(governmentContributionsDTO);
            }

            logger.info(String.valueOf(governmentContributionsList.size()).concat(" record(s) found."));
        }

        return governmentContributionsDTOList;
    }

    @Override
    public List<GovernmentContributionsDTO> findByParameter(String param) {
        logger.info("Retrieving employee's government contributions with search parameter '%".concat(param).concat("%' from the database."));

        List<GovernmentContributions> governmentContributionsList = governmentContributionsRepository.findByStringParameter(param);
        List<GovernmentContributionsDTO> governmentContributionsDTOList = new ArrayList<>();

        if (!governmentContributionsList.isEmpty()) {
            logger.info("Employee's government contributions with parameter '%".concat(param).concat("%' has successfully retrieved."));

            for (GovernmentContributions governmentContributions : governmentContributionsList) {
                AllowanceDTO allowanceDTO = new AllowanceDTO();

                GovernmentContributionsDTO governmentContributionsDTO = new GovernmentContributionsDTO();

                governmentContributionsDTO.setId(governmentContributions.getId());
                governmentContributionsDTO.setSssContributionAmount(governmentContributions.getSssContrbutionAmount());
                governmentContributionsDTO.setHdmfContributionAmount(governmentContributions.getHdmfContrbutionAmount());
                governmentContributionsDTO.setPhilhealthContributionAmount(governmentContributions.getPhilhealthContributionAmount());
                governmentContributionsDTO.setEmployeeDTO(employeeProfileService.getById(governmentContributions.getEmployee().getId()));
                governmentContributionsDTO.setCreatedBy(governmentContributions.getCreatedBy());
                governmentContributionsDTO.setDateAndTimeCreated(governmentContributions.getDateAndTimeCreated());
                governmentContributionsDTO.setUpdatedBy(governmentContributions.getUpdatedBy());
                governmentContributionsDTO.setDateAndTimeUpdated(governmentContributions.getDateAndTimeUpdated());

                governmentContributionsDTOList.add(governmentContributionsDTO);
            }

            logger.info(String.valueOf(governmentContributionsList.size()).concat(" record(s) found."));
        }

        return governmentContributionsDTOList;
    }

    @Override
    public GovernmentContributionsDTO findByEmployeeProfileDTO(EmployeeProfileDTO employeeProfileDTO) {
        logger.info("Retrieving employee's government contributions record with employee id "
                .concat(employeeProfileDTO.getId().toString()));

        GovernmentContributions governmentContributions = governmentContributionsRepository
                .findByEmployee(employeeProfileRepository.getReferenceById(employeeProfileDTO.getId()));
        GovernmentContributionsDTO governmentContributionsDTO = new GovernmentContributionsDTO();

        governmentContributionsDTO.setId(governmentContributions.getId());
        governmentContributionsDTO.setSssContributionAmount(governmentContributions.getSssContrbutionAmount());
        governmentContributionsDTO.setHdmfContributionAmount(governmentContributions.getHdmfContrbutionAmount());
        governmentContributionsDTO.setPhilhealthContributionAmount(governmentContributions.getPhilhealthContributionAmount());
        governmentContributionsDTO.setEmployeeDTO(employeeProfileService.getById(governmentContributions.getEmployee().getId()));
        governmentContributionsDTO.setCreatedBy(governmentContributions.getCreatedBy());
        governmentContributionsDTO.setDateAndTimeCreated(governmentContributions.getDateAndTimeCreated());
        governmentContributionsDTO.setUpdatedBy(governmentContributions.getUpdatedBy());
        governmentContributionsDTO.setDateAndTimeUpdated(governmentContributions.getDateAndTimeUpdated());

        logger.info("Government contriibutions record with employee id "
                .concat(employeeProfileDTO.getId().toString()).concat(" is successfully retrieved."));
        return governmentContributionsDTO;
    }
}

package io.softwaregarage.hris.compenben.services.impls;

import io.softwaregarage.hris.compenben.dtos.AllowanceDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.compenben.entities.Allowance;
import io.softwaregarage.hris.compenben.repositories.AllowanceRepository;
import io.softwaregarage.hris.profile.repositories.EmployeeProfileRepository;
import io.softwaregarage.hris.compenben.services.AllowanceService;
import io.softwaregarage.hris.profile.services.impls.EmployeeProfileServiceImpl;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AllowanceServiceImpl implements AllowanceService {
    private final Logger logger = LoggerFactory.getLogger(AllowanceServiceImpl.class);

    private final AllowanceRepository allowanceRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeProfileService employeeProfileService;

    public AllowanceServiceImpl(AllowanceRepository allowanceRepository,
                                EmployeeProfileRepository employeeProfileRepository,
                                EmployeeProfileService employeeProfileService) {
        this.allowanceRepository = allowanceRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeProfileService = employeeProfileService;
    }

    @Override
    public void saveOrUpdate(AllowanceDTO object) {
        Allowance allowance;
        String logMessage;

        if (object.getId() != null) {
            allowance = allowanceRepository.getReferenceById(object.getId());
            logMessage = "Employee's allowance record with id ".concat(object.getId().toString()).concat(" is successfully updated.");
        } else {
            allowance = new Allowance();
            allowance.setCreatedBy(object.getCreatedBy());
            allowance.setDateAndTimeCreated(LocalDateTime.now(ZoneId.of("Asia/Manila")));
            logMessage = "Employee's allowance record is successfully created.";
        }

        allowance.setAllowanceCode(object.getAllowanceCode());
        allowance.setAllowanceType(object.getAllowanceType());
        allowance.setAllowanceAmount(object.getAllowanceAmount());
        allowance.setTaxable(object.isTaxable());
        allowance.setAllowanceCutOff(object.getAllowanceCutOff());
        allowance.setEmployee(employeeProfileRepository.getReferenceById(object.getEmployeeDTO().getId()));
        allowance.setUpdatedBy(object.getUpdatedBy());
        allowance.setDateAndTimeUpdated(LocalDateTime.now(ZoneId.of("Asia/Manila")));

        allowanceRepository.save(allowance);
        logger.info(logMessage);
    }

    @Override
    public AllowanceDTO getById(UUID id) {
        logger.info("Retrieving employee's allowance record with UUID ".concat(id.toString()));

        Allowance allowance = allowanceRepository.getReferenceById(id);
        AllowanceDTO allowanceDTO = this.buildAllowanceDTO(allowance);

        logger.info("Employee's allowance record with id ".concat(id.toString()).concat(" is successfully retrieved."));
        return allowanceDTO;
    }

    @Override
    public void delete(AllowanceDTO object) {
        if (object != null) {
            logger.warn("You are about to delete the employee's allowance record permanently.");

            String id = object.getId().toString();
            Allowance allowance = allowanceRepository.getReferenceById(object.getId());
            allowanceRepository.delete(allowance);

            logger.info("Employee's allowance record with id ".concat(id).concat(" is successfully deleted."));
        }
    }

    @Override
    public List<AllowanceDTO> getAll(int page, int pageSize) {
        logger.info("Retrieving employee's allowance from the database.");
        List<Allowance> allowanceList = allowanceRepository.findAll(PageRequest.of(page, pageSize)).stream().toList();

        logger.info("Employee's allowance successfully retrieved.");
        List<AllowanceDTO> allowanceDTOList = new ArrayList<>();

        if (!allowanceList.isEmpty()) {
            EmployeeProfileService employeeProfileService = new EmployeeProfileServiceImpl(employeeProfileRepository);

            for (Allowance allowance : allowanceList) {
                allowanceDTOList.add(this.buildAllowanceDTO(allowance));
            }

            logger.info(String.valueOf(allowanceList.size()).concat(" record(s) found."));
        }

        return allowanceDTOList;
    }

    @Override
    public List<AllowanceDTO> findByParameter(String param) {
        logger.info("Retrieving employee's allowance with search parameter '%".concat(param).concat("%' from the database."));

        List<Allowance> allowanceList = allowanceRepository.findByStringParameter(param);
        List<AllowanceDTO> allowanceDTOList = new ArrayList<>();

        if (!allowanceList.isEmpty()) {
            logger.info("Employee's allowance with parameter '%".concat(param).concat("%' has successfully retrieved."));

            for (Allowance allowance : allowanceList) {
                allowanceDTOList.add(this.buildAllowanceDTO(allowance));
            }

            logger.info(String.valueOf(allowanceList.size()).concat(" record(s) found."));
        }

        return allowanceDTOList;
    }

    @Override
    public BigDecimal getSumOfTaxableAllowanceByEmployeeDTO(EmployeeProfileDTO employeeProfileDTO, int cutOffNumber) {
        return allowanceRepository.findAllowanceByEmployee(
                        employeeProfileRepository.getReferenceById(employeeProfileDTO.getId())
                ).stream()
                .filter(Allowance::isTaxable)
                .filter(allowance -> allowance.getAllowanceCutOff() != null
                        && allowance.getAllowanceCutOff().equals(cutOffNumber))
                .map(Allowance::getAllowanceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getSumOfNonTaxableAllowanceByEmployeeDTO(EmployeeProfileDTO employeeProfileDTO, int cutOffNumber) {
        return allowanceRepository.findAllowanceByEmployee(
                        employeeProfileRepository.getReferenceById(employeeProfileDTO.getId())
                ).stream()
                .filter(allowance -> !allowance.isTaxable())
                .filter(allowance -> allowance.getAllowanceCutOff() != null
                        && allowance.getAllowanceCutOff().equals(cutOffNumber))
                .map(Allowance::getAllowanceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }


    private AllowanceDTO buildAllowanceDTO(Allowance allowance) {
        AllowanceDTO allowanceDTO = new AllowanceDTO();

        allowanceDTO.setId(allowance.getId());
        allowanceDTO.setAllowanceCode(allowance.getAllowanceCode());
        allowanceDTO.setAllowanceType(allowance.getAllowanceType());
        allowanceDTO.setAllowanceAmount(allowance.getAllowanceAmount());
        allowanceDTO.setEmployeeDTO(employeeProfileService.getById(allowance.getEmployee().getId()));
        allowanceDTO.setTaxable(allowance.isTaxable());
        allowanceDTO.setAllowanceCutOff(allowance.getAllowanceCutOff());
        allowanceDTO.setCreatedBy(allowance.getCreatedBy());
        allowanceDTO.setDateAndTimeCreated(allowance.getDateAndTimeCreated());
        allowanceDTO.setUpdatedBy(allowance.getUpdatedBy());
        allowanceDTO.setDateAndTimeUpdated(allowance.getDateAndTimeUpdated());

        return allowanceDTO;
    }

}

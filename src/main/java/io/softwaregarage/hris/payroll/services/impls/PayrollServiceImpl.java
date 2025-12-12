package io.softwaregarage.hris.payroll.services.impls;

import io.softwaregarage.hris.payroll.dtos.PayrollDTO;
import io.softwaregarage.hris.payroll.entities.Payroll;
import io.softwaregarage.hris.payroll.repositories.PayrollRepository;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.profile.repositories.EmployeeProfileRepository;
import io.softwaregarage.hris.payroll.services.PayrollService;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PayrollServiceImpl implements PayrollService {
    private final Logger logger = LoggerFactory.getLogger(PayrollServiceImpl.class);
    private final PayrollRepository payrollRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeProfileService employeeProfileService;

    public PayrollServiceImpl(PayrollRepository payrollRepository,
                              EmployeeProfileRepository employeeProfileRepository,
                              EmployeeProfileService employeeProfileService) {
        this.payrollRepository = payrollRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeProfileService = employeeProfileService;
    }

    @Override
    public void saveOrUpdate(PayrollDTO object) {
        logger.info("Getting the employee payroll data transfer object.");
        logger.info("Preparing the employee payroll object to be saved in the database.");

        Payroll payroll;
        String logMessage;

        if (object.getId() != null) {
            payroll = payrollRepository.getReferenceById(object.getId());
            logMessage = "Employee payroll record with ID ".concat(object.getId().toString()).concat(" has successfully updated.");
        } else {
            payroll = new Payroll();
            payroll.setCreatedBy(object.getCreatedBy());
            payroll.setDateAndTimeCreated(LocalDateTime.now(ZoneId.of("Asia/Manila")));
            logMessage = "A new employee payroll record has successfully saved in the database.";
        }

        payroll.setEmployee(employeeProfileRepository.getReferenceById(object.getEmployeeDTO().getId()));
        payroll.setCutOffFromDate(object.getCutOffFromDate());
        payroll.setCutOffToDate(object.getCutOffToDate());
        payroll.setBasicPayAmount(object.getBasicPayAmount());
        payroll.setOvertimePayAmount(object.getOvertimePayAmount());
        payroll.setTaxableAllowancePayAmount(object.getTaxableAllowancePayAmount());
        payroll.setNonTaxableAllowancePayAmount(object.getNonTaxableAllowancePayAmount());
        payroll.setAbsentDeductionAmount(object.getAbsentDeductionAmount());
        payroll.setLateOrUndertimeDeductionAmount(object.getLateOrUndertimeDeductionAmount());
        payroll.setRestDayPayAmount(object.getRestDayPayAmount());
        payroll.setNightDifferentialPayAmount(object.getNightDifferentialPayAmount());
        payroll.setLeavePayAmount(object.getLeavePayAmount());
        payroll.setRegularHolidayPayAmount(object.getRegularHolidayPayAmount());
        payroll.setSpecialHolidayPayAmount(object.getSpecialHolidayPayAmount());
        payroll.setSpecialNonWorkingHolidayPayAmount(object.getSpecialNonWorkingHolidayPayAmount());
        payroll.setAdjustmentPayAmount(object.getAdjustmentPayAmount());
        payroll.setTotalGrossPayAmount(object.getTotalGrossPayAmount());
        payroll.setSssDeductionAmount(object.getSssDeductionAmount());
        payroll.setHdmfDeductionAmount(object.getHdmfDeductionAmount());
        payroll.setPhilhealthDeductionAmount(object.getPhilhealthDeductionAmount());
        payroll.setWithholdingTaxDeductionAmount(object.getWithholdingTaxDeductionAmount());
        payroll.setTotalLoanDeductionAmount(object.getTotalLoanDeductionAmount());
        payroll.setOtherDeductionAmount(object.getOtherDeductionAmount());
        payroll.setTotalDeductionAmount(object.getTotalDeductionAmount());
        payroll.setUpdatedBy(object.getUpdatedBy());
        payroll.setDateAndTimeUpdated(LocalDateTime.now(ZoneId.of("Asia/Manila")));

        payrollRepository.save(payroll);
        logger.info(logMessage);
    }

    @Override
    public PayrollDTO getById(UUID id) {
        logger.info("Getting employee payroll record with ID ".concat(id.toString()).concat(" from the database."));
        Payroll payroll = payrollRepository.getReferenceById(id);

        logger.info("Employee record with ID ".concat(id.toString()).concat(" has successfully retrieved."));
        PayrollDTO payrollDTO = this.buildPayrollDTO(payroll);

        logger.info("Employee payroll data transfer object has successfully returned.");
        return payrollDTO;
    }

    @Override
    public void delete(PayrollDTO object) {
        logger.warn("You are about to delete an employee payroll record. Doing this will permanently erase in the database.");

        Payroll payroll = payrollRepository.getReferenceById(object.getId());
        payrollRepository.delete(payroll);

        logger.info("Employee payroll record with ID ".concat(object.getId().toString()).concat(" has successfully deleted in the database."));
    }

    @Override
    public List<PayrollDTO> getAll(int page, int pageSize) {
        logger.info("Retrieving employee payroll records from the database.");
        List<Payroll> payrollList = payrollRepository.findAll(PageRequest.of(page, pageSize)).stream().toList();

        logger.info("Employee payroll records successfully retrieved.");
        List<PayrollDTO> payrollDTOList = new ArrayList<>();

        if (!payrollList.isEmpty()) {
            for (Payroll payroll : payrollList) {
                payrollDTOList.add(this.buildPayrollDTO(payroll));
            }

            logger.info(String.valueOf(payrollList.size()).concat(" record(s) found."));
        }

        return payrollDTOList;
    }

    @Override
    public List<PayrollDTO> findByParameter(String param) {
        return List.of();
    }

    @Override
    public List<PayrollDTO> findPayrollDTOByCutOffDates(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        logger.info("Retrieving payrolls between "
                + startDate.format(dateTimeFormatter)
                + " and "
                + endDate.format(dateTimeFormatter)
                + " records from the database.");
        List<Payroll> payrollList = payrollRepository.findPayrollByCutOffDates(startDate, endDate);

        logger.info("Payroll records successfully retrieved.");
        List<PayrollDTO> payrollDTOList = new ArrayList<>();

        if (!payrollList.isEmpty()) {
            for (Payroll payroll : payrollList) {
                payrollDTOList.add(this.buildPayrollDTO(payroll));
            }

            logger.info(String.valueOf(payrollList.size()).concat(" record(s) found."));
        }

        return payrollDTOList;
    }

    @Override
    public List<PayrollDTO> findPayrollDTOByEmployeeProfileDTOAndCutOffFromDate(EmployeeProfileDTO employeeProfileDTO, LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        logger.info("Retrieving payrolls with employee id " + employeeProfileDTO.getId().toString()
                + " and cut-off dates between "
                + startDate.format(dateTimeFormatter)
                + " and "
                + endDate.format(dateTimeFormatter)
                + " records from the database.");
        List<Payroll> payrollList = payrollRepository.findPayrollByCutOffDates(startDate, endDate);

        logger.info("Payroll records successfully retrieved.");
        List<PayrollDTO> payrollDTOList = new ArrayList<>();

        if (!payrollList.isEmpty()) {
            for (Payroll payroll : payrollList) {
                payrollDTOList.add(this.buildPayrollDTO(payroll));
            }

            logger.info(String.valueOf(payrollList.size()).concat(" record(s) found."));
        }

        return payrollDTOList;
    }

    private PayrollDTO buildPayrollDTO(Payroll payroll) {
        PayrollDTO payrollDTO = new PayrollDTO();

        payrollDTO.setId(payroll.getId());
        payrollDTO.setEmployeeDTO(employeeProfileService.getById(payroll.getEmployee().getId()));
        payrollDTO.setCutOffFromDate(payroll.getCutOffFromDate());
        payrollDTO.setCutOffToDate(payroll.getCutOffToDate());
        payrollDTO.setBasicPayAmount(payroll.getBasicPayAmount());
        payrollDTO.setOvertimePayAmount(payroll.getOvertimePayAmount());
        payrollDTO.setTaxableAllowancePayAmount(payroll.getTaxableAllowancePayAmount());
        payrollDTO.setNonTaxableAllowancePayAmount(payroll.getNonTaxableAllowancePayAmount());
        payrollDTO.setAbsentDeductionAmount(payroll.getAbsentDeductionAmount());
        payrollDTO.setLateOrUndertimeDeductionAmount(payroll.getLateOrUndertimeDeductionAmount());
        payrollDTO.setRestDayPayAmount(payroll.getRestDayPayAmount());
        payrollDTO.setNightDifferentialPayAmount(payroll.getNightDifferentialPayAmount());
        payrollDTO.setLeavePayAmount(payroll.getLeavePayAmount());
        payrollDTO.setRegularHolidayPayAmount(payroll.getRegularHolidayPayAmount());
        payrollDTO.setSpecialHolidayPayAmount(payroll.getSpecialHolidayPayAmount());
        payrollDTO.setSpecialNonWorkingHolidayPayAmount(payroll.getSpecialNonWorkingHolidayPayAmount());
        payrollDTO.setAdjustmentPayAmount(payroll.getAdjustmentPayAmount());
        payrollDTO.setTotalGrossPayAmount(payroll.getTotalGrossPayAmount());
        payrollDTO.setSssDeductionAmount(payroll.getSssDeductionAmount());
        payrollDTO.setHdmfDeductionAmount(payroll.getHdmfDeductionAmount());
        payrollDTO.setPhilhealthDeductionAmount(payroll.getPhilhealthDeductionAmount());
        payrollDTO.setWithholdingTaxDeductionAmount(payroll.getWithholdingTaxDeductionAmount());
        payrollDTO.setTotalLoanDeductionAmount(payroll.getTotalLoanDeductionAmount());
        payrollDTO.setOtherDeductionAmount(payroll.getOtherDeductionAmount());
        payrollDTO.setTotalDeductionAmount(payroll.getTotalDeductionAmount());
        payrollDTO.setCreatedBy(payroll.getCreatedBy());
        payrollDTO.setDateAndTimeCreated(payroll.getDateAndTimeCreated());
        payrollDTO.setUpdatedBy(payroll.getUpdatedBy());
        payrollDTO.setDateAndTimeUpdated(payroll.getDateAndTimeUpdated());

        return payrollDTO;
    }
}

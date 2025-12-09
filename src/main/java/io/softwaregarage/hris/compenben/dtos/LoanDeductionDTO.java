package io.softwaregarage.hris.compenben.dtos;

import io.softwaregarage.hris.commons.BaseDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanDeductionDTO extends BaseDTO {
    private EmployeeProfileDTO employeeProfileDTO;
    private String loanType;
    private String loanDescription;
    private BigDecimal loanAmount;
    private LocalDate loanStartDate;
    private LocalDate loanEndDate;
    private BigDecimal monthlyDeduction;
    private Integer loanCutOff;

    public EmployeeProfileDTO getEmployeeDTO() {
        return employeeProfileDTO;
    }

    public void setEmployeeDTO(EmployeeProfileDTO employeeProfileDTO) {
        this.employeeProfileDTO = employeeProfileDTO;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLoanDescription() {
        return loanDescription;
    }

    public void setLoanDescription(String loanDescription) {
        this.loanDescription = loanDescription;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public LocalDate getLoanStartDate() {
        return loanStartDate;
    }

    public void setLoanStartDate(LocalDate loanStartDate) {
        this.loanStartDate = loanStartDate;
    }

    public LocalDate getLoanEndDate() {
        return loanEndDate;
    }

    public void setLoanEndDate(LocalDate loanEndDate) {
        this.loanEndDate = loanEndDate;
    }

    public BigDecimal getMonthlyDeduction() {
        return monthlyDeduction;
    }

    public void setMonthlyDeduction(BigDecimal monthlyDeduction) {
        this.monthlyDeduction = monthlyDeduction;
    }

    public Integer getLoanCutOff() {
        return loanCutOff;
    }

    public void setLoanCutOff(Integer loanCutOff) {
        this.loanCutOff = loanCutOff;
    }
}

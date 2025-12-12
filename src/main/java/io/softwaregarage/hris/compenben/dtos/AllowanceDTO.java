package io.softwaregarage.hris.compenben.dtos;

import io.softwaregarage.hris.commons.BaseDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import java.math.BigDecimal;

public class AllowanceDTO extends BaseDTO {
    private String allowanceCode;
    private String allowanceType;
    private BigDecimal allowanceAmount;
    private EmployeeProfileDTO employeeProfileDTO;
    private boolean taxable;
    private Integer allowanceCutOff;

    public String getAllowanceCode() {
        return allowanceCode;
    }

    public void setAllowanceCode(String allowanceCode) {
        this.allowanceCode = allowanceCode;
    }

    public String getAllowanceType() {
        return allowanceType;
    }

    public void setAllowanceType(String allowanceType) {
        this.allowanceType = allowanceType;
    }

    public BigDecimal getAllowanceAmount() {
        return allowanceAmount;
    }

    public void setAllowanceAmount(BigDecimal allowanceAmount) {
        this.allowanceAmount = allowanceAmount;
    }

    public EmployeeProfileDTO getEmployeeDTO() {
        return employeeProfileDTO;
    }

    public void setEmployeeDTO(EmployeeProfileDTO employeeProfileDTO) {
        this.employeeProfileDTO = employeeProfileDTO;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    public Integer getAllowanceCutOff() {
        return allowanceCutOff;
    }

    public void setAllowanceCutOff(Integer allowanceCutOff) {
        this.allowanceCutOff = allowanceCutOff;
    }
}

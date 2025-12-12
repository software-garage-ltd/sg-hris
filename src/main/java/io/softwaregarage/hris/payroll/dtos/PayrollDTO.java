package io.softwaregarage.hris.payroll.dtos;

import io.softwaregarage.hris.commons.BaseDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollDTO extends BaseDTO {
    private EmployeeProfileDTO employeeProfileDTO;
    private LocalDate cutOffFromDate;
    private LocalDate cutOffToDate;
    private BigDecimal basicPayAmount;
    private BigDecimal overtimePayAmount;
    private BigDecimal taxableAllowancePayAmount;
    private BigDecimal nonTaxableAllowancePayAmount;
    private BigDecimal absentDeductionAmount;
    private BigDecimal lateOrUndertimeDeductionAmount;
    private BigDecimal restDayPayAmount;
    private BigDecimal nightDifferentialPayAmount;
    private BigDecimal leavePayAmount;
    private BigDecimal regularHolidayPayAmount;
    private BigDecimal specialHolidayPayAmount;
    private BigDecimal specialNonWorkingHolidayPayAmount;
    private BigDecimal adjustmentPayAmount;
    private BigDecimal totalGrossPayAmount;
    private BigDecimal sssDeductionAmount;
    private BigDecimal hdmfDeductionAmount;
    private BigDecimal philhealthDeductionAmount;
    private BigDecimal withholdingTaxDeductionAmount;
    private BigDecimal totalLoanDeductionAmount;
    private BigDecimal otherDeductionAmount;
    private BigDecimal totalDeductionAmount;

    public EmployeeProfileDTO getEmployeeDTO() {
        return employeeProfileDTO;
    }

    public void setEmployeeDTO(EmployeeProfileDTO employeeProfileDTO) {
        this.employeeProfileDTO = employeeProfileDTO;
    }

    public LocalDate getCutOffFromDate() {
        return cutOffFromDate;
    }

    public void setCutOffFromDate(LocalDate cutOffFromDate) {
        this.cutOffFromDate = cutOffFromDate;
    }

    public LocalDate getCutOffToDate() {
        return cutOffToDate;
    }

    public void setCutOffToDate(LocalDate cutOffToDate) {
        this.cutOffToDate = cutOffToDate;
    }

    public BigDecimal getBasicPayAmount() {
        return basicPayAmount;
    }

    public void setBasicPayAmount(BigDecimal basicPayAmount) {
        this.basicPayAmount = basicPayAmount;
    }

    public BigDecimal getOvertimePayAmount() {
        return overtimePayAmount;
    }

    public void setOvertimePayAmount(BigDecimal overtimePayAmount) {
        this.overtimePayAmount = overtimePayAmount;
    }

    public BigDecimal getTaxableAllowancePayAmount() {
        return taxableAllowancePayAmount;
    }

    public void setTaxableAllowancePayAmount(BigDecimal taxableAllowancePayAmount) {
        this.taxableAllowancePayAmount = taxableAllowancePayAmount;
    }

    public BigDecimal getNonTaxableAllowancePayAmount() {
        return nonTaxableAllowancePayAmount;
    }

    public void setNonTaxableAllowancePayAmount(BigDecimal nonTaxableAllowancePayAmount) {
        this.nonTaxableAllowancePayAmount = nonTaxableAllowancePayAmount;
    }

    public BigDecimal getAbsentDeductionAmount() {
        return absentDeductionAmount;
    }

    public void setAbsentDeductionAmount(BigDecimal absentDeductionAmount) {
        this.absentDeductionAmount = absentDeductionAmount;
    }

    public BigDecimal getLateOrUndertimeDeductionAmount() {
        return lateOrUndertimeDeductionAmount;
    }

    public void setLateOrUndertimeDeductionAmount(BigDecimal lateOrUndertimeDeductionAmount) {
        this.lateOrUndertimeDeductionAmount = lateOrUndertimeDeductionAmount;
    }

    public BigDecimal getRestDayPayAmount() {
        return restDayPayAmount;
    }

    public void setRestDayPayAmount(BigDecimal restDayPayAmount) {
        this.restDayPayAmount = restDayPayAmount;
    }

    public BigDecimal getNightDifferentialPayAmount() {
        return nightDifferentialPayAmount;
    }

    public void setNightDifferentialPayAmount(BigDecimal nightDifferentialPayAmount) {
        this.nightDifferentialPayAmount = nightDifferentialPayAmount;
    }

    public BigDecimal getLeavePayAmount() {
        return leavePayAmount;
    }

    public void setLeavePayAmount(BigDecimal leavePayAmount) {
        this.leavePayAmount = leavePayAmount;
    }

    public BigDecimal getRegularHolidayPayAmount() {
        return regularHolidayPayAmount;
    }

    public void setRegularHolidayPayAmount(BigDecimal regularHolidayPayAmount) {
        this.regularHolidayPayAmount = regularHolidayPayAmount;
    }

    public BigDecimal getSpecialHolidayPayAmount() {
        return specialHolidayPayAmount;
    }

    public void setSpecialHolidayPayAmount(BigDecimal specialHolidayPayAmount) {
        this.specialHolidayPayAmount = specialHolidayPayAmount;
    }

    public BigDecimal getSpecialNonWorkingHolidayPayAmount() {
        return specialNonWorkingHolidayPayAmount;
    }

    public void setSpecialNonWorkingHolidayPayAmount(BigDecimal specialNonWorkingHolidayPayAmount) {
        this.specialNonWorkingHolidayPayAmount = specialNonWorkingHolidayPayAmount;
    }

    public BigDecimal getAdjustmentPayAmount() {
        return adjustmentPayAmount;
    }

    public void setAdjustmentPayAmount(BigDecimal adjustmentPayAmount) {
        this.adjustmentPayAmount = adjustmentPayAmount;
    }

    public BigDecimal getTotalGrossPayAmount() {
        return totalGrossPayAmount;
    }

    public void setTotalGrossPayAmount(BigDecimal totalGrossPayAmount) {
        this.totalGrossPayAmount = totalGrossPayAmount;
    }

    public BigDecimal getSssDeductionAmount() {
        return sssDeductionAmount;
    }

    public void setSssDeductionAmount(BigDecimal sssDeductionAmount) {
        this.sssDeductionAmount = sssDeductionAmount;
    }

    public BigDecimal getHdmfDeductionAmount() {
        return hdmfDeductionAmount;
    }

    public void setHdmfDeductionAmount(BigDecimal hdmfDeductionAmount) {
        this.hdmfDeductionAmount = hdmfDeductionAmount;
    }

    public BigDecimal getPhilhealthDeductionAmount() {
        return philhealthDeductionAmount;
    }

    public void setPhilhealthDeductionAmount(BigDecimal philhealthDeductionAmount) {
        this.philhealthDeductionAmount = philhealthDeductionAmount;
    }

    public BigDecimal getWithholdingTaxDeductionAmount() {
        return withholdingTaxDeductionAmount;
    }

    public void setWithholdingTaxDeductionAmount(BigDecimal withholdingTaxDeductionAmount) {
        this.withholdingTaxDeductionAmount = withholdingTaxDeductionAmount;
    }

    public BigDecimal getTotalLoanDeductionAmount() {
        return totalLoanDeductionAmount;
    }

    public void setTotalLoanDeductionAmount(BigDecimal totalLoanDeductionAmount) {
        this.totalLoanDeductionAmount = totalLoanDeductionAmount;
    }

    public BigDecimal getOtherDeductionAmount() {
        return otherDeductionAmount;
    }

    public void setOtherDeductionAmount(BigDecimal otherDeductionAmount) {
        this.otherDeductionAmount = otherDeductionAmount;
    }

    public BigDecimal getTotalDeductionAmount() {
        return totalDeductionAmount;
    }

    public void setTotalDeductionAmount(BigDecimal totalDeductionAmount) {
        this.totalDeductionAmount = totalDeductionAmount;
    }
}

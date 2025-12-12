package io.softwaregarage.hris.payroll.entities;

import io.softwaregarage.hris.commons.BaseEntity;
import io.softwaregarage.hris.profile.entities.EmployeeProfile;

import jakarta.persistence.*;

import java.math.BigDecimal;

import java.time.LocalDate;

@Entity
@Table(name = "sg_hris_employee_payroll")
public class Payroll extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private EmployeeProfile employeeProfile;

    @Column(name = "cut_off_from_date", nullable = false)
    private LocalDate cutOffFromDate;

    @Column(name = "cut_off_to_date", nullable = false)
    private LocalDate cutOffToDate;

    @Column(name = "basic_pay_amount", nullable = false)
    private BigDecimal basicPayAmount;

    @Column(name = "overtime_pay_amount", nullable = false)
    private BigDecimal overtimePayAmount;

    @Column(name = "taxable_allowance_pay_amount", nullable = false)
    private BigDecimal taxableAllowancePayAmount;

    @Column(name = "non_taxable_allowance_pay_amount", nullable = false)
    private BigDecimal nonTaxableAllowancePayAmount;

    @Column(name = "absent_deduction_amount", nullable = false)
    private BigDecimal absentDeductionAmount;

    @Column(name = "late_or_undertime_deduction_amount", nullable = false)
    private BigDecimal lateOrUndertimeDeductionAmount;

    @Column(name = "rest_day_pay_amount", nullable = false)
    private BigDecimal restDayPayAmount;

    @Column(name = "night_differential_pay_amount", nullable = false)
    private BigDecimal nightDifferentialPayAmount;

    @Column(name = "leave_pay_amount", nullable = false)
    private BigDecimal leavePayAmount;

    @Column(name = "regular_holiday_pay_amount", nullable = false)
    private BigDecimal regularHolidayPayAmount;

    @Column(name = "special_holiday_pay_amount", nullable = false)
    private BigDecimal specialHolidayPayAmount;

    @Column(name = "special_non_working_holiday_pay_amount", nullable = false)
    private BigDecimal specialNonWorkingHolidayPayAmount;

    @Column(name = "adjustment_pay_amount", nullable = false)
    private BigDecimal adjustmentPayAmount;

    @Column(name = "total_gross_pay_amount", nullable = false)
    private BigDecimal totalGrossPayAmount;

    @Column(name = "sss_deduction_amount", nullable = false)
    private BigDecimal sssDeductionAmount;

    @Column(name = "hdmf_deduction_amount", nullable = false)
    private BigDecimal hdmfDeductionAmount;

    @Column(name = "philhealth_deduction_amount", nullable = false)
    private BigDecimal philhealthDeductionAmount;

    @Column(name = "withholding_tax_deduction_amount", nullable = false)
    private BigDecimal withholdingTaxDeductionAmount;

    @Column(name = "total_loan_deduction_amount", nullable = false)
    private BigDecimal totalLoanDeductionAmount;

    @Column(name = "other_deduction_amount", nullable = false)
    private BigDecimal otherDeductionAmount;

    @Column(name = "total_deduction_amount", nullable = false)
    private BigDecimal totalDeductionAmount;

    public EmployeeProfile getEmployee() {
        return employeeProfile;
    }

    public void setEmployee(EmployeeProfile employeeProfile) {
        this.employeeProfile = employeeProfile;
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

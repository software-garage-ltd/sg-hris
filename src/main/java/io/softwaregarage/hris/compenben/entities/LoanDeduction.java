package io.softwaregarage.hris.compenben.entities;

import io.softwaregarage.hris.commons.BaseEntity;
import io.softwaregarage.hris.profile.entities.EmployeeProfile;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sg_hris_loan_deduction")
public class LoanDeduction extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private EmployeeProfile employeeProfile;

    @Column(name = "loan_type", length = 100, nullable = false)
    private String loanType;

    @Column(name = "loan_description", length = 150, nullable = false)
    private String loanDescription;

    @Column(name = "loan_amount", nullable = false)
    private BigDecimal loanAmount;

    @Column(name = "loan_start_date", nullable = false)
    private LocalDate loanStartDate;

    @Column(name = "loan_end_date", nullable = false)
    private LocalDate loanEndDate;

    @Column(name = "monthly_deduction", nullable = false)
    private BigDecimal monthlyDeduction;

    @Column(name = "loan_cutoff", nullable = false)
    private Integer loanCutOff;

    public EmployeeProfile getEmployee() {
        return employeeProfile;
    }

    public void setEmployee(EmployeeProfile employeeProfile) {
        this.employeeProfile = employeeProfile;
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

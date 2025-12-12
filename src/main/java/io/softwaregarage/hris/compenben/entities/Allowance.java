package io.softwaregarage.hris.compenben.entities;

import io.softwaregarage.hris.commons.BaseEntity;
import io.softwaregarage.hris.profile.entities.EmployeeProfile;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sg_hris_allowance_benefits")
public class Allowance extends BaseEntity {
    @Column(name = "allowance_code", length = 50, nullable = false, unique = true)
    private String allowanceCode;

    @Column(name = "allowance_type", length = 50, nullable = false)
    private String allowanceType;

    @Column(name = "allowance_amount", nullable = false)
    private BigDecimal allowanceAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private EmployeeProfile employeeProfile;

    @Column(name = "allowance_is_taxable", nullable = false)
    private boolean taxable;

    @Column(name = "allowance_cut_off", nullable = false)
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

    public EmployeeProfile getEmployee() {
        return employeeProfile;
    }

    public void setEmployee(EmployeeProfile employeeProfile) {
        this.employeeProfile = employeeProfile;
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

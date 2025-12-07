package io.softwaregarage.hris.compenben.entities;

import io.softwaregarage.hris.commons.BaseEntity;
import io.softwaregarage.hris.profile.entities.EmployeeProfile;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sg_hris_government_contributions")
public class GovernmentContributions extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false, unique = true)
    private EmployeeProfile employeeProfile;

    @Column(name = "sss_contribution_amount", nullable = false)
    private BigDecimal sssContrbutionAmount;

    @Column(name = "sss_contribution_cutoff", nullable = false)
    private Integer sssContributionCutOff;

    @Column(name = "hdmf_contribution_amount", nullable = false)
    private BigDecimal hdmfContrbutionAmount;

    @Column(name = "hdmf_contribution_cutoff", nullable = false)
    private Integer hdmfContributionCutOff;

    @Column(name = "philhealth_contribution_amount", nullable = false)
    private BigDecimal philhealthContributionAmount;

    @Column(name = "philhealth_contribution_cutoff", nullable = false)
    private Integer philhealthContributionCutOff;

    public EmployeeProfile getEmployee() {
        return employeeProfile;
    }

    public void setEmployee(EmployeeProfile employeeProfile) {
        this.employeeProfile = employeeProfile;
    }

    public BigDecimal getSssContrbutionAmount() {
        return sssContrbutionAmount;
    }

    public void setSssContrbutionAmount(BigDecimal sssContrbutionAmount) {
        this.sssContrbutionAmount = sssContrbutionAmount;
    }

    public Integer getSssContributionCutOff() {
        return sssContributionCutOff;
    }

    public void setSssContributionCutOff(Integer sssContributionCutOff) {
        this.sssContributionCutOff = sssContributionCutOff;
    }

    public BigDecimal getHdmfContrbutionAmount() {
        return hdmfContrbutionAmount;
    }

    public void setHdmfContrbutionAmount(BigDecimal hdmfContrbutionAmount) {
        this.hdmfContrbutionAmount = hdmfContrbutionAmount;
    }

    public Integer getHdmfContributionCutOff() {
        return hdmfContributionCutOff;
    }

    public void setHdmfContributionCutOff(Integer hdmfContributionCutOff) {
        this.hdmfContributionCutOff = hdmfContributionCutOff;
    }

    public BigDecimal getPhilhealthContributionAmount() {
        return philhealthContributionAmount;
    }

    public void setPhilhealthContributionAmount(BigDecimal philhealthContributionAmount) {
        this.philhealthContributionAmount = philhealthContributionAmount;
    }

    public Integer getPhilhealthContributionCutOff() {
        return philhealthContributionCutOff;
    }

    public void setPhilhealthContributionCutOff(Integer philhealthContributionCutOff) {
        this.philhealthContributionCutOff = philhealthContributionCutOff;
    }
}

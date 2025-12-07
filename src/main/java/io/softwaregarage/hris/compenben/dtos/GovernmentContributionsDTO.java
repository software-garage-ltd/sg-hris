package io.softwaregarage.hris.compenben.dtos;

import io.softwaregarage.hris.commons.BaseDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import java.math.BigDecimal;

public class GovernmentContributionsDTO extends BaseDTO {
    private EmployeeProfileDTO employeeProfileDTO;
    private BigDecimal sssContributionAmount;
    private Integer sssContributionCutOff;
    private BigDecimal hdmfContributionAmount;
    private Integer hdmfContributionCutOff;
    private BigDecimal philhealthContributionAmount;
    private Integer philhealthContributionCutOff;

    public EmployeeProfileDTO getEmployeeDTO() {
        return employeeProfileDTO;
    }

    public void setEmployeeDTO(EmployeeProfileDTO employeeProfileDTO) {
        this.employeeProfileDTO = employeeProfileDTO;
    }

    public BigDecimal getSssContributionAmount() {
        return sssContributionAmount;
    }

    public void setSssContributionAmount(BigDecimal sssContributionAmount) {
        this.sssContributionAmount = sssContributionAmount;
    }

    public Integer getSssContributionCutOff() {
        return sssContributionCutOff;
    }

    public void setSssContributionCutOff(Integer sssContributionCutOff) {
        this.sssContributionCutOff = sssContributionCutOff;
    }

    public BigDecimal getHdmfContributionAmount() {
        return hdmfContributionAmount;
    }

    public void setHdmfContributionAmount(BigDecimal hdmfContributionAmount) {
        this.hdmfContributionAmount = hdmfContributionAmount;
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

package io.softwaregarage.hris.attendance.dtos;

import io.softwaregarage.hris.commons.BaseDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import java.time.LocalDate;
import java.time.LocalTime;

public class EmployeeOvertimeDTO extends BaseDTO {
    private EmployeeProfileDTO employeeProfile;
    private LocalDate overtimeDate;
    private LocalTime overtimeStartTime;
    private LocalTime overtimeEndTime;
    private Integer overtimeNumberOfHours;
    private String status;
    private EmployeeProfileDTO assignedApproverEmployeeDTO;

    public EmployeeProfileDTO getEmployeeProfile() {
        return employeeProfile;
    }

    public void setEmployeeProfile(EmployeeProfileDTO employeeProfile) {
        this.employeeProfile = employeeProfile;
    }

    public LocalDate getOvertimeDate() {
        return overtimeDate;
    }

    public void setOvertimeDate(LocalDate overtimeDate) {
        this.overtimeDate = overtimeDate;
    }

    public LocalTime getOvertimeStartTime() {
        return overtimeStartTime;
    }

    public void setOvertimeStartTime(LocalTime overtimeStartTime) {
        this.overtimeStartTime = overtimeStartTime;
    }

    public LocalTime getOvertimeEndTime() {
        return overtimeEndTime;
    }

    public void setOvertimeEndTime(LocalTime overtimeEndTime) {
        this.overtimeEndTime = overtimeEndTime;
    }

    public Integer getOvertimeNumberOfHours() {
        return overtimeNumberOfHours;
    }

    public void setOvertimeNumberOfHours(Integer overtimeNumberOfHours) {
        this.overtimeNumberOfHours = overtimeNumberOfHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EmployeeProfileDTO getAssignedApproverEmployeeDTO() {
        return assignedApproverEmployeeDTO;
    }

    public void setAssignedApproverEmployeeDTO(EmployeeProfileDTO assignedApproverEmployeeDTO) {
        this.assignedApproverEmployeeDTO = assignedApproverEmployeeDTO;
    }
}

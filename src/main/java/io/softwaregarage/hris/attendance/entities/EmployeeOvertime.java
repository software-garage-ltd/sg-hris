package io.softwaregarage.hris.attendance.entities;

import io.softwaregarage.hris.commons.BaseEntity;
import io.softwaregarage.hris.profile.entities.EmployeeProfile;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sg_hris_employee_overtime")
public class EmployeeOvertime extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private EmployeeProfile employeeProfile;

    @Column(name = "overtime_date", nullable = false)
    private LocalDate overtimeDate;

    @Column(name = "overtime_start_time", nullable = false)
    private LocalTime overtimeStartTime;

    @Column(name = "overtime_end_time", nullable = false)
    private LocalTime overtimeEndTime;

    @Column(name = "overtime_number_of_hours", nullable = false)
    private Integer overtimeNumberOfHours;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_approver_employee_id", referencedColumnName = "id", nullable = false)
    private EmployeeProfile assignedApproverEmployee;

    public EmployeeProfile getEmployeeProfile() {
        return employeeProfile;
    }

    public void setEmployeeProfile(EmployeeProfile employeeProfile) {
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

    public EmployeeProfile getAssignedApproverEmployee() {
        return assignedApproverEmployee;
    }

    public void setAssignedApproverEmployee(EmployeeProfile assignedApproverEmployee) {
        this.assignedApproverEmployee = assignedApproverEmployee;
    }
}

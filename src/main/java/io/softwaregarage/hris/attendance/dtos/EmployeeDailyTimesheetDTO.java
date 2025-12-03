package io.softwaregarage.hris.attendance.dtos;

import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import java.time.LocalDate;
import java.time.LocalTime;

public class EmployeeDailyTimesheetDTO {
    private EmployeeProfileDTO employeeProfileDTO;
    private LocalDate logDate;
    private LocalTime logInTime;
    private LocalTime logOutTime;
    private Long numberOfHours;
    private String status;
    private EmployeeShiftScheduleDTO employeeShiftScheduleDTO;

    public EmployeeProfileDTO getEmployeeProfileDTO() {
        return employeeProfileDTO;
    }

    public void setEmployeeProfileDTO(EmployeeProfileDTO employeeProfileDTO) {
        this.employeeProfileDTO = employeeProfileDTO;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public LocalTime getLogInTime() {
        return logInTime;
    }

    public void setLogInTime(LocalTime logInTime) {
        this.logInTime = logInTime;
    }

    public LocalTime getLogOutTime() {
        return logOutTime;
    }

    public void setLogOutTime(LocalTime logOutTime) {
        this.logOutTime = logOutTime;
    }

    public Long getNumberOfHours() {
        return numberOfHours;
    }

    public void setNumberOfHours(Long numberOfHours) {
        this.numberOfHours = numberOfHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EmployeeShiftScheduleDTO getEmployeeShiftScheduleDTO() {
        return employeeShiftScheduleDTO;
    }

    public void setEmployeeShiftScheduleDTO(EmployeeShiftScheduleDTO employeeShiftScheduleDTO) {
        this.employeeShiftScheduleDTO = employeeShiftScheduleDTO;
    }
}

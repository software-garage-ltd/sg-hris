package io.softwaregarage.hris.payroll.services;

import io.softwaregarage.hris.admin.dtos.CalendarHolidaysDTO;
import io.softwaregarage.hris.admin.services.CalendarHolidaysService;
import io.softwaregarage.hris.attendance.dtos.EmployeeDailyTimesheetDTO;
import io.softwaregarage.hris.attendance.dtos.EmployeeLeaveFilingDTO;
import io.softwaregarage.hris.attendance.dtos.EmployeeOvertimeDTO;
import io.softwaregarage.hris.attendance.dtos.EmployeeTimesheetDTO;
import io.softwaregarage.hris.attendance.services.EmployeeLeaveFilingService;
import io.softwaregarage.hris.attendance.services.EmployeeOvertimeService;
import io.softwaregarage.hris.attendance.services.EmployeeTimesheetService;
import io.softwaregarage.hris.compenben.dtos.LoanDeductionDTO;
import io.softwaregarage.hris.compenben.services.LoanDeductionService;
import io.softwaregarage.hris.payroll.dtos.RatesDTO;
import io.softwaregarage.hris.payroll.dtos.TaxRatesDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class PayrollCalculatorService {
    private final CalendarHolidaysService calendarHolidaysService;
    private final EmployeeTimesheetService employeeTimesheetService;
    private final EmployeeLeaveFilingService employeeLeaveFilingService;
    private final EmployeeOvertimeService  employeeOvertimeService;
    private final LoanDeductionService loanDeductionService;
    private final RatesService ratesService;
    private final TaxRatesService taxRatesService;

    private List<CalendarHolidaysDTO> listOfHolidays;
    private List<EmployeeDailyTimesheetDTO> listOfDailyTimesheet;

    private static String REGULAR_HOLIDAY = "Regular Holiday";
    private static String SPECIAL_HOLIDAY = "Special Holiday";
    private static String SPECIAL_NON_WORKING_HOLIDAY = "Special Non-Working Holiday";

    public PayrollCalculatorService(CalendarHolidaysService calendarHolidaysService,
                                    EmployeeTimesheetService employeeTimesheetService,
                                    EmployeeLeaveFilingService employeeLeaveFilingService,
                                    EmployeeOvertimeService employeeOvertimeService,
                                    LoanDeductionService loanDeductionService,
                                    RatesService ratesService,
                                    TaxRatesService taxRatesService) {
        this.calendarHolidaysService = calendarHolidaysService;
        this.employeeTimesheetService = employeeTimesheetService;
        this.employeeLeaveFilingService = employeeLeaveFilingService;
        this.employeeOvertimeService = employeeOvertimeService;
        this.loanDeductionService = loanDeductionService;
        this.ratesService = ratesService;
        this.taxRatesService = taxRatesService;
    }

    private List<EmployeeDailyTimesheetDTO> getEmployeeDailyTimesheet(EmployeeProfileDTO employeeProfileDTO,
                                                                     LocalDate fromDate,
                                                                     LocalDate toDate) {
        // Get the employee timesheets within cutoff.
        List<EmployeeTimesheetDTO> employeeTimesheetDTOList = employeeTimesheetService
                .findTimesheetByEmployeeAndLogDate(employeeProfileDTO, fromDate, toDate)
                .stream()
                .filter(employeeTimesheetDTO -> employeeTimesheetDTO.getStatus()
                        .equals("APPROVED"))
                .toList();

        // Transform the employee timesheets into daily timesheets for easy computation.
        List<EmployeeDailyTimesheetDTO> dailyTimesheets = employeeTimesheetDTOList.stream()
                .collect(Collectors.groupingBy(EmployeeTimesheetDTO::getLogDate))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<EmployeeTimesheetDTO> logs = entry.getValue();

                    // Find log in and log out
                    EmployeeTimesheetDTO logInEntry = logs.stream()
                            .filter(l -> "Log In".equalsIgnoreCase(l.getLogDetail()))
                            .findFirst().orElse(null);

                    EmployeeTimesheetDTO logOutEntry = logs.stream()
                            .filter(l -> "Log Out".equalsIgnoreCase(l.getLogDetail()))
                            .findFirst().orElse(null);

                    LocalTime logIn = logInEntry != null ? logInEntry.getLogTime() : null;
                    LocalTime logOut = logOutEntry != null ? logOutEntry.getLogTime() : null;

                    long hours = 0;
                    if (logIn != null && logOut != null) {
                        hours = Duration.between(logIn, logOut).toHours();
                    }

                    EmployeeDailyTimesheetDTO dailyTimesheetDTO = new EmployeeDailyTimesheetDTO();
                    dailyTimesheetDTO.setEmployeeProfileDTO(logs.get(0).getEmployeeDTO());
                    dailyTimesheetDTO.setLogDate(entry.getKey());
                    dailyTimesheetDTO.setLogInTime(logIn);
                    dailyTimesheetDTO.setLogOutTime(logOut);
                    dailyTimesheetDTO.setNumberOfHours(hours);
                    dailyTimesheetDTO.setStatus(logs.get(0).getStatus());
                    dailyTimesheetDTO.setEmployeeShiftScheduleDTO(logs.get(0).getShiftScheduleDTO());

                    return dailyTimesheetDTO;
                }).collect(Collectors.toList());

        return dailyTimesheets;
    }

    public BigDecimal computeBasicPay(EmployeeProfileDTO employeeProfileDTO,
                                      LocalDate fromDate,
                                      LocalDate toDate) {
        // Get employee daily timesheets within cutoff
        List<EmployeeDailyTimesheetDTO> employeeDailyTimesheetDTOList = this.getEmployeeDailyTimesheet(employeeProfileDTO, fromDate, toDate);
        RatesDTO ratesDTO = ratesService.findByEmployeeDTO(employeeProfileDTO);
        BigDecimal dailyRate = ratesDTO.getDailyCompensationRate();
        return dailyRate.multiply(BigDecimal.valueOf(employeeDailyTimesheetDTOList.size()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeOvertime(EmployeeProfileDTO employeeProfileDTO,
                                      LocalDate fromDate,
                                      LocalDate toDate) {
        RatesDTO rates = ratesService.findByEmployeeDTO(employeeProfileDTO);
        BigDecimal hourlyRate = rates.getHourlyCompensationRate();
        BigDecimal overtimePay = BigDecimal.ZERO;

        // Get approved overtime within cut-off dates
        List<EmployeeOvertimeDTO> approvedOvertimes = employeeOvertimeService.findByEmployeeDTO(employeeProfileDTO)
                .stream()
                .filter(dto -> "APPROVED".equals(dto.getStatus()))
                .filter(dto -> {
                    LocalDate overtimeDate = dto.getOvertimeDate();
                    return overtimeDate != null &&
                            !overtimeDate.isBefore(fromDate) &&
                            !overtimeDate.isAfter(toDate);
                })
                .toList();

        if (!approvedOvertimes.isEmpty()) {
            // Load holiday dates
            List<LocalDate> regularHolidays = calendarHolidaysService.findByParameter(REGULAR_HOLIDAY)
                    .stream()
                    .map(CalendarHolidaysDTO::getHolidayDate)
                    .filter(date -> !date.isBefore(fromDate) && !date.isAfter(toDate))
                    .toList();

            List<LocalDate> specialHolidays = calendarHolidaysService.findByParameter(SPECIAL_HOLIDAY)
                    .stream()
                    .map(CalendarHolidaysDTO::getHolidayDate)
                    .filter(date -> !date.isBefore(fromDate) && !date.isAfter(toDate))
                    .toList();

            List<LocalDate> specialNonWorkingHolidays = calendarHolidaysService.findByParameter(SPECIAL_NON_WORKING_HOLIDAY)
                    .stream()
                    .map(CalendarHolidaysDTO::getHolidayDate)
                    .filter(date -> !date.isBefore(fromDate) && !date.isAfter(toDate))
                    .toList();

            // Compute overtime pay
            for (EmployeeOvertimeDTO overtime : approvedOvertimes) {
                int hours = overtime.getOvertimeNumberOfHours();
                if (hours <= 0) continue;

                LocalDate date = overtime.getOvertimeDate();
                BigDecimal base = hourlyRate.multiply(BigDecimal.valueOf(hours));

                if (regularHolidays.contains(date)) {
                    overtimePay = overtimePay.add(base.multiply(BigDecimal.valueOf(2.0)).multiply(BigDecimal.valueOf(1.3)));
                } else if (specialHolidays.contains(date)) {
                    overtimePay = overtimePay.add(base.multiply(BigDecimal.valueOf(1.3)).multiply(BigDecimal.valueOf(1.3)));
                } else if (specialNonWorkingHolidays.contains(date)) {
                    overtimePay = overtimePay.add(base.multiply(BigDecimal.valueOf(1.3)).multiply(BigDecimal.valueOf(1.3)));
                } else {
                    overtimePay = overtimePay.add(base);
                }
            }
        }

        return overtimePay.setScale(2, RoundingMode.HALF_UP);
    }


    public BigDecimal computeAmountForRegularHoliday(EmployeeProfileDTO employeeProfileDTO,
                                                     LocalDate fromDate,
                                                     LocalDate toDate) {
        BigDecimal totalRegularHolidayAmount = BigDecimal.ZERO;

        List<EmployeeDailyTimesheetDTO> timesheets = this.getEmployeeDailyTimesheet(employeeProfileDTO, fromDate, toDate);

        List<CalendarHolidaysDTO> holidays = calendarHolidaysService.findByParameter(REGULAR_HOLIDAY)
                .stream()
                .filter(h -> !h.getHolidayDate().isBefore(fromDate) && !h.getHolidayDate().isAfter(toDate))
                .toList();

        RatesDTO rates = ratesService.findByEmployeeDTO(employeeProfileDTO);
        BigDecimal dailyRate = rates.getDailyCompensationRate();

        for (CalendarHolidaysDTO holiday : holidays) {
            LocalDate holidayDate = holiday.getHolidayDate();

            Optional<EmployeeDailyTimesheetDTO> tsOpt = timesheets.stream()
                    .filter(ts -> ts.getLogDate().equals(holidayDate))
                    .findFirst();

            if (tsOpt.isPresent()) {
                EmployeeDailyTimesheetDTO ts = tsOpt.get();
                int noOfWorkingHours = ts.getEmployeeShiftScheduleDTO().getShiftHours();

                if (ts.getNumberOfHours() > 0) {
                    // Worked on holiday
                    BigDecimal base = dailyRate.multiply(BigDecimal.valueOf(2.0));

                    // Holiday + Rest Day
                    String workedDay = ts.getLogDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    if (!ts.getEmployeeShiftScheduleDTO().getShiftScheduledDays().contains(workedDay)) {
                        base = dailyRate.multiply(BigDecimal.valueOf(2.6));
                    }

                    totalRegularHolidayAmount = totalRegularHolidayAmount.add(base);
                } else {
                    // Did not work → 100% holiday pay
                    totalRegularHolidayAmount = totalRegularHolidayAmount.add(dailyRate);
                }
            }
        }

        return totalRegularHolidayAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeAmountForSpecialHoliday(EmployeeProfileDTO employeeProfileDTO,
                                                     LocalDate fromDate,
                                                     LocalDate toDate) {
        BigDecimal totalSpecialHolidayAmount = BigDecimal.ZERO;

        List<EmployeeDailyTimesheetDTO> timesheets =
                this.getEmployeeDailyTimesheet(employeeProfileDTO, fromDate, toDate);

        List<CalendarHolidaysDTO> holidays = calendarHolidaysService.findByParameter(SPECIAL_HOLIDAY)
                .stream()
                .filter(h -> !h.getHolidayDate().isBefore(fromDate) && !h.getHolidayDate().isAfter(toDate))
                .toList();

        RatesDTO rates = ratesService.findByEmployeeDTO(employeeProfileDTO);
        BigDecimal dailyRate = rates.getDailyCompensationRate();

        for (CalendarHolidaysDTO holiday : holidays) {
            LocalDate holidayDate = holiday.getHolidayDate();

            Optional<EmployeeDailyTimesheetDTO> tsOpt = timesheets.stream()
                    .filter(ts -> ts.getLogDate().equals(holidayDate))
                    .findFirst();

            if (tsOpt.isPresent()) {
                EmployeeDailyTimesheetDTO ts = tsOpt.get();
                int noOfWorkingHours = ts.getEmployeeShiftScheduleDTO().getShiftHours();

                if (ts.getNumberOfHours() > 0) {
                    // Base holiday pay
                    BigDecimal base = dailyRate.multiply(BigDecimal.valueOf(1.3));

                    // Holiday + Rest Day
                    String workedDay = ts.getLogDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    if (!ts.getEmployeeShiftScheduleDTO().getShiftScheduledDays().contains(workedDay)) {
                        base = dailyRate.multiply(BigDecimal.valueOf(1.5));
                    }

                    totalSpecialHolidayAmount = totalSpecialHolidayAmount.add(base);
                }
            }
        }

        return totalSpecialHolidayAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeAmountForSpecialNonWorkingHoliday(EmployeeProfileDTO employeeProfileDTO,
                                                               LocalDate fromDate,
                                                               LocalDate toDate) {
        BigDecimal totalSpecialNonWorkingHolidayAmount = BigDecimal.ZERO;

        // Get employee timesheets within cutoff
        List<EmployeeDailyTimesheetDTO> timesheets =
                this.getEmployeeDailyTimesheet(employeeProfileDTO, fromDate, toDate);

        // Get special non-working holidays within cutoff
        List<CalendarHolidaysDTO> holidays = calendarHolidaysService.findByParameter(SPECIAL_NON_WORKING_HOLIDAY)
                .stream()
                .filter(h -> !h.getHolidayDate().isBefore(fromDate) && !h.getHolidayDate().isAfter(toDate))
                .toList();

        RatesDTO rates = ratesService.findByEmployeeDTO(employeeProfileDTO);
        BigDecimal dailyRate = rates.getDailyCompensationRate();
        BigDecimal hourlyRate = rates.getHourlyCompensationRate();

        for (CalendarHolidaysDTO holiday : holidays) {
            LocalDate holidayDate = holiday.getHolidayDate();

            Optional<EmployeeDailyTimesheetDTO> tsOpt = timesheets.stream()
                    .filter(ts -> ts.getLogDate().equals(holidayDate))
                    .findFirst();

            if (tsOpt.isPresent()) {
                EmployeeDailyTimesheetDTO ts = tsOpt.get();
                int scheduledHours = ts.getEmployeeShiftScheduleDTO().getShiftHours();

                if (ts.getNumberOfHours() > 0) {
                    // Base holiday pay (worked on special non-working holiday)
                    BigDecimal base = dailyRate.multiply(BigDecimal.valueOf(1.3));

                    // Holiday + Rest Day
                    String workedDay = ts.getLogDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    if (!ts.getEmployeeShiftScheduleDTO().getShiftScheduledDays().contains(workedDay)) {
                        base = dailyRate.multiply(BigDecimal.valueOf(1.5));
                    }

                    totalSpecialNonWorkingHolidayAmount = totalSpecialNonWorkingHolidayAmount.add(base);
                }
                // else → no work, no pay (unless company policy overrides)
            }
        }

        return totalSpecialNonWorkingHolidayAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeAmountForAbsences(EmployeeProfileDTO employeeProfileDTO,
                                               LocalDate fromDate,
                                               LocalDate toDate) {
        BigDecimal totalAbsentAmount = BigDecimal.ZERO;

        List<EmployeeDailyTimesheetDTO> timesheets =
                this.getEmployeeDailyTimesheet(employeeProfileDTO, fromDate, toDate);

        // Get the list of employee's filed leaves.
        List<EmployeeLeaveFilingDTO> employeeLeaveFilingDTOList =
                employeeLeaveFilingService.getByEmployeeDTO(employeeProfileDTO);

        // Filter the filed leaves based on the date duration.
        employeeLeaveFilingDTOList.stream()
                .filter(leave -> {
                    LocalDate leaveDateFrom = leave.getLeaveDateAndTimeFrom().toLocalDate();
                    LocalDate leaveDateTo = leave.getLeaveDateAndTimeTo().toLocalDate();
                    return (leaveDateFrom.isBefore(toDate) || leaveDateFrom.isEqual(toDate)) &&
                            (leaveDateTo.isAfter(fromDate)  || leaveDateTo.isEqual(fromDate));
                })
                .toList();

        if (employeeLeaveFilingDTOList.isEmpty()) {
            RatesDTO employeeRatesDTO = ratesService.findByEmployeeDTO(employeeProfileDTO);
            BigDecimal absentRate = employeeRatesDTO.getDailyAbsentDeductionRate();
            long absentCount = 0;

            for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                LocalDate finalDate = date;

                // Check if scheduled workday
                boolean isScheduledWorkday = timesheets.get(0).getEmployeeShiftScheduleDTO()
                        .getShiftScheduledDays()
                        .contains(dayName);

                if (isScheduledWorkday) {
                    boolean hasTimesheet = timesheets.stream()
                            .anyMatch(ts -> ts.getLogDate().equals(finalDate));

                    if (!hasTimesheet) {
                        absentCount = absentCount + 1;
                    }
                }
            }

            // Absent day → add daily rate
            totalAbsentAmount = absentRate.multiply(BigDecimal.valueOf(absentCount));
        }

        return totalAbsentAmount;
    }

    public BigDecimal computeAmountForRestDay(EmployeeProfileDTO employeeProfileDTO,
                                              LocalDate fromDate,
                                              LocalDate toDate) {
        BigDecimal totalRestDayAmount = BigDecimal.ZERO;

        List<EmployeeDailyTimesheetDTO> timesheets =
                this.getEmployeeDailyTimesheet(employeeProfileDTO, fromDate, toDate);

        RatesDTO ratesDTO = ratesService.findByEmployeeDTO(employeeProfileDTO);
        BigDecimal dailyRate = ratesDTO.getDailyCompensationRate();
        BigDecimal hourlyRate = ratesDTO.getHourlyCompensationRate();

        for (EmployeeDailyTimesheetDTO ts : timesheets) {
            int noOfWorkingHours = ts.getEmployeeShiftScheduleDTO().getShiftHours();
            String workedDay = ts.getLogDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            // Check if this day is NOT part of scheduled workdays → rest day
            if (!ts.getEmployeeShiftScheduleDTO().getShiftScheduledDays().contains(workedDay)) {
                if (ts.getNumberOfHours() > 0) {
                    // Worked on rest day → 130% pay
                    BigDecimal base = dailyRate.multiply(BigDecimal.valueOf(1.3));

                    // Overtime on rest day
                    Duration workDuration = Duration.between(ts.getLogInTime(), ts.getLogOutTime());
                    if (workDuration.toHours() > noOfWorkingHours) {
                        BigDecimal overtimeRate = hourlyRate
                                .multiply(BigDecimal.valueOf(workDuration.toHours() - noOfWorkingHours))
                                .multiply(BigDecimal.valueOf(1.3)) // rest day rate
                                .multiply(BigDecimal.valueOf(1.3)); // overtime premium
                        base = base.add(overtimeRate);
                    }

                    totalRestDayAmount = totalRestDayAmount.add(base);
                }
                // else → no work, no pay
            }
        }

        return totalRestDayAmount;
    }

    public BigDecimal computeNightDifferential(EmployeeProfileDTO employeeProfileDTO,
                                               LocalDate fromDate,
                                               LocalDate toDate) {
        BigDecimal totalNDPay = BigDecimal.ZERO;

        List<EmployeeDailyTimesheetDTO> timesheets =
                this.getEmployeeDailyTimesheet(employeeProfileDTO, fromDate, toDate);

        RatesDTO ratesDTO = ratesService.findByEmployeeDTO(employeeProfileDTO);
        BigDecimal hourlyRate = ratesDTO.getHourlyCompensationRate();
        BigDecimal ndRate = BigDecimal.valueOf(0.10); // could be externalized

        for (EmployeeDailyTimesheetDTO ts : timesheets) {
            LocalDateTime logInDateTime = LocalDateTime.of(ts.getLogDate(), ts.getLogInTime());
            LocalDateTime logOutDateTime = LocalDateTime.of(ts.getLogDate(), ts.getLogOutTime());

            if (ts.getLogOutTime().isBefore(ts.getLogInTime())) {
                logOutDateTime = logOutDateTime.plusDays(1);
            }

            LocalDateTime ndStart = logInDateTime.toLocalDate().atTime(22, 0);
            LocalDateTime ndEnd   = logInDateTime.toLocalDate().plusDays(1).atTime(6, 0);

            LocalDateTime overlapStart = logInDateTime.isAfter(ndStart) ? logInDateTime : ndStart;
            LocalDateTime overlapEnd   = logOutDateTime.isBefore(ndEnd) ? logOutDateTime : ndEnd;

            if (overlapEnd.isAfter(overlapStart)) {
                long ndMinutes = Duration.between(overlapStart, overlapEnd).toMinutes();
                BigDecimal ndHours = BigDecimal.valueOf(ndMinutes)
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

                BigDecimal ndPay = ndHours.multiply(hourlyRate).multiply(ndRate);
                totalNDPay = totalNDPay.add(ndPay);
            }
        }

        return totalNDPay;
    }

    public BigDecimal computeLeaves(EmployeeProfileDTO employeeProfileDTO,
                                    LocalDate fromDate,
                                    LocalDate toDate) {
        BigDecimal totalLeaveAmount = BigDecimal.ZERO;

        List<EmployeeLeaveFilingDTO> employeeLeaveFilingDTOList =
                employeeLeaveFilingService.getByEmployeeDTO(employeeProfileDTO);

        if (!employeeLeaveFilingDTOList.isEmpty()) {
            List<EmployeeLeaveFilingDTO> filteredLeaves = employeeLeaveFilingDTOList.stream()
                    .filter(leave -> {
                        LocalDate leaveFrom = leave.getLeaveDateAndTimeFrom().toLocalDate();
                        LocalDate leaveTo   = leave.getLeaveDateAndTimeTo().toLocalDate();
                        return (leaveFrom.isBefore(toDate) || leaveFrom.isEqual(toDate)) &&
                                (leaveTo.isAfter(fromDate)  || leaveTo.isEqual(fromDate));
                    })
                    .toList();

            RatesDTO ratesDTO = ratesService.findByEmployeeDTO(employeeProfileDTO);
            BigDecimal dailyRate = ratesDTO.getDailyCompensationRate();
            BigDecimal absentRate = ratesDTO.getDailyAbsentDeductionRate();

            for (EmployeeLeaveFilingDTO leave : filteredLeaves) {
                LocalDate leaveFrom = leave.getLeaveDateAndTimeFrom().toLocalDate();
                LocalDate leaveTo   = leave.getLeaveDateAndTimeTo().toLocalDate();

                LocalDate start = leaveFrom.isBefore(fromDate) ? fromDate : leaveFrom;
                LocalDate end   = leaveTo.isAfter(toDate) ? toDate : leaveTo;

                long leaveDays = ChronoUnit.DAYS.between(start, end) + 1;

                String leaveType = leave.getLeaveBenefitsDTO().getLeaveType();

                if (!leaveType.equalsIgnoreCase("(UL) Unpaid Leave")) {
                    totalLeaveAmount = totalLeaveAmount.add(
                            dailyRate.multiply(BigDecimal.valueOf(leaveDays))
                    );
                } else {
                    totalLeaveAmount = totalLeaveAmount.subtract(
                            absentRate.multiply(BigDecimal.valueOf(leaveDays))
                    );
                }
            }
        }

        return totalLeaveAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeLateAndUndertime(EmployeeProfileDTO employeeProfileDTO,
                                              LocalDate fromDate,
                                              LocalDate toDate) {
        BigDecimal totalDeduction = BigDecimal.ZERO;

        List<EmployeeDailyTimesheetDTO> timesheets =
                this.getEmployeeDailyTimesheet(employeeProfileDTO, fromDate, toDate);

        RatesDTO ratesDTO = ratesService.findByEmployeeDTO(employeeProfileDTO);
        BigDecimal hourlyRate = ratesDTO.getHourlyCompensationRate();

        for (EmployeeDailyTimesheetDTO ts : timesheets) {
            LocalDate logDate = ts.getLogDate();

            // Scheduled shift start and end
            LocalTime scheduledStart = ts.getEmployeeShiftScheduleDTO().getShiftStartTime();
            LocalTime scheduledEnd = ts.getEmployeeShiftScheduleDTO().getShiftEndTime();

            // Actual log in/out
            LocalTime actualIn  = ts.getLogInTime();
            LocalTime actualOut = ts.getLogOutTime();

            // Grace period of 15 minutes
            LocalTime allowedStart = scheduledStart.plusMinutes(15);
            LocalTime allowedEnd = scheduledEnd.minusMinutes(15);

            // Compute late minutes
            long lateMinutes = 0;
            if (actualIn.isAfter(allowedStart)) {
                lateMinutes = Duration.between(allowedStart, actualIn).toMinutes();
            }

            // Compute undertime minutes
            long undertimeMinutes = 0;
            if (actualOut.isBefore(allowedEnd)) {
                undertimeMinutes = Duration.between(actualOut, allowedEnd).toMinutes();
            }

            // Convert to hours (with decimals)
            BigDecimal lateHours = BigDecimal.valueOf(lateMinutes)
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            BigDecimal undertimeHours = BigDecimal.valueOf(undertimeMinutes)
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

            // Deduction = (late + undertime) × hourly rate
            BigDecimal deduction = lateHours.add(undertimeHours).multiply(hourlyRate);
            totalDeduction = totalDeduction.add(deduction);
        }

        return totalDeduction.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeLoanDeductions(EmployeeProfileDTO employeeProfileDTO,
                                            LocalDate fromDate,
                                            LocalDate toDate) {
        BigDecimal totalLoanDeduction = BigDecimal.ZERO;

        // Get loans for employee
        List<LoanDeductionDTO> loans = loanDeductionService.findByEmployeeProfileDTO(employeeProfileDTO);

        if (!loans.isEmpty()) {
            List<LoanDeductionDTO> activeLoans = loans.stream()
                    .filter(loan -> {
                        LocalDate loanStart = loan.getLoanStartDate();
                        LocalDate loanEnd   = loan.getLoanEndDate();
                        return (loanStart.isBefore(toDate) || loanStart.isEqual(toDate)) &&
                                (loanEnd.isAfter(fromDate)  || loanEnd.isEqual(fromDate));
                    })
                    .toList();

            for (LoanDeductionDTO loan : activeLoans) {
                // If payroll is semi-monthly, divide deduction by 2
                BigDecimal deductionPerCutoff = loan.getMonthlyDeduction().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

                totalLoanDeduction = totalLoanDeduction.add(deductionPerCutoff);
            }
        }

        return totalLoanDeduction.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeWithholdingTax(BigDecimal totalGrossPayAmount,
                                            BigDecimal totalDeductionAmount) {
        // Step 1: Compute taxable income for the cutoff
        BigDecimal taxableIncome = totalGrossPayAmount.subtract(totalDeductionAmount);
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal withholdingTax = BigDecimal.ZERO;

        // Step 2: Get active tax rates for the current year (already adjusted for cutoff frequency)
        List<TaxRatesDTO> taxRates = taxRatesService.getTaxRatesByYear(LocalDate.now().getYear())
                .stream()
                .filter(TaxRatesDTO::isActiveTaxRate)
                .sorted(Comparator.comparing(TaxRatesDTO::getLowerBoundAmount))
                .toList();

        // Step 3: Find the bracket where taxable income falls
        for (TaxRatesDTO bracket : taxRates) {
            boolean withinCeiling = bracket.getUpperBoundAmount() == null
                    || taxableIncome.compareTo(bracket.getUpperBoundAmount()) <= 0;

            if (taxableIncome.compareTo(bracket.getLowerBoundAmount()) >= 0 && withinCeiling) {
                BigDecimal excess = taxableIncome.subtract(bracket.getLowerBoundAmount());
                withholdingTax = bracket.getBaseTax().add(excess.multiply(bracket.getRate()));
                break;
            }
        }

        // Step 4: Return rounded value
        return withholdingTax.setScale(2, RoundingMode.HALF_UP);
    }

}

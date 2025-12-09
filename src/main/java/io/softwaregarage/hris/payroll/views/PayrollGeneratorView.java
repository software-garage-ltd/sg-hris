package io.softwaregarage.hris.payroll.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;

import io.softwaregarage.hris.attendance.dtos.EmployeeTimesheetDTO;
import io.softwaregarage.hris.attendance.services.EmployeeTimesheetService;
import io.softwaregarage.hris.compenben.dtos.GovernmentContributionsDTO;
import io.softwaregarage.hris.compenben.services.AllowanceService;
import io.softwaregarage.hris.compenben.services.GovernmentContributionsService;
import io.softwaregarage.hris.payroll.dtos.PayrollDTO;
import io.softwaregarage.hris.payroll.services.PayrollCalculatorService;
import io.softwaregarage.hris.payroll.services.PayrollService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;
import io.softwaregarage.hris.utils.SecurityUtil;
import io.softwaregarage.hris.commons.views.MainLayout;

import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.util.Objects;

@RolesAllowed({"ROLE_ADMIN",
        "ROLE_HR_MANAGER",
        "ROLE_PAYROLL_MANAGER",
        "ROLE_PAYROLL_EMPLOYEE"})
@PageTitle("Payroll Generator")
@Route(value = "payroll-generator", layout = MainLayout.class)
public class PayrollGeneratorView extends VerticalLayout {
    private final EmployeeProfileService employeeProfileService;
    private final PayrollService payrollService;
    private final EmployeeTimesheetService employeeTimesheetService;
    private final AllowanceService allowanceService;
    private final GovernmentContributionsService  governmentContributionsService;
    private final PayrollCalculatorService payrollCalculatorService;

    private ComboBox<EmployeeProfileDTO> employeeProfileComboBox;
    private DatePicker cutOffFromDatePicker, cutOffToDatePicker;
    private Button searchCutOffButton, generatePayrollButton;
    private Grid<EmployeeTimesheetDTO> timesheetDTOGrid;

    private String loggedInUser;

    public PayrollGeneratorView(EmployeeProfileService employeeProfileService,
                                PayrollService payrollService,
                                EmployeeTimesheetService employeeTimesheetService,
                                AllowanceService allowanceService,
                                GovernmentContributionsService governmentContributionsService,
                                PayrollCalculatorService payrollCalculatorService) {
        this.employeeProfileService = employeeProfileService;
        this.payrollService = payrollService;
        this.employeeTimesheetService = employeeTimesheetService;
        this.allowanceService = allowanceService;
        this.governmentContributionsService = governmentContributionsService;
        this.payrollCalculatorService = payrollCalculatorService;

        // Get the logged-in user of the system.
        loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        this.add(buildFilterToolbar(), buildTimesheetDTOGrid());
        this.setSizeFull();
        this.setAlignItems(Alignment.STRETCH);
    }

    public HorizontalLayout buildFilterToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();

        Query<EmployeeProfileDTO, Void> employeeQuery = new Query<>();

        employeeProfileComboBox = new ComboBox<>();
        employeeProfileComboBox.setItems((employeeDTO, filterString) ->
                        employeeDTO.getEmployeeFullName()
                                .toLowerCase()
                                .contains(filterString.toLowerCase()),
                employeeProfileService.getAll(employeeQuery.getPage(), employeeQuery.getPageSize()));
        employeeProfileComboBox.setItemLabelGenerator(EmployeeProfileDTO::getEmployeeFullName);
        employeeProfileComboBox.setClearButtonVisible(true);
        employeeProfileComboBox.getStyle().set("margin-right", "5px");
        employeeProfileComboBox.setPlaceholder("Select Employee");

        cutOffFromDatePicker = new DatePicker();
        cutOffFromDatePicker.getStyle().set("margin-right", "5px");
        cutOffFromDatePicker.setPlaceholder("Cut-off from");

        cutOffToDatePicker = new DatePicker();
        cutOffToDatePicker.getStyle().set("margin-right", "5px");
        cutOffToDatePicker.setPlaceholder("Cut-off to");

        searchCutOffButton = new Button("Search");
        searchCutOffButton.setIconAfterText(true);
        searchCutOffButton.setIcon(LumoIcon.SEARCH.create());
        searchCutOffButton.addClickListener(e -> {
            if (cutOffFromDatePicker.getValue() != null && cutOffToDatePicker.getValue() != null) {
                timesheetDTOGrid.setItems(employeeTimesheetService
                        .findTimesheetByEmployeeAndLogDate(employeeProfileComboBox.getValue(),
                                cutOffFromDatePicker.getValue(),
                                cutOffToDatePicker.getValue()));
            }
        });

        generatePayrollButton = new Button("Generate Payroll");
        generatePayrollButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        generatePayrollButton.addClickListener(buttonClickEvent -> {
            if (timesheetDTOGrid.getDataProvider().size(new Query<>()) >= 1) {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader("Generate Payroll");
                confirmDialog.setText("""
                                      WARNING!
                                      Any employee's timesheet that was not approved in the given cut-off dates will not
                                      be included in generating the payroll. Are you sure you want to generate the
                                      payroll?
                                      """);
                confirmDialog.addConfirmListener(confirmEvent -> {
                    // ----- Pay Amounts -----
                    // Get the employee's total pay based on his timesheet.
                    BigDecimal basicPay = payrollCalculatorService.computeBasicPay(employeeProfileComboBox.getValue(),
                            cutOffFromDatePicker.getValue(),
                            cutOffToDatePicker.getValue());

                    // Get the employee's total overtime pay based on his overtime request.
                    BigDecimal overtimePay = payrollCalculatorService.computeOvertime(employeeProfileComboBox.getValue(),
                            cutOffFromDatePicker.getValue(),
                            cutOffToDatePicker.getValue());

                    // Get the employee's total allowances;
                    BigDecimal allowancePay = allowanceService
                            .getSumOfAllowanceByEmployeeDTO(employeeProfileComboBox.getValue());

                    // Get the computed amount for regular holidays.
                    BigDecimal regularHolidayAmount = payrollCalculatorService
                            .computeAmountForRegularHoliday(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue());

                    // Get the computed amount for special holidays.
                    BigDecimal specialHolidayAmount = payrollCalculatorService
                            .computeAmountForSpecialHoliday(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue());

                    // Get the computed amount for special non-working holidays.
                    BigDecimal specialNonWorkingHolidayAmount = payrollCalculatorService
                            .computeAmountForSpecialNonWorkingHoliday(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue());

                    // Get the computed amount for rest days.
                    BigDecimal restDayAmount = payrollCalculatorService
                            .computeAmountForRestDay(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue());

                    // Get the computed amount for night differentials.
                    BigDecimal nightDifferentialAmount = payrollCalculatorService
                            .computeNightDifferential(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue());

                    // Get the computed amount for filed leaves.
                    BigDecimal leavePayAmount = payrollCalculatorService
                            .computeLeaves(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue());

                    BigDecimal totalGrossPayAmount = basicPay.add(allowancePay)
                            .add(overtimePay)
                            .add(restDayAmount)
                            .add(nightDifferentialAmount)
                            .add(leavePayAmount)
                            .add(regularHolidayAmount)
                            .add(specialHolidayAmount)
                            .add(specialNonWorkingHolidayAmount)
                            .add(BigDecimal.ZERO);

                    // ----- DEDUCTIONS -----
                    // Get the employee's deduction amount for absences.
                    BigDecimal absentDeductionAmount = payrollCalculatorService
                            .computeAmountForAbsences(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue());

                    // Get the computed late and undertime in the timesheet.
                    BigDecimal lateOrUndertimeAmount = payrollCalculatorService
                            .computeLateAndUndertime(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue());

                    // Get the employee's government contributions.
                    Integer cutOffDay = cutOffToDatePicker.getValue().getDayOfMonth() <= 15 ? 1 : 2;

                    GovernmentContributionsDTO governmentContributionsDTO = governmentContributionsService
                            .findByEmployeeProfileDTO(employeeProfileComboBox.getValue());

                    BigDecimal sssDeductionAmount = cutOffDay
                            .equals(governmentContributionsDTO.getSssContributionCutOff())
                            ? governmentContributionsDTO.getSssContributionAmount()
                            : BigDecimal.ZERO;

                    BigDecimal hdmfDeductionAmount = cutOffDay
                            .equals(governmentContributionsDTO.getHdmfContributionCutOff())
                            ? governmentContributionsDTO.getHdmfContributionAmount()
                            : BigDecimal.ZERO;

                    BigDecimal philhealthDeductionAmount = cutOffDay
                            .equals(governmentContributionsDTO.getPhilhealthContributionCutOff())
                            ? governmentContributionsDTO.getPhilhealthContributionAmount()
                            : BigDecimal.ZERO;

                    // Get the employee's loan
                    BigDecimal totalLoanDeductionAmount = payrollCalculatorService
                            .computeLoanDeductions(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue());

                    BigDecimal totalDeductionsAmount = absentDeductionAmount.add(lateOrUndertimeAmount)
                            .add(sssDeductionAmount)
                            .add(hdmfDeductionAmount)
                            .add(philhealthDeductionAmount)
                            .add(totalLoanDeductionAmount);

                    // Get the employee's withholding tax
                    BigDecimal withHoldingTaxDeduction = payrollCalculatorService
                            .computeWithholdingTax(totalGrossPayAmount, totalDeductionsAmount, 24);

                    PayrollDTO payrollDTO = new PayrollDTO();
                    payrollDTO.setEmployeeDTO(employeeProfileComboBox.getValue());
                    payrollDTO.setCutOffFromDate(cutOffFromDatePicker.getValue());
                    payrollDTO.setCutOffToDate(cutOffToDatePicker.getValue());
                    payrollDTO.setBasicPayAmount(basicPay);
                    payrollDTO.setOvertimePayAmount(overtimePay);
                    payrollDTO.setAllowancePayAmount(allowancePay);
                    payrollDTO.setAbsentDeductionAmount(absentDeductionAmount);
                    payrollDTO.setLateOrUndertimeDeductionAmount(lateOrUndertimeAmount);
                    payrollDTO.setRestDayPayAmount(restDayAmount);
                    payrollDTO.setNightDifferentialPayAmount(nightDifferentialAmount);
                    payrollDTO.setLeavePayAmount(leavePayAmount);
                    payrollDTO.setRegularHolidayPayAmount(regularHolidayAmount);
                    payrollDTO.setSpecialHolidayPayAmount(specialHolidayAmount);
                    payrollDTO.setSpecialNonWorkingHolidayPayAmount(specialNonWorkingHolidayAmount);
                    payrollDTO.setAdjustmentPayAmount(BigDecimal.ZERO);
                    payrollDTO.setTotalGrossPayAmount(totalGrossPayAmount);
                    payrollDTO.setSssDeductionAmount(sssDeductionAmount);
                    payrollDTO.setHdmfDeductionAmount(hdmfDeductionAmount);
                    payrollDTO.setPhilhealthDeductionAmount(philhealthDeductionAmount);
                    payrollDTO.setTotalLoanDeductionAmount(totalLoanDeductionAmount);
                    payrollDTO.setOtherDeductionAmount(BigDecimal.ZERO);
                    payrollDTO.setTotalDeductionAmount(totalDeductionsAmount);
                    payrollDTO.setWithholdingTaxDeductionAmount(withHoldingTaxDeduction);
                    payrollDTO.setCreatedBy(loggedInUser);
                    payrollDTO.setUpdatedBy(loggedInUser);

                    payrollService.saveOrUpdate(payrollDTO);

                    // Iterate over the data grid and changed the timesheet that has an APPROVED status to PROCESSED.
                    timesheetDTOGrid.getListDataView().getItems().forEach(item -> {
                        if (item.getStatus().contentEquals("APPROVED")) {
                            item.setStatus("PROCESSED");
                            item.setUpdatedBy(loggedInUser);

                            employeeTimesheetService.saveOrUpdate(item);
                        }
                    });

                    // Refresh the grid after iterating each row.
                    timesheetDTOGrid.getDataProvider().refreshAll();
                });
                confirmDialog.setRejectable(true);
                confirmDialog.addRejectListener(rejectEvent -> confirmDialog.close());
                confirmDialog.open();
            } else {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                notification.setText("Please search the employeeTimesheet that you want to generate the payroll.");
                notification.setDuration(5000);
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.open();
            }
        });


        Span searchCutOffSpan = new Span();
        searchCutOffSpan.getStyle().set("margin", "0 auto 0 0");
        searchCutOffSpan.add(employeeProfileComboBox, cutOffFromDatePicker, cutOffToDatePicker, searchCutOffButton);

        toolbar.add(searchCutOffSpan, generatePayrollButton);

        return toolbar; 
    }

    public Grid<EmployeeTimesheetDTO> buildTimesheetDTOGrid() {
        timesheetDTOGrid = new Grid<>(EmployeeTimesheetDTO.class, false);

        timesheetDTOGrid.addColumn(employeeTimesheetDTO -> employeeTimesheetDTO.getEmployeeDTO().getEmployeeNumber())
                        .setHeader("Employee No.")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(employeeTimesheetDTO -> employeeTimesheetDTO.getEmployeeDTO().getFirstName()
                                                                             .concat(" ")
                                                                             .concat(employeeTimesheetDTO.getEmployeeDTO().getMiddleName())
                                                                             .concat(" ")
                                                                             .concat(employeeTimesheetDTO.getEmployeeDTO().getLastName())
                                                                             .concat(employeeTimesheetDTO.getEmployeeDTO().getSuffix() != null ? employeeTimesheetDTO.getEmployeeDTO().getSuffix() : ""))
                        .setHeader("Employee Name")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(EmployeeTimesheetDTO::getLogDate)
                        .setHeader("Log Date")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(employeeTimesheetDTO -> employeeTimesheetDTO.getShiftScheduleDTO().getShiftSchedule())
                        .setHeader("Shift Schedule")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(EmployeeTimesheetDTO::getLogTime)
                        .setHeader("Log Time")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(EmployeeTimesheetDTO::getLogDetail)
                        .setHeader("Log Detail")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, (layout, employeeTimesheetDTO) -> {
                            String theme = String.format("badge %s", employeeTimesheetDTO.getStatus().equalsIgnoreCase("APPROVED") ? "success" : "tertiary");

                            Span activeSpan = new Span();
                            activeSpan.getElement().setAttribute("theme", theme);
                            activeSpan.setText(employeeTimesheetDTO.getStatus());

                            layout.setJustifyContentMode(JustifyContentMode.CENTER);
                            layout.add(activeSpan);
                        }))
                        .setHeader("Status")
                        .setSortable(true);
        timesheetDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                                          GridVariant.LUMO_COLUMN_BORDERS,
                                          GridVariant.LUMO_WRAP_CELL_CONTENT);
        timesheetDTOGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        timesheetDTOGrid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        timesheetDTOGrid.setEmptyStateText("No approved or rejected timesheet records found.");
        timesheetDTOGrid.setAllRowsVisible(true);

        return timesheetDTOGrid;
    }
}

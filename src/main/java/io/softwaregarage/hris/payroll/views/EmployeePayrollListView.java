package io.softwaregarage.hris.payroll.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;

import io.softwaregarage.hris.admin.dtos.UserDTO;
import io.softwaregarage.hris.admin.services.UserService;
import io.softwaregarage.hris.attendance.dtos.EmployeeTimesheetDTO;
import io.softwaregarage.hris.attendance.services.EmployeeTimesheetService;
import io.softwaregarage.hris.commons.views.MainLayout;
import io.softwaregarage.hris.payroll.dtos.PayrollDTO;
import io.softwaregarage.hris.payroll.services.PayrollService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;
import io.softwaregarage.hris.utils.EmailUtil;
import io.softwaregarage.hris.utils.SecurityUtil;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.vaadin.lineawesome.LineAwesomeIcon;

@RolesAllowed({"ROLE_ADMIN",
        "ROLE_HR_MANAGER",
        "ROLE_PAYROLL_MANAGER",
        "ROLE_PAYROLL_EMPLOYEE"})
@PageTitle("Payroll")
@Route(value = "employee-payroll-list", layout = MainLayout.class)
public class EmployeePayrollListView extends VerticalLayout {
    @Resource
    private final PayrollService payrollService;

    @Resource
    private final EmployeeProfileService employeeProfileService;

    @Resource
    private final EmployeeTimesheetService employeeTimesheetService;

    @Resource
    private final UserService userService;

    @Resource
    private final EmailUtil emailUtil;

    private ComboBox<EmployeeProfileDTO> employeeProfileComboBox;
    private DatePicker cutOffFromDatePicker, cutOffToDatePicker;
    private Button searchCutOffButton;
    private Grid<PayrollDTO> payrollDTOGrid;

    private String loggedInUser;

    public EmployeePayrollListView(PayrollService payrollService,
                                   EmployeeProfileService employeeProfileService,
                                   EmployeeTimesheetService employeeTimesheetService,
                                   UserService userService,
                                   EmailUtil emailUtil) {
        this.payrollService = payrollService;
        this.employeeProfileService = employeeProfileService;
        this.employeeTimesheetService = employeeTimesheetService;
        this.userService = userService;
        this.emailUtil = emailUtil;

        // Get the logged-in user of the system.
        loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        this.add(buildPayrollSearchFilterToolbar(), buildPayrollDTOGrid());
        this.setSizeFull();
        this.setAlignItems(Alignment.STRETCH);
    }

    public Component buildPayrollSearchFilterToolbar() {
        HorizontalLayout payrollSearchFilterLayout = new HorizontalLayout();

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
                if (employeeProfileComboBox.getValue() != null) {
                    payrollDTOGrid.setItems(payrollService
                            .findPayrollDTOByEmployeeProfileDTOAndCutOffFromDate(employeeProfileComboBox.getValue(),
                                    cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue()
                            )
                    );
                } else {
                    payrollDTOGrid.setItems(payrollService
                            .findPayrollDTOByCutOffDates(cutOffFromDatePicker.getValue(),
                                    cutOffToDatePicker.getValue()
                            )
                    );
                }
            }
        });

        payrollSearchFilterLayout.add(employeeProfileComboBox,
                cutOffFromDatePicker,
                cutOffToDatePicker,
                searchCutOffButton);
        return payrollSearchFilterLayout;
    }

    public Grid<PayrollDTO> buildPayrollDTOGrid() {
        payrollDTOGrid = new Grid<>(PayrollDTO.class, false);

        payrollDTOGrid.addColumn(payrollDTO -> payrollDTO.getEmployeeDTO().getEmployeeNumber())
                .setHeader("Employee No.");
        payrollDTOGrid.addColumn(payrollDTO -> payrollDTO.getEmployeeDTO().getFirstName()
                        .concat(" ")
                        .concat(payrollDTO.getEmployeeDTO().getMiddleName())
                        .concat(" ")
                        .concat(payrollDTO.getEmployeeDTO().getLastName())
                        .concat(payrollDTO.getEmployeeDTO().getSuffix() != null
                                ? payrollDTO.getEmployeeDTO().getSuffix()
                                : ""))
                .setHeader("Employee Name");
        payrollDTOGrid.addColumn(payrollDTO -> "PHP " + payrollDTO.getTotalGrossPayAmount())
                .setHeader("Total Gross Amount");
        payrollDTOGrid.addColumn(payrollDTO -> "PHP " + payrollDTO.getTotalDeductionAmount())
                .setHeader("Total Deduction Amount");
        payrollDTOGrid.addColumn(payrollDTO -> "PHP " + payrollDTO.getWithholdingTaxDeductionAmount())
                .setHeader("Withholding Tax");
        payrollDTOGrid.addColumn(payrollDTO -> "PHP " + payrollDTO.getTotalGrossPayAmount()
                        .add(payrollDTO.getNonTaxableAllowancePayAmount())
                        .subtract(payrollDTO.getTotalDeductionAmount())
                        .subtract(payrollDTO.getWithholdingTaxDeductionAmount()))
                .setHeader("Total Net Amount");
        payrollDTOGrid.addColumn(payrollDTO ->
                        payrollDTO.getCutOffFromDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                                + " to "
                                + payrollDTO.getCutOffToDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                .setHeader("Cut Off Dates");
        payrollDTOGrid.addComponentColumn(payrollDTO -> buildRowToolbar())
                .setHeader("Action");
        payrollDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        payrollDTOGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        payrollDTOGrid.setEmptyStateText("No payroll records found.");
        payrollDTOGrid.setAllRowsVisible(true);

        return payrollDTOGrid;
    }

    public HorizontalLayout buildRowToolbar() {
        HorizontalLayout rowToolbarLayout = new HorizontalLayout();

        Button viewButton = new Button();
        viewButton.setTooltipText("View Employee Payroll");
        viewButton.setIcon(LineAwesomeIcon.SEARCH_SOLID.create());
        viewButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        viewButton.addClickListener(buttonClickEvent -> viewButton.getUI().ifPresent(ui -> {
            if (payrollDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                PayrollDTO selectedPayrollDTO = payrollDTOGrid.getSelectionModel().getFirstSelectedItem().get();
                ui.navigate(EmployeePayrollDetailsView.class, selectedPayrollDTO.getId().toString());
            }
        }));

        Button editButton = new Button();
        editButton.setTooltipText("Edit Employee Payroll");
        editButton.setIcon(LineAwesomeIcon.PENCIL_ALT_SOLID.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        editButton.addClickListener(buttonClickEvent -> editButton.getUI().ifPresent(ui -> {
            if (payrollDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                PayrollDTO selectedPayrollDTO = payrollDTOGrid.getSelectionModel().getFirstSelectedItem().get();
                ui.navigate(EmployeePayrollFormView.class, selectedPayrollDTO.getId().toString());
            }
        }));

        Button deleteButton = new Button();
        deleteButton.setTooltipText("Delete Employee Payroll");
        deleteButton.setIcon(LineAwesomeIcon.TRASH_ALT_SOLID.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(buttonClickEvent -> {
            deleteButton.getUI().ifPresent(ui -> {
                if (payrollDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                    PayrollDTO selectedPayrollDTO = payrollDTOGrid.getSelectionModel().getFirstSelectedItem().get();

                    // Show the confirmation dialog.
                    ConfirmDialog confirmDialog = new ConfirmDialog();
                    confirmDialog.setHeader("Delete Rate");
                    confirmDialog.setText(new Html("""
                                                   <p>
                                                   WARNING! Deleting this employee payroll may not be able to process the
                                                   employee's salary. You have to generate again the employee payroll.
                                                   Are you sure you want to delete the selected employee payroll?
                                                   </p>
                                                   """));
                    confirmDialog.setConfirmText("Yes, Delete it.");
                    confirmDialog.setConfirmButtonTheme("error primary");
                    confirmDialog.addConfirmListener(confirmEvent -> {
                        // Get the selected employee payroll and delete it.
                        payrollService.delete(selectedPayrollDTO);

                        // Refresh the data grid from the backend after the delete operation.
                        payrollDTOGrid.getDataProvider().refreshAll();

                        // From the selected payroll, get the employee information and use it reset the status of timesheet
                        // from PROCESSED to APPROVED.
                        List<EmployeeTimesheetDTO> listOfEmployeeTimesheet = employeeTimesheetService
                                .findTimesheetByEmployeeAndLogDate(selectedPayrollDTO.getEmployeeDTO(),
                                        selectedPayrollDTO.getCutOffFromDate(),
                                        selectedPayrollDTO.getCutOffToDate());

                        if (!listOfEmployeeTimesheet.isEmpty()) {
                            for (EmployeeTimesheetDTO employeeTimesheetDTO : listOfEmployeeTimesheet) {
                                employeeTimesheetDTO.setStatus("APPROVED");
                                employeeTimesheetDTO.setUpdatedBy(loggedInUser);

                                employeeTimesheetService.saveOrUpdate(employeeTimesheetDTO);
                            }
                        }

                        // Show notification message.
                        Notification notification = Notification.show("You have successfully deleted the selected employee payroll.",
                                5000,
                                Notification.Position.TOP_CENTER);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                        // Close the confirmation dialog.
                        confirmDialog.close();
                    });
                    confirmDialog.setCancelable(true);
                    confirmDialog.setCancelText("No");
                    confirmDialog.open();
                }
            });
        });

        Button sendPayslipButton = new Button();
        sendPayslipButton.setTooltipText("Send Employee Payslip");
        sendPayslipButton.setIcon(LineAwesomeIcon.ENVELOPE_OPEN_SOLID.create());
        sendPayslipButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendPayslipButton.addClickListener(buttonClickEvent -> {
            if (payrollDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                PayrollDTO selectedPayrollDTO = payrollDTOGrid.getSelectionModel().getFirstSelectedItem().get();
                UserDTO userDTO = userService.getByEmployeeProfileDTO(selectedPayrollDTO.getEmployeeDTO());

                emailUtil.sendEmployeePayslipEmail(userDTO.getEmailAddress(),
                        userDTO.getEmployeeDTO().getFirstName(),
                        selectedPayrollDTO);

                // Show notification message.
                Notification notification = Notification.show("You have successfully emailed the employee's payslip.",
                        5000,
                        Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });

        rowToolbarLayout.add(viewButton, editButton, deleteButton, sendPayslipButton);
        rowToolbarLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        rowToolbarLayout.getStyle().set("flex-wrap", "wrap");

        return rowToolbarLayout;
    }
}

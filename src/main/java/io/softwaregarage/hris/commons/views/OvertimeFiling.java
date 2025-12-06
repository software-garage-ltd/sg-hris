package io.softwaregarage.hris.commons.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.softwaregarage.hris.admin.dtos.UserDTO;
import io.softwaregarage.hris.admin.services.UserService;
import io.softwaregarage.hris.attendance.dtos.EmployeeOvertimeDTO;
import io.softwaregarage.hris.attendance.services.EmployeeOvertimeService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;
import io.softwaregarage.hris.utils.SecurityUtil;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.vaadin.lineawesome.LineAwesomeIcon;

@PermitAll
@PageTitle("My Overtime Filings")
@Route(value = "overtime-filing-view", layout = MainLayout.class)
public class OvertimeFiling extends VerticalLayout {
    @Resource
    private final EmployeeOvertimeService employeeOvertimeService;

    @Resource
    private final EmployeeProfileService employeeProfileService;

    @Resource
    private final UserService userService;

    private UserDTO userDTO;
    private EmployeeProfileDTO employeeProfileDTO;
    private EmployeeOvertimeDTO employeeOvertimeDTO;

    private List<EmployeeOvertimeDTO> employeeOvertimeDTOList;
    private List<EmployeeProfileDTO> approverEmployeeProfileDTOList;

    private String loggedInUser;

    private Grid<EmployeeOvertimeDTO> employeeOvertimeDTOGrid;
    private FormLayout overtimeFilingLayout;
    private ComboBox<EmployeeProfileDTO> employeeApproverDTOComboBox;
    private DatePicker overtimeDatePicker;
    private TimePicker startTimePicker, endTimePicker;
    private IntegerField numberOfHoursField;
    private Button saveButton, cancelButton;

    public OvertimeFiling(EmployeeOvertimeService employeeOvertimeService,
                           EmployeeProfileService employeeProfileService,
                           UserService userService) {
        this.employeeOvertimeService = employeeOvertimeService;
        this.employeeProfileService = employeeProfileService;
        this.userService = userService;

        loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        if (loggedInUser != null) {
            userDTO = userService.getByUsername(loggedInUser);
        }

        if (userDTO != null) {
            employeeProfileDTO = userDTO.getEmployeeDTO();
        }

        if (employeeProfileDTO != null) {
            employeeOvertimeDTOList = employeeOvertimeService.findByEmployeeDTO(employeeProfileDTO);
        }

        employeeOvertimeDTOGrid = new Grid<>(EmployeeOvertimeDTO.class, false);
        overtimeFilingLayout = new FormLayout();

        this.buildOvertimeFilingLayout();
        this.buildEmployeeOvertimeFilingDTOGrid();

        this.setSizeFull();
        this.setAlignItems(Alignment.STRETCH);
        this.setPadding(true);
        this.add(overtimeFilingLayout, employeeOvertimeDTOGrid);
    }

    private void buildOvertimeFilingLayout() {
        employeeApproverDTOComboBox = new ComboBox<>("Approver");
        // Create the query object that will do the pagination of employee records in the combo box component.
        Query<EmployeeProfileDTO, Void> query = new Query<>();
        approverEmployeeProfileDTOList = employeeProfileService.getEmployeesWhoAreApprovers();
        employeeApproverDTOComboBox.setItems((employeeDTO, filterString) ->
                        employeeDTO.getEmployeeFullName().toLowerCase().contains(filterString.toLowerCase()),
                approverEmployeeProfileDTOList);
        employeeApproverDTOComboBox.setItemLabelGenerator(employeeDTO ->
                employeeDTO.getFirstName().concat(" ").concat(employeeDTO.getLastName()));
        employeeApproverDTOComboBox.setRequired(true);
        employeeApproverDTOComboBox.setRequiredIndicatorVisible(true);

        overtimeDatePicker = new DatePicker("Overtime Date");
        overtimeDatePicker.setRequiredIndicatorVisible(true);
        overtimeDatePicker.setRequired(true);

        startTimePicker = new TimePicker("Start Time");
        startTimePicker.setStep(Duration.ofMinutes(1));
        startTimePicker.setRequiredIndicatorVisible(true);
        startTimePicker.setRequired(true);

        endTimePicker = new TimePicker("End Time");
        endTimePicker.setStep(Duration.ofMinutes(1));
        endTimePicker.setRequiredIndicatorVisible(true);
        endTimePicker.setRequired(true);

        numberOfHoursField = new IntegerField("No. of hours");
        numberOfHoursField.setMin(1);
        numberOfHoursField.setMax(4);
        numberOfHoursField.setRequired(true);
        numberOfHoursField.setRequiredIndicatorVisible(true);

        saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            // Save the overtime filing.
            if (employeeOvertimeDTO == null) {
                employeeOvertimeDTO = new EmployeeOvertimeDTO();
                employeeOvertimeDTO.setCreatedBy(loggedInUser);
            }

            employeeOvertimeDTO.setEmployeeProfile(employeeProfileDTO);
            employeeOvertimeDTO.setAssignedApproverEmployeeDTO(employeeApproverDTOComboBox.getValue());
            employeeOvertimeDTO.setOvertimeDate(overtimeDatePicker.getValue());
            employeeOvertimeDTO.setOvertimeStartTime(startTimePicker.getValue());
            employeeOvertimeDTO.setOvertimeEndTime(endTimePicker.getValue());
            employeeOvertimeDTO.setOvertimeNumberOfHours(numberOfHoursField.getValue());
            employeeOvertimeDTO.setStatus("PENDING");
            employeeOvertimeDTO.setUpdatedBy(loggedInUser);

            employeeOvertimeService.saveOrUpdate(employeeOvertimeDTO);

            // Clear the fields.
            this.clearFields();

            // Show notification message.
            Notification notification = Notification.show("You have successfully filed your overtime request.",  5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Update the data grid.
            this.updateOvertimeDataGrid();
        });

        cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(buttonClickEvent -> this.clearFields());

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setPadding(true);

        overtimeFilingLayout.setColspan(buttonLayout, 2);
        overtimeFilingLayout.add(employeeApproverDTOComboBox,
                overtimeDatePicker,
                startTimePicker,
                endTimePicker,
                numberOfHoursField,
                buttonLayout);
        overtimeFilingLayout.setMaxWidth("720px");
    }

    private void buildEmployeeOvertimeFilingDTOGrid() {
        employeeOvertimeDTOGrid.addColumn(employeeLeaveFilingDTO ->
                        employeeLeaveFilingDTO.getAssignedApproverEmployeeDTO().getFirstName()
                        + " "
                        + employeeLeaveFilingDTO.getAssignedApproverEmployeeDTO().getLastName())
                .setHeader("Approver");
        employeeOvertimeDTOGrid.addColumn(new LocalDateRenderer<>(EmployeeOvertimeDTO::getOvertimeDate,
                        "MMM dd, yyyy"))
                .setHeader("Overtime Date");
        employeeOvertimeDTOGrid.addColumn(employeeOvertimeDTO ->
                        employeeOvertimeDTO.getOvertimeStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")))
                .setHeader("Start Time");
        employeeOvertimeDTOGrid.addColumn(employeeOvertimeDTO ->
                        employeeOvertimeDTO.getOvertimeEndTime().format(DateTimeFormatter.ofPattern("hh:mm a")))
                .setHeader("End Time");
        employeeOvertimeDTOGrid.addColumn(employeeOvertimeDTO ->
                        String.valueOf(employeeOvertimeDTO.getOvertimeNumberOfHours()))
                .setHeader("No. of Hours");
        employeeOvertimeDTOGrid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, (layout, employeeOvertimeDTO) -> {
            Icon statusIcon = null;
            String theme = "";
            String status = employeeOvertimeDTO.getStatus();

            if (status.equals("APPROVED")) {
                statusIcon = VaadinIcon.CHECK.create();
                theme = "badge success";
            } else if (status.equals("REJECTED")) {
                statusIcon = VaadinIcon.EXCLAMATION_CIRCLE_O.create();
                theme = "badge error";
            } else if (status.equals("CANCELLED")) {
                statusIcon = VaadinIcon.BAN.create();
                theme = "badge contrast";
            } else {
                statusIcon = VaadinIcon.INFO_CIRCLE_O.create();
                theme = "badge";
            }

            statusIcon.getStyle().set("padding", "var(--lumo-space-xs)");

            Span statusSpan = new Span(statusIcon, new Span(status));
            statusSpan.getElement().setAttribute("theme", theme);

            layout.setJustifyContentMode(JustifyContentMode.CENTER);
            layout.add(statusSpan);
        })).setHeader("Status");
        employeeOvertimeDTOGrid.addComponentColumn(employeeOvertimeDTO ->
                employeeOvertimeDTO.getStatus().equalsIgnoreCase("PENDING")
                        ? this.buildRowToolbar()
                        : new Span()).setHeader("Action");
        employeeOvertimeDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        employeeOvertimeDTOGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        employeeOvertimeDTOGrid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        employeeOvertimeDTOGrid.setAllRowsVisible(true);
        employeeOvertimeDTOGrid.setEmptyStateText("No overtime filing found.");
        employeeOvertimeDTOGrid.setItems(employeeOvertimeDTOList);
    }

    public Component buildRowToolbar() {
        HorizontalLayout rowToolbarLayout = new HorizontalLayout();

        Button cancelOvertimeButton = new Button();
        cancelOvertimeButton.setTooltipText("Cancel Overtime");
        cancelOvertimeButton.setIcon(LineAwesomeIcon.BAN_SOLID.create());
        cancelOvertimeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        cancelOvertimeButton.addClickListener(buttonClickEvent -> cancelOvertimeButton.getUI().ifPresent(ui -> {
            if (employeeOvertimeDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                if (employeeOvertimeDTOGrid.getSelectionModel().getFirstSelectedItem().get().getStatus().equals("PENDING")) {
                    // Show the confirmation dialog.
                    ConfirmDialog confirmDialog = new ConfirmDialog();
                    confirmDialog.setHeader("Cancel Leave Filing");
                    confirmDialog.setText(new Html("<p>Are you sure you want to cancel your filed overtime?</p>"));
                    confirmDialog.setConfirmText("Yes");
                    confirmDialog.addConfirmListener(confirmEvent -> {
                        // Set the status of leave filing to "CANCELLED" and save.
                        EmployeeOvertimeDTO employeeOvertimeDTO = employeeOvertimeDTOGrid.getSelectionModel().getFirstSelectedItem().get();
                        employeeOvertimeDTO.setStatus("CANCELLED");
                        employeeOvertimeDTO.setUpdatedBy(loggedInUser);

                        employeeOvertimeService.saveOrUpdate(employeeOvertimeDTO);

                        // Show notification message.
                        Notification notification = Notification.show("You have successfully cancelled your overtime request.",  5000, Notification.Position.TOP_CENTER);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                        // Close the confirmation dialog.
                        confirmDialog.close();

                        // Update the data grid.
                        this.updateOvertimeDataGrid();
                    });
                    confirmDialog.setCancelable(true);
                    confirmDialog.setCancelText("No");
                    confirmDialog.open();
                } else {
                    Notification notification = Notification.show("You cannot cancel your overtime request that is already approved, rejected or cancelled.",  5000, Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR, NotificationVariant.LUMO_PRIMARY);
                }
            }
        }));

        Button editOvertimeButton = new Button();
        editOvertimeButton.setTooltipText("Edit Overtime");
        editOvertimeButton.setIcon(LineAwesomeIcon.PENCIL_ALT_SOLID.create());
        editOvertimeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        editOvertimeButton.addClickListener(buttonClickEvent -> editOvertimeButton.getUI().ifPresent(ui -> {
            if (employeeOvertimeDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                if (employeeOvertimeDTOGrid.getSelectionModel().getFirstSelectedItem().get().getStatus().equals("PENDING")) {
                    employeeOvertimeDTO = employeeOvertimeDTOGrid.getSelectionModel().getFirstSelectedItem().get();

                    employeeApproverDTOComboBox.setValue(employeeOvertimeDTO.getAssignedApproverEmployeeDTO());
                    overtimeDatePicker.setValue(employeeOvertimeDTO.getOvertimeDate());
                    startTimePicker.setValue(employeeOvertimeDTO.getOvertimeStartTime());
                    endTimePicker.setValue(employeeOvertimeDTO.getOvertimeEndTime());
                    numberOfHoursField.setValue(employeeOvertimeDTO.getOvertimeNumberOfHours());
                } else {
                    Notification notification = Notification.show("You cannot edit your overtime request that is already approved, rejected or cancelled.",  5000, Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR, NotificationVariant.LUMO_PRIMARY);
                }
            }
        }));

        rowToolbarLayout.add(cancelOvertimeButton, editOvertimeButton);
        rowToolbarLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        rowToolbarLayout.getStyle().set("flex-wrap", "wrap");

        return rowToolbarLayout;
    }

    private void clearFields() {
        employeeApproverDTOComboBox.clear();
        overtimeDatePicker.clear();
        startTimePicker.clear();
        endTimePicker.clear();
        numberOfHoursField.clear();
    }

    private void updateOvertimeDataGrid() {
        employeeOvertimeDTOList = employeeOvertimeService.findByEmployeeDTO(employeeProfileDTO);
        employeeOvertimeDTOGrid.setItems(employeeOvertimeDTOList);
    }
}

package io.softwaregarage.hris.attendance.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.softwaregarage.hris.admin.dtos.UserDTO;
import io.softwaregarage.hris.admin.services.UserService;
import io.softwaregarage.hris.attendance.dtos.EmployeeOvertimeDTO;
import io.softwaregarage.hris.attendance.services.EmployeeOvertimeService;
import io.softwaregarage.hris.commons.views.MainLayout;
import io.softwaregarage.hris.utils.SecurityUtil;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.vaadin.lineawesome.LineAwesomeIcon;

@RolesAllowed({"ROLE_ADMIN",
        "ROLE_HR_MANAGER",
        "ROLE_HR_SUPERVISOR",
        "ROLE_MANAGER",
        "ROLE_SUPERVISOR"})
@Route(value = "overtime-approvals-list-view", layout = MainLayout.class)
@PageTitle("Overtime Approvals")
public class EmployeeOvertimeApprovalsList extends VerticalLayout {
    @Resource
    private final EmployeeOvertimeService employeeOvertimeService;

    @Resource
    private final UserService userService;

    private Grid<EmployeeOvertimeDTO> overtimeDTOGrid;
    private TextField searchFilterTextField;

    private final String loggedInUser;
    private UserDTO userDTO;

    public EmployeeOvertimeApprovalsList(EmployeeOvertimeService employeeOvertimeService,
                                          UserService userService) {
        this.employeeOvertimeService = employeeOvertimeService;
        this.userService = userService;

        // Get the logged-in user of the system.
        loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        if (SecurityUtil.getAuthenticatedUser() != null) {
            userDTO = userService.getByUsername(loggedInUser);
        }

        this.add(buildHeaderToolbar(), buildOvertimeFilingDTOGrid());
        this.setSizeFull();
        this.setAlignItems(Alignment.STRETCH);
    }

    public HorizontalLayout buildHeaderToolbar() {
        HorizontalLayout headerToolbarLayout = new HorizontalLayout();

        searchFilterTextField = new TextField();
        searchFilterTextField.setWidth("350px");
        searchFilterTextField.setPlaceholder("Search");
        searchFilterTextField.setPrefixComponent(LineAwesomeIcon.SEARCH_SOLID.create());
        searchFilterTextField.getStyle().set("margin", "0 auto 0 0");
        searchFilterTextField.setValueChangeMode(ValueChangeMode.LAZY);
        searchFilterTextField.addValueChangeListener(valueChangeEvent -> this.updateOvertimeFilingDTOGrid());

        headerToolbarLayout.add(searchFilterTextField);
        headerToolbarLayout.setAlignItems(Alignment.CENTER);
        headerToolbarLayout.getThemeList().clear();

        return headerToolbarLayout;
    }

    private Grid<EmployeeOvertimeDTO> buildOvertimeFilingDTOGrid() {
        overtimeDTOGrid = new Grid<>(EmployeeOvertimeDTO.class, false);

        overtimeDTOGrid.addColumn(employeeOvertimeDTO -> employeeOvertimeDTO.getEmployeeProfile().getEmployeeNumber())
                .setHeader("Employee No.")
                .setSortable(true);
        overtimeDTOGrid.addColumn(employeeOvertimeDTO -> employeeOvertimeDTO.getEmployeeProfile().getFirstName()
                        .concat(" ")
                        .concat(employeeOvertimeDTO.getEmployeeProfile().getMiddleName())
                        .concat(" ")
                        .concat(employeeOvertimeDTO.getEmployeeProfile().getLastName())
                        .concat(employeeOvertimeDTO.getEmployeeProfile().getSuffix() != null ? employeeOvertimeDTO.getEmployeeProfile().getSuffix() : ""))
                .setHeader("Employee Name")
                .setSortable(true);
        overtimeDTOGrid.addColumn(new LocalDateRenderer<>(EmployeeOvertimeDTO::getOvertimeDate, "MMM dd, yyyy"))
                .setHeader("Date")
                .setSortable(true);
        overtimeDTOGrid.addColumn(employeeOvertimeDTO -> employeeOvertimeDTO.getOvertimeStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")))
                .setHeader("Time Start")
                .setSortable(true);
        overtimeDTOGrid.addColumn(employeeOvertimeDTO -> employeeOvertimeDTO.getOvertimeEndTime().format(DateTimeFormatter.ofPattern("hh:mm a")))
                .setHeader("Time End")
                .setSortable(true);
        overtimeDTOGrid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, (layout, employeeOvertimeDTO) -> {
                    String theme;
                    String status = employeeOvertimeDTO.getStatus();

                    if ("PENDING".equalsIgnoreCase(status)) {
                        theme = String.format("badge %s", "contrast");
                    } else if ("APPROVED".equalsIgnoreCase(status)) {
                        theme = String.format("badge %s", "success");
                    } else if ("REJECTED".equalsIgnoreCase(status)) {
                        theme = String.format("badge %s", "error");
                    } else {
                        theme = String.format("badge");
                    }

                    Span activeSpan = new Span();
                    activeSpan.getElement().setAttribute("theme", theme);
                    activeSpan.setText(status);

                    layout.setJustifyContentMode(JustifyContentMode.CENTER);
                    layout.add(activeSpan);
                }))
                .setHeader("Status")
                .setSortable(true);
        overtimeDTOGrid.addComponentColumn(employeeOvertimeDTO -> buildRowToolbar()).setHeader("Action");
        overtimeDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        overtimeDTOGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        overtimeDTOGrid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        overtimeDTOGrid.setAllRowsVisible(true);
        overtimeDTOGrid.setEmptyStateText("No pending overtime approvals found.");
        overtimeDTOGrid.setItems(employeeOvertimeService.findByAssignedApproverEmployeeDTO(userDTO.getEmployeeDTO())
                .stream()
                .filter(employeeOvertimeDTO -> employeeOvertimeDTO.getStatus()
                        .equalsIgnoreCase("PENDING")).toList());

        return overtimeDTOGrid;
    }

    public HorizontalLayout buildRowToolbar() {
        HorizontalLayout rowToolbarLayout = new HorizontalLayout();

        Button approveButton = new Button();
        approveButton.setTooltipText("Approve Leave");
        approveButton.setIcon(LineAwesomeIcon.THUMBS_UP.create());
        approveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        approveButton.addClickListener(buttonClickEvent -> approveButton.getUI().ifPresent(ui -> {
            if (overtimeDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                if (overtimeDTOGrid.getSelectionModel().getFirstSelectedItem().get().getStatus().equals("PENDING")) {
                    EmployeeOvertimeDTO employeeOvertimeDTO = overtimeDTOGrid.getSelectionModel().getFirstSelectedItem().get();

                    // Set the status of leave filing to "APPROVED" and save.
                    employeeOvertimeDTO.setStatus("APPROVED");
                    employeeOvertimeDTO.setUpdatedBy(loggedInUser);

                    employeeOvertimeService.saveOrUpdate(employeeOvertimeDTO);

                    // Update the data grid.
                    overtimeDTOGrid.setItems(employeeOvertimeService.findByEmployeeDTO(userDTO.getEmployeeDTO()));

                    // Show notification message.
                    Notification notification = Notification.show("You have successfully approved the overtime request.",  5000, Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    // Reload the main layout.
                    reloadMainLayout();
                }
            }
        }));

        Button rejectButton = new Button();
        rejectButton.setTooltipText("Reject Leave");
        rejectButton.setIcon(LineAwesomeIcon.THUMBS_DOWN.create());
        rejectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        rejectButton.addClickListener(buttonClickEvent -> rejectButton.getUI().ifPresent(ui -> {
            if (overtimeDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                if (overtimeDTOGrid.getSelectionModel().getFirstSelectedItem().get().getStatus().equals("PENDING")) {
                    // Show the confirmation dialog.
                    ConfirmDialog confirmDialog = new ConfirmDialog();
                    confirmDialog.setHeader("Reject Overtime Filing");
                    confirmDialog.setText(new Html("<p>Are you sure you want to reject the filed overtime?</p>"));
                    confirmDialog.setConfirmText("Yes");
                    confirmDialog.addConfirmListener(confirmEvent -> {
                        // Set the status of leave filing to "REJECTED" and save.
                        EmployeeOvertimeDTO employeeOvertimeDTO = overtimeDTOGrid.getSelectionModel().getFirstSelectedItem().get();
                        employeeOvertimeDTO.setStatus("REJECTED");
                        employeeOvertimeDTO.setUpdatedBy(loggedInUser);

                        employeeOvertimeService.saveOrUpdate(employeeOvertimeDTO);

                        // Show notification message.
                        Notification notification = Notification.show("You have successfully rejected the overtime request.",  5000, Notification.Position.TOP_CENTER);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                        // Close the confirmation dialog.
                        confirmDialog.close();

                        // Update the data grid.
                        updateOvertimeFilingDTOGrid();

                        // Reload the main layout.
                        reloadMainLayout();
                    });
                    confirmDialog.setCancelable(true);
                    confirmDialog.setCancelText("No");
                    confirmDialog.open();
                }
            }
        }));

        rowToolbarLayout.add(approveButton, rejectButton);
        rowToolbarLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        rowToolbarLayout.getStyle().set("flex-wrap", "wrap");

        return rowToolbarLayout;
    }

    private void updateOvertimeFilingDTOGrid() {
        if (!searchFilterTextField.getValue().isEmpty()) {
            overtimeDTOGrid.setItems(employeeOvertimeService.findByParameter(searchFilterTextField.getValue())
                    .stream()
                    .filter(employeeOvertimeDTO ->
                            employeeOvertimeDTO.getStatus().equalsIgnoreCase("PENDING"))
                    .filter(employeeOvertimeDTO ->
                            employeeOvertimeDTO.getAssignedApproverEmployeeDTO().equals(userDTO.getEmployeeDTO()))
                    .toList());
        } else {
            overtimeDTOGrid.setItems(employeeOvertimeService.findByAssignedApproverEmployeeDTO(userDTO.getEmployeeDTO())
                    .stream().filter(employeeOvertimeDTO -> employeeOvertimeDTO.getStatus()
                            .equalsIgnoreCase("PENDING")).toList());
        }
    }

    private void reloadMainLayout() {
        getUI().get().getPage().reload();
    }
}

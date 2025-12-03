package io.softwaregarage.hris.attendance.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.server.streams.DownloadEvent;
import com.vaadin.flow.server.streams.DownloadHandler;
import io.softwaregarage.hris.admin.dtos.UserDTO;
import io.softwaregarage.hris.admin.services.UserService;
import io.softwaregarage.hris.attendance.dtos.EmployeeShiftScheduleDTO;
import io.softwaregarage.hris.attendance.dtos.EmployeeTimesheetDTO;
import io.softwaregarage.hris.attendance.services.EmployeeShiftScheduleService;
import io.softwaregarage.hris.attendance.services.EmployeeTimesheetService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;
import io.softwaregarage.hris.utils.SecurityUtil;
import io.softwaregarage.hris.commons.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.vaadin.lineawesome.LineAwesomeIcon;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR",
               "ROLE_MANAGER",
               "ROLE_SUPERVISOR"})
@PageTitle("Timesheet Approvals")
@Route(value = "timesheets-view", layout = MainLayout.class)
public class EmployeeTimesheetListView extends VerticalLayout {
    @Resource private final EmployeeTimesheetService employeeTimesheetService;
    @Resource private final EmployeeProfileService employeeProfileService;
    @Resource private final EmployeeShiftScheduleService employeeShiftScheduleService;
    @Resource private final UserService userService;

    private Grid<EmployeeTimesheetDTO> timesheetDTOGrid;
    private ComboBox<EmployeeProfileDTO> employeeDTOComboBox;
    private DatePicker startDatePicker, endDatePicker;

    List<EmployeeTimesheetDTO> employeeTimesheetDTOList = new ArrayList<>();
    private String loggedInUser;

    public EmployeeTimesheetListView(EmployeeTimesheetService employeeTimesheetService,
                                     EmployeeProfileService employeeProfileService,
                                     EmployeeShiftScheduleService employeeShiftScheduleService,
                                     UserService userService) {
        this.employeeTimesheetService = employeeTimesheetService;
        this.employeeProfileService = employeeProfileService;
        this.employeeShiftScheduleService = employeeShiftScheduleService;
        this.userService = userService;

        // Get the logged-in user of the system.
        loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        this.add(this.buildHeaderToolbar(),
                 this.buildTimesheetDTOGrid());
        this.setSizeFull();
        this.setAlignItems(Alignment.STRETCH);
    }

    public HorizontalLayout buildHeaderToolbar() {
        HorizontalLayout headerToolbarLayout = new HorizontalLayout();

        employeeDTOComboBox = new ComboBox<>("Employee");

        // Create the query object that will do the pagination of employee records in the combo box component.
        Query<EmployeeProfileDTO, Void> employeeQuery = new Query<>();
        List<EmployeeProfileDTO> employeeProfileDTOList = employeeProfileService.getAll(employeeQuery.getPage(), employeeQuery.getPageSize());
        UserDTO userDTO = userService.getByUsername(loggedInUser);

        if (!"ROLE_ADMIN".equals(userDTO.getRole())) {
            List<EmployeeProfileDTO> filteredProfiles = employeeProfileDTOList.stream()
                    .filter(employeeProfileDTO -> employeeShiftScheduleService
                            .getEmployeeShiftScheduleByEmployeeDTO(employeeProfileDTO).stream()
                            .filter(EmployeeShiftScheduleDTO::isActiveShift)
                            .findFirst()
                            .map(EmployeeShiftScheduleDTO::getAssignedApproverEmployeeProfileDTO)
                            .filter(obj -> true)
                            .map(approver -> approver.getEmployeeNumber().equals(userDTO.getEmployeeDTO().getEmployeeNumber()))
                            .orElse(false))
                    .toList();

            employeeDTOComboBox.setItems((employeeDTO, filterString) -> employeeDTO
                            .getEmployeeFullName().toLowerCase().contains(filterString.toLowerCase()), filteredProfiles);
        } else {
            employeeDTOComboBox.setItems((employeeDTO, filterString) -> employeeDTO
                            .getEmployeeFullName().toLowerCase().contains(filterString.toLowerCase()), employeeProfileDTOList);
        }

        employeeDTOComboBox.setItemLabelGenerator(EmployeeProfileDTO::getEmployeeFullName);
        employeeDTOComboBox.setClearButtonVisible(true);

        startDatePicker = new DatePicker("Start date");
        startDatePicker.setRequired(true);

        endDatePicker = new DatePicker("End date");
        endDatePicker.setRequired(true);

        Anchor downloadTimesheetLink = new Anchor(createCsvDownloadHandler(employeeDTOComboBox.getValue(),
                                                            startDatePicker.getValue(),
                                                            endDatePicker.getValue()),
                                                            "Download Timesheet");
        downloadTimesheetLink.getStyle().set("margin", "0 0 0 auto");
        downloadTimesheetLink.setEnabled(false);

        Button searchButton = new Button("Search");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.getStyle().set("display", "block");
        searchButton.addClickListener(event -> {
            executeSearchAndUpdateResult(employeeDTOComboBox.getValue(), startDatePicker.getValue(), endDatePicker.getValue());
            downloadTimesheetLink.setEnabled(timesheetDTOGrid.getDataProvider().size(new Query<>()) >= 1);
        });

        headerToolbarLayout.add(employeeDTOComboBox,
                                startDatePicker,
                                endDatePicker,
                                searchButton,
                                downloadTimesheetLink);
        headerToolbarLayout.setWrap(true);
        headerToolbarLayout.setAlignItems(Alignment.END);

        return headerToolbarLayout;
    }

    public void executeSearchAndUpdateResult(EmployeeProfileDTO employeeProfileDTO, LocalDate startDate, LocalDate endDate) {
        if (employeeProfileDTO != null) {
            employeeTimesheetDTOList = employeeTimesheetService.findTimesheetByEmployeeAndLogDate(employeeProfileDTO,
                    startDate, endDate);
        } else {
            employeeTimesheetDTOList = employeeTimesheetService.findByLogDateRange(startDatePicker.getValue(),
                    endDatePicker.getValue());
        }

        this.updateEmployeeTimesheetGrid(employeeTimesheetDTOList.stream()
                .filter(timesheet -> timesheet.getStatus().equals("PENDING")
                        || timesheet.getStatus().equals("REJECTED"))
                .toList());
    }

    private Grid<EmployeeTimesheetDTO> buildTimesheetDTOGrid() {
        timesheetDTOGrid = new Grid<>(EmployeeTimesheetDTO.class, false);

        // Include the ID of the record but do not display it.
        timesheetDTOGrid.addColumn(EmployeeTimesheetDTO::getId).setHeader("ID").setVisible(false);
        timesheetDTOGrid.addColumn(employeeTimesheetDTO -> employeeTimesheetDTO.getEmployeeDTO().getEmployeeNumber())
                        .setHeader("Employee No.")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(employeeTimesheetDTO -> employeeTimesheetDTO.getEmployeeDTO().getFirstName()
                                                                            .concat(" ")
                                                                            .concat(employeeTimesheetDTO.getEmployeeDTO().getLastName())
                                                                            .concat(employeeTimesheetDTO.getEmployeeDTO().getSuffix() != null ? employeeTimesheetDTO.getEmployeeDTO().getSuffix() : ""))
                        .setHeader("Employee Name")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(employeeTimesheetDTO -> DateTimeFormatter.ofPattern("MMM dd, yyyy").format(employeeTimesheetDTO.getLogDate()))
                        .setHeader("Log Date")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(employeeTimesheetDTO -> employeeTimesheetDTO.getShiftScheduleDTO().getShiftSchedule())
                        .setHeader("Shift Schedule")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(employeeTimesheetDTO -> DateTimeFormatter.ofPattern("hh:mm:ss a").format(employeeTimesheetDTO.getLogTime()))
                        .setHeader("Log Time")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(EmployeeTimesheetDTO::getLogDetail)
                        .setHeader("Log Detail")
                        .setSortable(true);
        timesheetDTOGrid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, (layout, employeeTimesheetDTO) -> {
                                        String theme;
                                        String status = employeeTimesheetDTO.getStatus();
                                        switch (status) {
                                            case "APPROVED":
                                                theme = String.format("badge success");
                                                break;
                                            case "REJECTED":
                                                theme = String.format("badge error");
                                                break;
                                            case "PROCESSED":
                                                theme = String.format("badge info");
                                                break;
                                            default:
                                                theme = String.format("badge contrast");
                                        }

                                        Span activeSpan = new Span();
                                        activeSpan.getElement().setAttribute("theme", theme);
                                        activeSpan.setText(employeeTimesheetDTO.getStatus());

                                        layout.setJustifyContentMode(JustifyContentMode.CENTER);
                                        layout.add(activeSpan);
                                    }))
                        .setHeader("Status")
                        .setSortable(true);
        timesheetDTOGrid.addComponentColumn(employeeTimesheetDTO -> buildRowToolbar(employeeTimesheetDTO)).setHeader("Action");
        timesheetDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                                          GridVariant.LUMO_COLUMN_BORDERS,
                                          GridVariant.LUMO_WRAP_CELL_CONTENT);
        timesheetDTOGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        timesheetDTOGrid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        timesheetDTOGrid.setEmptyStateText("No timesheet records found.");

        return timesheetDTOGrid;
    }

    public HorizontalLayout buildRowToolbar(EmployeeTimesheetDTO employeeTimesheetDTO) {
        HorizontalLayout rowToolbarLayout = new HorizontalLayout();

        Button viewButton = new Button();
        viewButton.setTooltipText("View Time Log");
        viewButton.setIcon(LineAwesomeIcon.SEARCH_SOLID.create());
        viewButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        viewButton.addClickListener(buttonClickEvent -> viewButton.getUI().ifPresent(ui -> {
            buildViewTimesheetDialog(employeeTimesheetDTO).open();
        }));

        Button editButton = new Button();
        editButton.setTooltipText("Edit Time Log");
        editButton.setIcon(LineAwesomeIcon.PENCIL_ALT_SOLID.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        editButton.addClickListener(buttonClickEvent -> editButton.getUI().ifPresent(ui -> {
            if (employeeTimesheetDTO.getId() != null) {
                buildEditTimesheetDialog(employeeTimesheetDTO).open();
            }
        }));

        Button approveButton = new Button();
        approveButton.setTooltipText("Approve Time log");
        approveButton.setIcon(LineAwesomeIcon.THUMBS_UP.create());
        approveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        approveButton.addClickListener(buttonClickEvent -> approveButton.getUI().ifPresent(ui -> {
            if (employeeTimesheetDTO.getId() != null) {
                employeeTimesheetDTO.setStatus("APPROVED");
                employeeTimesheetDTO.setUpdatedBy(loggedInUser);
                employeeTimesheetService.saveOrUpdate(employeeTimesheetDTO);

                Notification notification = Notification.show("Time log approved successfully.", 5000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                executeSearchAndUpdateResult(employeeDTOComboBox.getValue(), startDatePicker.getValue(), endDatePicker.getValue());
            }
        }));

        Button rejectButton = new Button();
        rejectButton.setTooltipText("Reject Time Log");
        rejectButton.setIcon(LineAwesomeIcon.THUMBS_DOWN.create());
        rejectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        rejectButton.addClickListener(buttonClickEvent -> rejectButton.getUI().ifPresent(ui -> {
            if (employeeTimesheetDTO.getId() != null) {
                employeeTimesheetDTO.setStatus("REJECTED");
                employeeTimesheetDTO.setUpdatedBy(loggedInUser);
                employeeTimesheetService.saveOrUpdate(employeeTimesheetDTO);

                Notification notification = Notification.show("Time log rejected successfully.", 5000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

                executeSearchAndUpdateResult(employeeDTOComboBox.getValue(), startDatePicker.getValue(), endDatePicker.getValue());
            }
        }));

        // Removed the Approve button once the status of the employee's timesheet is approved.
        if ("REJECTED".equals(employeeTimesheetDTO.getStatus())) {
            rowToolbarLayout.add(viewButton, editButton, approveButton);
        } else {
            rowToolbarLayout.add(viewButton, editButton, approveButton, rejectButton);
        }

        rowToolbarLayout.setJustifyContentMode(JustifyContentMode.START);
        rowToolbarLayout.getStyle().set("flex-wrap", "wrap");

        return rowToolbarLayout;
    }

    private void updateEmployeeTimesheetGrid(List<EmployeeTimesheetDTO> employeeTimesheetDTOList) {
        timesheetDTOGrid.setItems(employeeTimesheetDTOList);
    }

    private Dialog buildViewTimesheetDialog(EmployeeTimesheetDTO employeeTimesheetDTO) {
        FormLayout timesheetDetailsLayout = new FormLayout();

        Span employeeNoLabelSpan = new Span("Employee No");
        Span employeeNoValueSpan = new Span(employeeTimesheetDTO.getEmployeeDTO().getEmployeeNumber());
        employeeNoValueSpan.getStyle().setFontWeight("bold");

        Span employeeNameLabelSpan = new Span("Employee Name");
        String employeeName = employeeTimesheetDTO.getEmployeeDTO().getFirstName()
                .concat(" ")
                .concat(employeeTimesheetDTO.getEmployeeDTO().getMiddleName())
                .concat(" ")
                .concat(employeeTimesheetDTO.getEmployeeDTO().getLastName())
                .concat(employeeTimesheetDTO.getEmployeeDTO().getSuffix() != null ? " ".concat(employeeTimesheetDTO.getEmployeeDTO().getSuffix()) : "");
        Span employeeNameValueSpan = new Span(employeeName);
        employeeNameValueSpan.getStyle().setFontWeight("bold");

        Span logDateLabelSpan = new Span("Log Date");
        Span logDateValueSpan = new Span(employeeTimesheetDTO.getLogDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        logDateValueSpan.getStyle().setFontWeight("bold");

        Span logTimeInLabelSpan = new Span("Log Time");
        Span logTimeInValueSpan = new Span(employeeTimesheetDTO.getLogTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
        logTimeInValueSpan.getStyle().setFontWeight("bold");

        Span logTimeOutLabelSpan = new Span("Log Detail");
        Span logTimeOutValueSpan = new Span(employeeTimesheetDTO.getLogDetail());
        logTimeOutValueSpan.getStyle().setFontWeight("bold");

        Span shiftScheduleLabelSpan = new Span("Shift Schedule");
        Span shiftScheduleValueSpan = new Span(employeeTimesheetDTO.getShiftScheduleDTO().getShiftSchedule());
        shiftScheduleValueSpan.getStyle().setFontWeight("bold");

        Span shiftScheduleTimeLabelSpan = new Span("Shift Time");
        Span shiftScheduleTimeValueSpan = new Span(employeeTimesheetDTO.getShiftScheduleDTO()
                                                                  .getShiftStartTime()
                                                                  .format(DateTimeFormatter.ofPattern("hh:mm a"))
                                                                  .concat(" to ")
                                                                  .concat(employeeTimesheetDTO.getShiftScheduleDTO()
                                                                                               .getShiftEndTime()
                                                                                               .format(DateTimeFormatter.ofPattern("hh:mm a"))));
        shiftScheduleTimeValueSpan.getStyle().setFontWeight("bold");

        Span statusLabelSpan = new Span("Status");
        Span statusValueSpan = new Span(employeeTimesheetDTO.getStatus());
        statusValueSpan.getStyle().setFontWeight("bold");

        String fileName = "log_image_".concat(employeeTimesheetDTO.getEmployeeDTO().getEmployeeNumber()).concat(".jpg");
        Image logImage = this.convertLogImage(fileName, employeeTimesheetDTO.getLogImage());

        timesheetDetailsLayout.add(employeeNoLabelSpan,
                                   employeeNoValueSpan,
                                   employeeNameLabelSpan,
                                   employeeNameValueSpan,
                                   logDateLabelSpan,
                                   logDateValueSpan,
                                   logTimeInLabelSpan,
                                   logTimeInValueSpan,
                                   logTimeOutLabelSpan,
                                   logTimeOutValueSpan,
                                   shiftScheduleLabelSpan,
                                   shiftScheduleValueSpan,
                                   shiftScheduleTimeLabelSpan,
                                   shiftScheduleTimeValueSpan,
                                   statusLabelSpan,
                                   statusValueSpan,
                                   logImage);
        timesheetDetailsLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                                                  new FormLayout.ResponsiveStep("500px", 2));
        timesheetDetailsLayout.setColspan(logImage, 2);
        timesheetDetailsLayout.getStyle().set("width", "500px").set("max-width", "100%");

        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setHeaderTitle("View Timesheet Details");
        dialog.setCloseOnOutsideClick(false);
        dialog.add(timesheetDetailsLayout);

        Button closeButton = new Button("Close", e -> dialog.close());

        dialog.getFooter().add(closeButton);

        return dialog;
    }

    private Dialog buildEditTimesheetDialog(EmployeeTimesheetDTO employeeTimesheetDTO) {
        Span employeeNoLabelSpan = new Span("Employee No");
        Span employeeNoValueSpan = new Span(employeeTimesheetDTO.getEmployeeDTO().getEmployeeNumber());
        employeeNoValueSpan.getStyle().setFontWeight("bold");

        Span employeeNameLabelSpan = new Span("Employee Name");
        String employeeName = employeeTimesheetDTO.getEmployeeDTO().getFirstName()
                .concat(" ")
                .concat(employeeTimesheetDTO.getEmployeeDTO().getMiddleName())
                .concat(" ")
                .concat(employeeTimesheetDTO.getEmployeeDTO().getLastName())
                .concat(employeeTimesheetDTO.getEmployeeDTO().getSuffix() != null ? " ".concat(employeeTimesheetDTO.getEmployeeDTO().getSuffix()) : "");
        Span employeeNameValueSpan = new Span(employeeName);
        employeeNameValueSpan.getStyle().setFontWeight("bold");

        DatePicker logDatePicker = new DatePicker("Log Date");
        logDatePicker.setValue(employeeTimesheetDTO.getLogDate());

        TimePicker logTimeTimePicker = new TimePicker("Log Time");
        logTimeTimePicker.setStep(Duration.ofSeconds(1));
        logTimeTimePicker.setRequired(true);
        logTimeTimePicker.setRequiredIndicatorVisible(true);
        logTimeTimePicker.setValue(employeeTimesheetDTO.getLogTime());

        RadioButtonGroup<String> logDetailRadioGroup = new RadioButtonGroup<>("Log Detail");
        logDetailRadioGroup.setItems("Log In", "Log Out");
        logDetailRadioGroup.setValue(employeeTimesheetDTO.getLogDetail());

        FormLayout formLayout = new FormLayout();
        formLayout.add(employeeNoLabelSpan,
                       employeeNoValueSpan,
                       employeeNameLabelSpan,
                       employeeNameValueSpan,
                       logDatePicker,
                       logTimeTimePicker,
                       logDetailRadioGroup);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                                      new FormLayout.ResponsiveStep("500px", 2));
        formLayout.getStyle().set("width", "500px").set("max-width", "100%");

        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setHeaderTitle("Edit Timesheet Details");
        dialog.setCloseOnOutsideClick(false);
        dialog.add(formLayout);

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            employeeTimesheetDTO.setLogDate(logDatePicker.getValue());
            employeeTimesheetDTO.setLogTime(logTimeTimePicker.getValue());
            employeeTimesheetDTO.setLogDetail(logDetailRadioGroup.getValue());
            employeeTimesheetDTO.setUpdatedBy(loggedInUser);

            employeeTimesheetService.saveOrUpdate(employeeTimesheetDTO);

            // Close the dialog.
            dialog.close();

            // Show the notification message.
            Notification notification = Notification.show("Timesheet successfully updated.");
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setDuration(5000);

            // Update the timesheet grid.
            executeSearchAndUpdateResult(employeeDTOComboBox.getValue(), startDatePicker.getValue(), endDatePicker.getValue());
        });

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        return dialog;
    }

    private Image convertLogImage(String fileName, byte[] logImage) {
        Image image = new Image();
        image.setSrc(downloadHandler -> {
            try (OutputStream out = downloadHandler.getOutputStream()) {
                out.write(logImage);
                downloadHandler.setFileName(fileName);
                out.flush();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        image.setWidth("320px");
        image.setHeight("auto");
        return image;
    }

    /**
     * Returns a DownloadHandler that streams CSV for the given grid.
     * Uses event.getWriter() so text is written using correct charset.
     */
    private DownloadHandler createCsvDownloadHandler(EmployeeProfileDTO employeeProfileDTO, LocalDate startDate, LocalDate endDate) {
        if (employeeProfileDTO != null) {
            employeeTimesheetDTOList = employeeTimesheetService.findTimesheetByEmployeeAndLogDate(employeeProfileDTO, startDate, endDate);
        } else {
            employeeTimesheetDTOList = employeeTimesheetService.findByLogDateRange(startDatePicker.getValue(), endDatePicker.getValue());
        }

        return (DownloadEvent event) -> {
            // Suggested filename and content type
            event.setFileName("employee_timesheet_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM_dd_yyyy_HHmmss")) + ".csv");
            event.setContentType("text/csv; charset=" + StandardCharsets.UTF_8.name());

            try (Writer writer = event.getWriter()) {
                // CSV header
                writer.write("Employee No.,Employee Name,Log Date,Shift Schedule,Log Time,Log Detail,Status\n");

                // Fetch items from the grid's DataProvider (honors provider filters).
                // This streams rows directly to the response writer — avoids building full string in memory.
                employeeTimesheetDTOList.forEach(employeeTimesheetDTO -> {
                    try {
                        writer.write(makeCsvLine(employeeTimesheetDTO));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                writer.flush();
            } catch (IOException e) {
                // If you want, set an HTTP error status or log the error:
                // event.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new RuntimeException("Failed to generate CSV", e);
            }
        };
    }

    /** Build a properly escaped CSV line for the person (always quotes fields). */
    private String makeCsvLine(EmployeeTimesheetDTO p) {
        return quote(String.valueOf(p.getEmployeeDTO().getEmployeeNumber())) + ","
                + quote(p.getEmployeeDTO().getEmployeeFullName()) + ","
                + quote(p.getLogDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))) + ","
                + quote(p.getShiftScheduleDTO().getShiftSchedule()) + ","
                + quote(p.getLogTime().format(DateTimeFormatter.ofPattern("H:mm:ss a"))) + ","
                + quote(p.getLogDetail()) + ","
                + p.getStatus() + "\n";
    }

    /** Quote and double-quote escape */
    private String quote(String s) {
        if (s == null) {
            return "\"\"";
        }
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

}

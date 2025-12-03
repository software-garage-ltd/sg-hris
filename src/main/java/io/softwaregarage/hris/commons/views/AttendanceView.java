package io.softwaregarage.hris.commons.views;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.softwaregarage.hris.admin.dtos.UserDTO;
import io.softwaregarage.hris.admin.services.UserService;
import io.softwaregarage.hris.attendance.dtos.EmployeeShiftScheduleDTO;
import io.softwaregarage.hris.attendance.dtos.EmployeeTimesheetDTO;
import io.softwaregarage.hris.attendance.services.EmployeeShiftScheduleService;
import io.softwaregarage.hris.attendance.services.EmployeeTimesheetService;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;
import io.softwaregarage.hris.utils.SecurityUtil;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@PermitAll
@PageTitle("My Attendance")
@Route(value = "attendance-view", layout = MainLayout.class)
@JsModule("clockAndDate.js")
public class AttendanceView extends VerticalLayout {
    @Resource private final EmployeeTimesheetService employeeTimesheetService;
    @Resource private final EmployeeProfileService employeeProfileService;
    @Resource private final EmployeeShiftScheduleService employeeShiftScheduleService;
    @Resource private final UserService userService;

    private UserDTO userDTO;
    private EmployeeProfileDTO employeeProfileDTO;
    private EmployeeShiftScheduleDTO employeeShiftScheduleDTO;

    private List<EmployeeShiftScheduleDTO> listOfEmployeeShiftScheduleDTO;
    private List<EmployeeTimesheetDTO> employeeTimesheetDTOList;

    private String loggedInUser;
    private byte[] imageBytes;

    private RadioButtonGroup<String> statusRadioGroup;
    private Grid<EmployeeTimesheetDTO> employeeTimesheetGrid;

    public AttendanceView(EmployeeTimesheetService employeeTimesheetService,
                          EmployeeProfileService employeeProfileService,
                          EmployeeShiftScheduleService employeeShiftScheduleService,
                          UserService userService) {
        this.employeeTimesheetService = employeeTimesheetService;
        this.employeeProfileService = employeeProfileService;
        this.employeeShiftScheduleService = employeeShiftScheduleService;
        this.userService = userService;

        loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        if (loggedInUser != null) {
            userDTO = userService.getByUsername(loggedInUser);
        }

        if (userDTO != null) {
            employeeProfileDTO = userDTO.getEmployeeDTO();
        }

        if (employeeProfileDTO != null) {
            listOfEmployeeShiftScheduleDTO = employeeShiftScheduleService.getEmployeeShiftScheduleByEmployeeDTO(employeeProfileDTO);
        }

        if (listOfEmployeeShiftScheduleDTO != null && !listOfEmployeeShiftScheduleDTO.isEmpty()) {
            employeeShiftScheduleDTO = listOfEmployeeShiftScheduleDTO.stream().filter(employeeShiftSchedule -> employeeShiftSchedule.isActiveShift()).findFirst().get();
        } else {
            Dialog addShiftScheduleDialog = new Dialog("Add Shift Schedule");
            addShiftScheduleDialog.add(new Span("There is no assigned shift schedule to you. Ask your HR to assign you a shift."));
            addShiftScheduleDialog.getFooter().add(new Button("Back to My Dashboard",
                                             buttonClickEvent -> {
                                                                    UI.getCurrent().getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
                                                                    addShiftScheduleDialog.close();
                                                              }));
            addShiftScheduleDialog.setCloseOnOutsideClick(false);
            addShiftScheduleDialog.open();
        }

        TabSheet timesheetTabSheet = new TabSheet();
        timesheetTabSheet.add("Time Clock", this.buildCameraTimeInAndOut());
        timesheetTabSheet.add("Timesheet", this.buildEmployeeTimesheet());

        this.setSizeFull();
        this.setAlignItems(Alignment.STRETCH);
        this.setPadding(true);
        this.add(timesheetTabSheet);

        // Call the JavaScript function every second to update the clock
        UI.getCurrent().getPage().executeJs("setInterval(() => updateClock(), 1000);");
    }

    /**
     * This method will build the whole camera time in and time out.
     *
     * @return The camera time in and time out component.
     */
    private Component buildCameraTimeInAndOut() {
        // HTML element to display the clock
        Html clockHtml = new Html("<div id='clock' style='font-size: 26px; text-align: center;'></div>");

        statusRadioGroup = new RadioButtonGroup<>();
        statusRadioGroup.setItems("Log In", "Log Out");
        statusRadioGroup.setValue("Log In");

        Button submitButton = new Button("Submit");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setWidth("150px");
        submitButton.addClickListener(e -> {
            // Execute the Javascript that converts the captured image into array of bytes.
            getUI().ifPresent(ui ->
                    ui.getPage().executeJs("""
                                            const video = document.getElementById('webcam');
                                            const canvas = document.createElement('canvas');
                                            canvas.width = video.videoWidth;
                                            canvas.height = video.videoHeight;
                                            canvas.getContext('2d').drawImage(video, 0, 0);
                                            const dataUrl = canvas.toDataURL('image/png');
                                            $0.$server.receiveImage(dataUrl);
                                            """, getElement()
                    )
            );

            // Show the notification message after capturing the image.
            Notification notification = Notification.show("Log In".equals(statusRadioGroup.getValue()) ? "You have successfully logged in." : "You have successfully logged out.",  5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        HorizontalLayout loginLayout = new HorizontalLayout();
        loginLayout.setPadding(true);
        loginLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginLayout.add(statusRadioGroup, submitButton);

        VerticalLayout cameraTimeInAndOutLayout = new VerticalLayout();
        cameraTimeInAndOutLayout.setAlignItems(Alignment.CENTER);
        cameraTimeInAndOutLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        cameraTimeInAndOutLayout.add(clockHtml, buildWebCamera(), loginLayout);

        return cameraTimeInAndOutLayout;
    }

    /**
     * This will build and load the HTML5 web camera component.
     *
     * @return The web camera component.\
     */
    private Component buildWebCamera() {
        // Create <video> element
        Element videoElement = new Element("video");
        videoElement.setAttribute("id", "webcam");
        videoElement.setAttribute("autoplay", "");
        videoElement.setAttribute("playsinline", "");
//        videoElement.setAttribute("width", "320");
//        videoElement.setAttribute("height", "320");
        videoElement.getStyle().set("padding", "0 !important");
        videoElement.getStyle().set("margin", "0 !important");
        videoElement.getStyle().set("display", "block"); // Avoid inline spacing
        videoElement.getStyle().set("box-sizing", "border-box");

        // Wrap in a Div to attach to layout
        Div videoWrapper = new Div();
        videoWrapper.getElement().appendChild(videoElement);

        // Auto-start webcam on attach
        getElement().addAttachListener(event -> {
            UI ui = UI.getCurrent();
            ui.getPage().executeJs("""
                                    navigator.mediaDevices.getUserMedia({ video: true })
                                             .then(stream => { document.getElementById('webcam').srcObject = stream; })
                                             .catch(err => console.error('Webcam error:', err));
                                    """);
        });

        return videoWrapper;
    }

    /**
     * This method acts as a Javascript function that converts the image data URL
     * into array of bytes.
     *
     * @param dataUrl - The image data URL.
     */
    @ClientCallable
    private void receiveImage(String dataUrl) {
        if (dataUrl == null || !dataUrl.startsWith("data:image")) return;
        String base64 = dataUrl.split(",")[1];
        imageBytes = Base64.getDecoder().decode(base64);

        EmployeeTimesheetDTO  employeeTimesheetDTO = new EmployeeTimesheetDTO();
        employeeTimesheetDTO.setEmployeeDTO(employeeProfileDTO);
        employeeTimesheetDTO.setLogDate(LocalDate.now(ZoneId.of("Asia/Manila")));
        employeeTimesheetDTO.setLogTime(LocalTime.now(ZoneId.of("Asia/Manila")));
        employeeTimesheetDTO.setLogDetail(statusRadioGroup.getValue());
        employeeTimesheetDTO.setLogImage(this.imageBytes);
        employeeTimesheetDTO.setStatus("PENDING");
        employeeTimesheetDTO.setShiftScheduleDTO(employeeShiftScheduleDTO);
        employeeTimesheetDTO.setCreatedBy(loggedInUser);
        employeeTimesheetDTO.setUpdatedBy(loggedInUser);

        employeeTimesheetService.saveOrUpdate(employeeTimesheetDTO);
    }

    /**
     * This will build the timesheet component of the employee.
     *
     * @return The timesheet component.
     */
    private Component buildEmployeeTimesheet() {
        DatePicker startDatePicker = new DatePicker("Start date");
        startDatePicker.setRequired(true);

        DatePicker endDatePicker = new DatePicker("End date");
        endDatePicker.setRequired(true);

        Button searchButton = new Button("Search");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.getStyle().set("display", "block");
        searchButton.addClickListener(event -> {
            employeeTimesheetDTOList = employeeTimesheetService.findTimesheetByEmployeeAndLogDate(employeeProfileDTO,
                                                    startDatePicker.getValue(),
                                                    endDatePicker.getValue());
            employeeTimesheetGrid.setItems(employeeTimesheetDTOList);
        });

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWrap(true);
        searchLayout.setAlignItems(Alignment.END);
        searchLayout.add(startDatePicker, endDatePicker, searchButton);

        employeeTimesheetGrid = new Grid<>(EmployeeTimesheetDTO.class, false);
        employeeTimesheetGrid.addColumn(employeeTimesheetDTO ->
                        DateTimeFormatter.ofPattern("MMM dd, yyyy").format(employeeTimesheetDTO.getLogDate()))
                .setHeader("Date");
        employeeTimesheetGrid.addColumn(EmployeeTimesheetDTO::getLogDetail)
                .setHeader("Log Detail");
        employeeTimesheetGrid.addColumn(employeeTimesheetDTO ->
                        DateTimeFormatter.ofPattern("hh:mm:ss a").format(employeeTimesheetDTO.getLogTime()))
                .setHeader("Time");
        employeeTimesheetGrid.addColumn(employeeTimesheetDTO ->
                        employeeTimesheetDTO.getShiftScheduleDTO().getShiftSchedule())
                .setHeader("Shift Schedule");
        employeeTimesheetGrid.addColumn(employeeTimesheetDTO ->
                        DateTimeFormatter.ofPattern("hh:mm:ss a")
                                .format(employeeTimesheetDTO.getShiftScheduleDTO().getShiftStartTime()))
                .setHeader("Shift Start Time");
        employeeTimesheetGrid.addColumn(employeeTimesheetDTO ->
                        DateTimeFormatter.ofPattern("hh:mm:ss a")
                                .format(employeeTimesheetDTO.getShiftScheduleDTO().getShiftEndTime()))
                .setHeader("Shift End Time");
        employeeTimesheetGrid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, (layout, employeeTimesheetDTO) -> {
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
                                            })).setHeader("Status");
        employeeTimesheetGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                                               GridVariant.LUMO_COLUMN_BORDERS,
                                               GridVariant.LUMO_WRAP_CELL_CONTENT);
        employeeTimesheetGrid.setEmptyStateText("No timesheet records found.");
        employeeTimesheetGrid.setAllRowsVisible(true);

        VerticalLayout employeeTimesheetWrapperLayout = new VerticalLayout();
        employeeTimesheetWrapperLayout.setSpacing(true);
        employeeTimesheetWrapperLayout.add(searchLayout,  employeeTimesheetGrid);

        return  employeeTimesheetWrapperLayout;
    }
}

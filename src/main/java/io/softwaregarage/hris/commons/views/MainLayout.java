package io.softwaregarage.hris.commons.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.theme.lumo.LumoUtility;

import io.softwaregarage.hris.admin.views.*;
import io.softwaregarage.hris.attendance.services.EmployeeOvertimeService;
import io.softwaregarage.hris.attendance.views.EmployeeOvertimeApprovalsList;
import io.softwaregarage.hris.compenben.views.*;
import io.softwaregarage.hris.admin.dtos.UserDTO;
import io.softwaregarage.hris.admin.services.UserService;
import io.softwaregarage.hris.attendance.services.EmployeeLeaveFilingService;
import io.softwaregarage.hris.configs.SecurityConfig;
import io.softwaregarage.hris.payroll.views.*;
import io.softwaregarage.hris.profile.dtos.DocumentProfileDTO;
import io.softwaregarage.hris.profile.services.DocumentProfileService;
import io.softwaregarage.hris.utils.SecurityUtil;
import io.softwaregarage.hris.attendance.views.EmployeeShiftListView;
import io.softwaregarage.hris.attendance.views.EmployeeLeaveApprovalsListView;
import io.softwaregarage.hris.attendance.views.EmployeeTimesheetListView;
import io.softwaregarage.hris.profile.views.DepartmentProfileListView;
import io.softwaregarage.hris.profile.views.EmployeeProfileListView;
import io.softwaregarage.hris.profile.views.PositionProfileListView;
import io.softwaregarage.hris.utils.StringUtil;

import jakarta.annotation.Resource;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    @Resource
    private final UserService userService;

    @Resource
    private final EmployeeLeaveFilingService employeeLeaveFilingService;

    @Resource
    private final EmployeeOvertimeService employeeOvertimeService;

    @Resource
    private final DocumentProfileService documentProfileService;

    private UserDTO userDTO;
    private H1 viewTitle;
    private DownloadHandler imageHandler;
    private String fullName;

    public MainLayout(UserService userService,
                      EmployeeLeaveFilingService employeeLeaveFilingService,
                      EmployeeOvertimeService employeeOvertimeService,
                      DocumentProfileService documentProfileService) {
        this.userService = userService;
        this.employeeLeaveFilingService = employeeLeaveFilingService;
        this.employeeOvertimeService = employeeOvertimeService;
        this.documentProfileService = documentProfileService;

        // Gets the user data transfer object based from the logged-in user.
        if (SecurityUtil.getAuthenticatedUser() != null) {
            userDTO = userService.getByUsername(SecurityUtil.getAuthenticatedUser().getUsername());
            fullName = userDTO.getEmployeeDTO().getFirstName() + " " + userDTO.getEmployeeDTO().getLastName();

            // Set the image component from the employees document file which will be added in the avatar component.
            DocumentProfileDTO documentProfileDTO = documentProfileService.getIDPictureByEmployeeDTO(userDTO.getEmployeeDTO());

            if (documentProfileDTO != null) {
                byte[] fileData = documentProfileDTO.getFileData();
                String fileName = documentProfileDTO.getFileName();
                String mimeType = documentProfileDTO.getFileType();

                imageHandler = DownloadHandler.fromInputStream(downloadEvent -> {
                    try {
                        return new DownloadResponse(new ByteArrayInputStream(fileData), fileName, mimeType, fileData.length);
                    } catch (Exception e) {
                        return DownloadResponse.error(500);
                    }
                });
            }
        }

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.NONE);
        viewTitle.getStyle().setWidth("50%");

        // This will show the user's avatar if logged in the application.
        if (userDTO != null) {
            Avatar userAvatar = new Avatar(fullName);
            userAvatar.addThemeVariants(AvatarVariant.LUMO_LARGE);

            if (imageHandler != null && !imageHandler.equals(DownloadResponse.error(500))) {
                userAvatar.setImageHandler(imageHandler);
            } else {
                userAvatar.setColorIndex((int) (Math.random() * 7) + 1);
            }

            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setTarget(userAvatar);
            contextMenu.setOpenOnClick(true);
            contextMenu.addItem(this.createProfileDiv());
            contextMenu.addItem("Change Password", menuItemClickEvent -> this.buildChangePasswordDialog().open());
            contextMenu.addItem("Logout", menuItemClickEvent -> SecurityUtil.logout());

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.add(userAvatar);
            verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, userAvatar);

            addToNavbar(true, toggle, viewTitle, verticalLayout);
        } else {
            addToNavbar(true, toggle, viewTitle);
        }
    }

    private void addDrawerContent() {
        Span appName = new Span("Software Garage HRIS");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(this.createNavigationLayout());

        addToDrawer(header, scroller, createFooter());
    }

    private Div createProfileDiv() {
        Span profileGreetingsSpan = new Span("Welcome ".concat(fullName).concat("!"));
        profileGreetingsSpan.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        Div profileDiv = new Div();
        profileDiv.add(profileGreetingsSpan);
        profileDiv.getStyle().setPaddingLeft("15px");
        profileDiv.getStyle().setPaddingRight("15px");
        profileDiv.getStyle().setPaddingTop("5px");
        profileDiv.getStyle().setPaddingBottom("5px");

        return profileDiv;
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("My Dashboard", DashboardView.class, LineAwesomeIcon.CHART_BAR_SOLID.create()));
        nav.addItem(new SideNavItem("My Profile", EmployeeInfoView.class, LineAwesomeIcon.USER_TIE_SOLID.create()));
        nav.addItem(new SideNavItem("My Attendance", AttendanceView.class, LineAwesomeIcon.CLOCK.create()));
        nav.addItem(new SideNavItem("My Overtime Filings", OvertimeFiling.class, LineAwesomeIcon.STOPWATCH_SOLID.create()));
        nav.addItem(new SideNavItem("My Leave Filings", LeaveFilingView.class, LineAwesomeIcon.DOOR_OPEN_SOLID.create()));

        if (!this.createEmployeeNavigation().getItems().isEmpty()) {
            nav.addItem(this.createEmployeeNavigation());
        }

        if (!this.createAttendanceNavigation().getItems().isEmpty()) {
            nav.addItem(this.createAttendanceNavigation());
        }

        if (!this.createCompenbenNavigation().getItems().isEmpty()) {
            nav.addItem(this.createCompenbenNavigation());
        }

        if (!this.createPayrollNavigation().getItems().isEmpty()) {
            nav.addItem(this.createPayrollNavigation());
        }

        if (!this.createAdminNavigation().getItems().isEmpty()) {
            nav.addItem(this.createAdminNavigation());
        }

        return nav;
    }

    private SideNavItem createEmployeeNavigation() {
        SideNavItem navItem = new SideNavItem("Employee Details");
        navItem.setExpanded(false);

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
                userDTO.getRole().equals("ROLE_HR_MANAGER") ||
                userDTO.getRole().equals("ROLE_HR_SUPERVISOR") ||
                userDTO.getRole().equals("ROLE_HR_EMPLOYEE")) {
            navItem.addItem(new SideNavItem("Employees", EmployeeProfileListView.class, LineAwesomeIcon.ID_BADGE_SOLID.create()));
            navItem.addItem(new SideNavItem("Assign Position", PositionProfileListView.class, LineAwesomeIcon.USER_CHECK_SOLID.create()));
            navItem.addItem(new SideNavItem("Assign Department", DepartmentProfileListView.class, LineAwesomeIcon.USER_CIRCLE_SOLID.create()));
        }

        return navItem;
    }

    private SideNavItem createAttendanceNavigation() {
        SideNavItem navItem = new SideNavItem("Attendance");
        navItem.setExpanded(false);

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
                userDTO.getRole().equals("ROLE_HR_MANAGER") ||
                userDTO.getRole().equals("ROLE_HR_SUPERVISOR")) {
            navItem.addItem(new SideNavItem("Employee Shift", EmployeeShiftListView.class, LineAwesomeIcon.CALENDAR_DAY_SOLID.create()));
        }

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
                userDTO.getRole().equals("ROLE_HR_MANAGER") ||
                userDTO.getRole().equals("ROLE_HR_SUPERVISOR") ||
                userDTO.getRole().equals("ROLE_MANAGER") ||
                userDTO.getRole().equals("ROLE_SUPERVISOR")) {
            navItem.addItem(new SideNavItem("Timesheet Approvals", EmployeeTimesheetListView.class, LineAwesomeIcon.CALENDAR_WEEK_SOLID.create()));
        }

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
                userDTO.getRole().equals("ROLE_HR_MANAGER") ||
                userDTO.getRole().equals("ROLE_HR_SUPERVISOR") ||
                userDTO.getRole().equals("ROLE_MANAGER") ||
                userDTO.getRole().equals("ROLE_SUPERVISOR")) {
            // Get the count of pending leaves to approved. Check every 5 seconds.
            int pendingLeaveCounts = employeeLeaveFilingService.getByLeaveStatusAndAssignedApproverEmployeeDTO("PENDING", userDTO.getEmployeeDTO()).size();

            // Show a notification badge that displays the count of leaves to be approved.
            Span counter = new Span(String.valueOf(pendingLeaveCounts));
            counter.getElement().getThemeList().add("badge pill medium error primary");
            counter.getStyle().set("margin-inline-start", "var(--lumo-space-s)");

            // Create the navigation item for leave approvals.
            SideNavItem leaveApprovalNavItem = new SideNavItem("Leave Approvals", EmployeeLeaveApprovalsListView.class, LineAwesomeIcon.CALENDAR_CHECK.create());
            if (pendingLeaveCounts > 0) leaveApprovalNavItem.setSuffixComponent(counter);

            navItem.addItem(leaveApprovalNavItem);
        }

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
                userDTO.getRole().equals("ROLE_HR_MANAGER") ||
                userDTO.getRole().equals("ROLE_HR_SUPERVISOR") ||
                userDTO.getRole().equals("ROLE_MANAGER") ||
                userDTO.getRole().equals("ROLE_SUPERVISOR")) {
            // Get the count of pending overtimes to approved. Check every 5 seconds.
            int pendingOvertimeCounts = employeeOvertimeService
                    .findByAssignedApproverEmployeeDTO(userDTO.getEmployeeDTO())
                    .stream()
                    .filter(employeeOvertimeDTO ->
                            employeeOvertimeDTO.getStatus().equals("PENDING"))
                    .toList()
                    .size();

            // Show a notification badge that displays the count of leaves to be approved.
            Span counter = new Span(String.valueOf(pendingOvertimeCounts));
            counter.getElement().getThemeList().add("badge pill medium error primary");
            counter.getStyle().set("margin-inline-start", "var(--lumo-space-s)");

            // Create the navigation item for leave approvals.
            SideNavItem overtimeApprovalNavItem = new SideNavItem("Overtime Approvals",
                    EmployeeOvertimeApprovalsList.class,
                    LineAwesomeIcon.STOPWATCH_SOLID.create());
            if (pendingOvertimeCounts > 0) overtimeApprovalNavItem.setSuffixComponent(counter);

            navItem.addItem(overtimeApprovalNavItem);
        }

        return navItem;
    }

    private SideNavItem createCompenbenNavigation() {
        SideNavItem navItem = new SideNavItem("Compenben");
        navItem.setExpanded(false);

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
                userDTO.getRole().equals("ROLE_HR_MANAGER") ||
                userDTO.getRole().equals("ROLE_HR_SUPERVISOR")) {
            navItem.addItem(new SideNavItem("Allowances", AllowanceListView.class, LineAwesomeIcon.COINS_SOLID.create()));
            navItem.addItem(new SideNavItem("Contributions", GovernmentContributionsListView.class, LineAwesomeIcon.HAND_HOLDING_USD_SOLID.create()));
            navItem.addItem(new SideNavItem("Loan Deductions", LoanDeductionListView.class, LineAwesomeIcon.MONEY_BILL_WAVE_SOLID.create()));
            navItem.addItem(new SideNavItem("Leave Benefits", LeaveBenefitsListView.class, LineAwesomeIcon.DOOR_OPEN_SOLID.create()));
        }

        return navItem;
    }

    private SideNavItem createPayrollNavigation() {
        SideNavItem navItem = new SideNavItem("Payroll");
        navItem.setExpanded(false);

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
                userDTO.getRole().equals("ROLE_HR_MANAGER") ||
                userDTO.getRole().equals("ROLE_PAYROLL_MANAGER") ||
                userDTO.getRole().equals("ROLE_PAYROLL_EMPLOYEE")) {
            navItem.addItem(new SideNavItem("Rates", RatesListView.class, LineAwesomeIcon.MONEY_CHECK_SOLID.create()));
            navItem.addItem(new SideNavItem("Tax Rates", TaxRatesListView.class, LineAwesomeIcon.PERCENT_SOLID.create()));
            navItem.addItem(new SideNavItem("Tax Exemptions", TaxExemptionsListView.class, LineAwesomeIcon.PERCENTAGE_SOLID.create()));
            navItem.addItem(new SideNavItem("Payroll Generator", PayrollGeneratorView.class, LineAwesomeIcon.FILE_INVOICE_DOLLAR_SOLID.create()));
            navItem.addItem(new SideNavItem("Employee Payroll", EmployeePayrollListView.class, LineAwesomeIcon.FILE_INVOICE_DOLLAR_SOLID.create()));
        }

        return navItem;
    }

    private SideNavItem createAdminNavigation() {
        SideNavItem navItem = new SideNavItem("Administration");
        navItem.setExpanded(false);

        if (userDTO.getRole().equals("ROLE_ADMIN") || userDTO.getRole().equals("ROLE_HR_MANAGER")) {
            navItem.addItem(new SideNavItem("Calendar Holidays", CalendarHolidaysListView.class, LineAwesomeIcon.CALENDAR.create()));
            navItem.addItem(new SideNavItem("Positions", PositionListView.class, LineAwesomeIcon.SITEMAP_SOLID.create()));
            navItem.addItem(new SideNavItem("Departments", DepartmentListView.class, LineAwesomeIcon.BUILDING_SOLID.create()));
        }

        if (userDTO.getRole().equals("ROLE_ADMIN")) {
            navItem.addItem(new SideNavItem("Groups", GroupListView.class, LineAwesomeIcon.USERS_SOLID.create()));
            navItem.addItem(new SideNavItem("Users", UserListView.class, LineAwesomeIcon.USER_LOCK_SOLID.create()));
        }

        return navItem;
    }

    private VerticalLayout createNavigationLayout() {
        VerticalLayout navigationLayout = new VerticalLayout();

        navigationLayout.add(this.createNavigation());
        navigationLayout.setSpacing(true);
        navigationLayout.setSizeUndefined();

        return navigationLayout;
    }

    private Footer createFooter() {
        return new Footer();
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private Dialog buildChangePasswordDialog() {
        Dialog changePasswordDialog = new Dialog("Change Password");

        PasswordField currentPasswordField = new PasswordField("Current Password");
        currentPasswordField.setRequired(true);
        currentPasswordField.setRequiredIndicatorVisible(true);

        PasswordField newPasswordField = new PasswordField("New Password");
        newPasswordField.setRequired(true);
        newPasswordField.setRequiredIndicatorVisible(true);

        PasswordField confirmNewPasswordField = new PasswordField("Confirm New Password");
        confirmNewPasswordField.setRequired(true);
        confirmNewPasswordField.setRequiredIndicatorVisible(true);

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(clickEvent -> {
            Notification successNotification, errorNotification;
            boolean isCurrentPasswordMatch = new SecurityConfig().passwordEncoder().matches(currentPasswordField.getValue(), userDTO.getPassword());

            if (isCurrentPasswordMatch) {
                if (newPasswordField.getValue().equals(confirmNewPasswordField.getValue())) {
                    userDTO.setPassword(StringUtil.encryptPassword(newPasswordField.getValue()));
                    userDTO.setUpdatedBy(Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername());
                    userDTO.setDateAndTimeUpdated(LocalDateTime.now(ZoneId.of("Asia/Manila")));

                    userService.saveOrUpdate(userDTO);

                    successNotification = new Notification("You have successfully updated your password.", 5000, Notification.Position.TOP_CENTER);
                    successNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    successNotification.open();

                    changePasswordDialog.close();
                } else {
                    errorNotification = new Notification("Your new password is not matched!", 5000, Notification.Position.TOP_CENTER);
                    errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    errorNotification.open();
                }
            } else {
                errorNotification = new Notification("Your current password is wrong!", 5000, Notification.Position.TOP_CENTER);
                errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotification.open();
            }
        });

        FormLayout changePasswordLayout = new FormLayout();
        changePasswordLayout.add(currentPasswordField,
                newPasswordField,
                confirmNewPasswordField);
        changePasswordLayout.setWidth("500px");

        changePasswordDialog.add(changePasswordLayout);
        changePasswordDialog.getFooter().add(saveButton);
        changePasswordDialog.setCloseOnOutsideClick(false);

        return changePasswordDialog;
    }
}

package io.softwaregarage.hris.compenben.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.softwaregarage.hris.admin.dtos.UserDTO;
import io.softwaregarage.hris.admin.services.UserService;
import io.softwaregarage.hris.compenben.dtos.AllowanceDTO;
import io.softwaregarage.hris.compenben.services.AllowanceService;
import io.softwaregarage.hris.commons.views.MainLayout;
import io.softwaregarage.hris.utils.SecurityUtil;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import org.vaadin.lineawesome.LineAwesomeIcon;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR"})
@PageTitle("Allowances")
@Route(value = "allowance-list", layout = MainLayout.class)
public class AllowanceListView extends VerticalLayout {
    @Resource
    private final AllowanceService allowanceService;

    @Resource
    private final UserService userService;

    private UserDTO userDTO;

    private Grid<AllowanceDTO> allowanceDTOGrid;
    private TextField searchFilterTextField;

    public AllowanceListView(AllowanceService allowanceService, UserService userService) {
        this.allowanceService = allowanceService;
        this.userService = userService;

        if (SecurityUtil.getAuthenticatedUser() != null) {
            userDTO = userService.getByUsername(SecurityUtil.getAuthenticatedUser().getUsername());
        }

        this.add(buildHeaderToolbar(), buildAllowanceDTOGrid());
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
        searchFilterTextField.addValueChangeListener(valueChangeEvent -> this.updateAllowanceDTOGrid());

        Button addButton = new Button("Add Allowance");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(buttonClickEvent -> addButton.getUI().ifPresent(ui -> ui.navigate(AllowanceFormView.class)));

        headerToolbarLayout.add(searchFilterTextField, addButton);
        headerToolbarLayout.setAlignItems(Alignment.CENTER);
        headerToolbarLayout.getThemeList().clear();

        return headerToolbarLayout;
    }

    private Grid<AllowanceDTO> buildAllowanceDTOGrid() {
        allowanceDTOGrid = new Grid<>(AllowanceDTO.class, false);

        allowanceDTOGrid.addColumn(allowanceDTO -> allowanceDTO.getEmployeeDTO().getEmployeeNumber())
                .setHeader("Employee No.")
                .setSortable(true);
        allowanceDTOGrid.addColumn(allowanceDTO -> allowanceDTO.getEmployeeDTO().getFirstName()
                        .concat(" ")
                        .concat(allowanceDTO.getEmployeeDTO().getLastName())
                        .concat(allowanceDTO.getEmployeeDTO().getSuffix() != null ? allowanceDTO.getEmployeeDTO().getSuffix() : ""))
                .setHeader("Employee Name")
                .setSortable(true);
        allowanceDTOGrid.addColumn(AllowanceDTO::getAllowanceCode)
                .setHeader("Allowance Code")
                .setSortable(true);
        allowanceDTOGrid.addColumn(AllowanceDTO::getAllowanceType)
                .setHeader("Allowance Type")
                .setSortable(true);
        allowanceDTOGrid.addColumn(AllowanceDTO::getAllowanceAmount)
                .setHeader("Allowance Amount")
                .setSortable(true);
        allowanceDTOGrid.addColumn(allowanceDTO -> allowanceDTO.isTaxable() ? "Yes" : "No")
                .setHeader("Is Taxable?")
                .setSortable(true);
        allowanceDTOGrid.addComponentColumn(userDTO -> buildRowToolbar()).setHeader("Action");
        allowanceDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        allowanceDTOGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        allowanceDTOGrid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        allowanceDTOGrid.setEmptyStateText("No benefit records found.");
        allowanceDTOGrid.setItems((query -> allowanceService.getAll(query.getPage(), query.getPageSize()).stream()));

        return allowanceDTOGrid;
    }

    public HorizontalLayout buildRowToolbar() {
        HorizontalLayout rowToolbarLayout = new HorizontalLayout();

        Button viewButton = new Button();
        viewButton.setTooltipText("View Allowance");
        viewButton.setIcon(LineAwesomeIcon.SEARCH_SOLID.create());
        viewButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        viewButton.addClickListener(buttonClickEvent -> viewButton.getUI().ifPresent(ui -> {
            if (allowanceDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                AllowanceDTO selectedAllowanceDTO = allowanceDTOGrid.getSelectionModel().getFirstSelectedItem().get();
                ui.navigate(AllowanceDetailsView.class, selectedAllowanceDTO.getId().toString());
            }
        }));

        Button editButton = new Button();
        editButton.setTooltipText("Edit Allowance");
        editButton.setIcon(LineAwesomeIcon.PENCIL_ALT_SOLID.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        editButton.addClickListener(buttonClickEvent -> editButton.getUI().ifPresent(ui -> {
            if (allowanceDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                AllowanceDTO selectedAllowanceDTO = allowanceDTOGrid.getSelectionModel().getFirstSelectedItem().get();
                ui.navigate(AllowanceFormView.class, selectedAllowanceDTO.getId().toString());
            }
        }));

        // Show the delete button if the role of the logged-in user is ROLE_ADMIN.
        Button deleteButton = new Button();
        deleteButton.setTooltipText("Delete Allowance");
        deleteButton.setIcon(LineAwesomeIcon.TRASH_ALT_SOLID.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(buttonClickEvent -> {
            if (allowanceDTOGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                AllowanceDTO selectedAllowanceDTO = allowanceDTOGrid.getSelectionModel()
                        .getFirstSelectedItem().get();

                // Check first if that record is not an active employee anymore. If it is not active, you may proceed
                // for the deletion.
                if (selectedAllowanceDTO.getEmployeeDTO().getStatus().equals("RESIGNED")
                        || selectedAllowanceDTO.getEmployeeDTO().getStatus().equals("RETIRED")
                        || selectedAllowanceDTO.getEmployeeDTO().getStatus().equals("TERMINATED")
                        || selectedAllowanceDTO.getEmployeeDTO().getStatus().equals("DECEASED")) {
                    // Show the confirmation dialog.
                    ConfirmDialog confirmDialog = new ConfirmDialog();
                    confirmDialog.setHeader("Delete Allowance");
                    confirmDialog.setText(new Html("""
                                               <p>
                                               WARNING! Are you sure you want to delete the selected employee allowance?
                                               </p>
                                               """));
                    confirmDialog.setConfirmText("Yes, Delete it.");
                    confirmDialog.setConfirmButtonTheme("error primary");
                    confirmDialog.addConfirmListener(confirmEvent -> {
                        // Get the selected allowance and delete it.
                        allowanceService.delete(selectedAllowanceDTO);

                        // Refresh the data grid from the backend after the delete operation.
                        allowanceDTOGrid.getDataProvider().refreshAll();

                        // Show notification message.
                        Notification notification = Notification.show("You have successfully deleted the selected employee allowance.",
                                5000,
                                Notification.Position.TOP_CENTER);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                        // Close the confirmation dialog.
                        confirmDialog.close();
                    });
                    confirmDialog.setCancelable(true);
                    confirmDialog.setCancelText("No");
                    confirmDialog.open();
                } else {
                    // Show notification message.
                    Notification notification = Notification.show("You cannot delete the selected employee allowance. Employee is still in active status.",
                            5000,
                            Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });

        if (userDTO.getRole().equals("ROLE_ADMIN")) {
            rowToolbarLayout.add(viewButton, editButton, deleteButton);
        } else {
            rowToolbarLayout.add(viewButton, editButton);
        }

        rowToolbarLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        rowToolbarLayout.getStyle().set("flex-wrap", "wrap");

        return rowToolbarLayout;
    }

    private void updateAllowanceDTOGrid() {
        if (searchFilterTextField.getValue() != null || searchFilterTextField.getValue().isBlank()) {
            allowanceDTOGrid.setItems(allowanceService.findByParameter(searchFilterTextField.getValue()));
        } else {
            allowanceDTOGrid.setItems(query -> allowanceService.getAll(query.getPage(), query.getPageSize()).stream());
        }
    }
}

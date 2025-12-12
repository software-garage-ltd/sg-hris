package io.softwaregarage.hris.compenben.views;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.*;

import io.softwaregarage.hris.compenben.dtos.AllowanceDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.compenben.services.AllowanceService;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;
import io.softwaregarage.hris.utils.SecurityUtil;
import io.softwaregarage.hris.utils.StringUtil;
import io.softwaregarage.hris.commons.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR"})
@PageTitle("Allowance Form")
@Route(value = "allowance-form", layout = MainLayout.class)
public class AllowanceFormView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource private final AllowanceService allowanceService;
    @Resource private final EmployeeProfileService employeeProfileService;

    private AllowanceDTO allowanceDTO;
    private UUID parameterId;

    private final FormLayout allowanceDTOFormLayout = new FormLayout();
    private ComboBox<EmployeeProfileDTO> employeeDTOComboBox;
    private ComboBox<String> allowanceTypeComboBox;
    private BigDecimalField allowanceAmountField;
    private ToggleButton allowanceTaxableToggleButton;
    private RadioButtonGroup<Integer> allowanceCutOffGroup;

    public AllowanceFormView(AllowanceService allowanceService,
                             EmployeeProfileService employeeProfileService) {
        this.allowanceService = allowanceService;
        this.employeeProfileService = employeeProfileService;

        add(allowanceDTOFormLayout);

        setSizeFull();
        setMargin(true);
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, allowanceDTOFormLayout);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        if (s != null) {
            parameterId = UUID.fromString(s);
            allowanceDTO = allowanceService.getById(parameterId);
        }

        buildAllowanceFormLayout();
    }

    private void buildAllowanceFormLayout() {
        // Create the query object that will do the pagination of employee records in the combo box component.
        Query<EmployeeProfileDTO, Void> employeeQuery = new Query<>();

        employeeDTOComboBox = new ComboBox<>("Employee");
        employeeDTOComboBox.setItems((employeeDTO, filterString) -> employeeDTO.getEmployeeFullName().toLowerCase().contains(filterString.toLowerCase()),
                                                                                       employeeProfileService.getAll(employeeQuery.getPage(), employeeQuery.getPageSize()));
        employeeDTOComboBox.setItemLabelGenerator(EmployeeProfileDTO::getEmployeeFullName);
        employeeDTOComboBox.setClearButtonVisible(true);
        employeeDTOComboBox.setRequired(true);
        employeeDTOComboBox.setRequiredIndicatorVisible(true);
        if (allowanceDTO != null) employeeDTOComboBox.setValue(allowanceDTO.getEmployeeDTO());

        allowanceTypeComboBox = new ComboBox<>("Allowance Type");
        allowanceTypeComboBox.setItems("(CA) Clothing Allowance",
                                       "(FA) Food Allowance",
                                       "(LA) Laundry Allowance",
                                       "(TA) Travel Allowance",
                                       "(RA) Rice Allowance",
                                       "(OA) Other Allowance");
        allowanceTypeComboBox.setClearButtonVisible(true);
        allowanceTypeComboBox.setRequired(true);
        allowanceTypeComboBox.setRequiredIndicatorVisible(true);
        if (allowanceDTO != null) allowanceTypeComboBox.setValue(allowanceDTO.getAllowanceType());

        // Add a prefix div label for each of the decimal fields.
        Div phpPrefix = new Div();
        phpPrefix.setText("PHP");

        allowanceAmountField = new BigDecimalField("Allowance Amount");
        allowanceAmountField.setPlaceholder("0.00");
        allowanceAmountField.setRequired(true);
        allowanceAmountField.setRequiredIndicatorVisible(true);
        allowanceAmountField.setPrefixComponent(phpPrefix);
        if (allowanceDTO != null) allowanceAmountField.setValue(allowanceDTO.getAllowanceAmount());

        allowanceTaxableToggleButton = new ToggleButton("Is Taxable?");
        allowanceTaxableToggleButton.setRequiredIndicatorVisible(true);
        if (allowanceDTO != null) allowanceTaxableToggleButton.setValue(allowanceDTO.isTaxable());

        // Add a set of values for the radio button cut-offs.
        Set<Integer> cutOffValues =  new HashSet<>();
        cutOffValues.add(1);
        cutOffValues.add(2);

        allowanceCutOffGroup = new RadioButtonGroup<>("Allowance Cut-Off");
        allowanceCutOffGroup.setRequired(true);
        allowanceCutOffGroup.setRequiredIndicatorVisible(true);
        allowanceCutOffGroup.setItems(cutOffValues);
        allowanceCutOffGroup.setItemLabelGenerator(integer -> {
            switch (integer) {
                case 1: return "1st Cut-Off";
                case 2: return "2nd Cut-Off";
                default: return "Unknown";
            }
        });
        if (allowanceDTO != null) allowanceCutOffGroup.setValue(allowanceDTO.getAllowanceCutOff());

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            saveOrUpdateAllowanceDTO();
            saveButton.getUI().ifPresent(ui -> ui.navigate(AllowanceListView.class));
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(buttonClickEvent -> cancelButton.getUI().ifPresent(ui -> ui.navigate(AllowanceListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setMaxWidth("720px");
        buttonLayout.setPadding(true);

        allowanceDTOFormLayout.add(employeeDTOComboBox,
                allowanceTypeComboBox,
                allowanceAmountField,
                allowanceTaxableToggleButton,
                allowanceCutOffGroup,
                buttonLayout);
        allowanceDTOFormLayout.setColspan(employeeDTOComboBox, 2);
        allowanceDTOFormLayout.setColspan(buttonLayout, 2);
        allowanceDTOFormLayout.setMaxWidth("720px");
    }

    private void saveOrUpdateAllowanceDTO() {
        String loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        if (parameterId != null) {
            allowanceDTO = allowanceService.getById(parameterId);
        } else {
            allowanceDTO = new AllowanceDTO();
            allowanceDTO.setCreatedBy(loggedInUser);
        }

        allowanceDTO.setEmployeeDTO(employeeDTOComboBox.getValue());
        allowanceDTO.setAllowanceCode(StringUtil.generateDataCode(allowanceTypeComboBox.getValue(),
                                                                  employeeDTOComboBox.getValue().getEmployeeNumber()));
        allowanceDTO.setAllowanceType(allowanceTypeComboBox.getValue());
        allowanceDTO.setAllowanceAmount(allowanceAmountField.getValue());
        allowanceDTO.setTaxable(allowanceTaxableToggleButton.getValue());
        allowanceDTO.setAllowanceCutOff(allowanceCutOffGroup.getValue());
        allowanceDTO.setUpdatedBy(loggedInUser);

        allowanceService.saveOrUpdate(allowanceDTO);

        // Show notification message.
        Notification notification = Notification.show("You have successfully saved an allowance record.",  5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}

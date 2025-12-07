package io.softwaregarage.hris.compenben.views;

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

import io.softwaregarage.hris.compenben.dtos.GovernmentContributionsDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.compenben.services.GovernmentContributionsService;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;
import io.softwaregarage.hris.utils.SecurityUtil;
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
@PageTitle("Government Contributions Form")
@Route(value = "government-contributions-form", layout = MainLayout.class)
public class GovernmentContributionsFormView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource private final GovernmentContributionsService governmentContributionsService;
    @Resource private final EmployeeProfileService employeeProfileService;

    private GovernmentContributionsDTO governmentContributionsDTO;
    private UUID parameterId;

    private final FormLayout governmentContributionsDTOFormLayout = new FormLayout();
    private ComboBox<EmployeeProfileDTO> employeeDTOComboBox;
    private BigDecimalField sssAmountField, hdmfAmountField, philhealthAmountField;
    private RadioButtonGroup<Integer> sssCutOffRadioButtonGroup,
            hdmfCutOffRadioButtonGroup,
            philhealthCutOffRadioButtonGroup;

    public GovernmentContributionsFormView(GovernmentContributionsService governmentContributionsService,
                                           EmployeeProfileService employeeProfileService) {
        this.governmentContributionsService = governmentContributionsService;
        this.employeeProfileService = employeeProfileService;

        add(governmentContributionsDTOFormLayout);

        setSizeFull();
        setMargin(true);
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, governmentContributionsDTOFormLayout);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        if (s != null) {
            parameterId = UUID.fromString(s);
            governmentContributionsDTO = governmentContributionsService.getById(parameterId);
        }

        buildGovernmentContributionsFormLayout();
    }

    private void buildGovernmentContributionsFormLayout() {
        // Create the query object that will do the pagination of employee records in the combo box component.
        Query<EmployeeProfileDTO, Void> employeeQuery = new Query<>();

        employeeDTOComboBox = new ComboBox<>("Employee");
        employeeDTOComboBox.setItems((employeeDTO, filterString) -> employeeDTO.getEmployeeFullName().toLowerCase().contains(filterString.toLowerCase()),
                                                                                       employeeProfileService.getAll(employeeQuery.getPage(), employeeQuery.getPageSize()));
        employeeDTOComboBox.setItemLabelGenerator(EmployeeProfileDTO::getEmployeeFullName);
        employeeDTOComboBox.setClearButtonVisible(true);
        employeeDTOComboBox.setRequired(true);
        employeeDTOComboBox.setRequiredIndicatorVisible(true);
        if (governmentContributionsDTO != null) employeeDTOComboBox.setValue(governmentContributionsDTO.getEmployeeDTO());

        // Add a prefix div label for each of the decimal fields.
        Div phpPrefix = new Div();
        phpPrefix.setText("PHP");

        // Add a set of values for the radio button cut-offs.
        Set<Integer> cutOffValues =  new HashSet<>();
        cutOffValues.add(1);
        cutOffValues.add(2);

        sssAmountField = new BigDecimalField("SSS Contribution Amount");
        sssAmountField.setPlaceholder("0.00");
        sssAmountField.setRequired(true);
        sssAmountField.setRequiredIndicatorVisible(true);
        sssAmountField.setPrefixComponent(phpPrefix);
        if (governmentContributionsDTO != null) sssAmountField.setValue(governmentContributionsDTO.getSssContributionAmount());

        sssCutOffRadioButtonGroup = new RadioButtonGroup<>("SSS Cut-off");
        sssCutOffRadioButtonGroup.setItems(cutOffValues);
        sssCutOffRadioButtonGroup.setItemLabelGenerator(integer -> {
            switch (integer) {
                case 1: return "1st Cut-Off";
                case 2: return "2nd Cut-Off";
                default: return "Unknown";
            }
        });
        sssCutOffRadioButtonGroup.setRequired(true);
        sssCutOffRadioButtonGroup.setRequiredIndicatorVisible(true);
        if (governmentContributionsDTO != null) sssCutOffRadioButtonGroup.setValue(governmentContributionsDTO.getSssContributionCutOff());

        hdmfAmountField = new BigDecimalField("HDMF Contribution Amount");
        hdmfAmountField.setPlaceholder("0.00");
        hdmfAmountField.setRequired(true);
        hdmfAmountField.setRequiredIndicatorVisible(true);
        hdmfAmountField.setPrefixComponent(phpPrefix);
        if (governmentContributionsDTO != null) hdmfAmountField.setValue(governmentContributionsDTO.getHdmfContributionAmount());

        hdmfCutOffRadioButtonGroup = new RadioButtonGroup<>("HDMF Cut-off");
        hdmfCutOffRadioButtonGroup.setItems(cutOffValues);
        hdmfCutOffRadioButtonGroup.setItemLabelGenerator(integer -> {
            switch (integer) {
                case 1: return "1st Cut-Off";
                case 2: return "2nd Cut-Off";
                default: return "Unknown";
            }
        });
        hdmfCutOffRadioButtonGroup.setRequired(true);
        hdmfCutOffRadioButtonGroup.setRequiredIndicatorVisible(true);
        if (governmentContributionsDTO != null) hdmfCutOffRadioButtonGroup.setValue(governmentContributionsDTO.getHdmfContributionCutOff());

        philhealthAmountField = new BigDecimalField("Philhealth Contribution Amount");
        philhealthAmountField.setPlaceholder("0.00");
        philhealthAmountField.setRequired(true);
        philhealthAmountField.setRequiredIndicatorVisible(true);
        philhealthAmountField.setPrefixComponent(phpPrefix);
        if (governmentContributionsDTO != null) philhealthAmountField.setValue(governmentContributionsDTO.getPhilhealthContributionAmount());

        philhealthCutOffRadioButtonGroup = new RadioButtonGroup<>("Philhealth Cut-off");
        philhealthCutOffRadioButtonGroup.setItems(cutOffValues);
        philhealthCutOffRadioButtonGroup.setItemLabelGenerator(integer -> {
            switch (integer) {
                case 1: return "1st Cut-Off";
                case 2: return "2nd Cut-Off";
                default: return "Unknown";
            }
        });
        philhealthCutOffRadioButtonGroup.setRequired(true);
        philhealthCutOffRadioButtonGroup.setRequiredIndicatorVisible(true);
        if (governmentContributionsDTO != null) philhealthCutOffRadioButtonGroup.setValue(governmentContributionsDTO.getPhilhealthContributionCutOff());

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            saveOrUpdateGovernmentContributionsDTO();
            saveButton.getUI().ifPresent(ui -> ui.navigate(GovernmentContributionsListView.class));

            Notification notification = Notification.show("Successfully added government contributions.", 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(buttonClickEvent -> cancelButton.getUI().ifPresent(ui -> ui.navigate(GovernmentContributionsListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setMaxWidth("720px");
        buttonLayout.setPadding(true);

        governmentContributionsDTOFormLayout.add(employeeDTOComboBox,
                                                 sssAmountField,
                                                 sssCutOffRadioButtonGroup,
                                                 hdmfAmountField,
                                                 hdmfCutOffRadioButtonGroup,
                                                 philhealthAmountField,
                                                 philhealthCutOffRadioButtonGroup,
                                                 buttonLayout);
        governmentContributionsDTOFormLayout.setColspan(employeeDTOComboBox, 2);
        governmentContributionsDTOFormLayout.setColspan(buttonLayout, 2);
        governmentContributionsDTOFormLayout.setMaxWidth("720px");
    }

    private void saveOrUpdateGovernmentContributionsDTO() {
        String loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        if (parameterId != null) {
            governmentContributionsDTO = governmentContributionsService.getById(parameterId);
        } else {
            governmentContributionsDTO = new GovernmentContributionsDTO();
            governmentContributionsDTO.setCreatedBy(loggedInUser);
        }

        governmentContributionsDTO.setEmployeeDTO(employeeDTOComboBox.getValue());
        governmentContributionsDTO.setSssContributionAmount(sssAmountField.getValue());
        governmentContributionsDTO.setSssContributionCutOff(sssCutOffRadioButtonGroup.getValue());
        governmentContributionsDTO.setHdmfContributionAmount(hdmfAmountField.getValue());
        governmentContributionsDTO.setHdmfContributionCutOff(hdmfCutOffRadioButtonGroup.getValue());
        governmentContributionsDTO.setPhilhealthContributionAmount(philhealthAmountField.getValue());
        governmentContributionsDTO.setPhilhealthContributionCutOff(philhealthCutOffRadioButtonGroup.getValue());
        governmentContributionsDTO.setUpdatedBy(loggedInUser);

        governmentContributionsService.saveOrUpdate(governmentContributionsDTO);

        // Show notification message.
        Notification notification = Notification.show("You have successfully saved a government contribution record.",  5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}

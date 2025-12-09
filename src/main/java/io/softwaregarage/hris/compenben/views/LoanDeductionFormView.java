package io.softwaregarage.hris.compenben.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.*;

import io.softwaregarage.hris.compenben.dtos.LoanDeductionDTO;
import io.softwaregarage.hris.profile.dtos.EmployeeProfileDTO;
import io.softwaregarage.hris.compenben.services.LoanDeductionService;
import io.softwaregarage.hris.profile.services.EmployeeProfileService;
import io.softwaregarage.hris.utils.SecurityUtil;
import io.softwaregarage.hris.commons.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.util.Objects;
import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR"})
@PageTitle("Loan Deduction Form")
@Route(value = "loan-deduction-form", layout = MainLayout.class)
public class LoanDeductionFormView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource private final LoanDeductionService loanDeductionService;
    @Resource private final EmployeeProfileService employeeProfileService;

    private LoanDeductionDTO loanDeductionDTO;
    private UUID parameterId;

    private final FormLayout loanDeductionsDTOFormLayout = new FormLayout();
    private ComboBox<EmployeeProfileDTO> employeeDTOComboBox;
    private ComboBox<String> loanTypeComboBox;
    private TextField loanDescriptionTextField;
    private DatePicker loanStartDatePicker, loanEndDatePicker;
    private BigDecimalField loanAmountField, monthlyDeductionField;
    private RadioButtonGroup<Integer> loanCutOffRadioButtonGroup;

    public LoanDeductionFormView(LoanDeductionService loanDeductionService,
                                 EmployeeProfileService employeeProfileService) {
        this.loanDeductionService = loanDeductionService;
        this.employeeProfileService = employeeProfileService;

        add(loanDeductionsDTOFormLayout);

        setSizeFull();
        setMargin(true);
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, loanDeductionsDTOFormLayout);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        if (s != null) {
            parameterId = UUID.fromString(s);
            loanDeductionDTO = loanDeductionService.getById(parameterId);
        }

        buildLoanDeductionsFormLayout();
    }

    private void buildLoanDeductionsFormLayout() {
        // Create the query object that will do the pagination of employee records in the combo box component.
        Query<EmployeeProfileDTO, Void> employeeQuery = new Query<>();

        employeeDTOComboBox = new ComboBox<>("Employee");
        employeeDTOComboBox.setItems((employeeDTO, filterString) -> employeeDTO.getEmployeeFullName().toLowerCase().contains(filterString.toLowerCase()),
                                                                                       employeeProfileService.getAll(employeeQuery.getPage(), employeeQuery.getPageSize()));
        employeeDTOComboBox.setItemLabelGenerator(EmployeeProfileDTO::getEmployeeFullName);
        employeeDTOComboBox.setClearButtonVisible(true);
        employeeDTOComboBox.setRequired(true);
        employeeDTOComboBox.setRequiredIndicatorVisible(true);
        if (loanDeductionDTO != null) employeeDTOComboBox.setValue(loanDeductionDTO.getEmployeeDTO());

        loanTypeComboBox = new ComboBox<>("Type");
        loanTypeComboBox.setItems("SSS Loan",
                                  "PagIbig (HDMF) Loan",
                                  "Bank Loan",
                                  "Company Loan");
        loanTypeComboBox.setClearButtonVisible(true);
        loanTypeComboBox.setRequired(true);
        loanTypeComboBox.setRequiredIndicatorVisible(true);
        if (loanDeductionDTO != null) loanTypeComboBox.setValue(loanDeductionDTO.getLoanType());

        loanDescriptionTextField = new TextField("Description");
        loanDescriptionTextField.setClearButtonVisible(true);
        loanDescriptionTextField.setRequired(true);
        loanDescriptionTextField.setRequiredIndicatorVisible(true);
        if (loanDeductionDTO != null) loanDescriptionTextField.setValue(loanDeductionDTO.getLoanDescription());

        loanStartDatePicker = new DatePicker("Start Date");
        loanStartDatePicker.setClearButtonVisible(true);
        loanStartDatePicker.setRequired(true);
        loanStartDatePicker.setRequiredIndicatorVisible(true);
        if (loanDeductionDTO != null) loanStartDatePicker.setValue(loanDeductionDTO.getLoanStartDate());

        loanEndDatePicker = new DatePicker("End Date");
        loanEndDatePicker.setClearButtonVisible(true);
        loanEndDatePicker.setRequired(true);
        loanEndDatePicker.setRequiredIndicatorVisible(true);
        if (loanDeductionDTO != null) loanEndDatePicker.setValue(loanDeductionDTO.getLoanEndDate());

        loanAmountField = new BigDecimalField("Loan Amount");
        loanAmountField.setPlaceholder("0.00");
        loanAmountField.setRequired(true);
        loanAmountField.setRequiredIndicatorVisible(true);
        loanAmountField.setPrefixComponent(new Span("PHP "));
        if (loanDeductionDTO != null) loanAmountField.setValue(loanDeductionDTO.getLoanAmount());

        monthlyDeductionField = new BigDecimalField("Monthly Deduction");
        monthlyDeductionField.setPlaceholder("0.00");
        monthlyDeductionField.setRequired(true);
        monthlyDeductionField.setRequiredIndicatorVisible(true);
        monthlyDeductionField.setPrefixComponent(new Span("PHP "));
        if (loanDeductionDTO != null) monthlyDeductionField.setValue(loanDeductionDTO.getMonthlyDeduction());

        loanCutOffRadioButtonGroup = new RadioButtonGroup<>("Loan Cut-Off");
        loanCutOffRadioButtonGroup.setItems(1, 2);
        loanCutOffRadioButtonGroup.setItemLabelGenerator(integer -> {
            switch (integer) {
                case 1: return "1st Cut-Off";
                case 2: return "2nd Cut-Off";
                default: return "Unknown";
            }
        });
        loanCutOffRadioButtonGroup.setRequired(true);
        loanCutOffRadioButtonGroup.setRequiredIndicatorVisible(true);
        if (loanDeductionDTO != null) loanCutOffRadioButtonGroup.setValue(loanDeductionDTO.getLoanCutOff());


        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            saveOrUpdateLoanDeductionsDTO();
            saveButton.getUI().ifPresent(ui -> ui.navigate(LoanDeductionListView.class));
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(buttonClickEvent -> cancelButton.getUI().ifPresent(ui -> ui.navigate(LoanDeductionListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setMaxWidth("720px");
        buttonLayout.setPadding(true);

        loanDeductionsDTOFormLayout.add(employeeDTOComboBox,
                                        loanTypeComboBox,
                                        loanDescriptionTextField,
                                        loanStartDatePicker,
                                        loanEndDatePicker,
                                        loanAmountField,
                                        monthlyDeductionField,
                                        loanCutOffRadioButtonGroup,
                                        buttonLayout);
        loanDeductionsDTOFormLayout.setColspan(employeeDTOComboBox, 2);
        loanDeductionsDTOFormLayout.setColspan(buttonLayout, 2);
        loanDeductionsDTOFormLayout.setMaxWidth("720px");
    }

    private void saveOrUpdateLoanDeductionsDTO() {
        String loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        if (parameterId != null) {
            loanDeductionDTO = loanDeductionService.getById(parameterId);
        } else {
            loanDeductionDTO = new LoanDeductionDTO();
            loanDeductionDTO.setCreatedBy(loggedInUser);
        }

        loanDeductionDTO.setEmployeeDTO(employeeDTOComboBox.getValue());
        loanDeductionDTO.setLoanType(loanTypeComboBox.getValue());
        loanDeductionDTO.setLoanDescription(loanDescriptionTextField.getValue());
        loanDeductionDTO.setLoanStartDate(loanStartDatePicker.getValue());
        loanDeductionDTO.setLoanEndDate(loanEndDatePicker.getValue());
        loanDeductionDTO.setLoanAmount(loanAmountField.getValue());
        loanDeductionDTO.setMonthlyDeduction(monthlyDeductionField.getValue());
        loanDeductionDTO.setLoanCutOff(loanCutOffRadioButtonGroup.getValue());
        loanDeductionDTO.setUpdatedBy(loggedInUser);

        loanDeductionService.saveOrUpdate(loanDeductionDTO);

        // Show notification message.
        Notification notification = Notification.show("You have successfully saved a loan deduction record.",  5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}

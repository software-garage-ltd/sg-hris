package io.softwaregarage.hris.payroll.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.softwaregarage.hris.commons.views.MainLayout;
import io.softwaregarage.hris.payroll.dtos.PayrollDTO;
import io.softwaregarage.hris.payroll.services.PayrollCalculatorService;
import io.softwaregarage.hris.payroll.services.PayrollService;
import io.softwaregarage.hris.utils.SecurityUtil;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
        "ROLE_HR_MANAGER",
        "ROLE_PAYROLL_MANAGER",
        "ROLE_PAYROLL_EMPLOYEE"})
@PageTitle("Employee Payroll Form")
@Route(value = "employee-payroll-form", layout = MainLayout.class)
public class EmployeePayrollFormView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource
    private final PayrollService payrollService;

    @Resource
    private final PayrollCalculatorService payrollCalculatorService;

    private PayrollDTO payrollDTO;
    private UUID parameterId;
    private String loggedInUser;

    // Component fields for pays.
    private BigDecimalField basicPayField,
            allowancePayField,
            restDayPayField,
            nightDifferentialPayField,
            leavePayField,
            regularHolidayPayField,
            specialHolidayPayField,
            specialNonWorkingHolidayPayField,
            adjustmentPayField;

    // Component fields for deductions.
    private BigDecimalField absentDeductionField,
            lateOrUndertimeDeductionField,
            sssDeductionField,
            hdmfDeductionField,
            philhealthDeductionField,
            loanDeductionField,
            otherDeductionField;

    // Component fields for the final computations.
    private BigDecimalField totalGrossPayField,
            totalDeductionField,
            totalNetPayField,
            withholdingTaxField;

    private VerticalLayout payrollLayout = new VerticalLayout();

    public EmployeePayrollFormView(PayrollService payrollService,
                                   PayrollCalculatorService payrollCalculatorService) {
        this.payrollService = payrollService;
        this.payrollCalculatorService = payrollCalculatorService;

        loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        add(payrollLayout);

        setSizeFull();
        setMargin(true);
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, payrollLayout);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {
        if (parameter != null) {
            parameterId = UUID.fromString(parameter);
            payrollDTO = payrollService.getById(parameterId);
        }

        buildPayrollLayout();
    }

    private void buildPayrollLayout() {
        FormLayout employeeFormLayout = new FormLayout();
        FormLayout payFieldsFormLayout = new FormLayout();
        FormLayout deductionFieldsFormLayout = new FormLayout();
        FormLayout computationFieldsFormLayout = new FormLayout();

        Span employeeNoLabelSpan = new Span("Employee No");
        employeeNoLabelSpan.getStyle().set("text-align", "right");

        Span employeeNoValueSpan = new Span();
        employeeNoValueSpan.getStyle().setFontWeight("bold");
        if (payrollDTO != null) employeeNoValueSpan.setText(payrollDTO.getEmployeeDTO().getEmployeeNumber());

        Span employeeNameLabelSpan = new Span("Employee Name");
        employeeNameLabelSpan.getStyle().set("text-align", "right");

        Span employeeNameValueSpan = new Span();
        employeeNameValueSpan.getStyle().setFontWeight("bold");
        if (payrollDTO != null) {
            String employeeName = payrollDTO.getEmployeeDTO().getFirstName()
                    .concat(" ")
                    .concat(payrollDTO.getEmployeeDTO().getMiddleName())
                    .concat(" ")
                    .concat(payrollDTO.getEmployeeDTO().getLastName())
                    .concat(payrollDTO.getEmployeeDTO().getSuffix() != null
                            ? " ".concat(payrollDTO.getEmployeeDTO().getSuffix())
                            : "");
            employeeNameValueSpan.setText(employeeName);
        }

        Span cutOffDatesLabelSpan = new Span("Cut-off Dates");
        cutOffDatesLabelSpan.getStyle().set("text-align", "right");

        Span cutOffDatesValueSpan = new Span();
        cutOffDatesValueSpan.getStyle().setFontWeight("bold");
        if (payrollDTO != null) {
            cutOffDatesValueSpan.setText(payrollDTO.getCutOffFromDate()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    .concat(" to ")
                    .concat(payrollDTO.getCutOffToDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        }

        basicPayField = new BigDecimalField("Basic Pay");
        basicPayField.setReadOnly(true);
        if (payrollDTO != null) basicPayField.setValue(payrollDTO.getBasicPayAmount());

        allowancePayField = new BigDecimalField("Allowance Pay");
        allowancePayField.setReadOnly(true);
        if (payrollDTO != null) allowancePayField.setValue(payrollDTO.getAllowancePayAmount());

        restDayPayField = new BigDecimalField("Rest Day Pay");
        restDayPayField.setReadOnly(true);
        if (payrollDTO != null) restDayPayField.setValue(payrollDTO.getRestDayPayAmount());

        nightDifferentialPayField = new BigDecimalField("Night Differential Pay");
        nightDifferentialPayField.setReadOnly(true);
        if (payrollDTO != null) nightDifferentialPayField.setValue(payrollDTO.getNightDifferentialPayAmount());

        leavePayField = new BigDecimalField("Leave Pay");
        leavePayField.setReadOnly(true);
        if (payrollDTO != null) leavePayField.setValue(payrollDTO.getLeavePayAmount());

        regularHolidayPayField = new BigDecimalField("Regular Holiday Pay");
        regularHolidayPayField.setReadOnly(true);
        if (payrollDTO != null) regularHolidayPayField.setValue(payrollDTO.getRegularHolidayPayAmount());

        specialHolidayPayField = new BigDecimalField("Special Holiday Pay");
        specialHolidayPayField.setReadOnly(true);
        if (payrollDTO != null) specialHolidayPayField.setValue(payrollDTO.getSpecialHolidayPayAmount());

        specialNonWorkingHolidayPayField = new BigDecimalField("Special Non-Working-Holiday Pay");
        specialNonWorkingHolidayPayField.setReadOnly(true);
        if (payrollDTO != null) specialNonWorkingHolidayPayField.setValue(payrollDTO.getSpecialNonWorkingHolidayPayAmount());

        adjustmentPayField = new BigDecimalField("Adjustment Pay");
        adjustmentPayField.addBlurListener(blurEvent -> {
            BigDecimal adjustmentPayAmount = adjustmentPayField.getValue();
            BigDecimal totalGrossPayAmount = totalGrossPayField.getValue();
            BigDecimal totalDeductionAmount = totalDeductionField.getValue();

            // Recompute the values after entering the values in the adjustment pay field.
            BigDecimal totalGrossPay = totalGrossPayAmount.add(adjustmentPayAmount);
            BigDecimal totalNetPay = totalGrossPay.subtract(totalDeductionAmount);
            BigDecimal withholdingTaxPay = payrollCalculatorService.computeWithholdingTax(totalGrossPay, totalDeductionAmount);

            totalGrossPayField.setValue(totalGrossPay);
            totalNetPayField.setValue(totalNetPay);
            withholdingTaxField.setValue(withholdingTaxPay);
        });
        if (payrollDTO != null) adjustmentPayField.setValue(payrollDTO.getAdjustmentPayAmount());

        absentDeductionField = new BigDecimalField("Absent Deduction");
        absentDeductionField.setReadOnly(true);
        if (payrollDTO != null) absentDeductionField.setValue(payrollDTO.getAbsentDeductionAmount());

        lateOrUndertimeDeductionField = new BigDecimalField("Late Or Under Time Deduction");
        lateOrUndertimeDeductionField.setReadOnly(true);
        if (payrollDTO != null) lateOrUndertimeDeductionField.setValue(payrollDTO.getLateOrUndertimeDeductionAmount());

        sssDeductionField = new BigDecimalField("SSS Deduction");
        sssDeductionField.setReadOnly(true);
        if (payrollDTO != null) sssDeductionField.setValue(payrollDTO.getSssDeductionAmount());

        hdmfDeductionField = new BigDecimalField("Pagibig Deduction");
        hdmfDeductionField.setReadOnly(true);
        if (payrollDTO != null) hdmfDeductionField.setValue(payrollDTO.getHdmfDeductionAmount());

        philhealthDeductionField = new BigDecimalField("Phil Health Deduction");
        philhealthDeductionField.setReadOnly(true);
        if (payrollDTO != null) philhealthDeductionField.setValue(payrollDTO.getPhilhealthDeductionAmount());

        loanDeductionField = new BigDecimalField("Loan Deduction");
        loanDeductionField.setReadOnly(true);
        if (payrollDTO != null) loanDeductionField.setValue(payrollDTO.getTotalLoanDeductionAmount());

        otherDeductionField = new BigDecimalField("Other Deduction");
        otherDeductionField.addBlurListener(blurEvent -> {
            BigDecimal otherDeduction = otherDeductionField.getValue();
            BigDecimal totalGrossPay = totalGrossPayField.getValue();
            BigDecimal totalDeduction = totalDeductionField.getValue();

            // Recompute the values after entering the values in the adjustment pay field.
            BigDecimal totalDeductionAmount = totalDeduction.add(otherDeduction);
            BigDecimal totalNetPay = totalGrossPay.subtract(totalDeductionAmount);
            BigDecimal withholdingTaxPay = payrollCalculatorService.computeWithholdingTax(totalGrossPay, totalDeductionAmount);

            totalDeductionField.setValue(totalDeductionAmount);
            totalNetPayField.setValue(totalNetPay);
            withholdingTaxField.setValue(withholdingTaxPay);
        });
        if (payrollDTO != null) otherDeductionField.setValue(payrollDTO.getOtherDeductionAmount());

        totalGrossPayField = new BigDecimalField("Total Gross Pay");
        totalGrossPayField.setReadOnly(true);
        if (payrollDTO != null) totalGrossPayField.setValue(payrollDTO.getTotalGrossPayAmount());

        totalDeductionField = new BigDecimalField("Total Deduction");
        totalDeductionField.setReadOnly(true);
        if (payrollDTO != null) totalDeductionField.setValue(payrollDTO.getTotalDeductionAmount());

        totalNetPayField = new BigDecimalField("Total Net Pay");
        totalNetPayField.setReadOnly(true);
        if (payrollDTO != null) totalNetPayField.setValue(payrollDTO.getTotalGrossPayAmount()
                .subtract(payrollDTO.getTotalDeductionAmount()));

        withholdingTaxField = new BigDecimalField("Withholding Tax");
        withholdingTaxField.setReadOnly(true);
        if (payrollDTO != null) withholdingTaxField.setValue(payrollDTO.getWithholdingTaxDeductionAmount());

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> {
            payrollDTO.setAdjustmentPayAmount(adjustmentPayField.getValue());
            payrollDTO.setOtherDeductionAmount(otherDeductionField.getValue());
            payrollDTO.setTotalGrossPayAmount(totalGrossPayField.getValue());
            payrollDTO.setTotalDeductionAmount(totalDeductionField.getValue());
            payrollDTO.setWithholdingTaxDeductionAmount(withholdingTaxField.getValue());
            payrollDTO.setUpdatedBy(loggedInUser);

            payrollService.saveOrUpdate(payrollDTO);

            // Show notification message.
            Notification notification = Notification.show("You have successfully updated the employee payroll.",
                    5000,
                    Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Return to employee payroll list.
            UI ui = saveButton.getUI().get();
            ui.navigate(EmployeePayrollListView.class);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event -> {
            cancelButton.getUI().get().navigate(EmployeePayrollListView.class);
        });

        HorizontalLayout employeeNoLayout = new HorizontalLayout();
        employeeNoLayout.add(employeeNoLabelSpan, employeeNoValueSpan);

        HorizontalLayout employeeNameLayout = new HorizontalLayout();
        employeeNameLayout.add(employeeNameLabelSpan, employeeNameValueSpan);

        HorizontalLayout cutOffDatesLayout = new HorizontalLayout();
        cutOffDatesLayout.add(cutOffDatesLabelSpan, cutOffDatesValueSpan);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setPadding(true);
        buttonLayout.setWidthFull();

        employeeFormLayout.add(employeeNoLayout, employeeNameLayout, cutOffDatesLayout);
        employeeFormLayout.setColspan(employeeNoLayout, 2);
        employeeFormLayout.setColspan(employeeNameLayout, 2);
        employeeFormLayout.setColspan(cutOffDatesLayout, 2);

        payFieldsFormLayout.add(basicPayField,
                allowancePayField,
                restDayPayField,
                nightDifferentialPayField,
                leavePayField,
                regularHolidayPayField,
                specialHolidayPayField,
                specialNonWorkingHolidayPayField,
                adjustmentPayField);

        deductionFieldsFormLayout.add(absentDeductionField,
                lateOrUndertimeDeductionField,
                sssDeductionField,
                hdmfDeductionField,
                philhealthDeductionField,
                loanDeductionField,
                otherDeductionField);

        computationFieldsFormLayout.add(totalGrossPayField,
                totalDeductionField,
                withholdingTaxField,
                totalNetPayField);

        payrollLayout.add(employeeFormLayout,
                payFieldsFormLayout,
                deductionFieldsFormLayout,
                computationFieldsFormLayout,
                buttonLayout);
        payrollLayout.setWidth("720px");
        payrollLayout.getStyle().set("overflow", "auto");
        payrollLayout.setSpacing(false);
    }
}

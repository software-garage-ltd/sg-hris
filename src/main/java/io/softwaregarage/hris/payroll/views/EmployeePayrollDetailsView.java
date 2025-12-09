package io.softwaregarage.hris.payroll.views;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.softwaregarage.hris.commons.views.MainLayout;
import io.softwaregarage.hris.payroll.dtos.PayrollDTO;
import io.softwaregarage.hris.payroll.services.PayrollService;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
        "ROLE_HR_MANAGER",
        "ROLE_PAYROLL_MANAGER",
        "ROLE_PAYROLL_EMPLOYEE"})
@PageTitle("Payroll Details")
@Route(value = "employee-payroll-details", layout = MainLayout.class)
public class EmployeePayrollDetailsView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource
    private final PayrollService payrollService;

    private PayrollDTO payrollDTO;

    private final VerticalLayout payrollDetailsLayout = new VerticalLayout();

    public EmployeePayrollDetailsView(PayrollService payrollService) {
        this.payrollService = payrollService;

        add(payrollDetailsLayout);

        setSizeFull();
        setMargin(true);
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, payrollDetailsLayout);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {
        if (parameter != null) {
            UUID parameterId = UUID.fromString(parameter);
            payrollDTO = payrollService.getById(parameterId);
        }

        buildPayrollDetailsLayout();
    }

    public void buildPayrollDetailsLayout() {
        FormLayout payDetailsLayout = new FormLayout();
        FormLayout deductionDetailsLayout = new FormLayout();
        VerticalLayout payrollLayout = new VerticalLayout();

        Span employeeNoLabelSpan = new Span("Employee No");
        employeeNoLabelSpan.getStyle().set("text-align", "right");

        Span employeeNoValueSpan = new Span(payrollDTO.getEmployeeDTO().getEmployeeNumber());
        employeeNoValueSpan.getStyle().setFontWeight("bold");

        Span employeeNameLabelSpan = new Span("Employee Name");
        employeeNameLabelSpan.getStyle().set("text-align", "right");

        String employeeName = payrollDTO.getEmployeeDTO().getFirstName()
                .concat(" ")
                .concat(payrollDTO.getEmployeeDTO().getMiddleName())
                .concat(" ")
                .concat(payrollDTO.getEmployeeDTO().getLastName())
                .concat(payrollDTO.getEmployeeDTO().getSuffix() != null
                        ? " ".concat(payrollDTO.getEmployeeDTO().getSuffix())
                        : "");

        Span employeeNameValueSpan = new Span(employeeName);
        employeeNameValueSpan.getStyle().setFontWeight("bold");

        Span cutOffDatesLabelSpan = new Span("Cut-off Dates");
        cutOffDatesLabelSpan.getStyle().set("text-align", "right");

        Span cutOffDatesValueSpan = new Span(payrollDTO.getCutOffFromDate()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                .concat(" to ")
                .concat(payrollDTO.getCutOffToDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
        );
        cutOffDatesValueSpan.getStyle().setFontWeight("bold");

        Span basicPayLabelSpan = new Span("Basic Pay");
        basicPayLabelSpan.getStyle().set("text-align", "right");

        Span basicPayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getBasicPayAmount())));
        basicPayValueSpan.getStyle().setFontWeight("bold");

        Span overtimePayLabelSpan = new Span("Overtime Pay");
        overtimePayLabelSpan.getStyle().set("text-align", "right");

        Span overtimePayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getOvertimePayAmount())));
        overtimePayValueSpan.getStyle().setFontWeight("bold");

        Span allowancePayLabelSpan = new Span("Allowance Pay");
        allowancePayLabelSpan.getStyle().set("text-align", "right");

        Span allowancePayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getAllowancePayAmount())));
        allowancePayValueSpan.getStyle().setFontWeight("bold");

        Span restDayPayLabelSpan = new Span("Rest Day Pay");
        restDayPayLabelSpan.getStyle().set("text-align", "right");

        Span restDayPayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getRestDayPayAmount())));
        restDayPayValueSpan.getStyle().setFontWeight("bold");

        Span nightDifferentialPayLabelSpan = new Span("Night Differential Pay");
        nightDifferentialPayLabelSpan.getStyle().set("text-align", "right");

        Span nightDifferentialValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getNightDifferentialPayAmount())));
        nightDifferentialValueSpan.getStyle().setFontWeight("bold");

        Span leavePayLabelSpan = new Span("Leave Pay");
        leavePayLabelSpan.getStyle().set("text-align", "right");

        Span leavePayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getLeavePayAmount())));
        leavePayValueSpan.getStyle().setFontWeight("bold");

        Span regularHolidayPayLabelSpan = new Span("Regular Holiday Pay");
        regularHolidayPayLabelSpan.getStyle().set("text-align", "right");

        Span regularHolidayPayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getRegularHolidayPayAmount())));
        regularHolidayPayValueSpan.getStyle().setFontWeight("bold");

        Span specialHolidayPayLabelSpan = new Span("Special Holiday Pay");
        specialHolidayPayLabelSpan.getStyle().set("text-align", "right");

        Span specialHolidayPayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getSpecialHolidayPayAmount())));
        specialHolidayPayValueSpan.getStyle().setFontWeight("bold");

        Span specialNonWorkingHolidayPayLabelSpan = new Span("Special Non-Working Holiday Pay");
        specialNonWorkingHolidayPayLabelSpan.getStyle().set("text-align", "right");

        Span specialNonWorkingHolidayPayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getSpecialNonWorkingHolidayPayAmount())));
        specialNonWorkingHolidayPayValueSpan.getStyle().setFontWeight("bold");

        Span adjustmentPayLabelSpan = new Span("Adjustment Pay");
        adjustmentPayLabelSpan.getStyle().set("text-align", "right");

        Span adjustmentPayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getAdjustmentPayAmount())));
        adjustmentPayValueSpan.getStyle().setFontWeight("bold");

        Span totalGrossPayLabelSpan = new Span("Total Gross Pay");
        totalGrossPayLabelSpan.getStyle().set("text-align", "right");

        Span totalGrossPayValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getTotalGrossPayAmount())));
        totalGrossPayValueSpan.getStyle().setFontWeight("bold");

        Span absentDeductionLabelSpan = new Span("Absent Deduction");
        absentDeductionLabelSpan.getStyle().set("text-align", "right");

        Span absentDeductionValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getAbsentDeductionAmount())));
        absentDeductionValueSpan.getStyle().setFontWeight("bold");

        Span lateOrUndertimeDeductionLabelSpan = new Span("Late or Undertime Deduction");
        lateOrUndertimeDeductionLabelSpan.getStyle().set("text-align", "right");

        Span lateOrUndertimeDeductionValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getLateOrUndertimeDeductionAmount())));
        lateOrUndertimeDeductionValueSpan.getStyle().setFontWeight("bold");

        Span sssDeductionLabelSpan = new Span("SSS Deduction");
        sssDeductionLabelSpan.getStyle().set("text-align", "right");

        Span sssDeductionValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getSssDeductionAmount())));
        sssDeductionValueSpan.getStyle().setFontWeight("bold");

        Span hdmfDeductionLabelSpan = new Span("Pag-Ibig Deduction");
        hdmfDeductionLabelSpan.getStyle().set("text-align", "right");

        Span hdmfDeductionValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getHdmfDeductionAmount())));
        hdmfDeductionValueSpan.getStyle().setFontWeight("bold");

        Span philhealthDeductionLabelSpan = new Span("Philhealth Deduction");
        philhealthDeductionLabelSpan.getStyle().set("text-align", "right");

        Span philhealthDeductionValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getPhilhealthDeductionAmount())));
        philhealthDeductionValueSpan.getStyle().setFontWeight("bold");

        Span otherDeductionLabelSpan = new Span("Other Deduction");
        otherDeductionLabelSpan.getStyle().set("text-align", "right");

        Span otherDeductionValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getOtherDeductionAmount())));
        otherDeductionValueSpan.getStyle().setFontWeight("bold");

        Span totalLoanDeductionLabelSpan = new Span("Loan Deduction");
        totalLoanDeductionLabelSpan.getStyle().set("text-align", "right");

        Span totalLoanDeductionValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getTotalLoanDeductionAmount())));
        totalLoanDeductionValueSpan.getStyle().setFontWeight("bold");

        Span totalDeductionLabelSpan = new Span("Total Deduction");
        totalDeductionLabelSpan.getStyle().set("text-align", "right");

        Span totalDeductionValueSpan = new Span("PHP ".concat(String.valueOf(payrollDTO.getTotalDeductionAmount())));
        totalDeductionValueSpan.getStyle().setFontWeight("bold");

        payDetailsLayout.add(
                basicPayLabelSpan,
                basicPayValueSpan,
                overtimePayLabelSpan,
                overtimePayValueSpan,
                allowancePayLabelSpan,
                allowancePayValueSpan,
                restDayPayLabelSpan,
                restDayPayValueSpan,
                nightDifferentialPayLabelSpan,
                nightDifferentialValueSpan,
                leavePayLabelSpan,
                leavePayValueSpan,
                regularHolidayPayLabelSpan,
                regularHolidayPayValueSpan,
                specialHolidayPayLabelSpan,
                specialHolidayPayValueSpan,
                specialNonWorkingHolidayPayLabelSpan,
                specialNonWorkingHolidayPayValueSpan,
                adjustmentPayLabelSpan,
                adjustmentPayValueSpan);
        payDetailsLayout.getStyle().setBackgroundColor("#BBFCC2");
        payDetailsLayout.getStyle().setBorder("1px solid #BBFCC2");
        payDetailsLayout.getStyle().setBorderRadius("5px");

        deductionDetailsLayout.add(absentDeductionLabelSpan,
                absentDeductionValueSpan,
                lateOrUndertimeDeductionLabelSpan,
                lateOrUndertimeDeductionValueSpan,
                sssDeductionLabelSpan,
                sssDeductionValueSpan,
                hdmfDeductionLabelSpan,
                hdmfDeductionValueSpan,
                philhealthDeductionLabelSpan,
                philhealthDeductionValueSpan,
                otherDeductionLabelSpan,
                otherDeductionValueSpan,
                totalLoanDeductionLabelSpan,
                totalLoanDeductionValueSpan);
        deductionDetailsLayout.getStyle().setBackgroundColor("#FCC2BB");
        deductionDetailsLayout.getStyle().setBorder("1px solid #FCC2BB");
        deductionDetailsLayout.getStyle().setBorderRadius("5px");

        payrollLayout.add(payDetailsLayout,
                new HorizontalLayout(totalGrossPayLabelSpan, totalGrossPayValueSpan),
                deductionDetailsLayout,
                new HorizontalLayout(totalDeductionLabelSpan, totalDeductionValueSpan));

        H4 headerWithholdingTax = new H4("Withholding Tax: PHP ".concat(payrollDTO.getWithholdingTaxDeductionAmount().toString()));

        BigDecimal netPayAmount = payrollDTO.getTotalGrossPayAmount()
                .subtract(payrollDTO.getTotalDeductionAmount())
                .subtract(payrollDTO.getWithholdingTaxDeductionAmount());

        H4 headerNetPay = new H4("Net Pay: PHP ".concat(netPayAmount.toString()));

        payrollDetailsLayout.add(new HorizontalLayout(employeeNoLabelSpan, employeeNoValueSpan),
                new HorizontalLayout(employeeNameLabelSpan, employeeNameValueSpan),
                new HorizontalLayout(cutOffDatesLabelSpan, cutOffDatesValueSpan),
                payrollLayout,
                headerWithholdingTax,
                headerNetPay);
        payrollDetailsLayout.setWidth("720px");
        payrollDetailsLayout.getStyle().set("overflow", "auto");
        payrollDetailsLayout.setSpacing(false);
    }
}

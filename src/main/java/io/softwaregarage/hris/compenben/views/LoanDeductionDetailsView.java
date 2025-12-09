package io.softwaregarage.hris.compenben.views;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.softwaregarage.hris.compenben.dtos.LoanDeductionDTO;
import io.softwaregarage.hris.compenben.services.LoanDeductionService;
import io.softwaregarage.hris.commons.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR"})
@PageTitle("Loan Deduction Details")
@Route(value = "loan-deduction-details", layout = MainLayout.class)
public class LoanDeductionDetailsView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource
    private final LoanDeductionService loanDeductionService;
    private LoanDeductionDTO loanDeductionDTO;

    private final FormLayout loanDeductionDetailsLayout = new FormLayout();

    public LoanDeductionDetailsView(LoanDeductionService loanDeductionService) {
        this.loanDeductionService = loanDeductionService;

        add(loanDeductionDetailsLayout);

        setSizeFull();
        setMargin(true);
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, loanDeductionDetailsLayout);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        if (s != null) {
            UUID parameterId = UUID.fromString(s);
            loanDeductionDTO = loanDeductionService.getById(parameterId);
        }

        buildLoanDeductionDetailsLayout();
    }

    public void buildLoanDeductionDetailsLayout() {
        Span employeeNoLabelSpan = new Span("Employee No");
        employeeNoLabelSpan.getStyle().set("text-align", "right");

        Span employeeNoValueSpan = new Span(loanDeductionDTO.getEmployeeDTO().getEmployeeNumber());
        employeeNoValueSpan.getStyle().setFontWeight("bold");

        Span employeeNameLabelSpan = new Span("Employee Name");
        employeeNameLabelSpan.getStyle().set("text-align", "right");

        String employeeName = loanDeductionDTO.getEmployeeDTO().getFirstName()
                                                               .concat(" ")
                                                               .concat(loanDeductionDTO.getEmployeeDTO().getMiddleName())
                                                               .concat(" ")
                                                               .concat(loanDeductionDTO.getEmployeeDTO().getLastName())
                                                               .concat(loanDeductionDTO.getEmployeeDTO().getSuffix() != null ?
                                                                       " ".concat(loanDeductionDTO.getEmployeeDTO().getSuffix()) :
                                                                       "");

        Span employeeNameValueSpan = new Span(employeeName);
        employeeNameValueSpan.getStyle().setFontWeight("bold");

        Span loanTypeLabelSpan = new Span("Type");
        loanTypeLabelSpan.getStyle().set("text-align", "right");

        Span loanTypeValueSpan = new Span(loanDeductionDTO.getLoanType());
        loanTypeValueSpan.getStyle().setFontWeight("bold");

        Span loanDescriptionLabelSpan = new Span("Description");
        loanDescriptionLabelSpan.getStyle().set("text-align", "right");

        Span loanDescriptionValueSpan = new Span(loanDeductionDTO.getLoanDescription());
        loanDescriptionValueSpan.getStyle().setFontWeight("bold");

        // Format the date to display in the page.
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);

        Span loanStartDateLabelSpan = new Span("Start Date");
        loanStartDateLabelSpan.getStyle().set("text-align", "right");

        Span loanStartDateValueSpan = new Span(df.format(loanDeductionDTO.getLoanStartDate()));
        loanStartDateValueSpan.getStyle().setFontWeight("bold");

        Span loanEndDateLabelSpan = new Span("End Date");
        loanEndDateLabelSpan.getStyle().set("text-align", "right");

        Span loanEndDateValueSpan = new Span(df.format(loanDeductionDTO.getLoanEndDate()));
        loanEndDateValueSpan.getStyle().setFontWeight("bold");

        Span loanAmountLabelSpan = new Span("Amount");
        loanAmountLabelSpan.getStyle().set("text-align", "right");

        Span loanAmountValueSpan = new Span("PHP " + loanDeductionDTO.getLoanAmount());
        loanAmountValueSpan.getStyle().setFontWeight("bold");

        Span monthlyDeductionLabelSpan = new Span("Monthly Deduction");
        monthlyDeductionLabelSpan.getStyle().set("text-align", "right");

        Span monthlyDeductionValueSpan = new Span("PHP " + loanDeductionDTO.getMonthlyDeduction());
        monthlyDeductionValueSpan.getStyle().setFontWeight("bold");

        Span loanCutOffLabelSpan = new Span("Loan Cut Off");
        loanCutOffLabelSpan.getStyle().set("text-align", "right");

        Span loanCutOffValueSpan = new Span(loanDeductionDTO.getLoanCutOff() == 1 ? "1st Cut-Off" : "2nd Cut-Off");
        loanCutOffValueSpan.getStyle().setFontWeight("bold");

        loanDeductionDetailsLayout.add(employeeNoLabelSpan,
                                        employeeNoValueSpan,
                                        employeeNameLabelSpan,
                                        employeeNameValueSpan,
                                        loanTypeLabelSpan,
                                        loanTypeValueSpan,
                                        loanDescriptionLabelSpan,
                                        loanDescriptionValueSpan,
                                        loanStartDateLabelSpan,
                                        loanStartDateValueSpan,
                                        loanEndDateLabelSpan,
                                        loanEndDateValueSpan,
                                        loanAmountLabelSpan,
                                        loanAmountValueSpan,
                                        monthlyDeductionLabelSpan,
                                        monthlyDeductionValueSpan,
                                        loanCutOffLabelSpan,
                                        loanCutOffValueSpan);
        loanDeductionDetailsLayout.setWidth("720px");
    }
}

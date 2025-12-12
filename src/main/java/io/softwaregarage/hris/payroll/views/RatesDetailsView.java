package io.softwaregarage.hris.payroll.views;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.softwaregarage.hris.payroll.dtos.RatesDTO;
import io.softwaregarage.hris.payroll.services.RatesService;
import io.softwaregarage.hris.commons.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
        "ROLE_HR_MANAGER",
        "ROLE_PAYROLL_MANAGER",
        "ROLE_PAYROLL_EMPLOYEE"})
@PageTitle("Rates Details")
@Route(value = "rates-details", layout = MainLayout.class)
public class RatesDetailsView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource
    private final RatesService ratesService;

    private RatesDTO ratesDTO;

    private final FormLayout ratesDetailsLayout = new FormLayout();

    public RatesDetailsView(RatesService ratesService) {
        this.ratesService = ratesService;

        add(ratesDetailsLayout);

        setSizeFull();
        setMargin(true);
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, ratesDetailsLayout);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {
        if (parameter != null) {
            UUID parameterId = UUID.fromString(parameter);
            ratesDTO = ratesService.getById(parameterId);
        }

        buildRatesDetailsLayout();
    }

    public void buildRatesDetailsLayout() {
        Span employeeNoLabelSpan = new Span("Employee No");
        employeeNoLabelSpan.getStyle().set("text-align", "right");

        Span employeeNoValueSpan = new Span(ratesDTO.getEmployeeDTO().getEmployeeNumber());
        employeeNoValueSpan.getStyle().setFontWeight("bold");

        Span employeeNameLabelSpan = new Span("Employee Name");
        employeeNameLabelSpan.getStyle().set("text-align", "right");

        String employeeName = ratesDTO.getEmployeeDTO().getFirstName()
                                                       .concat(" ")
                                                       .concat(ratesDTO.getEmployeeDTO().getMiddleName())
                                                       .concat(" ")
                                                       .concat(ratesDTO.getEmployeeDTO().getLastName())
                                                       .concat(ratesDTO.getEmployeeDTO().getSuffix() != null ? " ".concat(ratesDTO.getEmployeeDTO().getSuffix()) : "");

        Span employeeNameValueSpan = new Span(employeeName);
        employeeNameValueSpan.getStyle().setFontWeight("bold");

        Span isCurrentRatesLabelSpan = new Span("Rate Type");
        isCurrentRatesLabelSpan.getStyle().set("text-align", "right");

        Span isCurrentRatesValueSpan = new Span(ratesDTO.getRateType());
        isCurrentRatesValueSpan.getStyle().setFontWeight("bold");

        Span basicRateLabelSpan = new Span("Basic Rate");
        basicRateLabelSpan.getStyle().set("text-align", "right");

        Span basicRateValueSpan = new Span("PHP ".concat(String.valueOf(ratesDTO.getBasicCompensationRate())));
        basicRateValueSpan.getStyle().setFontWeight("bold");

        Span dailyRateLabelSpan = new Span("Daily Rate");
        dailyRateLabelSpan.getStyle().set("text-align", "right");

        Span dailyRateValueSpan = new Span("PHP ".concat(String.valueOf(ratesDTO.getDailyCompensationRate())));
        dailyRateValueSpan.getStyle().setFontWeight("bold");

        Span hourlyRateLabelSpan = new Span("Hourly Rate");
        hourlyRateLabelSpan.getStyle().set("text-align", "right");

        Span hourlyRateValueSpan = new Span("PHP ".concat(String.valueOf(ratesDTO.getHourlyCompensationRate())));
        hourlyRateValueSpan.getStyle().setFontWeight("bold");

        Span overtimeHourlyRateLabelSpan = new Span("Overtime Hourly Rate");
        overtimeHourlyRateLabelSpan.getStyle().set("text-align", "right");

        Span overtimeHourlyRateValueSpan = new Span("PHP ".concat(String.valueOf(ratesDTO.getOvertimeHourlyCompensationRate())));
        overtimeHourlyRateValueSpan.getStyle().setFontWeight("bold");

        Span lateHourlyRateLabelSpan = new Span("Late Hourly Rate");
        lateHourlyRateLabelSpan.getStyle().set("text-align", "right");

        Span lateHourlyRateValueSpan = new Span("PHP ".concat(String.valueOf(ratesDTO.getLateHourlyDeductionRate())));
        lateHourlyRateValueSpan.getStyle().setFontWeight("bold");

        Span absentDailyRateLabelSpan = new Span("Absent Daily Rate");
        absentDailyRateLabelSpan.getStyle().set("text-align", "right");

        Span absentDailyRateValueSpan = new Span("PHP ".concat(String.valueOf(ratesDTO.getDailyAbsentDeductionRate())));
        absentDailyRateValueSpan.getStyle().setFontWeight("bold");

        ratesDetailsLayout.add(employeeNoLabelSpan,
                            employeeNoValueSpan,
                            employeeNameLabelSpan,
                            employeeNameValueSpan,
                            basicRateLabelSpan,
                            basicRateValueSpan,
                            dailyRateLabelSpan,
                            dailyRateValueSpan,
                            hourlyRateLabelSpan,
                            hourlyRateValueSpan,
                            overtimeHourlyRateLabelSpan,
                            overtimeHourlyRateValueSpan,
                            lateHourlyRateLabelSpan,
                            lateHourlyRateValueSpan,
                            absentDailyRateLabelSpan,
                            absentDailyRateValueSpan,
                            isCurrentRatesLabelSpan,
                            isCurrentRatesValueSpan);
        ratesDetailsLayout.setWidth("720px");
    }
}

package io.softwaregarage.hris.compenben.views;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.softwaregarage.hris.compenben.dtos.AllowanceDTO;
import io.softwaregarage.hris.compenben.services.AllowanceService;
import io.softwaregarage.hris.commons.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR"})
@PageTitle("Allowance Details")
@Route(value = "allowance-details", layout = MainLayout.class)
public class AllowanceDetailsView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource
    private final AllowanceService allowanceService;
    private AllowanceDTO allowanceDTO;

    private final FormLayout allowanceDetailsLayout = new FormLayout();

    public AllowanceDetailsView(AllowanceService allowanceService) {
        this.allowanceService = allowanceService;

        add(allowanceDetailsLayout);

        setSizeFull();
        setMargin(true);
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, allowanceDetailsLayout);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        if (s != null) {
            UUID parameterId = UUID.fromString(s);
            allowanceDTO = allowanceService.getById(parameterId);
        }

        buildAllowanceDetailsLayout();
    }

    public void buildAllowanceDetailsLayout() {
        Span employeeNoLabelSpan = new Span("Employee No");
        employeeNoLabelSpan.getStyle().set("text-align", "right");

        Span employeeNoValueSpan = new Span(allowanceDTO.getEmployeeDTO().getEmployeeNumber());
        employeeNoValueSpan.getStyle().setFontWeight("bold");

        Span employeeNameLabelSpan = new Span("Employee Name");
        employeeNameLabelSpan.getStyle().set("text-align", "right");

        String employeeName = allowanceDTO.getEmployeeDTO().getFirstName()
                .concat(" ")
                .concat(allowanceDTO.getEmployeeDTO().getMiddleName())
                .concat(" ")
                .concat(allowanceDTO.getEmployeeDTO().getLastName())
                .concat(allowanceDTO.getEmployeeDTO().getSuffix() != null ? " ".concat(allowanceDTO.getEmployeeDTO().getSuffix()) : "");

        Span employeeNameValueSpan = new Span(employeeName);
        employeeNameValueSpan.getStyle().setFontWeight("bold");

        Span allowanceCodeLabelSpan = new Span("Allowance Code");
        allowanceCodeLabelSpan.getStyle().set("text-align", "right");

        Span allowanceCodeValueSpan = new Span(allowanceDTO.getAllowanceCode());
        allowanceCodeValueSpan.getStyle().setFontWeight("bold");

        Span allowanceTypeLabelSpan = new Span("Allowance Type");
        allowanceTypeLabelSpan.getStyle().set("text-align", "right");

        Span allowanceTypeValueSpan = new Span(allowanceDTO.getAllowanceType());
        allowanceTypeValueSpan.getStyle().setFontWeight("bold");

        Span allowanceAmountLabelSpan = new Span("Allowance Amount");
        allowanceAmountLabelSpan.getStyle().set("text-align", "right");

        Span allowanceAmountValueSpan = new Span("PHP" + allowanceDTO.getAllowanceAmount());
        allowanceAmountValueSpan.getStyle().setFontWeight("bold");

        Span allowanceTaxableLabelSpan = new Span("Is Taxable?");
        allowanceTaxableLabelSpan.getStyle().set("text-align", "right");

        Span allowancetaxableValueSpan = new Span(allowanceDTO.isTaxable() ? "Yes" : "No");
        allowancetaxableValueSpan.getStyle().setFontWeight("bold");

        Span allowanceCutOffLabelSpan = new Span("Cut-Off");
        allowanceCutOffLabelSpan.getStyle().set("text-align", "right");

        Span allowanceCutOffValueSpan = new Span(allowanceDTO.getAllowanceCutOff().equals(1) ? "1st Cut-Off" : "2nd Cut-Off");
        allowanceCutOffValueSpan.getStyle().setFontWeight("bold");

        allowanceDetailsLayout.add(employeeNoLabelSpan,
                employeeNoValueSpan,
                employeeNameLabelSpan,
                employeeNameValueSpan,
                allowanceCodeLabelSpan,
                allowanceCodeValueSpan,
                allowanceTypeLabelSpan,
                allowanceTypeValueSpan,
                allowanceAmountLabelSpan,
                allowanceAmountValueSpan,
                allowanceTaxableLabelSpan,
                allowancetaxableValueSpan,
                allowanceCutOffLabelSpan,
                allowanceCutOffValueSpan);
        allowanceDetailsLayout.setWidth("720px");
    }
}

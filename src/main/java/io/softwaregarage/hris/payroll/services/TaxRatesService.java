package io.softwaregarage.hris.payroll.services;

import io.softwaregarage.hris.commons.BaseService;
import io.softwaregarage.hris.payroll.dtos.TaxRatesDTO;

import java.util.List;

public interface TaxRatesService extends BaseService<TaxRatesDTO> {
    List<TaxRatesDTO> getTaxRatesByYear(int year);
}

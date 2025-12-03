package io.softwaregarage.hris.payroll.repositories;

import io.softwaregarage.hris.payroll.entities.TaxRates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaxRatesRepository extends JpaRepository<TaxRates, UUID> {
    @Query("SELECT tr FROM TaxRates tr WHERE tr.taxYear = :year")
    List<TaxRates> getTaxRatesByYear(@Param("year") int year);
}

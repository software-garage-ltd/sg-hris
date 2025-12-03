package io.softwaregarage.hris.payroll.services.impls;

import io.softwaregarage.hris.payroll.dtos.TaxRatesDTO;
import io.softwaregarage.hris.payroll.entities.TaxRates;
import io.softwaregarage.hris.payroll.repositories.TaxRatesRepository;
import io.softwaregarage.hris.payroll.services.TaxRatesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaxRatesServiceImpl implements TaxRatesService {
    private final Logger logger = LoggerFactory.getLogger(TaxRatesServiceImpl.class);

    private final TaxRatesRepository taxRatesRepository;

    public TaxRatesServiceImpl(TaxRatesRepository taxRatesRepository) {
        this.taxRatesRepository = taxRatesRepository;
    }

    @Override
    public void saveOrUpdate(TaxRatesDTO object) {
        TaxRates taxRates;
        String logMessage;

        if (object.getId() != null) {
            taxRates = taxRatesRepository.getReferenceById(object.getId());
            logMessage = "Tax rate record with id ".concat(object.getId().toString()).concat(" is successfully updated.");
        } else {
            taxRates = new TaxRates();
            taxRates.setCreatedBy(object.getCreatedBy());
            taxRates.setDateAndTimeCreated(LocalDateTime.now(ZoneId.of("Asia/Manila")));
            logMessage = "Tax rate record is successfully created.";
        }

        taxRates.setTaxYear(object.getTaxYear());
        taxRates.setEffectiveDate(object.getEffectiveDate());
        taxRates.setLowerBoundAmount(object.getLowerBoundAmount());
        taxRates.setUpperBoundAmount(object.getUpperBoundAmount());
        taxRates.setBaseTax(object.getBaseTax());
        taxRates.setRate(object.getRate());
        taxRates.setActiveTaxRate(object.isActiveTaxRate());
        taxRates.setUpdatedBy(object.getUpdatedBy());
        taxRates.setDateAndTimeUpdated(LocalDateTime.now(ZoneId.of("Asia/Manila")));

        taxRatesRepository.save(taxRates);
        logger.info(logMessage);
    }

    @Override
    public TaxRatesDTO getById(UUID id) {
        logger.info("Retrieving tax rate record with UUID ".concat(id.toString()));

        TaxRates taxRates = taxRatesRepository.getReferenceById(id);
        TaxRatesDTO taxRatesDTO = new TaxRatesDTO();

        taxRatesDTO.setId(taxRates.getId());
        taxRatesDTO.setTaxYear(taxRates.getTaxYear());
        taxRatesDTO.setEffectiveDate(taxRates.getEffectiveDate());
        taxRatesDTO.setLowerBoundAmount(taxRates.getLowerBoundAmount());
        taxRatesDTO.setUpperBoundAmount(taxRates.getUpperBoundAmount());
        taxRatesDTO.setBaseTax(taxRates.getBaseTax());
        taxRatesDTO.setRate(taxRates.getRate());
        taxRatesDTO.setActiveTaxRate(taxRates.isActiveTaxRate());
        taxRatesDTO.setCreatedBy(taxRates.getCreatedBy());
        taxRatesDTO.setDateAndTimeCreated(taxRates.getDateAndTimeCreated());
        taxRatesDTO.setUpdatedBy(taxRates.getUpdatedBy());
        taxRatesDTO.setDateAndTimeUpdated(taxRates.getDateAndTimeUpdated());

        logger.info("Tax rate record with id ".concat(id.toString()).concat(" is successfully retrieved."));

        return taxRatesDTO;
    }

    @Override
    public void delete(TaxRatesDTO object) {
        if (object != null) {
            logger.warn("You are about to delete the tax rate record permanently.");

            String id = object.getId().toString();
            TaxRates taxRates = taxRatesRepository.getReferenceById(object.getId());
            taxRatesRepository.delete(taxRates);

            logger.info("Tax rate record with id ".concat(id).concat(" is successfully deleted."));
        }
    }

    @Override
    public List<TaxRatesDTO> getAll(int page, int pageSize) {
        logger.info("Retrieving tax rate records from the database.");
        List<TaxRates> taxRatesList = taxRatesRepository.findAll(PageRequest.of(page, pageSize)).stream().toList();

        logger.info("Tax rate records successfully retrieved.");
        List<TaxRatesDTO> taxRatesDTOList = new ArrayList<>();

        if (!taxRatesList.isEmpty()) {
            for (TaxRates taxRates : taxRatesList) {
                TaxRatesDTO taxRatesDTO = new TaxRatesDTO();

                taxRatesDTO.setId(taxRates.getId());
                taxRatesDTO.setTaxYear(taxRates.getTaxYear());
                taxRatesDTO.setEffectiveDate(taxRates.getEffectiveDate());
                taxRatesDTO.setLowerBoundAmount(taxRates.getLowerBoundAmount());
                taxRatesDTO.setUpperBoundAmount(taxRates.getUpperBoundAmount());
                taxRatesDTO.setBaseTax(taxRates.getBaseTax());
                taxRatesDTO.setRate(taxRates.getRate());
                taxRatesDTO.setActiveTaxRate(taxRates.isActiveTaxRate());
                taxRatesDTO.setCreatedBy(taxRates.getCreatedBy());
                taxRatesDTO.setDateAndTimeCreated(taxRates.getDateAndTimeCreated());
                taxRatesDTO.setUpdatedBy(taxRates.getUpdatedBy());
                taxRatesDTO.setDateAndTimeUpdated(taxRates.getDateAndTimeUpdated());

                taxRatesDTOList.add(taxRatesDTO);
            }

            logger.info(String.valueOf(taxRatesList.size()).concat(" record(s) found."));
        }

        return taxRatesDTOList;
    }

    @Override
    public List<TaxRatesDTO> findByParameter(String param) {
        return List.of();
    }

    @Override
    public List<TaxRatesDTO> getTaxRatesByYear(int year) {
        logger.info("Retrieving tax rate records with tax year " + year + " from the database.");
        List<TaxRates> taxRatesList = taxRatesRepository.getTaxRatesByYear(year);

        logger.info("Tax rate records with tax year " + year + " has successfully retrieved.");
        List<TaxRatesDTO> taxRatesDTOList = new ArrayList<>();

        if (!taxRatesList.isEmpty()) {
            for (TaxRates taxRates : taxRatesList) {
                TaxRatesDTO taxRatesDTO = new TaxRatesDTO();

                taxRatesDTO.setId(taxRates.getId());
                taxRatesDTO.setTaxYear(taxRates.getTaxYear());
                taxRatesDTO.setEffectiveDate(taxRates.getEffectiveDate());
                taxRatesDTO.setLowerBoundAmount(taxRates.getLowerBoundAmount());
                taxRatesDTO.setUpperBoundAmount(taxRates.getUpperBoundAmount());
                taxRatesDTO.setBaseTax(taxRates.getBaseTax());
                taxRatesDTO.setRate(taxRates.getRate());
                taxRatesDTO.setActiveTaxRate(taxRates.isActiveTaxRate());
                taxRatesDTO.setCreatedBy(taxRates.getCreatedBy());
                taxRatesDTO.setDateAndTimeCreated(taxRates.getDateAndTimeCreated());
                taxRatesDTO.setUpdatedBy(taxRates.getUpdatedBy());
                taxRatesDTO.setDateAndTimeUpdated(taxRates.getDateAndTimeUpdated());

                taxRatesDTOList.add(taxRatesDTO);
            }

            logger.info(String.valueOf(taxRatesList.size()).concat(" record(s) found."));
        }

        return taxRatesDTOList;
    }
}

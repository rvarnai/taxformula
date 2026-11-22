package at.rvarnai.taxformula.service;

import at.rvarnai.taxformula.model.Year;

public interface FormulaFactoryService {

    String createFormula(Year year, String profitBeforeTaxCell, String basicTaxFreeAllowanceCell);

    default String createFormula(Year year) {
        return createFormula(year, "E34", "E35");
    }
}
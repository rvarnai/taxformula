package at.rvarnai.taxformula.service;

import at.rvarnai.taxformula.model.Year;

public interface FormulaFactoryService {

    String createFormula(Year year);
}
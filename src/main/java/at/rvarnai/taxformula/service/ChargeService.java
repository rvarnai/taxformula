package at.rvarnai.taxformula.service;

import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Level;
import at.rvarnai.taxformula.model.Year;

import java.math.BigDecimal;
import java.util.List;

public interface ChargeService {

    List<Year> getYears();

    List<Charge> getChargesByYear(Year year);

    Charge getCharge(Year year, Level level);

    BigDecimal getTaxAmount(Year year, Charge currentCharge, Charge nextCharge);
}
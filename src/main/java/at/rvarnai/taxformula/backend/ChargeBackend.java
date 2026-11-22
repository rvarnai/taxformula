package at.rvarnai.taxformula.backend;

import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Year;

import java.util.List;

public interface ChargeBackend {

    List<Charge> getChargesByYear(Year year);
}
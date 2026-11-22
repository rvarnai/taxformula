package at.rvarnai.taxformula.mapper;

import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Level;
import at.rvarnai.taxformula.repository.TaxChargeEntity;

import java.util.function.Function;

public class TaxChargeEntityToChargeMapper implements Function<TaxChargeEntity, Charge> {

    public static final TaxChargeEntityToChargeMapper INSTANCE = new TaxChargeEntityToChargeMapper();

    @Override
    public Charge apply(TaxChargeEntity entity) {
        return new Charge(Level.valueOf(entity.getLevel()), entity.getAmount(), entity.getPercentage());
    }

}
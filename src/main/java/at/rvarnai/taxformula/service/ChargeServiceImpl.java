package at.rvarnai.taxformula.service;

import at.rvarnai.taxformula.backend.ChargeBackend;
import at.rvarnai.taxformula.exception.NotFoundException;
import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Level;
import at.rvarnai.taxformula.model.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargeServiceImpl implements ChargeService {

    private final ChargeBackend backend;

    @Override
    public List<Year> getYears() {
        return List.of(Year.values());
    }

    @Override
    public List<Charge> getChargesByYear(Year year) {
        Assert.notNull(year, "year");
        return backend.getChargesByYear(year);
    }

    @Override
    public Charge getCharge(Year year, Level level) {
        Assert.notNull(year, "year");
        Assert.notNull(level, "level");
        final List<Charge> charges = backend.getChargesByYear(year);
        if (charges.isEmpty()) {
            throw new NotFoundException("year", year.name());
        }
        return charges.stream().filter(charge -> level == charge.level()).findFirst()
                .orElseThrow(() -> new NotFoundException("tax level", level.name()));
    }

    @Override
    public BigDecimal getTaxAmount(Year year, Charge currentCharge, Charge nextCharge) {
        Assert.notNull(year, "year");
        Assert.notNull(currentCharge, "currentCharge");
        Assert.notNull(nextCharge, "nextCharge");
        return (nextCharge.amount().subtract(currentCharge.amount())).multiply(currentCharge.percentage());
    }

}
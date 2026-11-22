package at.rvarnai.taxformula.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ChargeTest {

    @Test
    void formatsIncomeRangeAndTaxRate() {
        Charge charge = new Charge(Level.LEVEL2, new BigDecimal("21617"), new BigDecimal("0.300"));

        TaxBracket bracket = charge.toTaxBracket(
                new BigDecimal("21617"),
                new BigDecimal("33500"),
                new BigDecimal("0.300"));

        assertThat(bracket).isEqualTo(new TaxBracket("Over €21\u00a0617 up to €33\u00a0500", "30 %"));
    }

    @Test
    void formatsHighestBracketWithoutUpperLimit() {
        Charge charge = new Charge(Level.LEVEL6, new BigDecimal("100000"), new BigDecimal("0.55"));

        TaxBracket bracket = charge.toTaxBracket(
                new BigDecimal("100000"),
                BigDecimal.ZERO,
                new BigDecimal("0.55"));

        assertThat(bracket).isEqualTo(new TaxBracket("Over €100\u00a0000", "55 %"));
    }
}

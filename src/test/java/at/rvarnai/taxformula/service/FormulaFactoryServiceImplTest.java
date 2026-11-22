package at.rvarnai.taxformula.service;

import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Level;
import at.rvarnai.taxformula.model.Year;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormulaFactoryServiceImplTest {

    @Mock
    private ChargeService chargeService;

    @Test
    void createsExcelFormulaFromAllTaxLevels() {
        Charge level1 = charge(Level.LEVEL1, "10000", "0.10");
        Charge level2 = charge(Level.LEVEL2, "20000", "0.20");
        Charge level3 = charge(Level.LEVEL3, "30000", "0.30");
        Charge level4 = charge(Level.LEVEL4, "40000", "0.40");
        Charge level5 = charge(Level.LEVEL5, "50000", "0.50");
        Charge level6 = charge(Level.LEVEL6, "60000", "0.60");

        stubCharges(level1, level2, level3, level4, level5, level6);
        stubTaxAmounts(level1, level2, level3, level4, level5, level6);

        FormulaFactoryServiceImpl service = new FormulaFactoryServiceImpl(chargeService);
        String formula = service.createFormula(Year.CURRENT);

        assertThat(formula).startsWith("=");
        assertThat(formula).contains("(E34-E35)");
        assertThat(formula).contains("WENN");
        assertThat(formula).endsWith("))))))");
        verify(chargeService).getCharge(Year.CURRENT, Level.LEVEL1);
        verify(chargeService).getCharge(Year.CURRENT, Level.LEVEL6);
    }

    private void stubCharges(
            Charge level1,
            Charge level2,
            Charge level3,
            Charge level4,
            Charge level5,
            Charge level6) {
        when(chargeService.getCharge(Year.CURRENT, Level.LEVEL1)).thenReturn(level1);
        when(chargeService.getCharge(Year.CURRENT, Level.LEVEL2)).thenReturn(level2);
        when(chargeService.getCharge(Year.CURRENT, Level.LEVEL3)).thenReturn(level3);
        when(chargeService.getCharge(Year.CURRENT, Level.LEVEL4)).thenReturn(level4);
        when(chargeService.getCharge(Year.CURRENT, Level.LEVEL5)).thenReturn(level5);
        when(chargeService.getCharge(Year.CURRENT, Level.LEVEL6)).thenReturn(level6);
    }

    private void stubTaxAmounts(
            Charge level1,
            Charge level2,
            Charge level3,
            Charge level4,
            Charge level5,
            Charge level6) {
        when(chargeService.getTaxAmount(Year.CURRENT, level1, level2)).thenReturn(new BigDecimal("1000"));
        when(chargeService.getTaxAmount(Year.CURRENT, level2, level3)).thenReturn(new BigDecimal("2000"));
        when(chargeService.getTaxAmount(Year.CURRENT, level3, level4)).thenReturn(new BigDecimal("3000"));
        when(chargeService.getTaxAmount(Year.CURRENT, level4, level5)).thenReturn(new BigDecimal("4000"));
        when(chargeService.getTaxAmount(Year.CURRENT, level5, level6)).thenReturn(new BigDecimal("5000"));
    }

    private Charge charge(Level level, String amount, String percentage) {
        return new Charge(level, new BigDecimal(amount), new BigDecimal(percentage));
    }
}

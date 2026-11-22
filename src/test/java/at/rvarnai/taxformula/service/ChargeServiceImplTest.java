package at.rvarnai.taxformula.service;

import at.rvarnai.taxformula.backend.ChargeBackend;
import at.rvarnai.taxformula.exception.NotFoundException;
import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Level;
import at.rvarnai.taxformula.model.Year;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargeServiceImplTest {

    @Mock
    private ChargeBackend backend;

    @Test
    void getsAvailableYears() {
        ChargeServiceImpl service = new ChargeServiceImpl(backend);

        assertThat(service.getYears()).containsExactly(Year.values());
    }

    @Test
    void getsChargesForYear() {
        List<Charge> charges = List.of(charge(Level.LEVEL1, "13308", "0.20"));
        when(backend.getChargesByYear(Year.CURRENT)).thenReturn(charges);

        ChargeServiceImpl service = new ChargeServiceImpl(backend);

        assertThat(service.getChargesByYear(Year.CURRENT)).isSameAs(charges);
        verify(backend).getChargesByYear(Year.CURRENT);
    }

    @Test
    void getsRequestedChargeLevel() {
        List<Charge> charges = List.of(
                charge(Level.LEVEL1, "13308", "0.20"),
                charge(Level.LEVEL2, "21617", "0.30"));
        when(backend.getChargesByYear(Year.CURRENT)).thenReturn(charges);

        ChargeServiceImpl service = new ChargeServiceImpl(backend);

        assertThat(service.getCharge(Year.CURRENT, Level.LEVEL2))
                .isEqualTo(charge(Level.LEVEL2, "21617", "0.30"));
    }

    @Test
    void throwsNotFoundWhenYearHasNoCharges() {
        when(backend.getChargesByYear(Year.PREVIOUS)).thenReturn(List.of());

        ChargeServiceImpl service = new ChargeServiceImpl(backend);

        assertThatThrownBy(() -> service.getCharge(Year.PREVIOUS, Level.LEVEL1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Selected year PREVIOUS not found!");
    }

    @Test
    void throwsNotFoundWhenLevelIsMissing() {
        when(backend.getChargesByYear(Year.CURRENT))
                .thenReturn(List.of(charge(Level.LEVEL1, "13308", "0.20")));

        ChargeServiceImpl service = new ChargeServiceImpl(backend);

        assertThatThrownBy(() -> service.getCharge(Year.CURRENT, Level.LEVEL6))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Selected tax level LEVEL6 not found!");
    }

    @Test
    void calculatesTaxAmountFromAdjacentLevels() {
        Charge current = charge(Level.LEVEL1, "10000", "0.20");
        Charge next = charge(Level.LEVEL2, "20000", "0.30");

        ChargeServiceImpl service = new ChargeServiceImpl(backend);

        assertThat(service.getTaxAmount(Year.CURRENT, current, next))
                .isEqualByComparingTo(new BigDecimal("2000"));
    }

    private Charge charge(Level level, String amount, String percentage) {
        return new Charge(level, new BigDecimal(amount), new BigDecimal(percentage));
    }
}

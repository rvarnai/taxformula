package at.rvarnai.taxformula.backend;

import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Level;
import at.rvarnai.taxformula.model.Year;
import at.rvarnai.taxformula.repository.TaxChargeDao;
import at.rvarnai.taxformula.repository.TaxChargeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargeBackendImplTest {

    @Mock
    private TaxChargeDao dao;

    @Test
    void mapsRepositoryEntitiesToCharges() {
        TaxChargeEntity entity = new TaxChargeEntity(
                1L,
                2025,
                "LEVEL2",
                new BigDecimal("21617"),
                new BigDecimal("0.30"));
        when(dao.findByYear(Year.PREVIOUS.getValue())).thenReturn(List.of(entity));

        ChargeBackendImpl backend = new ChargeBackendImpl(dao);
        List<Charge> charges = backend.getChargesByYear(Year.PREVIOUS);

        assertThat(charges).containsExactly(
                new Charge(Level.LEVEL2, new BigDecimal("21617"), new BigDecimal("0.30")));
        verify(dao).findByYear(Year.PREVIOUS.getValue());
    }

    @Test
    void returnsEmptyListWhenYearHasNoCharges() {
        when(dao.findByYear(Year.PREVIOUS.getValue())).thenReturn(List.of());

        ChargeBackendImpl backend = new ChargeBackendImpl(dao);

        assertThat(backend.getChargesByYear(Year.PREVIOUS)).isEmpty();
    }
}

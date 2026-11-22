package at.rvarnai.taxformula.backend;

import at.rvarnai.taxformula.mapper.TaxChargeEntityToChargeMapper;
import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Year;
import at.rvarnai.taxformula.repository.TaxChargeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargeBackendImpl implements ChargeBackend {

    private final TaxChargeDao dao;

    @Override
    public List<Charge> getChargesByYear(Year year) {
        return dao.findByYear(year.getValue()).stream().map(TaxChargeEntityToChargeMapper.INSTANCE).toList();
    }

}

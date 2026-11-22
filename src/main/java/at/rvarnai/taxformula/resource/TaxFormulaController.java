package at.rvarnai.taxformula.resource;

import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.TaxBracket;
import at.rvarnai.taxformula.model.TaxFormula;
import at.rvarnai.taxformula.model.Year;
import at.rvarnai.taxformula.service.ChargeService;
import at.rvarnai.taxformula.service.FormulaFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TaxFormulaController {

    private final ChargeService chargeService;

    private final FormulaFactoryService formulaFactory;

    @GetMapping("/formula")
    public String getFormulaForNextYear(Model model) {
        model.addAttribute("formula", TaxFormula.builder().year(Year.NEXT.getValue()).build());
        model.addAttribute("years", chargeService.getYears());
        return "taxformula";
    }

    @PostMapping("/formula")
    public String selectYear(@ModelAttribute TaxFormula formula, Model model) {
        model.addAttribute("formula", getTaxFormula(
                Year.forValue(formula.getYear()),
                formula.getProfitBeforeTaxCell(),
                formula.getBasicTaxFreeAllowanceCell()));
        return "result";
    }

    private TaxFormula getTaxFormula(
            Year year,
            String profitBeforeTaxCell,
            String basicTaxFreeAllowanceCell) {
        return TaxFormula.builder()
                .year(year.getValue())
                .taxBrackets(getTaxBrackets(chargeService.getChargesByYear(year)))
                .excelFormula(formulaFactory.createFormula(year, profitBeforeTaxCell, basicTaxFreeAllowanceCell))
                .profitBeforeTaxCell(profitBeforeTaxCell)
                .basicTaxFreeAllowanceCell(basicTaxFreeAllowanceCell)
                .build();
    }

    private List<TaxBracket> getTaxBrackets(List<Charge> charges) {
        final List<Charge> chargesSorted = charges.stream().sorted(Comparator.comparing(Charge::level)).toList();
        final List<TaxBracket> taxBrackets = new ArrayList<>();

        taxBrackets.add(new TaxBracket("Up to " + Charge.AMOUNT_FORMAT.format(chargesSorted.getFirst().amount()), "0 %"));

        for (int i = 0; i < chargesSorted.size(); i++) {
            final Charge charge = chargesSorted.get(i);
            final BigDecimal from = charge.amount();
            final BigDecimal to = (i + 1 < chargesSorted.size()) ? chargesSorted.get(i + 1).amount() : BigDecimal.ZERO;
            taxBrackets.add(charge.toTaxBracket(from, to, charge.percentage()));
        }
        return taxBrackets;
    }

}
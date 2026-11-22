package at.rvarnai.taxformula.resource;

import at.rvarnai.taxformula.model.Charge;
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

import java.util.Comparator;

@Controller
@RequiredArgsConstructor
public class TaxFormulaController {

    private final ChargeService chargeService;

    private final FormulaFactoryService formulaFactory;

    @GetMapping("/formula")
    public String getFormulaForNextYear(Model model) {
        model.addAttribute("formula", getTaxFormula(Year.NEXT));
        model.addAttribute("years", chargeService.getYears());
        return "taxformula";
    }

    @PostMapping("/formula")
    public String selectYear(@ModelAttribute TaxFormula formula, Model model) {
        model.addAttribute("formula", getTaxFormula(Year.forValue(formula.getYear())));
        return "result";
    }

    private TaxFormula getTaxFormula(Year year) {
        final StringBuilder taxLevels = new StringBuilder();
        chargeService.getChargesByYear(year).stream()
                .sorted(Comparator.comparing(Charge::level)).forEach(charge -> taxLevels.append(charge).append("\n"));

        return TaxFormula.builder()
                .year(year.getValue())
                .taxLevels(taxLevels.toString())
                .excelFormula(formulaFactory.createFormula(year))
                .build();
    }

}
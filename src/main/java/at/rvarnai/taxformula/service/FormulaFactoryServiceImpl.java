package at.rvarnai.taxformula.service;

import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Level;
import at.rvarnai.taxformula.model.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FormulaFactoryServiceImpl implements FormulaFactoryService {

    private static final Pattern CELL_REFERENCE = Pattern.compile("[A-Z]{1,3}[1-9]{1,3}");

    private final ChargeService service;

    @Override
    public String createFormula(Year year, String profitBeforeTaxCell, String basicTaxFreeAllowanceCell) {
        validateCellReference(profitBeforeTaxCell, "Profit before tax");
        validateCellReference(basicTaxFreeAllowanceCell, "Basic tax-free allowance");
        String taxableAmount = "(" + profitBeforeTaxCell + "-" + basicTaxFreeAllowanceCell + ")";
        String ifTaxableAmount = "WENN(" + taxableAmount + "<=";

        Charge level1 = service.getCharge(year, Level.LEVEL1);
        Charge level2 = service.getCharge(year, Level.LEVEL2);
        Charge level3 = service.getCharge(year, Level.LEVEL3);
        Charge level4 = service.getCharge(year, Level.LEVEL4);
        Charge level5 = service.getCharge(year, Level.LEVEL5);
        Charge level6 = service.getCharge(year, Level.LEVEL6);

        BigDecimal taxAmount1 = service.getTaxAmount(year, level1, level2);
        BigDecimal taxAmount2 = service.getTaxAmount(year, level2, level3);
        BigDecimal taxAmount3 = service.getTaxAmount(year, level3, level4);
        BigDecimal taxAmount4 = service.getTaxAmount(year, level4, level5);
        BigDecimal taxAmount5 = service.getTaxAmount(year, level5, level6);

        BigDecimal taxAmount12 = taxAmount1.add(taxAmount2);
        BigDecimal taxAmount123 = taxAmount12.add(taxAmount3);
        BigDecimal taxAmount1234 = taxAmount123.add(taxAmount4);
        BigDecimal taxAmount12345 = taxAmount1234.add(taxAmount5);

        StringBuilder sb = new StringBuilder("=")

                // Level 0
                .append(ifTaxableAmount).append(level1.amount()).append(";0;")

                // Level 1
                .append(ifTaxableAmount).append(level2.amount()).append(";((").append(taxableAmount).append("-")
                .append(level1.amount()).append(")*").append(taxAmount1).append(")/")
                .append(level2.amount().subtract(level1.amount())).append(";")

                // Level 2
                .append(ifTaxableAmount).append(level3.amount()).append(";((").append(taxableAmount).append("-")
                .append(level2.amount()).append(")*").append(taxAmount2).append(")/")
                .append(level3.amount().subtract(level2.amount())).append("+").append(taxAmount1).append(";")

                // Level 3
                .append(ifTaxableAmount).append(level4.amount()).append(";((").append(taxableAmount).append("-")
                .append(level3.amount()).append(")*").append(taxAmount3).append(")/")
                .append(level4.amount().subtract(level3.amount())).append("+").append(taxAmount12).append(";")

                // Level 4
                .append(ifTaxableAmount).append(level5.amount()).append(";((").append(taxableAmount).append("-")
                .append(level4.amount()).append(")*").append(taxAmount4).append(")/")
                .append(level5.amount().subtract(level4.amount())).append("+").append(taxAmount123).append(";")

                // Level 5
                .append(ifTaxableAmount).append(level6.amount()).append(";((").append(taxableAmount).append("-")
                .append(level5.amount()).append(")*").append(taxAmount5).append(")/")
                .append(level6.amount().subtract(level5.amount())).append("+").append(taxAmount1234).append(";")

                // Level 6
                .append("(").append(taxableAmount).append("-").append(level6.amount()).append(")*")
                .append(level6.percentage()).append("+").append(taxAmount12345);

        return sb.append("))))))").toString().replace(".", ",");
    }

    private void validateCellReference(String cellReference, String fieldName) {
        if (cellReference == null || !CELL_REFERENCE.matcher(cellReference).matches()) {
            throw new IllegalArgumentException(fieldName + " must be a valid Excel cell reference (e.g. E34).");
        }
    }

}

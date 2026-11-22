package at.rvarnai.taxformula.model;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

public record Charge(Level level, BigDecimal amount, BigDecimal percentage) {

    public static final Format AMOUNT_FORMAT = NumberFormat.getIntegerInstance(Locale.of("de", "AT"));

    public TaxBracket toTaxBracket(BigDecimal from, BigDecimal to, BigDecimal percentage) {
        final String formattedAmounts = level == Level.LEVEL6 ?
                String.join("", "Over €", AMOUNT_FORMAT.format(from)) :
                String.join("", "Over €", AMOUNT_FORMAT.format(from), " up to €", AMOUNT_FORMAT.format(to));

        final String formattedPercentage = percentage
                .multiply(BigDecimal.valueOf(100))
                .stripTrailingZeros()
                .toPlainString();

        return new TaxBracket(formattedAmounts, formattedPercentage + " %");
    }

}
package at.rvarnai.taxformula.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxFormula {

    private Integer year;
    private List<TaxBracket> taxBrackets;
    private String excelFormula;
    @Builder.Default
    private String profitBeforeTaxCell = "E34";
    @Builder.Default
    private String basicTaxFreeAllowanceCell = "E35";
}

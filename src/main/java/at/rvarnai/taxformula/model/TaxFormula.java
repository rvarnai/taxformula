package at.rvarnai.taxformula.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxFormula {

    private Integer year;
    private String taxLevels;
    private String excelFormula;
}

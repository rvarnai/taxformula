package at.rvarnai.taxformula.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TAX_CHARGE")
public class TaxChargeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "TAX_YEAR")
    private Integer year;

    @Column(name = "TAX_LEVEL")
    private String level;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "PERCENTAGE")
    private BigDecimal percentage;

}
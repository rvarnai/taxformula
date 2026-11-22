package at.rvarnai.taxformula.model;

import java.math.BigDecimal;

public record Charge(Level level, BigDecimal amount, BigDecimal percentage) {

    @Override
    public String toString() {
        return "Charge{" +
                "level='" + level + '\'' +
                ", amount=" + amount +
                ", percentage=" + percentage +
                '}';
    }

}
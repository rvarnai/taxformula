package at.rvarnai.taxformula.model;

import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;

public record Charge(Level level, BigDecimal amount, BigDecimal percentage) {

    @Override
    @NonNull
    public String toString() {
        return "Charge{" +
                "level='" + level + '\'' +
                ", amount=" + amount +
                ", percentage=" + percentage +
                '}';
    }

}
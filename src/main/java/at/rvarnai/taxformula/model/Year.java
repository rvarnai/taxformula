package at.rvarnai.taxformula.model;

import at.rvarnai.taxformula.exception.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Year {
    NEXT(2027),
    CURRENT(2026),
    PREVIOUS(2025);

    private final int value;

    public static Year forValue(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(Year.values()).filter(year -> year.getValue() == value).findFirst()
                .orElseThrow(() -> new NotFoundException("year", String.valueOf(value)));
    }

}
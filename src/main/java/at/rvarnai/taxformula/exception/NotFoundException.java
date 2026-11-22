package at.rvarnai.taxformula.exception;

import java.io.Serial;

public class NotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7183457115719378446L;

    public NotFoundException(String subject, String value) {
        super(String.format("Selected %s %s not found!", subject, value));
    }

}
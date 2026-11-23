package at.rvarnai.taxformula.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class ApplicationModelAdvice {

    private final BuildProperties buildProperties;

    @ModelAttribute("applicationVersion")
    public String applicationVersion() {
        return buildProperties.getVersion();
    }
}

package at.rvarnai.taxformula;

import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Year;
import at.rvarnai.taxformula.service.ChargeService;
import at.rvarnai.taxformula.service.FormulaFactoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Comparator;
import java.util.List;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class TaxFormulaApplication implements CommandLineRunner {

	private final ChargeService chargeService;
	private final FormulaFactoryService formulaFactory;

	static void main(String[] args) {
		SpringApplication.run(TaxFormulaApplication.class, args);
	}

	@Override
	public void run(String @NonNull ... args) {
		final Year year = Year.NEXT;
		final List<Charge> charges = chargeService.getChargesByYear(year);

		log.info("Tax levels {}", year.getValue());
		charges.stream()
				.sorted(Comparator.comparing(Charge::level))
				.forEach(charge -> log.info("{}", charge));

		log.info("Excel formula {}\n{}", year.getValue(), formulaFactory.createFormula(year));
	}

}
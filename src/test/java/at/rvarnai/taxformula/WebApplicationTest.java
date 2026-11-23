package at.rvarnai.taxformula;

import at.rvarnai.taxformula.model.Charge;
import at.rvarnai.taxformula.model.Level;
import at.rvarnai.taxformula.model.Year;
import at.rvarnai.taxformula.resource.TaxFormulaController;
import at.rvarnai.taxformula.service.ChargeService;
import at.rvarnai.taxformula.service.FormulaFactoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaxFormulaController.class)
class WebApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ChargeService chargeService;

	@MockitoBean
	private FormulaFactoryService formulaFactory;

	@MockitoBean
	private BuildProperties buildProperties;

	@Test
	void shouldShowForm() throws Exception {
		when(chargeService.getYears()).thenReturn(List.of(Year.values()));
		when(buildProperties.getVersion()).thenReturn("test-version");
		this.mockMvc
				.perform(get("/formula"))
				.andExpect(status().isOk())
				.andExpect(view().name("taxformula"))
				.andExpect(model().attributeExists("formula"))
				.andExpect(model().attribute("years", List.of(Year.values())))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"profitBeforeTaxCell\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"E34\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pattern=\"[A-Z]{1,3}[1-9]{1,3}\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("title=\"Enter a cell reference, for example E34.\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"basicTaxFreeAllowanceCell\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("title=\"Enter a cell reference, for example E35.\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"E35\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("GitHub Repository")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("test-version")));
	}

	@Test
	void shouldSubmitForm() throws Exception {
		when(buildProperties.getVersion()).thenReturn("test-version");
		when(chargeService.getChargesByYear(Year.PREVIOUS)).thenReturn(List.of(
				new Charge(Level.LEVEL6, new BigDecimal("60000"), new BigDecimal("0.60")),
				new Charge(Level.LEVEL5, new BigDecimal("50000"), new BigDecimal("0.50")),
				new Charge(Level.LEVEL4, new BigDecimal("40000"), new BigDecimal("0.40")),
				new Charge(Level.LEVEL3, new BigDecimal("30000"), new BigDecimal("0.30")),
				new Charge(Level.LEVEL2, new BigDecimal("20000"), new BigDecimal("0.20")),
				new Charge(Level.LEVEL1, new BigDecimal("10000"), new BigDecimal("0.10"))));
		when(formulaFactory.createFormula(Year.PREVIOUS, "C12", "D7")).thenReturn("=(C12-D7)");
		this.mockMvc
				.perform(post("/formula")
				.param("year", String.valueOf(Year.PREVIOUS.getValue()))
				.param("profitBeforeTaxCell", "C12")
				.param("basicTaxFreeAllowanceCell", "D7"))
				.andExpect(status().isOk())
				.andExpect(view().name("result"))
				.andExpect(header().string("Content-Language", "en"))
				.andExpect(model().attributeExists("formula"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Up to 10\u00a0000")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Over €10\u00a0000 up to €20\u00a0000")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Over €50\u00a0000")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("60 %")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("=(C12-D7)")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Copy formula")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Austrian Federal Ministry of Finance (BMF)")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("professional tax advice")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("test-version")));
		verify(formulaFactory).createFormula(Year.PREVIOUS, "C12", "D7");
	}

	@Test
	void shouldRejectInvalidCellReference() throws Exception {
		when(chargeService.getChargesByYear(Year.PREVIOUS)).thenReturn(List.of(
				new Charge(Level.LEVEL1, new BigDecimal("10000"), new BigDecimal("0.10"))));
		when(formulaFactory.createFormula(Year.PREVIOUS, "E34)+SUM(A1", "E35"))
				.thenThrow(new IllegalArgumentException(
						"Profit before tax must be a valid Excel cell reference (e.g. E34)."));
		this.mockMvc
				.perform(post("/formula")
						.param("year", String.valueOf(Year.PREVIOUS.getValue()))
						.param("profitBeforeTaxCell", "E34)+SUM(A1")
						.param("basicTaxFreeAllowanceCell", "E35")
						.accept("application/problem+json"))
				.andExpect(status().isBadRequest())
				.andExpect(header().string("Content-Type", "application/problem+json"))
				.andExpect(jsonPath("$.title").value("Invalid request"))
				.andExpect(jsonPath("$.detail").value("Profit before tax must be a valid Excel cell reference (e.g. E34)."));
	}

	@Test
	void shouldReturnProblemDetailForUnknownYear() throws Exception {
		this.mockMvc
				.perform(post("/formula")
						.param("year", "2099")
						.accept("application/problem+json"))
				.andExpect(status().isNotFound())
				.andExpect(header().string("Content-Type", "application/problem+json"))
				.andExpect(jsonPath("$.title").value("Resource not found"))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.detail").value("Selected year 2099 not found!"))
				.andExpect(jsonPath("$.instance").value("/formula"));
	}

}

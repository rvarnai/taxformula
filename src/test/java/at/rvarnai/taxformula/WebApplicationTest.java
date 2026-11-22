package at.rvarnai.taxformula;

import at.rvarnai.taxformula.model.Year;
import at.rvarnai.taxformula.resource.TaxFormulaController;
import at.rvarnai.taxformula.service.ChargeService;
import at.rvarnai.taxformula.service.FormulaFactoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(TaxFormulaController.class)
class WebApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ChargeService chargeService;

	@MockitoBean
	private FormulaFactoryService formulaFactory;

	@Test
	void shouldShowForm() throws Exception {
		when(chargeService.getYears()).thenReturn(List.of(Year.values()));
		this.mockMvc
				.perform(get("/formula"))
				.andExpect(status().isOk())
				.andExpect(view().name("taxformula"))
				.andExpect(model().attributeExists("formula"))
				.andExpect(model().attribute("years", List.of(Year.values())));
	}

	@Test
	void shouldSubmitForm() throws Exception {
		this.mockMvc
				.perform(post("/formula")
				.param("year", String.valueOf(Year.PREVIOUS.getValue())))
				.andExpect(status().isOk())
				.andExpect(view().name("result"))
				.andExpect(header().string("Content-Language", "en"))
				.andExpect(model().attributeExists("formula"));
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

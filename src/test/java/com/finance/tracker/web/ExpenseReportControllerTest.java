package com.finance.tracker.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.tracker.exception.InvalidInputException;
import com.finance.tracker.exception.ResourceNotFoundException;
import com.finance.tracker.model.dto.SavingsDTO;
import com.finance.tracker.model.vo.FileReportVO;
import com.finance.tracker.model.vo.FinancesVO;
import com.finance.tracker.model.vo.SuccessResponseVO;
import com.finance.tracker.rest.v1.ExpenseReportController;
import com.finance.tracker.service.ExpenseReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExpenseReportController.class)
@ExtendWith(SpringExtension.class)
public class ExpenseReportControllerTest {
    private static final String BASE_URL = "/api/v1/finances";
    private static final String POST_URL = BASE_URL + "/submit-finances";
    private static final String GET_URL = BASE_URL + "/report";

    @MockBean
    private ExpenseReportService expenseReportService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void submitFinancesSuccessTest() throws Exception {
        SavingsDTO savingsDTO = createSavingsDTO();
        SuccessResponseVO<FinancesVO> responseVO = SuccessResponseVO.of(200, "Finances recorded successfully",
                new FinancesVO(100000, 30000, 60000, 10000, null));
        when(expenseReportService.submitFinances(any(SavingsDTO.class), anyString()))
                .thenReturn(responseVO);
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savingsDTO))
        ).andExpect(status().isCreated());
    }

    @Test
    void submitFinancesFailureTest() throws Exception {
        when(expenseReportService.submitFinances(any(SavingsDTO.class), any()))
                .thenThrow(new InvalidInputException("Invalid financial input data entered"));
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSavingsDTO()))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getExpenseReportSuccessTest() throws Exception {
        FileReportVO fileReportVO = new FileReportVO("https://aws.s3.user1.com/report.pdf");
        SuccessResponseVO<FileReportVO> reportVOSuccessResponseVO = SuccessResponseVO.of(200, "Report generated successfully", fileReportVO);
        when(expenseReportService.getExpenseReport(anyInt(), anyInt(), anyString()))
                .thenReturn(reportVOSuccessResponseVO);
        mockMvc.perform(get(GET_URL)
                .param("month", "11")
                .param("year", "2025")
        ).andExpect(status().isOk());
    }

    @Test
    void getExpenseReportInvalidInputFailureTest() throws Exception {
        when(expenseReportService.getExpenseReport(anyInt(), anyInt(), any()))
                .thenThrow(new InvalidInputException("Invalid month or year entered"));
        mockMvc.perform(get(GET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("month", "14")
                .param("year", "2025")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getExpenseReportNotFoundFailureTest() throws Exception {
        when(expenseReportService.getExpenseReport(anyInt(), anyInt(), any()))
                .thenThrow(new ResourceNotFoundException("No expense report found"));
        mockMvc.perform(get(GET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("month", "11")
                .param("year", "2025")
        ).andExpect(status().isNotFound());
    }

    private SavingsDTO createSavingsDTO() {
        SavingsDTO savingsDTO = new SavingsDTO();
        savingsDTO.setExpectedSavings(60000);
        savingsDTO.setMonth(11);
        return savingsDTO;
    }

}

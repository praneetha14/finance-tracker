package com.finance.tracker.service.impl;

import com.finance.tracker.model.dto.MonthlyExpenseReportModel;
import com.finance.tracker.service.PdfService;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service implementation for generating PDF reports for monthly expenses.
 * This class uses Freemarker templates to render expense data into HTML,
 * and then converts the HTML to a PDF file using iText.
 *
 * This class prepare data model for the Freemarker template. Render HTML using the Freemarker template engine.
 * Convert the rendered HTML into a PDF document using iText.
 */
@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    /** Freemarker configuration for loading and processing templates. */
    private final Configuration freemarkerConfiguration;

    /**
     * Generates a monthly expense report in PDF format.
     *
     * @param reportModel the data model containing user and expense details.
     * @return a byte array representing the generated PDF file.
     * @throws RuntimeException if an error occurs during PDF generation or template processing.
     */
    @Override
    public byte[] generateMonthlyExpenseReport(MonthlyExpenseReportModel reportModel) {
        try {
            // Prepare Freemarker template data
            Map<String, Object> model = new HashMap<>();
            model.put("firstName", reportModel.getFirstName());
            model.put("lastName", reportModel.getLastName());
            model.put("email", reportModel.getEmail());
            model.put("salary", reportModel.getSalary());
            model.put("mobile", reportModel.getMobile());
            model.put("month", reportModel.getMonth());
            model.put("year", reportModel.getYear());
            model.put("totalDefaultExpenses", reportModel.getTotalDefaultExpenses());
            model.put("actualExpenses", reportModel.getActualExpenses());
            model.put("percentageChange", reportModel.getPercentageChange());
            model.put("totalExpectedExpenses", reportModel.getTotalExpectedExpenses());
            model.put("totalExpectedSavings", reportModel.getTotalExpectedSavings());
            model.put("actualSavings", reportModel.getActualSavings());
            model.put("percentageChangeSavings", reportModel.getPercentageChangeSavings());
            model.put("expenseItems", reportModel.getExpenseItems());

            // Render HTML using Freemarker Template
            Template template = freemarkerConfiguration.getTemplate("FinanceReport.ftl");
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String htmlContent = stringWriter.toString();

            // Convert HTML to PDF using iText
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(
                    writer, document,
                    new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8
            );
            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating expense report PDF", e);
        }
    }
}


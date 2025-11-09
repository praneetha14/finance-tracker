package com.finance.tracker;

import com.finance.tracker.model.properties.AwsProperties;
import com.finance.tracker.repository.DefaultExpenseRepository;
import com.finance.tracker.repository.ExpenseReportRepository;
import com.finance.tracker.repository.ExpenseRepository;
import com.finance.tracker.repository.MonthlyExpenseRepository;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.service.CloudService;
import com.finance.tracker.service.ExpenseReportService;
import com.finance.tracker.service.ExpenseService;
import com.finance.tracker.service.MonthlyExpenseService;
import com.finance.tracker.service.PdfService;
import com.finance.tracker.service.UserService;
import com.finance.tracker.service.impl.ExpenseReportServiceImpl;
import com.finance.tracker.service.impl.ExpenseServiceImpl;
import com.finance.tracker.service.impl.MonthlyExpenseServiceImpl;
import com.finance.tracker.service.impl.UserServiceImpl;
import com.finance.tracker.service.utils.AuthenticationUtils;
import freemarker.template.TemplateExceptionHandler;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

/**
 * Spring Boot auto-configuration class for the Finance Tracker application.
 * This class defines and initializes application-wide beans, including:
 *   Service layer implementations
 *   Freemarker configuration for PDF generation
 *   AWS S3 client setup
 *   OpenAPI (Swagger) documentation configuration
 *
 */
@Configuration
@EnableConfigurationProperties({AwsProperties.class})
public class FinanceTrackerAutoConfiguration {

    @Autowired
    private AwsProperties awsProperties;

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public ExpenseService expenseService(ExpenseRepository expenseRepository, AuthenticationUtils authenticationUtils,
                                         MonthlyExpenseRepository monthlyExpenseRepository,
                                         DefaultExpenseRepository defaultExpenseRepository) {
        return new ExpenseServiceImpl(expenseRepository, authenticationUtils, monthlyExpenseRepository, defaultExpenseRepository);
    }

    @Bean
    public ExpenseReportService expenseReportService(AuthenticationUtils authenticationUtils, ExpenseReportRepository
                                                             expenseReportRepository, MonthlyExpenseRepository monthlyExpenseRepository, DefaultExpenseRepository defaultExpenseRepository,
                                                     PdfService pdfService, CloudService cloudService) {
        return new ExpenseReportServiceImpl(authenticationUtils, expenseReportRepository, monthlyExpenseRepository,
                defaultExpenseRepository, pdfService, cloudService);
    }

    @Bean
    public MonthlyExpenseService monthlyExpenseService(MonthlyExpenseRepository monthlyExpenseRepository,
                                                       DefaultExpenseRepository defaultExpenseRepository,
                                                       AuthenticationUtils authenticationUtils) {
        return new MonthlyExpenseServiceImpl(monthlyExpenseRepository, defaultExpenseRepository, authenticationUtils);
    }

    @Bean
    public AuthenticationUtils authenticationUtils(UserRepository userRepository) {
        return new AuthenticationUtils(userRepository);
    }

    @Bean
    public freemarker.template.Configuration freemarkerConfiguration() {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates"); // src/main/resources/templates
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey())
                        )
                )
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Tracker API")
                        .version("1.0")
                        .description("API documentation for Finance Tracker"))
                .servers(List.of(new Server().url("http://localhost:8080")))
                // Add global security requirement for API key in Authorization header
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("ApiKeyAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                        )
                );
    }
}

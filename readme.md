# Finance Tracker Application

## Overview
Finance Tracker is a comprehensive Spring Boot application designed to help users track their personal finances, manage expenses, and generate detailed financial reports. The application provides REST API endpoints for expense management, user registration, and automated PDF report generation with cloud storage integration.

## Technologies Used

### Core Framework & Language
- *Java 17* - Primary programming language
- *Spring Boot 3.5.7* - Application framework
- *Spring Data JPA* - Database access layer
- *Spring Web* - REST API development

### Database
- *MySQL* - Primary production database
- *H2 Database* - Testing database

### Development Tools
- *Lombok* - Reduces boilerplate code
- *Gradle* - Build automation tool
- *JUnit 5* - Testing framework
- *JaCoCo* - Code coverage analysis

### Cloud & External Services
- *AWS S3* - File storage for PDF reports
- *AWS SDK 2.25.14* - AWS integration

### PDF & Templating
- *iText PDF 5.5.13.3* - PDF generation
- *XMLWorker* - HTML to PDF conversion
- *Freemarker 2.3.32* - Template engine

### API Documentation
- *SpringDoc OpenAPI 3* - API documentation (Swagger)

## System Architecture

### Package Structure

com.finance.tracker/
├── entity/           # JPA entities
├── repository/       # Data access layer
├── service/          # Business logic layer
├── service/impl/     # Service implementations
├── rest/v1/         # REST controllers
├── model/           # Data models
│   ├── dto/         # Data Transfer Objects
│   ├── vo/          # Value Objects
│   ├── enums/       # Enumerations
│   └── properties/  # Configuration properties
├── exception/       # Custom exceptions
└── service/utils/   # Utility classes


### Database Schema

#### Core Entities
- *UserEntity* - User information (name, email, salary, API key)
- *ExpenseEntity* - Expense type definitions
- *ExpenseReportEntity* - Monthly financial reports
- *MonthlyExpenseEntity* - Monthly expense tracking
- *DefaultExpenseEntity* - Default expense templates

## System Flow Diagrams

### 1. User Registration & Expense Management Flow
mermaid
graph TD
A[Client Request] --> B[UserController]
B --> C[UserService]
C --> D[UserRepository]
D --> E[MySQL Database]

    F[Expense Request] --> G[ExpenseController]
    G --> H[ExpenseService]
    H --> I[ExpenseRepository]
    I --> E

    J[Default Expense] --> K[DefaultExpenseRepository]
    K --> E


### 2. Financial Report Generation Flow
mermaid
graph TD
A[Report Request] --> B[ExpenseReportController]
B --> C[ExpenseReportService]
C --> D[Gather Financial Data]
D --> E[ExpenseReportRepository]
E --> F[MySQL Database]

    C --> G[PdfService]
    G --> H[Freemarker Template]
    H --> I[Generate PDF]
    I --> J[AwsCloudService]
    J --> K[Upload to S3]
    K --> L[Return Signed URL]
    L --> M[Client Response]


### 3. Monthly Expense Tracking Flow
mermaid
graph TD
A[Submit Finances] --> B[ExpenseReportController]
B --> C[ExpenseReportService]
C --> D[Calculate Savings]
D --> E[MonthlyExpenseService]
E --> F[MonthlyExpenseRepository]
F --> G[MySQL Database]

    C --> H[Create Report Entry]
    H --> I[ExpenseReportRepository]
    I --> G


### 4. Authentication Flow
mermaid
graph TD
A[API Request] --> B[Extract API Key]
B --> C[UserService Validation]
C --> D[Query User by API Key]
D --> E[MySQL Database]
E --> F{Valid User?}
F -->|Yes| G[Process Request]
F -->|No| H[Return Unauthorized]


## API Endpoints

### User Management
- POST /api/v1/users/register - Register new user
- GET /api/v1/users/user-details - Get user details

### Expense Management
- POST /api/v1/expenses/create-expense - Create new expense
- PATCH /api/v1/expenses/update-default-expense - Update default expense

### Financial Reports
- POST /api/v1/finances/submit-finances - Submit monthly finances
- GET /api/v1/finances/report - Generate PDF report

### Monthly Expenses
- GET /api/v1/monthly-expenses - Get monthly expense data

## Key Features

### 1. Expense Management
- Create user-specific and default expenses
- Update default expense amounts
- Track various expense categories

### 2. Financial Reporting
- Generate monthly PDF reports
- Calculate expected vs actual savings
- Upload reports to AWS S3 with signed URLs

### 3. User Management
- User registration with API key generation
- Secure API access using custom API keys

### 4. Monthly Tracking
- Submit monthly financial data
- Track savings and expense patterns
- Historical data analysis

## Configuration

### Database Configuration
yaml
spring:
datasource:
username: ${MYSQL_DATABASE_USER:root}
password: ${MYSQL_DATABASE_PASSWORD}
url: jdbc:mysql://${MYSQL_DATABASE_HOST:localhost:3306}/${MYSQL_DATABASE_NAME:finance_tracker}


### AWS Configuration
yaml
finance:
tracker:
aws:
access-key: ${AWS_ACCESS_KEY}
secret-key: ${AWS_SECRET}
bucket: ${BUCKET_NAME:finance-tracker-praneetha}
region: ${REGION:ap-south-1}


## Security Features
- API key-based authentication
- Optional authorization headers
- Environment variable configuration for sensitive data

## Testing
- JUnit 5 for unit testing
- H2 database for test environment
- JaCoCo for code coverage reporting
- Test coverage reports generated in HTML format

## Build & Deployment
- Gradle build system
- Java 17 toolchain
- JAR packaging for deployment
- Environment-specific configuration support

## Development Setup

1. *Prerequisites*
    - Java 17+
    - MySQL database
    - AWS S3 bucket (for file storage)

2. *Environment Variables*
   bash
   MYSQL_DATABASE_USER=your_username
   MYSQL_DATABASE_PASSWORD=your_password
   AWS_ACCESS_KEY=your_aws_access_key
   AWS_SECRET=your_aws_secret_key
   BUCKET_NAME=your_s3_bucket


3. *Build Commands*
   bash
   ./gradlew build
   ./gradlew test
   ./gradlew jacocoTestReport


4. *Run Application*
   bash
   ./gradlew bootRun


## API Documentation
Once the application is running, access the Swagger UI at:
http://localhost:8080/swagger-ui.html

This comprehensive finance tracking system provides a robust foundation for personal financial management with modern cloud integration and detailed reporting capabilities.
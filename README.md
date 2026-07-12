# Car Dealership Inventory System

[![CI/CD Pipeline](https://github.com/Rudra9905/Incubyte/actions/workflows/ci.yml/badge.svg)](https://github.com/Rudra9905/Incubyte/actions/workflows/ci.yml)

A comprehensive full-stack web application designed for car dealerships to manage their vehicle inventory efficiently. The application provides a secure platform for staff to track vehicles, manage stock levels, and perform advanced searches.

## 🚀 Features

- **User Authentication**: Secure JWT-based registration and login system for dealership staff.
- **Inventory Management**: Full CRUD operations (Create, Read, Update, Delete) for vehicles.
- **Advanced Search**: Search and filter vehicles by make, model, year, and price.
- **Stock Management**: One-click restock functionality for vehicles.
- **Responsive UI**: A modern, clean, and responsive single-page application (SPA).
- **Robust Security**: Protected API endpoints and secure password hashing.
- **Uptime Monitoring**: Dedicated public health check endpoint (`/api/health`) to configure ping tools (like Uptime Robot) and prevent backend sleep cycles on hosting platforms (e.g., Render/Heroku).

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot (Java 17)
- **Database**: PostgreSQL(Neon DB)
- **Security**: Spring Security with JWT
- **ORM**: Hibernate / Spring Data JPA
- **Testing**: JUnit 5, Mockito, JaCoCo

### Frontend
- **Framework**: React 18 with TypeScript (Vite)
- **Styling**: Tailwind CSS
- **Routing**: React Router DOM
- **Data Fetching**: TanStack Query (React Query) & Axios
- **Form Management**: React Hook Form
- **Testing**: Vitest, React Testing Library

## ⚙️ Local Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js (v18+) and npm
- PostgreSQL installed and running
- Maven

### Backend Setup

1. **Database Configuration**:
   Create a new PostgreSQL database (e.g., `car_dealership`).
   Update the database credentials in `Backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/car_dealership
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

2. **Build and Run**:
   Navigate to the backend directory and run the application:
   ```bash
   cd Backend
   mvn clean install
   mvn spring-boot:run
   ```
   The backend API will be available at `http://localhost:8080`.

### Frontend Setup

1. **Install Dependencies**:
   Navigate to the frontend directory and install the required npm packages:
   ```bash
   cd Frontend
   npm install
   ```

2. **Run the Development Server**:
   Start the Vite development server:
   ```bash
   npm run dev
   ```
   The frontend application will be available at `http://localhost:5173`.

## 📸 Screenshots

*(Please replace these placeholders with actual screenshots of your running application before submitting)*

### 1. Login Page
<img width="1917" height="905" alt="image" src="https://github.com/user-attachments/assets/0d8a12a7-5308-49ae-85ef-96efa0b57ce9" />

*Secure authentication portal for dealership staff.*

### 2. Inventory Dashboard
<img width="1896" height="906" alt="image" src="https://github.com/user-attachments/assets/33be24de-8d49-4181-b74f-7e4530cb8c3a" />


### 3. Add/Edit Vehicle
<img width="1895" height="905" alt="image" src="https://github.com/user-attachments/assets/dc481f9a-ded6-4588-a850-aa1a5ba0ae6c" />

*Form for adding new vehicles or editing existing inventory.*

## 🧪 Test Report

The project follows a Test-Driven Development (TDD) approach with comprehensive unit and integration tests.

### Backend Test Results
- **Frameworks**: JUnit 5, Mockito, Spring Boot Test
- **Total Tests Run**: 73
- **Failures**: 0
- **Errors**: 0
- **Execution Time**: ~16.7s
- **Test Suites Covered**:
  - `HealthControllerTest` (1 test)
  - `VehicleControllerTest` (12 tests)
  - `AuthServiceTest` (4 tests)
  - `JwtServiceTest` (3 tests)
  - `RegistrationServiceTest` (21 tests)
  - `VehicleServiceTest` (12 tests)
  - *Plus additional context loads and sample tests.*

### Frontend Test Results
- **Frameworks**: Vitest, React Testing Library
- **Total Tests Run**: 14
- **Failures**: 0
- **Execution Time**: ~3.35s
- **Test Suites Covered**:
  - `LoginPage.test.tsx` (4 tests)
  - `RegisterPage.test.tsx` (5 tests)
  - `DashboardPage.test.tsx` (5 tests)

*(To run tests yourself, use `mvn test` in the Backend folder and `npm run test` in the Frontend folder).*

### Test-Driven Development (TDD) Proof

As per the project requirements, the development of this application followed strict TDD principles (Red-Green-Refactor). Below is the proof of the TDD workflow for one of the test cases:

#### 1. Failing Test (Red Phase)
Example : Registration Service Test Cases
<img width="940" height="495" alt="image" src="https://github.com/user-attachments/assets/83b163c8-30b0-476f-bab4-0e9809152991" />



#### 2. Passing Test (Green Phase)
After Implementing the minimal code for passing all test cases 
<img width="940" height="499" alt="image" src="https://github.com/user-attachments/assets/beb82b23-42fd-42d2-bec2-6338ed839041" />



## 🔄 CI/CD Pipeline

The project features a fully automated CI/CD pipeline using **GitHub Actions**. Upon every push or pull request to the `main` or `master` branches, the following checks are automatically executed:

- **Backend Integration**: Configures JDK 21 and runs the full Maven test suite (`mvn clean test`), including H2-based integration tests.
- **Frontend Integration**: Sets up Node.js, installs dependencies using `npm ci`, and runs vitest tests (`npm run test`).

The workflow configuration is defined in [.github/workflows/ci.yml](.github/workflows/ci.yml).

## 🤖 My AI Usage

During the development of this project, I utilized AI assistance (Antigravity / Gemini 3.1 Pro) to improve my workflow and code quality in the following ways:
- **Scaffolding and Boilerplate**: Generated initial project structures for both Spring Boot and React (Vite) environments.
- **Test Generation**: Assisted in writing comprehensive unit tests (e.g., covering all 72 backend test cases and React component tests) ensuring high test coverage and adherence to TDD.
- **Debugging & Refactoring**: Helped resolve complex bugs, such as resolving PostgreSQL `InvalidDataAccessResourceUsageException` by refactoring JPQL queries into native SQL with explicit type casting.
- **Documentation**: Aided in structuring this README and generating a detailed interview prep assessment.

*Note: But all the Ai-genrated Code is Checked and Reviwed by MySelf before commitment.

## 🌍 Live Deployment 
https://incubyte-kata-rudra.vercel.app

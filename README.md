# Car Dealership Inventory System

A comprehensive full-stack web application designed for car dealerships to manage their vehicle inventory efficiently. The application provides a secure platform for staff to track vehicles, manage stock levels, and perform advanced searches.

## 🚀 Features

- **User Authentication**: Secure JWT-based registration and login system for dealership staff.
- **Inventory Management**: Full CRUD operations (Create, Read, Update, Delete) for vehicles.
- **Advanced Search**: Search and filter vehicles by make, model, year, and price.
- **Stock Management**: One-click restock functionality for vehicles.
- **Responsive UI**: A modern, clean, and responsive single-page application (SPA).
- **Robust Security**: Protected API endpoints and secure password hashing.

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
![Login Page](docs/screenshots/login.png)
*Secure authentication portal for dealership staff.*

### 2. Inventory Dashboard
![Dashboard](docs/screenshots/dashboard.png)
*Main dashboard showing the list of vehicles and management options.*

### 3. Add/Edit Vehicle
![Add Vehicle](docs/screenshots/add-vehicle.png)
*Form for adding new vehicles or editing existing inventory.*

## 🧪 Test Report

The project follows a Test-Driven Development (TDD) approach with comprehensive unit and integration tests.

### Backend Test Results
- **Frameworks**: JUnit 5, Mockito, Spring Boot Test
- **Total Tests Run**: 72
- **Failures**: 0
- **Errors**: 0
- **Execution Time**: ~16.7s
- **Test Suites Covered**:
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
![Failing Test ](docs/screenshots/RegistrationTest.png)


#### 2. Passing Test (Green Phase)
*Screenshot showing the same test passing after writing the minimum required code.*


## 🤖 My AI Usage

During the development of this project, I utilized AI assistance (Antigravity / Gemini 3.1 Pro) to improve my workflow and code quality in the following ways:
- **Scaffolding and Boilerplate**: Generated initial project structures for both Spring Boot and React (Vite) environments.
- **Test Generation**: Assisted in writing comprehensive unit tests (e.g., covering all 72 backend test cases and React component tests) ensuring high test coverage and adherence to TDD.
- **Debugging & Refactoring**: Helped resolve complex bugs, such as resolving PostgreSQL `InvalidDataAccessResourceUsageException` by refactoring JPQL queries into native SQL with explicit type casting.
- **Documentation**: Aided in structuring this README and generating a detailed interview prep assessment.

*Note: All AI-generated code was thoroughly reviewed, tested, and customized to meet the specific requirements of the assignment rubric.*

## 🌍 Live Deployment (Optional)

[Link to Live Application](#) *(Replace with your Vercel/Netlify/AWS link if deployed)*

# 📚 Library Management System

A comprehensive Spring Boot-based Library Management System designed to modernize and automate library operations. This robust, production-ready solution provides a complete set of RESTful APIs for managing all aspects of a library's operations, from book inventory to borrower management and lending processes.

## 🏗️ Project Overview

### System Architecture

The application follows a clean, layered architecture:

1. **Presentation Layer**: REST Controllers that handle HTTP requests and responses
2. **Service Layer**: Business logic and validation
3. **Repository Layer**: Data access and persistence
4. **Model Layer**: Domain entities and DTOs

### Core Components

- **Book Management**: Complete CRUD operations for library inventory
- **Borrower Management**: Member registration and profile management
- **Lending System**: Book checkout, return, and tracking
- **Reporting**: Comprehensive reports on library operations
- **Validation**: Robust input validation and error handling

### Technology Stack

- **Backend**: Spring Boot 3.x, Java 17
- **Database**: H2 (Development), Configurable for production databases
- **Build Tool**: Maven
- **API Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit 5, Mockito, Spring Test

## 📊 Data Model

The system is built around three main entities:

1. **Book**
   - Represents a book in the library
   - Tracks availability and borrowing status
   - Includes metadata like title, author, ISBN, etc.

2. **Borrower**
   - Represents a library member
   - Stores contact information and membership details
   - Maintains borrowing history

3. **BorrowRecord**
   - Tracks book lending transactions
   - Records checkout and return dates
   - Manages due dates and overdue status

### Database Schema

The system uses JPA entities with the following relationships:
- One-to-Many: Borrower to BorrowRecord
- Many-to-One: BorrowRecord to Book
- Many-to-One: BorrowRecord to Borrower

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6.3+
- Your favorite IDE (IntelliJ IDEA recommended)

## 🚀 Key Features

### Book Management
- 📚 Add new books with details like title, author, and ISBN
- 🔍 Search and filter books by various criteria (title, author, and ISBN)
- 📝 Update book information and availability status
- 🗑️ Remove books from the system
- 🔄 Track book availability and borrowing history

### Borrower Management
- 👥 Register and manage library members
- 📋 Track borrower information and contact details
- 🔄 Update borrower profiles
- 📊 View complete borrowing history with status (BORROWED/RETURNED/OVERDUE)
- 🔍 Search and filter borrowers by name, email, or ID

### Lending System
- 📖 Check out books to borrowers with automatic due date calculation
- 🔄 Process book returns with status updates
- ⏰ Track due dates and automatically mark overdue books
- 📅 View current and past loans with detailed status
- 🔍 Filter loans by status (BORROWED/RETURNED/OVERDUE)

### Data Validation & Error Handling
- 🔒 Comprehensive input validation for all API endpoints
- 🛡️ Consistent error responses with meaningful messages
- 📅 Automatic due date calculation (14 days from borrowing)
- ⚠️ Robust handling of edge cases:
  - Book already borrowed
  - Borrower limits (max 3 books per borrower)
  - Duplicate book ISBN prevention
  - Invalid return operations

### API Documentation
- 📚 Comprehensive API documentation
- 🎯 Clear response formats
- 🔄 Standardized error responses
- 📋 Example requests and responses

## Features

- **Book Management**: Add, update, delete, and list books
- **Borrower Management**: Register and manage library members
- **Lending System**: Handle book checkouts and returns
- **Validation**: Input validation and proper error handling
- **RESTful API**: Follows REST principles for API design

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Build Tool**: Maven
- **Java Version**: 17 LTS
- **API Documentation**: SpringDoc OpenAPI
- **Validation**: Bean Validation 3.0
- **Testing**: JUnit 5, Mockito, Spring Test

### Development Tools
- **IDE**: IntelliJ IDEA / VS Code
- **Version Control**: Git
- **API Testing**: Postman / cURL
- **Code Quality**: Checkstyle, SpotBugs

### Dependencies
- Spring Web
- Spring Data JPA
- H2 Database (for development)
- ModelMapper (for DTO conversions)
- Spring Boot DevTools (for development)

## Project Structure

```
src/main/java/org/example/
├── config/         # Configuration classes
├── controller/     # REST controllers
├── dto/            # Data Transfer Objects
├── exception/      # Custom exceptions and exception handlers
├── model/          # Entity classes
├── repository/     # Data access layer
└── service/        # Business logic layer
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6.3 or higher
- Git (for version control)
- Your favorite IDE (IntelliJ IDEA recommended)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/sohamoo7/library-management-system.git
   cd library-management-system
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The application will start on `http://localhost:8080`

### API Base URL
All API endpoints are prefixed with `/api`. For example: `http://localhost:8080/api/books`

### Database Configuration
By default, the application uses an in-memory H2 database for development. For production:

1. Update `application.properties` or `application.yml`
2. Configure your preferred database (MySQL, PostgreSQL, etc.)
3. Set up the connection URL, username, and password

### API Documentation

#### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

#### Actuator Endpoints
Monitor and manage your application:
```
http://localhost:8080/actuator
```

## 📚 API Reference

### Books
- `GET /api/books` - Get all books
- `POST /api/books` - Add a new book
- `PUT /api/books/{id}` - Update a book
- `DELETE /api/books/{id}` - Delete a book

Get first page with 10 books: http://localhost:8080/api/books, 
Get second page with 5 books: http://localhost:8080/api/books?page=1&size=5, 
Get books sorted by author descending: http://localhost:8080/api/books?sort=author,desc, 
Get available books in "Tech" category: http://localhost:8080/api/books?category=Tech&available=true, 
Get Books by Category and Availability URL: http://localhost:8080/api/books?category={categoryName}&available=true/false, 	


### Borrowers
- `GET /api/borrowers` - Get all borrowers
- `POST /api/borrowers` - Register a new borrower
- `GET /api/borrowers/{id}/books` - Get books borrowed by a user
  
POST  http://localhost:8080/api/borrowers, 		
GET all borrowers  http://localhost:8080/api/borrowers, 
GET to see records  http://localhost:8080/api/borrowers/{id}/records, 
GET overdue  http://localhost:8080/api/borrowers/overdue, 
 


## 🧪 Testing

Run unit tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify -Pintegration-test
```

### Test Coverage
The project includes comprehensive test coverage for:
- Service layer unit tests
- Controller layer tests with MockMvc
- Repository layer tests with @DataJpaTest
- Integration tests for critical workflows

## 🧩 Project Structure

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── config/         # Configuration classes
│   │   ├── controller/     # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Custom exceptions and handlers
│   │   ├── model/          # Entity classes
│   │   ├── repository/     # Data access layer
│   │   └── service/        # Business logic layer
│   └── resources/
│       ├── static/         # Static resources
│       └── application.yml # Application properties
└── test/                   # Test files
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE] file for details.

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Maven](https://maven.apache.org/) - Dependency Management
- [H2 Database](https://www.h2database.com/) - In-memory database

## 📧 Support

For any issues or feature requests, please open an issue in the project repository.

## 🔧 Development Status

✅ Core functionality implemented  
✅ Comprehensive test coverage  
✅ API documentation available  

## 🚀 Future Enhancements

- [ ] Add authentication and authorization
- [ ] Implement fine calculation for overdue books
- [ ] Add email notifications for due dates
- [ ] Implement book reservation system
- [ ] Add reporting and analytics dashboard

Project Link: [https://github.com/sohamoo7/library-management-system](https://github.com/sohamoo7/library-management-system)

## Error Handling

The application uses a global exception handler to provide consistent error responses. All error responses include:
- HTTP status code
- Error message
- Timestamp
- Additional error details when applicable

## Contributing

(Add contribution guidelines here)

## License

(Add license information here)



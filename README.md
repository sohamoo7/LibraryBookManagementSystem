# Library Management System

A comprehensive Spring Boot-based Library Management System designed to streamline library operations. This system provides a robust backend API for managing library resources, borrowers, and lending operations with proper validation and error handling.

## 🚀 Key Features

### Book Management
- 📚 Add new books with details like title, author, ISBN, and publication year
- 🔍 Search and filter books by various criteria
- 📝 Update book information and availability status
- 🗑️ Remove books from the system

### Borrower Management
- 👥 Register and manage library members
- 📋 Track borrower information and contact details
- 🔄 Update borrower profiles
- 📊 View borrowing history

### Lending System
- 📖 Check out books to borrowers
- 🔄 Process book returns
- ⏰ Track due dates and calculate fines
- 🔔 Send overdue notifications

### Security & Validation
- 🔒 Input validation for all API endpoints
- 🛡️ Consistent error handling and meaningful error messages
- 📅 Automatic due date calculation
- ⚠️ Handling of edge cases (e.g., book already borrowed, borrower limits)

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
- Lombok (for reducing boilerplate)
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
   git clone https://github.com/yourusername/library-management-system.git
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

Get first page with 10 books: http://localhost:8080/api/books
Get second page with 5 books: http://localhost:8080/api/books?page=1&size=5
Get books sorted by author descending: http://localhost:8080/api/books?sort=author,desc
Get available books in "Tech" category: http://localhost:8080/api/books?category=Tech&available=true
Get Books by Category and Availability URL: http://localhost:8080/api/books?category={categoryName}&available=true/false	


### Borrowers
- `GET /api/borrowers` - Get all borrowers
- `POST /api/borrowers` - Register a new borrower
- `GET /api/borrowers/{id}/books` - Get books borrowed by a user
  
POST  http://localhost:8080/api/borrowers		
GET all borrowers  http://localhost:8080/api/borrowers
GET to see records  http://localhost:8080/api/borrowers/{id}/records
GET overdue  http://localhost:8080/api/borrowers/overdue
 


## 🧪 Testing

Run unit tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify -Pintegration-test
```

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

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Maven](https://maven.apache.org/) - Dependency Management
- [H2 Database](https://www.h2database.com/) - In-memory database

## 📧 Contact

Your Name - your.email@example.com

Project Link: [https://github.com/yourusername/library-management-system](https://github.com/yourusername/library-management-system)

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



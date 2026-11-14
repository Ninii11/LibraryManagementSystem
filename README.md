# Library Management System

## Project Description

Library Management System is a Spring Boot web application developed for the Spring Boot Midterm Project.

The application allows users to manage books, authors, and categories through a web interface.

## Technologies Used

- Java 21
- Spring Boot 4.1.2
- Spring MVC
- Spring Data JPA
- Hibernate
- H2 Database
- Thymeleaf
- Maven
- Lombok
- Jakarta Bean Validation

## Features

- Create, view, update, and delete books
- Manage authors and categories
- Search books by title
- Search authors by name
- Upload and display book cover images
- Validate user input
- Prevent duplicate ISBNs, author emails, and category names
- Pagination and alphabetical sorting
- Database relationships
- Custom repository queries
- Transaction management
- HTTP GET, POST, PUT, and DELETE endpoints

## Project Architecture

The application follows a layered architecture:

- **Entity:** Database models and relationships
- **Repository:** Database access and custom queries
- **Service:** Business logic and transactions
- **Controller:** HTTP request handling
- **Templates:** Thymeleaf HTML pages

## Database Entities

**Author**
- ID
- Name
- Email

**Book**
- ID
- Title
- ISBN
- Publication Year
- Cover Image Path
- Author
- Categories

**Category**
- ID
- Name

### Relationships

- One Author can have many Books (`@OneToMany`).
- Each Book belongs to one Author (`@ManyToOne`).
- Books and Categories have a many-to-many relationship (`@ManyToMany`).

## Running the Application

### Requirements

- Java 21
- Maven (or the included Maven Wrapper)

### Steps

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Allow Maven to download the dependencies.
4. Run `LibraryApplication.java`.
5. Open `http://localhost:8080/books`.

The application uses a file-based H2 database, which is created automatically if it does not exist.

### H2 Console

URL: `http://localhost:8080/h2-console`

JDBC URL: `jdbc:h2:file:./data/librarydb`

Username: `sa`

Password: Leave empty.

## File Upload

Book cover images can be uploaded in JPG or PNG format, up to 5 MB.

Uploaded files are stored in the local `uploads` directory, and their paths are saved in the database.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/books` | List books with pagination |
| GET | `/books/new` | Display new book form |
| GET | `/books/edit/{id}` | Display edit form |
| GET | `/books/search?title=...` | Search books |
| POST | `/books/save` | Create or update a book |
| POST | `/books/delete/{id}` | Delete a book from the web interface |
| PUT | `/books/{id}` | Update a book using JSON |
| DELETE | `/books/{id}` | Delete a book using HTTP DELETE |

## Validation

The application uses Jakarta Bean Validation annotations, including `@NotBlank`, `@NotNull`, `@Email`, and `@Min`.

It also checks for duplicate ISBNs, author emails, and category names.

## Project Purpose

This project demonstrates Spring Boot layered architecture, dependency injection, JPA relationships, database queries, transaction management, validation, file uploads, pagination, sorting, and HTTP request handling.
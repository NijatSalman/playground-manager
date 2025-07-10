# Playground Manager Application

This Spring Boot application manages a playground system where children can visit play sites with limited capacity. It supports registering visitors, handling queues when sites are full, auto-promoting kids from queues, and tracking visitor details in-memory. The system offers APIs for managing play sites, removing visitors, and grouping or counting current attendees.

## Table of Contents
- [General Info](#general-info)
- [Technologies](#technologies)
- [Running the Application](#running-the-application)
    - [Run from Docker](#run-from-docker)
    - [Run Locally](#run-locally)
- [URLs](#urls)

## General Info

The application includes the following features:
- **API Documentation:** Swagger is integrated for better API documentation and usability.
- **Error Handling:** A global exception handler is implemented to manage errors gracefully.
- **Tracing:** Micrometer Tracing is utilized for logs in application.
- **Testing:** Comprehensive unit tests ensure that the functionality and reliability of the application are maintained.

### Business Logic and Functional Highlights
- **Play Site Management:** Create, update, retrieve, and delete play sites, each with custom attraction types and capacities.
- **Visitor Registration:** Add kids to a play site if space is available, or place them in a waiting queue if full.
- **Queue Handling:** Automatically promote kids from the waiting queue when a spot opens in the play site.
- **Visitor Removal:** Remove a visitor by their ticket number, cleanly updating play site state and promoting queued visitors.
- **Visitor Tracking:** Track each visitor’s name, age, ticket number, play site, and whether they are in the queue.
- **In-Memory Storage:** All play site and visitor data are managed in-memory using thread-safe structures.
- **Grouped View:** View all visitors grouped by play site name.
- **Total Count:** Retrieve the current total number of active visitors in the system.

## Technologies

The project is created using:
- **JDK 21**
- **Gradle**
- **Spring Boot 3.5.3**
- **Springdoc OpenAPI**
- **Docker**
- **Lombok**

## Running the Application

### Run from Docker

1. Ensure that Docker is installed on your machine.
2. Navigate to the project root directory.
3. Build and start the Docker container using the following commands:

```bash
./gradlew clean build
docker build -t playground .
docker run -it -p 8080:8080 playground
```

### Run Locally

#### Prerequisites
- Ensure that **JDK 21** is installed on your machine.
- Install **Gradle** if not already installed.

#### Using IntelliJ IDEA
1. Open the `PlaygroundManagerApplication.java` in IntelliJ.
2. Perform a clean build by selecting **Build > Rebuild Project**.
3. Click the **Run** button to start the application.

#### Using Command Line
1. Navigate to the project root directory.
2. Run the backend application using Gradle:

```bash
./gradlew bootRun
```

## URLs

- **Backend API Documentation:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Health Check Endpoint:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
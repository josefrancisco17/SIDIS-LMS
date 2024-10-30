# Distributed Library Management System

## 1. Global Artifacts

### 1.1 Project Overview

This project is a distributed version of a Library Management System (LMS) that was originally developed as a monolithic REST application. The system has been re-engineered into a microservices architecture to improve performance, reliability, and scalability.

### 1.2 Functionality

#### Internal Communication Architecture
- When one of the instances receives a request for the creation of an object, apart from saving or updating on itself it also sends PUT HTTP request to the other instances.
- Each MicroService has http repositories that will handle the requests needed to insure the functionalities required by each microservice, whether they are GET (get data from other instances) or PUT (save data between instances) requests types.
- Each service exposes an `/internal` endpoint for service-to-service communication, which takes an object created in another instance saves or updates that received object, ensuring that the data is always equal in both instances of every microservice.
- Internal endpoints are protected using a special service-to-service bearer token authentication. 
- Example internal endpoints:
  ```
  PUT .../internal/readers/
  PUT .../internal/books/
  PUT .../internal/auth/
  PUT .../internal/lendings/
  ```

#### Authentication & Security
- JWT based authentication
- For most of the request it is required a bearer token generated using a rsa public and private key which can represent an admin, librarian or reader that can be obtained in both of the auth services and that can be used in every other instaces, due to the fact that in each one of those we always do a get request to the auth services to check if that login data is correct.

#### Database Design Choices
- Each service maintains its own H2 database
- Database isolation ensures:
    - Service independence
    - Data integrity
    - Scalability
- TCP mode enables:
    - Multiple service instances sharing same database
    - External database monitoring


## 3. Architecture

### Service Features

#### Auth Service
- Handles user authentication and authorization
- Manages user credentials and tokens
- 2 instances (ports 9000, 9001)
- Separate H2 databases for each instance

#### Book Service
- Manages book, genres and authors management
- 2 instances (ports 9010, 9011)
- Separate H2 databases for each instance

#### Lending Service
- Manages book lending operations
- Tracks book lendings and returns
- 2 instances (ports 9020, 9021)
- Separate H2 databases for each instance

#### Reader Service
- Manages reader profiles and information
- 2 instances (ports 9030, 9031)
- Separate H2 databases for each instance

### Project Structure
```
SIDIS-LMS/
├── .idea/
├── AuthService/
├── BookService/
├── db/
│   ├── Auth/
│   ├── Book/
│   ├── Lending/
│   └── Reader/
├── Docs/
├── LendingService/
├── Postman/
└── ReaderService/
```

### MicroService Structure
```
[ServiceName]/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── libraryapi.[name]service/
│   │   │       ├── api/
│   │   │       ├── bootstrapping/
│   │   │       ├── configuration/
│   │   │       ├── exceptions/
│   │   │       ├── fileStorage/
│   │   │       ├── model/
│   │   │       ├── repositories/
│   │   │       ├── services/
│   │   │       ├── util/
│   │   │       └── [Name]ServiceApplication.java
│   │   └── resources/
│   │       ├── uploads/
│   │       ├── application.properties
│   │       ├── rsa.private.key
│   │       └── rsa.public.key
│   └── test/
│       ├── java/
│       └── resources/
├── .env
├── .gitignore
└── pom.xml
```
### Run Configurations

Each service has two instances configured in IntelliJ IDEA with specific VM run configurations:

#### Auth Service
1. AuthServiceApplication1
   ```
   -server.port=9000
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9005/~/Programming/IntelliJProjects/SIDIS-LMS/db/Auth/authdb1
   ```
2. AuthServiceApplication2
   ```
   -server.port=9001
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9007/~/Programming/IntelliJProjects/SIDIS-LMS/db/Auth/authdb2
   ```

#### Book Service
1. BookServiceApplication1
   ```
   -server.port=9010
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9015/~/Programming/IntelliJProjects/SIDIS-LMS/db/Book/bookdb1
   ```
2. BookServiceApplication2
   ```
   -server.port=9011
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9017/~/Programming/IntelliJProjects/SIDIS-LMS/db/Book/bookdb2
   ```

#### Lending Service
1. LendingServiceApplication1
   ```
   -server.port=9020
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9025/~/Programming/IntelliJProjects/SIDIS-LMS/db/Lending/lendingdb1
   ```
2. LendingServiceApplication2
   ```
   -server.port=9021
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9027/~/Programming/IntelliJProjects/SIDIS-LMS/db/Lending/lendingdb2
   ```

#### Reader Service
1. ReaderServiceApplication1
   ```
   -server.port=9030
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9035/~/Programming/IntelliJProjects/SIDIS-LMS/db/Reader/readerdb1
   ```
2. ReaderServiceApplication2
   ```
   -server.port=9031
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9037/~/Programming/IntelliJProjects/SIDIS-LMS/db/Reader/readerdb2
   ```

### Database Setup

- In order to run the DBs just run the start_db.sh present in the /db folder, which will run all the db in the specific ports present in the .env files. In order to stop them all only do Crl + C or terminate the process.
Each service has its own H2 database instance running in TCP mode. The databases are stored in the project's `db` directory, organized by service:

- It is also possible to run them all separately using the following commands:
- 
```bash
# Start H2 Databases for Auth Service
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9005 -web -webPort 9006 -baseDir ./db/Auth
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9007 -web -webPort 9008 -baseDir ./db/Auth

# Start H2 Databases for Book Service
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9015 -web -webPort 9016 -baseDir ./db/Book
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9017 -web -webPort 9018 -baseDir ./db/Book

# Start H2 Databases for Lending Service
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9025 -web -webPort 9026 -baseDir ./db/Lending
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9027 -web -webPort 9028 -baseDir ./db/Lending

# Start H2 Databases for Reader Service
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9035 -web -webPort 9036 -baseDir ./db/Reader
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9037 -web -webPort 9038 -baseDir ./db/Reader
```



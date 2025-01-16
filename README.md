# Distributed Library Management System

## 1. Global Artifacts

### 1.1 Project Overview

This project is a distributed version of a Library Management System (LMS) re-engineered from a monolithic REST application into a microservices architecture. It leverages RabbitMQ for communication and synchronization between instances to improve scalability, fault tolerance, and data consistency.

### 1.2 Functionality

#### Internal Communication Architecture
- Multiple instances of the same service type synchronize their state using RabbitMQ topic exchanges. This ensures all instances of a given service type have consistent data, as well as, other instances that require that information in order to provide its own functionalities.
- We adopted the event-driven behavior, in other words when a specific service publishes a message with the object of the newly created or updated object to the Message Broker (RabbitMQ docker instance) all the services that require that data will be previously subscribed, and then consuming that same message and storing that object in their own JPA repositories.  
- External API endpoints are protected using token authentication, that can is managed in the AuthServices and used all across the API, in order to provide Authentication and Authorization to all requests, taking into account the client's requirements.

#### Authentication & Security
- JWT based authentication
- For most of the request it is required a bearer token generated using a rsa public and private key which can represent an admin, librarian or reader that can be obtained in both of the auth services and that can be used in every other instances.

#### Database Design Choices
- Each service maintains its own H2 database
- Database isolation ensures:
    - Service independence
    - Data integrity
    - Scalability
- TCP mode enables:
    - Multiple service instances sharing same database
    - External database monitoring

### 1.3 Saga Pattern Implementation
- The system employs the Saga Pattern to coordinate long-running transactions across multiple microservices, ensuring data consistency in distributed environments. This pattern is particularly useful for scenarios involving multiple steps or inter-service dependencies.
- The Saga pattern was implemented for the following user stories:
  - As a librarian ,The ability to create a Book, Author, and Genre in a single process. This involves at least two services and ensures consistency across them through the Saga pattern.
  - As a reader, The ability to suggest the acquisition of a new Book, which requires coordination between services like the AcquisitionService.
  - Upon returning a Book, the ability for a reader to recommend it positively or negatively, which involves services like RecommendationService

- The Saga Pattern manages distributed transactions in our microservices system using event-driven choreography.
- Each service performs its tasks independently, publishing events like notifyTempBook and notifyAcquisition.
- Compensation mechanisms handle errors, such as deleting temporary states (deleteTempBook) if an acquisition fails. State checks (e.g., checkExistence) ensure idempotency, avoiding duplication. The system maintains consistency and scalability by processing events in sequence and leveraging message queues like RabbitMQ.

#### Bootstrap Design Choices

- Instead of using dedicated bootstrap services to initialize data, required data is directly embedded into the bootstrap logic of the services, simplifying setup.
- An alternative solution to enable the synchronization of instances started after a delay would involve creating a dedicated queue. This queue would listen for newly started services, and once these services are up, they would send a message requesting all the data needed for synchronization. One of the existing services would then receive the request and respond by sending the required data.

## 2. Architecture

### 2.1 Service Features

- To improve scalability, maintainability, and separation of concerns, the services now follow a CQRS (Command Query Responsibility Segregation) architecture. Each service is split into Query and Command components:
  - Command Services handle write operations (e.g., POST, PUT, PATCH).
  - Query Services handle read operations (GET requests).
- This separation allows independent scaling and optimization of read and write operations.

#### Auth Service
- Command:
    - Handles user authentication and authorization write operations (e.g., registration, token generation, password changes).
    - 2 instances.
- Query:
    - Handles fetching user information and validation of JWTs.
    - 2 instances.
- Databases:
- Separate H2 databases per instance for both Command and Query.

#### Book Service
- Command:
    - Manages creation, update, and deletion of books, genres, and authors.
    - 2 instances.
- Query:
    - Handles retrieving book data, including filtering by genres, authors, and search functionality.
    - 2 instances.
- Databases:
- Separate H2 databases per instance for both Command and Query.

#### Lending Service
- Command:
    - Manages book lending operations (e.g., lend, return, update lending status).
    - 2 instance.
- Query:
    - Handles retrieving lending history, due dates, and borrower details.
    - 2 instances.
- Databases:
- Separate H2 databases per instance for both Command and Query.

#### Reader Service
- Command:
    - Manages creation, update, and deletion of reader profiles.
    - 2 instances.
- Query:
    - Handles fetching reader information, including profiles, borrowing history, and statistics.
    - 2 instances.
- Databases:
- Separate H2 databases per instance for both Command and Query.

## 3. Decisions Made
1. Synchronization with Topics
   - We decided to use synchronization via RabbitMQ topics to ensure multiple instances to be kept in sync, which improves scalability and data consistency across instances.
2. Saving Full Models Instead of Minimal Data
   - When receiving data, such as from the Reader Service after synchronization, we decided to save the entire model (e.g., the full Book model) instead of only the required data. This simplifies the implementation but may lead to unnecessary database bloat.
3. Direct Bootstrap Data Initialization
    - We decided to load the necessary initial data directly during the bootstrap phase of services, rather than creating dedicated bootstrap services that use RabbitMQ to send data.
4. Handling Media Files (Images, etc.)
   - We initially decided to send media files (e.g., profile images) through RabbitMQ, although we acknowledge that it would be more efficient to send only the file paths instead of the actual files., as well as, only saving the desired path to the file instead of saving it in the databases, due to the unnecessary bloat that it causes.
### 4. Project Structure
```
SIDIS-LMS/
├── .idea/                       # IDE-specific project settings
├── AuthServiceCommand/          # Command (write) operations for Auth service
├── AuthServiceQuery/            # Query (read) operations for Auth service
├── BookServiceCommand/          # Command (write) operations for Book service
├── BookServiceQuery/            # Query (read) operations for Book service
├── LendingServiceCommand/       # Command (write) operations for Lending service
├── LendingServiceQuery/         # Query (read) operations for Lending service
├── ReaderServiceCommand/        # Command (write) operations for Reader service
├── ReaderServiceQuery/          # Query (read) operations for Reader service
├── RecommendationServiceCommand/# Command (write) operations for Recommendation service
├── db/                          # Databases for services
│   ├── Auth/
│   ├── Book/
│   ├── Lending/
│   └── Reader/
├── Docs/                        # Documentation files
├── Postman/                     # Postman collections for API testing
├── uploads/                     # File storage for uploads
├── .env                         # Environment configuration file
├── .gitignore                   # Git ignore file for sensitive files
└── pom.xml                      # Maven project configuration file

```

### 5. MicroService Structure
```
[ServiceName]/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── libraryapi.[name]service[Command/Query]/
│   │   │       ├── api/                # REST controllers
│   │   │       ├── bootstrapping/      # Initial data setup
│   │   │       ├── configuration/      # Application configurations
│   │   │       ├── exceptions/         # Custom exceptions
│   │   │       ├── fileStorage/        # File handling logic
│   │   │       ├── model/              # Entity models
│   │   │       ├── rabbitMQ/           # RabbitMQ integration
│   │   │       ├── repositories/       # Data repositories (JPA/Hibernate)
│   │   │       ├── services/           # Business logic services
│   │   │       ├── util/               # Utility functions
│   │   │       └── [Name]ServiceApplication.java # Main class
│   │   └── resources/
│   │       ├── application.properties  # Configuration file
│   │       ├── rsa.private.key         # Private key for JWT signing
│   │       └── rsa.public.key          # Public key for JWT verification
│   └── test/
│       ├── java/                       # Unit and integration tests
│       └── resources/                  # Test-specific resources
├── target/                             # Compiled artifacts and outputs
├── .env                                # Environment-specific configurations
├── .gitignore                          # Files and folders to ignore in Git
└── pom.xml                             # Maven project configuration
```
### 6. Run Configurations

Each service has two instances configured in IntelliJ IDEA with specific VM run configurations:

#### Auth Service
1. AuthServiceQuery1
   ```
    -Dserver.port=9000
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9005/authquerydb1
   ```
2. AuthServiceQuery2
   ```
   -Dserver.port=9001
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9007/authquerydb2
   ```
3. AuthServiceCommand1
   ```
    -Dserver.port=9002
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9009/authcommanddb1
   ```
4. AuthServiceCommand2
   ```
    -Dserver.port=9003
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9011/authcommanddb2
   ```


#### Book Service
1. BookServiceQuery1
   ```
    -Dserver.port=9010
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9015/bookquerydb1
   ```
2. BookServiceQuery2
   ```
   -Dserver.port=9011
   -Dspring.datasource.url=jdbc:h2:tcp://localhost:9017//bookqueryd2
   ```
3. BookServiceCommand1
   ```
    -Dserver.port=9012
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9019/bookcommanddb1
   ```
4. BookServiceCommand2
   ```
    -Dserver.port=9013
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9021/bookcommanddb2
   ```

#### Lending Service
1. LendingServiceQuery1
   ```
    -Dserver.port=9020
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9025/lendingquerydb1

   ```
2. LendingServiceQuery2
   ```
    -Dserver.port=9021
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9027/lendingquerydb2
   ```

3. LendingServiceCommand1
   ```
    -Dserver.port=9022
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9029/lendingcommanddb1
   ```
4. LendingServiceCommand2
   ```
    -Dserver.port=9023
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9031/lendingcommanddb2
   ```

#### Reader Service
1. ReaderServiceQuery1
   ```
    -Dserver.port=9030
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9035/readerquerydb1
   ```
2. ReaderServiceQuery2
   ```
    -Dserver.port=9031
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9037/readerquerydb2
   ```
3. ReaderServiceCommand1
   ```
    -Dserver.port=9032
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9039/readercommanddb1
   ```
4. ReaderServiceCommand2
   ```
    -Dserver.port=9033
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9041/readercommanddb2
   ```

#### Recommendation Service
1. RecommendationCommand1
   ```
    -Dserver.port=9060
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9065/recommendationcommanddb1
   ```
2. RecommendationCommand2
   ```
    -Dserver.port=9061
    -Dspring.datasource.url=jdbc:h2:tcp://localhost:9066/recommendationcommanddb2
   ```

### 7. RabbitMQ Setup

- We are using RabbitMQ as a message broker for communication between microservices. The RabbitMQ instance is running as a Docker container with the following command:

   ```bash
    docker run -d -p 9050:5672 -p 15672:15672 --name rabbitmq rabbitmq:management
   ```
- This command runs RabbitMQ in the background, exposing the AMQP port (5672) for service communication and the management console (15672) for monitoring.

#### RabbitMQ Configuration
- To configure RabbitMQ for use in all services, the following properties are defined in each service's application.properties:
- 
- 
   ```
    spring.rabbitmq.host=localhost
    spring.rabbitmq.port=9050
    spring.rabbitmq.username=guest
    spring.rabbitmq.password=guest
   ```
  
- These properties ensure that all services connect to the RabbitMQ instance on the correct ports and use the default guest credentials for authentication.

### 8. Database Setup
- Start All Databases:
  - To start all databases at once, run the start_db.sh script located in the /db folder. This script will initialize and run all H2 database instances using the port configurations specified in the .env file.
  - To stop all databases, simply press Ctrl + C or terminate the process running the script.
  - It is also possible to run them all separately using the following commands:

- Run Databases Separately:

```bash
# Auth Service Databases
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9005 -baseDir ./db/Auth/authquerydb1
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9006 -baseDir ./db/Auth/authquerydb2
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9007 -baseDir ./db/Auth/authcommanddb1
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9008 -baseDir ./db/Auth/authcommanddb2

# Book Service Databases
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9015 -baseDir ./db/Book/bookquerydb1
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9016 -baseDir ./db/Book/bookquerydb2
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9017 -baseDir ./db/Book/bookcommanddb1
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9018 -baseDir ./db/Book/bookcommanddb2

# Lending Service Databases
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9025 -baseDir ./db/Lending/lendingquerydb1
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9026 -baseDir ./db/Lending/lendingquerydb2
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9027 -baseDir ./db/Lending/lendingcommanddb1
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9028 -baseDir ./db/Lending/lendingcommanddb2

# Reader Service Databases
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9035 -baseDir ./db/Reader/readerquerydb1
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9036 -baseDir ./db/Reader/readerquerydb2
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9037 -baseDir ./db/Reader/readercommanddb1
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9038 -baseDir ./db/Reader/readercommanddb2

# Recommendation Service Databases
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9065 -baseDir ./db/Recommendation/recommendationcommanddb1
java -cp h2*.jar org.h2.tools.Server -tcp -tcpPort 9066 -baseDir ./db/Recommendation/recommendationcommanddb2
```

### 9. Future Improvements
1. Database Sharding for Data Overload Reduction
    - In the future, we could explore the use of database sharding more extensively to address the current challenge of having the same data replicated across multiple services. This would reduce the overhead caused by storing and maintaining the same data in every instance, leading to improved performance and better resource allocation.
2. Enhanced Security with Salting
    - Another improvement for the future would be to implement salting for sensitive data, especially in the case of passwords, to further enhance security. By salting passwords and other sensitive information before hashing, we can add an extra layer of protection against brute-force and dictionary attacks, making the system more secure and resilient to potential breaches.
3. Media File Handling Optimization
    - As mentioned earlier, sending media files (like images) through RabbitMQ is not optimal. A better approach would be to send only the file paths instead of the actual files. This would reduce network load and storage requirements. Additionally, we should consider saving only the path to the media file in the database rather than storing the file itself, to avoid unnecessary bloat and improve system efficiency.
4. Full Model vs. Minimal Data
    - While the decision was made to save the full model of data (e.g., the full Book model) rather than just the minimal required data, this could lead to database bloat in the future. A future improvement would be to rethink this strategy by storing only the relevant information needed by each service or instance, which would optimize database performance and reduce redundant storage across the system.
5. Add possibility of running new instances at any time with respective db.
6. Add Bootstrap initialization for new instances.

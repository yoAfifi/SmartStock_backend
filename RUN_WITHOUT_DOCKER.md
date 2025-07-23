# Running SmartStock Backend Without Docker

This guide explains how to run the SmartStock backend microservices without Docker Compose.

## Prerequisites

- Java 17 or higher
- PostgreSQL installed and running
- Maven (or you can use the included Maven wrapper)

## Setup Steps

### 1. Database Setup

You have two options to set up the required PostgreSQL databases:

#### Option 1: Using the setup script

Run the provided `setup-databases.bat` script which will create all the necessary databases:

```
.\setup-databases.bat
```

#### Option 2: Manual setup

Execute the following SQL commands in your PostgreSQL client:

```sql
CREATE DATABASE "AuthService_db";
CREATE DATABASE "ProductService_db";
CREATE DATABASE "OrderService_db";
CREATE DATABASE "CustomerService_db";

GRANT ALL PRIVILEGES ON DATABASE "AuthService_db" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "ProductService_db" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "OrderService_db" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "CustomerService_db" TO postgres;
```

### 2. Running the Services

You have two options to run the services:

#### Option 1: Using the run script

Run the provided `run-services.bat` script which will start all services in the correct order:

```
.\run-services.bat
```

#### Option 2: Manual startup

Start each service manually in the following order:

1. **Eureka Server**:
   ```
   cd eureka-server
   .\mvnw.cmd spring-boot:run
   ```

2. **Auth Service**:
   ```
   cd AuthService
   .\mvnw.cmd spring-boot:run
   ```

3. **Customer Service**:
   ```
   cd CustomerService
   .\mvnw.cmd spring-boot:run
   ```

4. **Product Service**:
   ```
   cd ProductService
   .\mvnw.cmd spring-boot:run
   ```

5. **Order Service**:
   ```
   cd OrderService
   .\mvnw.cmd spring-boot:run
   ```

6. **Gateway Service**:
   ```
   cd GatewayService
   .\mvnw.cmd spring-boot:run
   ```

## Accessing the Application

- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761

## Service Ports

- Eureka Server: 8761
- Gateway Service: 8080
- Auth Service: 8081
- Customer Service: 8082
- Order Service: 8084
- Product Service: 8085

## Configuration Changes

The following configuration changes have been made to run without Docker:

1. Updated service URLs in `OrderService/src/main/resources/application.properties`
2. Updated Eureka server URLs in all service configurations

## Troubleshooting

- If you encounter database connection issues, ensure PostgreSQL is running and the databases are created
- If services can't register with Eureka, ensure the Eureka server is running before starting other services
- Check the logs of each service for specific error messages
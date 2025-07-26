# SmartStock Backend

A microservices-based backend system for inventory management.

## Quickstart

### Prerequisites
- Docker and Docker Compose
- Java 24 or higher
- Maven

### Setup and Running

1. Clone the repository:
```bash
git clone <repository-url>
cd SmartStock_backend
```

2. Build the services:
```bash
mvn clean package -DskipTests
```

3. Start the services:
```bash
docker-compose up --build
```

The following services will be available:
- Eureka Server: http://localhost:8761
- Gateway Service: http://localhost:8080
- Auth Service: http://localhost:8081
- Product Service: http://localhost:8082
- Order Service: http://localhost:8083
- Customer Service: http://localhost:8084

### Default Configuration

The services are configured with the following default values:

| Service | Port | Description |
|---------|------|-------------|
| Eureka Server | 8761 | Service Discovery |
| Gateway | 8080 | API Gateway |
| PostgreSQL | 5432 | Database |

Database Configuration:
- Database: smartstock
- Username: postgres
- Password: postgres

### Stopping the Services

To stop all services:
```bash
docker-compose down
```

To stop and remove volumes:
```bash
docker-compose down -v
``` 

### 
### docker start minio
docker compose up -d   # start (once)


### docker stop minio
docker compose down    # stop

### See current status
docker ps -a
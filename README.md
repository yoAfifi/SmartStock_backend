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

2. Create environment file:
```bash
cp .env.example .env
```

3. Build the services:
```bash
mvn clean package -DskipTests
```

4. Start the services:
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

### Environment Variables

The following environment variables can be configured in the `.env` file:

| Variable | Description | Default |
|----------|-------------|---------|
| POSTGRES_DB | Database name | smartstock |
| POSTGRES_USER | Database user | postgres |
| POSTGRES_PASSWORD | Database password | postgres |
| DB_PORT | PostgreSQL port | 5432 |
| EUREKA_PORT | Eureka Server port | 8761 |
| GATEWAY_PORT | Gateway Service port | 8080 |

### Stopping the Services

To stop all services:
```bash
docker-compose down
```

To stop and remove volumes:
```bash
docker-compose down -v
``` 
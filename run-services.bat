@echo off
echo ===== Setting up PostgreSQL Databases =====
echo Please make sure PostgreSQL is running and execute init-databases.sql manually
echo.

echo ===== Starting Eureka Server =====
start cmd /k "cd eureka-server && mvnw.cmd spring-boot:run"
echo Waiting for Eureka Server to start...
timeout /t 20

echo ===== Starting Auth Service =====
start cmd /k "cd AuthService && mvnw.cmd spring-boot:run"
echo Waiting for Auth Service to start...
timeout /t 15

echo ===== Starting Customer Service =====
start cmd /k "cd CustomerService && mvnw.cmd spring-boot:run"
echo Waiting for Customer Service to start...
timeout /t 15

echo ===== Starting Product Service =====
start cmd /k "cd ProductService && mvnw.cmd spring-boot:run"
echo Waiting for Product Service to start...
timeout /t 15

echo ===== Starting Order Service =====
start cmd /k "cd OrderService && mvnw.cmd spring-boot:run"
echo Waiting for Order Service to start...
timeout /t 15

echo ===== Starting Gateway Service =====
start cmd /k "cd GatewayService && mvnw.cmd spring-boot:run"

echo.
echo ===== All services started =====
echo You can access the application at: http://localhost:8080
echo Eureka dashboard is available at: http://localhost:8761
echo.
echo Press any key to exit this window (services will continue running)...
pause > nul
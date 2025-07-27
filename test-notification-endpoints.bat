@echo off
echo ===== Testing Notification Endpoints =====
echo.

echo 1. Testing direct ProductService access (port 8085) - TEST ENDPOINT:
echo GET http://localhost:8085/api/admin/notifications/test
curl -X GET "http://localhost:8085/api/admin/notifications/test" -H "Content-Type: application/json" -v
echo.
echo.

echo 2. Testing direct ProductService access (port 8085) - COUNT ENDPOINT:
echo GET http://localhost:8085/api/admin/notifications/count
curl -X GET "http://localhost:8085/api/admin/notifications/count" -H "Content-Type: application/json" -v
echo.
echo.

echo 3. Testing Gateway access (port 8080) - TEST ENDPOINT:
echo GET http://localhost:8080/api/admin/notifications/test
curl -X GET "http://localhost:8080/api/admin/notifications/test" -H "Content-Type: application/json" -v
echo.
echo.

echo 4. Testing Gateway access (port 8080) - COUNT ENDPOINT:
echo GET http://localhost:8080/api/admin/notifications/count
curl -X GET "http://localhost:8080/api/admin/notifications/count" -H "Content-Type: application/json" -v
echo.
echo.

echo 5. Testing ProductService with admin token (you need to provide a valid token):
echo GET http://localhost:8085/api/admin/notifications/count with Authorization header
echo Please replace YOUR_TOKEN_HERE with a valid admin JWT token
curl -X GET "http://localhost:8085/api/admin/notifications/count" -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_TOKEN_HERE" -v
echo.
echo.

echo 6. Testing Gateway with admin token:
echo GET http://localhost:8080/api/admin/notifications/count with Authorization header
curl -X GET "http://localhost:8080/api/admin/notifications/count" -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_TOKEN_HERE" -v
echo.
echo.

echo 7. Testing Eureka service discovery:
echo GET http://localhost:8761/eureka/apps
curl -X GET "http://localhost:8761/eureka/apps" -H "Content-Type: application/json"
echo.
echo.

echo ===== Test completed =====
pause 
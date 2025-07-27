@echo off
echo Testing notification endpoint...
echo.

echo Testing GET /api/admin/notifications/count
curl -X GET "http://localhost:8080/api/admin/notifications/count" -H "Content-Type: application/json"
echo.
echo.

echo Testing POST /api/admin/notifications/check-low-stock
curl -X POST "http://localhost:8080/api/admin/notifications/check-low-stock" -H "Content-Type: application/json"
echo.
echo.

echo Testing GET /api/admin/notifications
curl -X GET "http://localhost:8080/api/admin/notifications?page=0&size=10" -H "Content-Type: application/json"
echo.
echo.

pause 
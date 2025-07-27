@echo off
echo Stopping ProductService...
taskkill /f /im java.exe /fi "WINDOWTITLE eq ProductService*" 2>nul

echo Waiting for service to stop...
timeout /t 3 /nobreak >nul

echo Starting ProductService...
cd ProductService
start "ProductService" cmd /k "mvnw.cmd spring-boot:run"

echo ProductService restart initiated.
echo Please wait for the service to fully start before testing. 
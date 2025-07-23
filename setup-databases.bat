@echo off
echo ===== Setting up PostgreSQL Databases =====
echo This script will create the required databases for SmartStock
echo.

set PGPASSWORD=postgres

echo Please enter your PostgreSQL password (default is postgres):
set /p PGPASSWORD=Password: 

echo.
echo Creating databases...

psql -U postgres -c "CREATE DATABASE \"AuthService_db\";"
psql -U postgres -c "CREATE DATABASE \"ProductService_db\";"
psql -U postgres -c "CREATE DATABASE \"OrderService_db\";"
psql -U postgres -c "CREATE DATABASE \"CustomerService_db\";"

echo.
echo Granting privileges...

psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE \"AuthService_db\" TO postgres;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE \"ProductService_db\" TO postgres;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE \"OrderService_db\" TO postgres;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE \"CustomerService_db\" TO postgres;"

echo.
echo ===== Database setup complete =====
echo You can now run run-services.bat to start all services
echo.
echo Press any key to exit...
pause > nul
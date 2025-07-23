-- Create separate databases for each microservice
CREATE DATABASE "AuthService_db";
CREATE DATABASE "ProductService_db";
CREATE DATABASE "OrderService_db";
CREATE DATABASE "CustomerService_db";

-- Grant privileges to postgres user
GRANT ALL PRIVILEGES ON DATABASE "AuthService_db" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "ProductService_db" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "OrderService_db" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "CustomerService_db" TO postgres; 
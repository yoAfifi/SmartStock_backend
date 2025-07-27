# SmartStock Backend Implementation

This document describes the implementation of the SmartStock backend system with categories, reviews, notifications, and cart functionality.

## 🏗️ Architecture Overview

The system consists of multiple Spring Boot microservices:

- **ProductService** (Port 8085): Product management, categories, reviews, notifications
- **CartService** (Port 8087): Shopping cart functionality
- **Eureka Server** (Port 8761): Service discovery
- **PostgreSQL**: Database for each service
- **MinIO**: Object storage for product images

## 📋 Implemented Features

### Part A - Product Categories ✅
- Category entity with name, slug, audit fields
- Product-Category relationship (Many-to-One)
- Category CRUD operations (ADMIN only)
- Product creation/update with category assignment
- Liquibase migrations for database schema

### Part B - Product Reviews ✅
- ProductReview entity with rating, title, comment
- Unique constraint: one review per user per product
- Review CRUD operations with authentication
- Automatic average rating calculation
- Review summary with breakdown statistics

### Part C - Low-stock Admin Notifications ✅
- AdminNotification entity for low-stock alerts
- Automatic notification creation when stock < 5
- 24-hour deduplication to prevent spam
- Admin-only notification management endpoints

### Part D - CartService Microservice ✅
- Complete cart management system
- Stock validation via ProductService communication
- Price snapshot and synchronization
- JWT authentication integration

## 🚀 Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL
- MinIO (for image storage)

### Database Setup
1. Create databases:
```sql
CREATE DATABASE ProductService_db;
CREATE DATABASE CartService_db;
```

2. Run the setup script:
```bash
./setup-databases.bat
```

### Service Configuration

#### ProductService Configuration
- Port: 8085
- Database: ProductService_db
- Eureka registration: enabled
- JWT authentication: enabled

#### CartService Configuration
- Port: 8087
- Database: CartService_db
- Eureka registration: enabled
- JWT authentication: enabled
- OpenFeign client: enabled

### Running Services
1. Start Eureka Server
2. Start PostgreSQL and MinIO
3. Start ProductService
4. Start CartService

```bash
./run-services.bat
```

## 🔐 Security & Authentication

### JWT Token Structure
```json
{
  "sub": "123",           // User ID
  "roles": ["USER"],      // User roles
  "customerId": 123       // Customer ID
}
```

### Role-based Access
- **Public**: Product listing, category listing, product details
- **USER**: Product reviews, cart operations
- **ADMIN**: Category management, notification management

## 📚 API Documentation

### ProductService Endpoints

#### Categories
```
GET    /api/categories                    # List all categories
GET    /api/categories/{id}               # Get category by ID
POST   /api/categories                    # Create category (ADMIN)
PUT    /api/categories/{id}               # Update category (ADMIN)
DELETE /api/categories/{id}               # Delete category (ADMIN)
```

#### Products
```
GET    /api/products                      # List all products
GET    /api/products/{id}                 # Get product by ID
GET    /api/products/{id}/info            # Get product info (for CartService)
POST   /api/products                      # Create product
PUT    /api/products/{id}                 # Update product
DELETE /api/products/{id}                 # Delete product
PUT    /api/products/{id}/reduceStock     # Reduce stock
```

#### Reviews
```
GET    /api/products/{id}/reviews         # List product reviews
GET    /api/products/{id}/reviews/summary # Get review summary
POST   /api/products/{id}/reviews         # Create/update review (AUTH)
DELETE /api/products/{id}/reviews/{reviewId} # Delete review (AUTH)
```

#### Admin Notifications
```
GET    /api/admin/notifications           # List notifications (ADMIN)
PUT    /api/admin/notifications/{id}/read # Mark as read (ADMIN)
GET    /api/admin/notifications/count     # Unread count (ADMIN)
```

### CartService Endpoints

#### Cart Operations (All require authentication)
```
GET    /api/cart                          # Get user cart
POST   /api/cart/items                    # Add item to cart
PUT    /api/cart/items/{productId}        # Update item quantity
DELETE /api/cart/items/{productId}        # Remove item from cart
DELETE /api/cart                          # Clear cart
POST   /api/cart/refresh-prices           # Refresh prices from ProductService
```

## 🧪 Testing with Postman

### Sample JWT Tokens
```json
// User Token
{
  "sub": "123",
  "roles": ["USER"],
  "customerId": 123
}

// Admin Token
{
  "sub": "456",
  "roles": ["ADMIN"],
  "customerId": 456
}
```

### Test Scenarios

#### 1. Category Management (Admin)
```bash
# Create category
POST /api/categories
Authorization: Bearer <ADMIN_TOKEN>
{
  "name": "Electronics",
  "slug": "electronics"
}

# List categories
GET /api/categories
```

#### 2. Product Management
```bash
# Create product with category
POST /api/products
Content-Type: multipart/form-data
Authorization: Bearer <ADMIN_TOKEN>
{
  "product": {
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "stockQuantity": 10,
    "categoryId": 1
  },
  "file": <image_file>
}
```

#### 3. Review System
```bash
# Add review
POST /api/products/1/reviews
Authorization: Bearer <USER_TOKEN>
{
  "rating": 5,
  "title": "Great product!",
  "comment": "Excellent performance and quality."
}

# Get review summary
GET /api/products/1/reviews/summary
```

#### 4. Cart Operations
```bash
# Add item to cart
POST /api/cart/items
Authorization: Bearer <USER_TOKEN>
{
  "productId": 1,
  "quantity": 2
}

# Get cart
GET /api/cart
Authorization: Bearer <USER_TOKEN>
```

#### 5. Admin Notifications
```bash
# List unread notifications
GET /api/admin/notifications?read=false
Authorization: Bearer <ADMIN_TOKEN>

# Mark notification as read
PUT /api/admin/notifications/1/read
Authorization: Bearer <ADMIN_TOKEN>
```

## 🔧 Configuration Properties

### ProductService
```yaml
product:
  lowstock:
    threshold: 5  # Low stock threshold
```

### CartService
```yaml
# Uses same JWT secret as ProductService
jwt:
  secret: ${JWT_SECRET}
```

## 📊 Database Schema

### ProductService Tables
- `categories`: Product categories
- `products`: Products with category and average rating
- `product_reviews`: User reviews with unique constraint
- `admin_notifications`: Low-stock notifications

### CartService Tables
- `carts`: User carts (one per user)
- `cart_items`: Cart items with price snapshots

## 🚨 Error Handling

### Common Error Responses
```json
{
  "error": "Product not found",
  "status": 404
}

{
  "error": "Insufficient stock",
  "status": 409
}

{
  "error": "Unauthorized",
  "status": 401
}
```

## 🔄 Service Communication

### CartService → ProductService
- OpenFeign client for product information
- Stock validation before cart operations
- Price synchronization capabilities

### Event-driven Notifications
- Automatic low-stock detection
- 24-hour deduplication window
- Admin notification management

## 📝 Notes

1. **JWT Secret**: All services use the same JWT secret for token validation
2. **Database Migrations**: Liquibase handles all schema changes
3. **Stock Validation**: Cart operations validate stock availability
4. **Price Snapshots**: Cart items store price at time of addition
5. **Review Deduplication**: Users can only have one review per product

## 🎯 Acceptance Criteria Status

- ✅ All migrations run cleanly on fresh DB
- ✅ Product listing returns category and averageRating
- ✅ Review creation/update updates averageRating
- ✅ Low-stock notifications created (no duplicates within 24h)
- ✅ Cart endpoints fully functional with stock validation
- ✅ JWT authentication working across services
- ✅ Admin-only endpoints properly secured 
# Notification System Troubleshooting Guide

## Overview
The notification system automatically creates alerts when products have stock quantities below the configured threshold (default: 5 units).

## How It Works
1. **Automatic Triggers**: Notifications are created when:
   - A product is created with low stock
   - A product is updated and has low stock
   - Stock is reduced and results in low stock

2. **Manual Check**: You can manually trigger notifications for existing products using the "Check Low Stock" button in the admin dashboard.

## Current Issue
You have 4 products with stock quantities under 5 (0, 0, 4, 4) but no notifications are showing because:
- These products already existed in the database with low stock
- No operations were performed on them to trigger the notification service
- The notification system only creates alerts when products are modified

## Solution
1. **Use the Manual Check Button**: 
   - Go to the admin dashboard
   - Click the "Check Low Stock" button
   - This will scan all existing products and create notifications for those with low stock

2. **Verify Backend Services**:
   - Ensure ProductService is running
   - Check that the database has the `admin_notifications` table
   - Verify the notification endpoints are accessible

## Testing the System

### 1. Test Backend Endpoints
Run the test script:
```bash
test-notifications.bat
```

### 2. Check Database
Verify notifications are being created:
```sql
SELECT * FROM admin_notifications ORDER BY created_date DESC;
```

### 3. Check Application Logs
Look for notification service logs in the ProductService console output.

## Configuration
- **Low Stock Threshold**: Configured in `application.yml` as `product.lowstock.threshold` (default: 5)
- **Notification Duplication**: System prevents duplicate notifications for the same product within 24 hours

## Frontend Integration
- Notifications are displayed in the admin dashboard
- Real-time polling checks for new notifications every 30 seconds
- Critical stock alerts (≤2 units) are highlighted differently

## Common Issues
1. **404 Errors**: Backend services not running
2. **No Notifications**: Products exist but no operations triggered notifications
3. **Duplicate Notifications**: System prevents duplicates within 24 hours
4. **Frontend Errors**: Check browser console for API connection issues

## Next Steps
1. Start all backend services
2. Use the "Check Low Stock" button to create notifications for existing products
3. Verify notifications appear in the admin dashboard
4. Test by reducing stock on products to trigger automatic notifications 
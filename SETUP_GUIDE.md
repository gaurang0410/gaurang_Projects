# 🚀 Vehicle Service Management App - Setup Guide

## Quick Start Guide

Follow these steps to get the application running on your system.

---

## 📋 Pre-Installation Requirements

### Required Software:
1. **Java Development Kit (JDK) 8 or higher**
   - Download: https://www.oracle.com/java/technologies/javase-downloads.html
   - Verify: Open Command Prompt and type `java -version`

2. **MySQL Server 5.7 or higher**
   - Download: https://dev.mysql.com/downloads/mysql/
   - During installation, set username as `root` and password as `root`

3. **MySQL Connector/J (JDBC Driver)**
   - Download: https://dev.mysql.com/downloads/connector/j/
   - Version: 8.0.33 or compatible

---

## 🛠️ Installation Steps

### Step 1: Extract and Organize Files

```
C:\Users\Gaurang Khanolkar\OneDrive\Desktop\oopj\project\
├── src/
├── lib/
├── database_schema.sql
├── README.md
├── compile.bat
├── run.bat
└── SETUP_GUIDE.md
```

### Step 2: Add JDBC Driver to lib Folder

1. Download `mysql-connector-j-8.0.33.jar` from MySQL website
2. Place the JAR file in the `lib/` folder
3. Rename to `mysql-connector-j-8.0.33.jar` (if needed)

### Step 3: Create the Database

**Option A: Using MySQL Command Line**

```bash
# Open Command Prompt
mysql -u root -p

# Enter your MySQL password when prompted
# Then run:
CREATE DATABASE IF NOT EXISTS vehicle_service;
USE vehicle_service;

# Paste the entire content of database_schema.sql here
```

**Option B: Using MySQL GUI Tool (MySQL Workbench)**

1. Open MySQL Workbench
2. Create new SQL tab
3. Open and run `database_schema.sql`

**Option C: Command Line Direct**

```bash
mysql -u root -p < database_schema.sql
```

### Step 4: Update Database Credentials (if different)

Edit `src/dao/DBConnection.java`:

```java
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/vehicle_service";
    private static final String USER = "root";           // Change if different username
    private static final String PASSWORD = "root";      // Change if different password
```

### Step 5: Compile the Application

**On Windows:**
```bash
# Navigate to project directory
cd C:\Users\Gaurang Khanolkar\OneDrive\Desktop\oopj\project

# Run compile script
compile.bat
```

**On Mac/Linux:**
```bash
cd ~/Desktop/oopj/project
chmod +x compile.sh
./compile.sh
```

### Step 6: Run the Application

**On Windows:**
```bash
run.bat
```

**On Mac/Linux:**
```bash
chmod +x run.sh
./run.sh
```

---

## 🔐 Login Credentials

When the application starts, you'll see a login screen:

- **Username:** `admin`
- **Password:** `admin`

---

## 📱 Application Features Overview

### Dashboard Menu Options:

1. **Add Customer**
   - Register new vehicle owners
   - Add phone, email, address

2. **Add Vehicle**
   - Register vehicles to customers
   - Store brand, model, registration number

3. **New Service**
   - Create service records for vehicles
   - Track service type, date, status, cost

4. **View Records**
   - View all customers, vehicles, and services
   - See data in tabular format

5. **Generate Bill**
   - Create bills for completed services
   - Print or save bills

6. **Logout**
   - Exit back to login screen

---

## 📊 Database Tables

The application uses 4 main tables:

### 1. customers
```sql
- customer_id (Primary Key)
- name
- phone
- email
- address
```

### 2. vehicles
```sql
- vehicle_id (Primary Key)
- customer_id (Foreign Key)
- brand
- model
- registration_number
```

### 3. services
```sql
- service_id (Primary Key)
- vehicle_id (Foreign Key)
- service_type
- service_date
- status (Pending, In Progress, Completed)
- cost
```

### 4. service_details (Optional)
```sql
- detail_id (Primary Key)
- service_id (Foreign Key)
- description
- parts_used
- labor_cost
```

---

## ⚠️ Common Issues & Solutions

### Issue 1: "Connection refused" Error
**Solution:**
- Ensure MySQL server is running
- Verify database `vehicle_service` exists
- Check username and password in DBConnection.java

### Issue 2: "Class not found: com.mysql.cj.jdbc.Driver"
**Solution:**
- Download MySQL JDBC driver JAR
- Place in `lib/` folder
- Check JAR filename in compile.bat/sh

### Issue 3: "SQLException: Access denied for user 'root'"
**Solution:**
- Verify MySQL password is correct
- Update `DBConnection.java` with correct password
- In MySQL: `ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';`

### Issue 4: Application closes immediately after running
**Solution:**
- Check if JAR file path is correct
- Verify all classes compiled successfully
- Check MySQL is running: `mysqld` service

### Issue 5: "Tables don't exist" Error
**Solution:**
- Run `database_schema.sql` script
- Verify you're using `vehicle_service` database
- Check table names are created: `SHOW TABLES;`

---

## 🧪 Test the Application

### Sample Test Case:

1. **Login**
   - Username: admin
   - Password: admin

2. **Add Customer**
   - Name: Rajesh Kumar
   - Phone: 9876543210
   - Email: rajesh@email.com
   - Address: Mumbai, India

3. **Add Vehicle**
   - Customer: Rajesh Kumar
   - Brand: Toyota
   - Model: Innova
   - Registration: MH01AB1234

4. **Add Service**
   - Vehicle: Toyota Innova
   - Service Type: Oil Change
   - Date: 2024-03-20
   - Status: Pending
   - Cost: 500

5. **Update Service Status**
   - Change status to "Completed"

6. **Generate Bill**
   - Select service from dropdown
   - View complete bill with customer and vehicle details

---

## 📁 Project Structure Explanation

```
project/
├── src/
│   ├── gui/                    # User Interface Components
│   │   ├── LoginFrame.java     # Login Screen
│   │   ├── DashboardFrame.java # Main Menu
│   │   ├── CustomerForm.java   # Customer Management
│   │   ├── VehicleForm.java    # Vehicle Management
│   │   ├── ServiceForm.java    # Service Management
│   │   ├── BillingFrame.java   # Bill Generation
│   │   └── RecordsFrame.java   # View All Records
│   │
│   ├── model/                  # Data Classes
│   │   ├── Customer.java       # Customer Object
│   │   ├── Vehicle.java        # Vehicle Object
│   │   └── Service.java        # Service Object
│   │
│   ├── dao/                    # Database Access Layer
│   │   ├── DBConnection.java   # JDBC Connection
│   │   ├── CustomerDAO.java    # Customer Operations
│   │   ├── VehicleDAO.java     # Vehicle Operations
│   │   └── ServiceDAO.java     # Service Operations
│   │
│   ├── service/                # Business Logic Layer
│   │   ├── CustomerService.java # Customer Logic
│   │   ├── VehicleService.java  # Vehicle Logic
│   │   └── ServiceManager.java  # Service Logic
│   │
│   └── Main.java               # Application Entry Point
│
├── lib/                        # External Libraries
│   └── mysql-connector-j-8.0.33.jar
│
├── database_schema.sql         # SQL Database Setup
├── README.md                   # Project Documentation
├── compile.bat                 # Windows Compile Script
├── run.bat                     # Windows Run Script
├── compile.sh                  # Linux/Mac Compile Script
├── run.sh                      # Linux/Mac Run Script
└── SETUP_GUIDE.md             # This File
```

---

## 🔄 Architecture Overview

```
┌─────────────────────────────────────┐
│        GUI Layer (Swing)            │
│  LoginFrame, DashboardFrame, etc.   │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│   Service Layer (Business Logic)    │
│  CustomerService, VehicleService    │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│    DAO Layer (Data Access)          │
│  CustomerDAO, VehicleDAO, ServiceDAO│
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│   Database Layer (MySQL)            │
│  customers, vehicles, services      │
└─────────────────────────────────────┘
```

---

## 💡 Tips for Development

1. **Code Organization**: Keep GUI, Logic, and Data access separate
2. **Error Handling**: Try-catch blocks are implemented for safety
3. **Connection Management**: Connections auto-close in DAO
4. **Input Validation**: Check for empty fields before operations
5. **Table Relationships**: Use foreign keys for data integrity

---

## 🚀 Next Steps

After successful setup:

1. Explore all menu options
2. Add sample data to test
3. View generated reports
4. Try deleting/updating records
5. Generate and print bills

---

## 📞 Need Help?

Refer to these resources:
- MySQL Documentation: https://dev.mysql.com/doc/
- Java Swing Tutorials: https://docs.oracle.com/javase/tutorial/uiswing/
- JDBC Documentation: https://docs.oracle.com/javase/tutorial/jdbc/

---

**Enjoy using the Vehicle Service Management App! 🎉**

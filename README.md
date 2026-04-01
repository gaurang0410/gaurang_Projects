# 🚗 Vehicle Service Management App

A complete Java application for managing vehicle service operations with a user-friendly GUI, database integration, and business logic layer.

---

## 📋 Project Structure

```
project/
├── src/
│   ├── gui/                    # Swing GUI Components
│   │   ├── LoginFrame.java
│   │   ├── DashboardFrame.java
│   │   ├── CustomerForm.java
│   │   ├── VehicleForm.java
│   │   ├── ServiceForm.java
│   │   ├── BillingFrame.java
│   │   └── RecordsFrame.java
│   │
│   ├── model/                  # Data Models
│   │   ├── Customer.java
│   │   ├── Vehicle.java
│   │   └── Service.java
│   │
│   ├── dao/                    # Data Access Objects (JDBC)
│   │   ├── DBConnection.java
│   │   ├── CustomerDAO.java
│   │   ├── VehicleDAO.java
│   │   └── ServiceDAO.java
│   │
│   ├── service/                # Business Logic Layer
│   │   ├── CustomerService.java
│   │   ├── VehicleService.java
│   │   └── ServiceManager.java
│   │
│   └── Main.java               # Application Entry Point
│
├── lib/                        # External Libraries (JDBC Driver)
├── database_schema.sql         # Database Setup Script
└── README.md                   # This file
```

---

## 🛠️ Prerequisites

- **Java JDK 8+** - Java Development Kit
- **MySQL 5.7+** - Database Server
- **MySQL Connector/J** - JDBC Driver for MySQL

---

## 📦 Setup Instructions

### Step 1: Download MySQL JDBC Driver

1. Download MySQL Connector/J from: https://dev.mysql.com/downloads/connector/j/
2. Extract the JAR file and copy `mysql-connector-j-*.jar` to the `lib` folder

### Step 2: Create the Database

1. Open MySQL Command Line or any MySQL GUI tool
2. Run the SQL script:
   ```bash
   mysql -u root -p < database_schema.sql
   ```

3. Or copy-paste the contents of `database_schema.sql` into MySQL client

### Step 3: Configure Database Connection

Edit `src/dao/DBConnection.java` and update the credentials:

```java
private static final String URL = "jdbc:mysql://localhost:3306/vehicle_service";
private static final String USER = "root";
private static final String PASSWORD = "root";  // Change this to your MySQL password
```

### Step 4: Compile the Project

```bash
cd project/src
javac -cp .;../lib/mysql-connector-j-*.jar gui/*.java model/*.java dao/*.java service/*.java Main.java
```

### Step 5: Run the Application

```bash
java -cp .;../lib/mysql-connector-j-*.jar Main
```

---

## 🔐 Default Credentials

- **Username:** admin
- **Password:** admin

---

## 🎯 Application Features

### 1. **Customer Management**
   - Add new customers
   - Update customer details
   - Delete customers
   - Search customers by name
   - View all customers

### 2. **Vehicle Management**
   - Add vehicles linked to customers
   - Update vehicle details
   - Delete vehicles
   - Search by registration number
   - View all vehicles

### 3. **Service Management**
   - Create service entries for vehicles
   - Assign service types (Oil Change, Tire Rotation, etc.)
   - Update service status (Pending → In Progress → Completed)
   - Track service costs

### 4. **Billing System**
   - Generate bills for completed services
   - Display customer and vehicle information
   - Calculate and display total costs
   - Print bills

### 5. **Records Viewing**
   - View all customers in tabular format
   - View all vehicles in tabular format
   - View all services in tabular format

---

## 💻 Application Flow

1. **Login Screen** → Enter credentials (admin/admin)
2. **Dashboard** → Choose operation
3. **Add Customer** → Enter customer details → Save to DB
4. **Add Vehicle** → Select customer → Enter vehicle details
5. **Add Service** → Select vehicle → Enter service info
6. **Update Status** → Change service status to "Completed"
7. **Generate Bill** → Select completed service → View/Print bill

---

## 🔧 Database Schema

### Customers
| Column | Type | Description |
|--------|------|-------------|
| customer_id | INT (PK) | Unique identifier |
| name | VARCHAR(100) | Customer name |
| phone | VARCHAR(15) | Contact number |
| email | VARCHAR(100) | Email address |
| address | VARCHAR(255) | Physical address |

### Vehicles
| Column | Type | Description |
|--------|------|-------------|
| vehicle_id | INT (PK) | Unique identifier |
| customer_id | INT (FK) | Links to customer |
| brand | VARCHAR(50) | Vehicle brand |
| model | VARCHAR(50) | Vehicle model |
| registration_number | VARCHAR(20) | License plate |

### Services
| Column | Type | Description |
|--------|------|-------------|
| service_id | INT (PK) | Unique identifier |
| vehicle_id | INT (FK) | Links to vehicle |
| service_type | VARCHAR(100) | Type of service |
| service_date | VARCHAR(20) | Date of service |
| status | VARCHAR(20) | Pending/In Progress/Completed |
| cost | DECIMAL(10,2) | Service cost |

---

## 🎨 Technology Stack

- **Frontend:** Java Swing
- **Backend:** Java
- **Database:** MySQL
- **Architecture:** 3-Layer (GUI → Service → DAO)

---

## ⚠️ Important Notes

1. Make sure MySQL server is running before starting the application
2. The database credentials must match your MySQL setup
3. JDBC connection will close automatically after each operation
4. All data is stored securely in the database

---

## 🚀 Advanced Features (Future Additions)

- [ ] Export bills as PDF
- [ ] Email notifications
- [ ] Auto cost calculation
- [ ] Dashboard charts
- [ ] Multiple user roles
- [ ] Service history reports
- [ ] AI chatbot support

---

## 🐛 Troubleshooting

### Connection Failed Error
- Check if MySQL is running
- Verify database credentials
- Ensure `vehicle_service` database exists

### JDBC Driver Not Found
- Download MySQL Connector/J
- Place JAR in `lib` folder
- Add to classpath during compilation

### Table Doesn't Exist
- Run the `database_schema.sql` script
- Verify you're using the correct database name

---

## 📝 Sample Test Case

1. **Add Customer:** Raj Kumar, 9876543210, raj@email.com, Mumbai
2. **Add Vehicle:** Toyota Fortuner, ABC-123
3. **Add Service:** Oil Change, 2024-03-20, Pending, 500
4. **Update Status:** Change to "Completed"
5. **Generate Bill:** View the complete bill

---

## 📞 Support

For issues or improvements, refer to the code structure and modify accordingly. The application is fully modular and extensible.

---

**Happy Coding! 🎉**

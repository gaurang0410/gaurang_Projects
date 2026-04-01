# 🚀 API-Based Vehicle Service Management System

## Overview

This is a **Vehicle Service Management System** built with Java Swing GUI and a **REST API backend**. The application has been migrated from hardcoded data to a modern API architecture, making it scalable and ready for real-world deployment.

- ✅ **REST API Server** running on port 8080
- ✅ **Swing GUI** for user interface
- ✅ **MySQL Database** for data persistence
- ✅ **Real-world data ready** - Can integrate external APIs
- ✅ **Production-ready** architecture

---

## 🎯 What's New

### Before
```
GUI → Direct Database Connections
```

### After
```
GUI → HTTP REST API → MySQL Database
```

**Benefits:**
- Single API can serve multiple clients (web, mobile, desktop)
- Easy to integrate real-world data sources
- Better security
- Scalable architecture

---

## 📋 Quick Start

### 1️⃣ Prerequisites
- Java 8+ installed
- MySQL Server running
- MySQL database created: `vehicle_service`

### 2️⃣ Download JSON Library
```bash
# Windows
.\download_json_library.bat

# Linux/Mac
./download_json_library.sh
```

Or download manually: [json-20231013.jar](https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar)

### 3️⃣ Compile
```bash
# Windows
.\compile.bat

# Linux/Mac
./compile.sh
```

### 4️⃣ Run
```bash
# Windows
.\run.bat

# Linux/Mac
./run.sh
```

### 5️⃣ Access Application
- **GUI**: Automatically opens
- **API**: http://localhost:8080
- **Login**: Use `admin` / `admin` or create new account

---

## 🏗️ Project Structure

```
project/
├── src/
│   ├── api/                    # NEW: REST API
│   │   ├── ApiServer.java      # API Server (port 8080)
│   │   └── ApiClient.java      # HTTP Client
│   ├── dao/                    # Data Access Objects
│   │   ├── UserDAO.java        # Uses API now
│   │   ├── CustomerDAO.java    # Uses API now
│   │   ├── VehicleDAO.java     # Uses API now
│   │   └── ServiceDAO.java     # Uses API now
│   ├── gui/                    # GUI Components
│   │   ├── LoginFrame.java
│   │   ├── SignUpFrame.java
│   │   ├── DashboardFrame.java
│   │   └── ...
│   ├── model/                  # Data Models
│   ├── service/                # Business Logic
│   └── Main.java               # Entry point (starts API + GUI)
│
├── lib/
│   ├── mysql-connector-j-8.0.33.jar
│   └── json-20231013.jar       # NEW: Required for API
│
├── database_schema.sql         # Database schema
├── compile.bat / compile.sh    # Compilation scripts
├── run.bat / run.sh            # Run scripts
│
├── API_SETUP.md                # Setup & troubleshooting
├── API_MIGRATION_SUMMARY.md    # Complete migration details
└── README.md                   # This file
```

---

## 🔌 API Endpoints

### Users
```
POST /api/users/register
POST /api/users/login
```

### Customers
```
GET  /api/customers
POST /api/customers
```

### Vehicles
```
GET  /api/vehicles
POST /api/vehicles
```

### Services
```
GET  /api/services
POST /api/services
```

### Health Check
```
GET /health
```

### Full documentation: See [API_SETUP.md](API_SETUP.md)

---

## 💻 Features

- 👤 **User Authentication**
  - Secure login with SHA-256 hashing
  - User registration
  - Session management

- 👥 **Customer Management**
  - Add/View/Search customers
  - Track customer information

- 🚗 **Vehicle Management**
  - Register vehicles
  - Link vehicles to customers
  - Track vehicle details

- 🔧 **Service Management**
  - Record service history
  - Track service costs
  - Update service status

- 💳 **Billing System**
  - Generate bills
  - Print invoices
  - Billing history

---

## 🌐 Architecture Diagram

```
┌──────────────────────────────────────────────┐
│         Swing GUI (User Interface)           │
│  LoginFrame | DashboardFrame | Forms         │
└────────────────────┬─────────────────────────┘
                     │
                     │ HTTP Requests (JSON)
                     ↓
┌──────────────────────────────────────────────┐
│      ApiClient (HTTP Communication)          │
│   - GET/POST operations                      │
│   - JSON marshalling                         │
│   - Error handling                           │
└────────────────────┬─────────────────────────┘
                     │
                     │ HTTP GET/POST
                     ↓
┌──────────────────────────────────────────────┐
│    ApiServer - REST API (Port: 8080)         │
│                                              │
│  Endpoints:                                  │
│  ├── /api/users (register, login)            │
│  ├── /api/customers (GET, POST)              │
│  ├── /api/vehicles (GET, POST)               │
│  ├── /api/services (GET, POST)               │
│  └── /health (status check)                  │
│                                              │
│  HTTP Server with JSON request handling      │
└────────────────────┬─────────────────────────┘
                     │
                     │ JDBC (SQL Queries)
                     ↓
┌──────────────────────────────────────────────┐
│          MySQL Database                      │
│  Database: vehicle_service                   │
│                                              │
│  Tables:                                     │
│  ├── users (authentication)                  │
│  ├── customers (customer info)               │
│  ├── vehicles (vehicle data)                 │
│  ├── services (service history)              │
│  └── service_details (details)               │
└──────────────────────────────────────────────┘
```

---

## 🧪 Testing the API

### Using curl (PowerShell):

```powershell
# Check API health
curl http://localhost:8080/health

# Get all customers
curl http://localhost:8080/api/customers

# Register new user
$body = @{
    username = "testuser"
    email = "test@example.com"
    password = "123456"
    full_name = "Test User"
} | ConvertTo-Json

curl -X POST -ContentType "application/json" `
  -Body $body `
  http://localhost:8080/api/users/register
```

---

## 🔧 Configuration

### API Server Port
Edit `src/api/ApiServer.java`:
```java
private static final int PORT = 8080;  // Change to 8081 if needed
```

### Database Connection
Edit `src/api/ApiServer.java`:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicle_service";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";  // Add password if needed
```

---

## 📚 Documentation

- **[API_SETUP.md](API_SETUP.md)** - Complete setup guide and troubleshooting
- **[API_MIGRATION_SUMMARY.md](API_MIGRATION_SUMMARY.md)** - Detailed migration information
- **[database_schema.sql](database_schema.sql)** - Database structure

---

## 🐛 Troubleshooting

### Common Issues

| Problem | Solution |
|---------|----------|
| `json-20231013.jar not found` | Run `download_json_library.bat` |
| Port 8080 already in use | Change PORT in ApiServer.java |
| MySQL connection error | Ensure MySQL is running and database exists |
| Compilation fails | Check JSON library is in classpath |

See [API_SETUP.md](API_SETUP.md) for more troubleshooting.

---

## 🌟 Advanced Usage

### Integrate Real-World APIs
The API architecture allows easy integration of external data sources:

```java
// Example: Fetch vehicle data from external API
private void handleGetVehicles(HttpExchange exchange) {
    // Fetch from: https://opendata.cars.com/vehicles
    JSONObject externalData = fetchFromExternalAPI();
    
    // Combine with local database
    // Return to client
}
```

### Suggested APIs to Integrate:
- 🚗 [CarQuery API](https://carqueryapi.com) - Vehicle specs
- 💰 [Kelly Blue Book API](https://www.kbb.com) - Pricing
- 🗺️ [Google Maps API](https://cloud.google.com/maps-platform) - Address validation

---

## 📊 Improvements Made

| Aspect | Before | After |
|--------|--------|-------|
| **Architecture** | Monolithic | API-based |
| **Data Source** | Hardcoded | HTTP API |
| **Scalability** | Single GUI | Multiple clients |
| **Integration** | Not possible | Easy with APIs |
| **Deployment** | Desktop only | Web/Mobile ready |
| **Security** | Basic | API with authentication |
| **Maintainability** | Coupled | Decoupled |

---

## 🎓 Learning Resources

- [REST API Best Practices](https://restfulapi.net/)
- [Java HTTP Server Documentation](https://docs.oracle.com/en/java/javase/11/docs/api/com.sun.net.httpserver/com/sun/net/httpserver/HttpServer.html)
- [JSON in Java](https://www.geeksforgeeks.org/java-json-processing/)

---

## 📝 License

This project is open source. Feel free to use and modify.

---

## ✅ Checklist

- [ ] Download JSON library
- [ ] Compile the project
- [ ] Run the application
- [ ] Test login (admin/admin)
- [ ] Create new user account
- [ ] Test adding customer
- [ ] Test adding vehicle
- [ ] Test service management
- [ ] Check API on http://localhost:8080

---

## 🎉 You're All Set!

Your application now uses a modern REST API architecture. Start using it and explore the possibilities!

```bash
# Quick start:
.\download_json_library.bat
.\compile.bat
.\run.bat
```

**Happy coding! 🚀**

---

## 📞 Support

For issues or questions:
1. Check [API_SETUP.md](API_SETUP.md) troubleshooting section
2. Review console output for error messages
3. Verify MySQL is running
4. Check classpath includes both JAR files

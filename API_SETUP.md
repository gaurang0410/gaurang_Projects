# REST API Setup Guide

## Overview
Your Vehicle Service Management System has been converted from hardcoded data to a **REST API architecture**. The API server runs on `http://localhost:8080` and handles all data operations via HTTP requests.

## ✨ What's Changed

### Before (Hardcoded)
- Direct database connections in DAOs
- DAO classes used JDBC
- Data was tightly coupled to GUI

### After (API-Based)
- Separate REST API Server
- DAOs now use HTTP requests
- Real-world data ready
- Scalable and flexible
- Can add real external APIs

---

## 🚀 Setup & Installation

### Step 1: Add JSON Library
The API uses the `org.json` library for handling API requests. You need to add this JAR file to your `lib/` folder.

**Download Options:**

#### Option A: Download from Maven (Recommended)
1. Go to [Maven Central](https://mvnrepository.com/artifact/org.json/json)
2. Download `json-20231013.jar` (or latest version)
3. Place it in `c:\Users\Gaurang Khanolkar\OneDrive\Desktop\oopj\project\lib\`

#### Option B: Download Now
If you can't download manually, run this command in PowerShell:
```powershell
cd "c:\Users\Gaurang Khanolkar\OneDrive\Desktop\oopj\project\lib"
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar" -OutFile "json-20231013.jar"
```

#### Option C: Use Maven/Gradle
If your project uses Maven, add to `pom.xml`:
```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20231013</version>
</dependency>
```

---

### Step 2: Update Compilation Command

Update your `compile.bat` to include the JSON library:

**Original:**
```batch
javac -cp ".;lib\mysql-connector-j-8.0.33.jar" ...
```

**New:**
```batch
javac -cp ".;lib\mysql-connector-j-8.0.33.jar;lib\json-20231013.jar" ...
```

Or use this complete batch file:

```batch
@echo off
cd /d "%~dp0"
echo Compiling Vehicle Service Management System...

javac -d "./" ^
  -cp ".;lib\mysql-connector-j-8.0.33.jar;lib\json-20231013.jar" ^
  "src\*.java" ^
  "src\api\*.java" ^
  "src\dao\*.java" ^
  "src\gui\*.java" ^
  "src\model\*.java" ^
  "src\service\*.java"

if %ERRORLEVEL% EQU 0 (
  echo Compilation successful!
) else (
  echo Compilation failed!
  pause
)
```

---

### Step 3: Update Run Command

Update your `run.bat` to include the JSON library:

```batch
@echo off
cd /d "%~dp0src"
java -cp ".;../lib/mysql-connector-j-8.0.33.jar;../lib/json-20231013.jar" Main
pause
```

---

## 🔧 How It Works

### Architecture Flow
```
┌─────────────┐
│   GUI       │ (LoginFrame, CustomerForm, etc.)
└──────┬──────┘
       │ HTTP Requests (JSON)
       ↓
┌─────────────────────────────────────┐
│   API Client (api/ApiClient.java)   │
└──────┬──────────────────────────────┘
       │ HTTP GET/POST
       ↓
┌─────────────────────────────────────┐
│   REST API Server (api/ApiServer.java) │  Port: 8080
│   - /api/users    (auth)            │
│   - /api/customers (CRUD)           │
│   - /api/vehicles (CRUD)            │
│   - /api/services (CRUD)            │
│   - /health (status check)          │
└──────┬──────────────────────────────┘
       │ JDBC Queries
       ↓
┌─────────────────────────────────────┐
│   MySQL Database                    │
│   (vehicle_service DB)              │
└─────────────────────────────────────┘
```

### Key Components

1. **ApiServer.java** - REST API Server running on port 8080
   - Handles all HTTP requests
   - Connects to MySQL
   - Returns JSON responses

2. **ApiClient.java** - HTTP Client
   - Makes requests to API Server
   - Handles JSON serialization
   - Error handling

3. **Updated DAOs** - Now use ApiClient instead of JDBC
   - UserDAO
   - CustomerDAO
   - VehicleDAO
   - ServiceDAO

4. **Main.java** - Updated to:
   - Start API server in background thread
   - Wait for server to be ready
   - Launch GUI

---

## 📡 API Endpoints

### Users API
- **POST** `/api/users/register` - Register new user
- **POST** `/api/users/login` - Login with credentials

### Customers API
- **GET** `/api/customers` - Get all customers
- **POST** `/api/customers` - Add new customer

### Vehicles API
- **GET** `/api/vehicles` - Get all vehicles
- **POST** `/api/vehicles` - Add new vehicle

### Services API
- **GET** `/api/services` - Get all services
- **POST** `/api/services` - Add new service

### Health Check
- **GET** `/health` - Check if API is running

---

## 🧪 Testing the API

### Test with curl (PowerShell):

```powershell
# Check if API is running
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

## 🐛 Troubleshooting

### "Port 8080 already in use"
- Another application is using port 8080
- Edit `ApiServer.java` to change `PORT = 8080` to another port (e.g., 8081)

### "API Server did not start"
- Check if MySQL is running
- Check database connection credentials in `ApiServer.java`
- Check console output for error messages

### "JSON library not found"
- Make sure `json-20231013.jar` is in the `lib/` folder
- Update `classpath` in compile and run scripts

### "Connection refused" when adding data
- API server might not be running
- Check if MySQL database exists
- Run database schema again

---

## 🌐 Integrating Real-World APIs (Future)

The advantage of this API architecture is that you can now easily integrate real-world data sources:

### Examples:

1. **Vehicle Data API** (Get vehicle specs, pricing)
   - Example: `https://api.carsxe.com`
   - Modify `VehiclesHandler` to fetch from external API

2. **Service Cost API** (Calculate service costs)
   - Example: Auto repair pricing APIs
   - Use in `handleAddService()` method

3. **User Location API** (Validate customer address)
   - Example: Google Maps API
   - Validate address during customer registration

### Integration Example:
```java
// Inside ApiServer.java - ServicesHandler
private void handleAddService(HttpExchange exchange, JSONObject json) {
    // Call external service cost API
    double estimatedCost = getServiceCostFromExternalAPI(serviceType);
    
    // Store in database
    // ...
}
```

---

## 📋 Next Steps

1. ✅ Download the JSON library
2. ✅ Update `compile.bat` and `run.bat`
3. ✅ Compile the project
4. ✅ Run the application
5. ✅ Test login and create new accounts
6. 🔄 (Optional) Integrate external APIs

---

## 📞 Support

If you encounter issues:
1. Check console output for error messages
2. Verify MySQL is running
3. Ensure JSON library is in `lib/` folder
4. Check that all files compiled successfully
5. Verify port 8080 is available

Happy coding! 🚀

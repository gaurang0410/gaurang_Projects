# Database Configuration Reference

This file documents how to configure the application for different scenarios.

---

## 🔧 Default Configuration

**File:** `src/dao/DBConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/vehicle_service";
private static final String USER = "root";
private static final String PASSWORD = "root";
```

---

## 📝 Common Configuration Scenarios

### Scenario 1: Standard Local Setup (Default)

```java
String URL = "jdbc:mysql://localhost:3306/vehicle_service";
String USER = "root";
String PASSWORD = "root";
```

**When to use:** Single user development on local machine

---

### Scenario 2: Different Password

If you set a different MySQL password during installation:

```java
private static final String URL = "jdbc:mysql://localhost:3306/vehicle_service";
private static final String USER = "root";
private static final String PASSWORD = "your_actual_password";  // Change this
```

**Example:**
```java
private static final String PASSWORD = "MySecurePass123";
```

---

### Scenario 3: Remote Database Server

If database is on another server:

```java
private static final String URL = "jdbc:mysql://192.168.1.100:3306/vehicle_service";
private static final String USER = "admin";
private static final String PASSWORD = "serverpass";
```

Replace:
- `192.168.1.100` with your server IP
- `admin` with your username
- `serverpass` with your password

---

### Scenario 4: Different Database Name

If you named the database differently:

```java
private static final String URL = "jdbc:mysql://localhost:3306/my_vehicle_db";
private static final String USER = "root";
private static final String PASSWORD = "root";
```

---

### Scenario 5: SSL Connection (Secure)

For secure connections:

```java
private static final String URL = "jdbc:mysql://localhost:3306/vehicle_service?useSSL=true&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "root";
```

---

### Scenario 6: Custom Port

If MySQL is on non-standard port:

```java
private static final String URL = "jdbc:mysql://localhost:3307/vehicle_service";  // Port 3307
private static final String USER = "root";
private static final String PASSWORD = "root";
```

---

## 🔑 How to Change Configuration

### Step 1: Open the File
Navigate to: `src/dao/DBConnection.java`

### Step 2: Locate Configuration Lines
Look for:
```java
private static final String URL = ...
private static final String USER = ...
private static final String PASSWORD = ...
```

### Step 3: Modify Values
Replace with your configuration

### Step 4: Recompile
```bash
compile.bat    # Windows
./compile.sh   # Mac/Linux
```

### Step 5: Test Connection
Run the application and try login

---

## 🔍 Verifying Configuration

### Check MySQL is Running
```bash
# Windows
net start MySQL80

# Mac/Linux
sudo systemctl status mysql
```

### Verify Database Exists
```sql
SHOW DATABASES;
/* Should list 'vehicle_service' */
```

### Verify User Credentials
```sql
SELECT user, host FROM mysql.user;
/* Should show your user */
```

### Test Connection Manually
```java
// Add this to Main.java temporarily
try {
    Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
    if (conn != null) {
        System.out.println("✓ Connection successful!");
        conn.close();
    }
} catch (SQLException e) {
    System.out.println("✗ Connection failed: " + e.getMessage());
}
```

---

## 🔐 Security Recommendations

### For Production:

1. **Use Environment Variables**
   ```java
   String URL = System.getenv("DB_URL");
   String USER = System.getenv("DB_USER");
   String PASSWORD = System.getenv("DB_PASSWORD");
   ```

2. **Use Configuration Files**
   Create `config.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/vehicle_service
   db.user=root
   db.password=secure_password
   ```

3. **Use Encrypted Passwords**
   Encrypt credentials and decrypt at runtime

4. **Separate Credentials by Environment**
   - Development: `dev-config.properties`
   - Production: `prod-config.properties`

### Example with Properties File:

```java
public class DBConnection {
    private static final Properties prop = new Properties();
    
    static {
        try {
            FileInputStream fis = new FileInputStream("config.properties");
            prop.load(fis);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        String url = prop.getProperty("db.url");
        String user = prop.getProperty("db.user");
        String password = prop.getProperty("db.password");
        
        return DriverManager.getConnection(url, user, password);
    }
}
```

---

## 🐛 Troubleshooting Configuration

### Error: "No suitable driver found"

**Solution:** Check JDBC JAR file
```
1. Verify mysql-connector-j-*.jar is in lib/
2. Check compile script includes correct path
3. Recompile and run
```

### Error: "Access denied for user"

**Solution:** Verify credentials
```sql
-- Check if user exists
SELECT user, host FROM mysql.user WHERE user='root';

-- Reset password if needed
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```

### Error: "Unknown database 'vehicle_service'"

**Solution:** Create database
```sql
CREATE DATABASE vehicle_service;
-- Then run database_schema.sql
```

### Error: "Connection Timeout"

**Solution:** Check server connectivity
```bash
# Test MySQL service
mysql -u root -p
# Should prompt for password if running

# Check port
netstat -an | grep 3306
```

---

## 📊 Configuration Checklist

Before running application:

- [ ] MySQL server running
- [ ] `vehicle_service` database created
- [ ] Database schema imported
- [ ] DBConnection.java URL is correct
- [ ] DBConnection.java USER is correct
- [ ] DBConnection.java PASSWORD is correct
- [ ] JDBC JAR in lib/ folder
- [ ] Application recompiled
- [ ] Test login works (admin/admin)

---

## 🔄 Quick Configuration Commands

### Create Database and User

```sql
-- Create database
CREATE DATABASE vehicle_service;

-- Create user (if different from root)
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin123';

-- Grant permissions
GRANT ALL PRIVILEGES ON vehicle_service.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;

-- Import schema
-- USE vehicle_service;
-- (paste contents of database_schema.sql)
```

### Reset to Defaults

```sql
-- Delete and recreate database
DROP DATABASE IF EXISTS vehicle_service;
CREATE DATABASE vehicle_service;

-- Run database_schema.sql script
```

---

## 💾 Backup & Restore

### Backup Database

```bash
mysqldump -u root -p vehicle_service > backup.sql
```

### Restore Database

```bash
mysql -u root -p vehicle_service < backup.sql
```

---

## 📋 Configuration Examples by Database

### MySQL (Default)
```java
URL = "jdbc:mysql://localhost:3306/vehicle_service"
```

### MariaDB
```java
URL = "jdbc:mariadb://localhost:3306/vehicle_service"
```

### PostgreSQL (Future)
```java
URL = "jdbc:postgresql://localhost:5432/vehicle_service"
```

---

**Last Updated:** 2024
**Version:** 1.0

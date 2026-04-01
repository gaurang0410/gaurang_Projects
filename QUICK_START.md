# ⚡ Quick Start Guide

## Vehicle Service Management App - Get Up and Running in 5 Minutes!

---

## 🎯 In a Nutshell

A complete **Java Swing + MySQL** application for managing vehicle services. Fully functional with **3-layer architecture**, database integration, and a user-friendly GUI.

---

## ⚙️ What You Need

1. **Java JDK 8+** - [Download](https://www.oracle.com/java/)
2. **MySQL Server** - [Download](https://www.mysql.com/)
3. **MySQL JDBC Driver** - [Download](https://dev.mysql.com/downloads/connector/j/)

---

## 🚀 5-Minute Setup

### 1️⃣ Prepare Files (1 min)

```
Project folder: C:\Users\Gaurang Khanolkar\OneDrive\Desktop\oopj\project\

Structure:
  ├── src/              (All Java code)
  ├── lib/              (Place JDBC JAR here)
  ├── database_schema.sql
  ├── compile.bat       (For Windows)
  └── run.bat           (For Windows)
```

**Download JDBC:** 
- Get `mysql-connector-j-8.0.33.jar`
- Drop in `lib/` folder

---

### 2️⃣ Create Database (1 min)

Open MySQL Command Line:

```bash
mysql -u root -p
# Enter password: root

# Copy-paste contents of database_schema.sql
```

Or use GUI tool: MySQL Workbench → Open `database_schema.sql` → Execute

---

### 3️⃣ Compile (1 min)

**Windows:**
```bash
cd C:\Users\Gaurang Khanolkar\OneDrive\Desktop\oopj\project
compile.bat
```

**Mac/Linux:**
```bash
cd ~/Desktop/oopj/project
./compile.sh
```

✨ If successful: "Compilation successful!" message

---

### 4️⃣ Run (1 min)

**Windows:**
```bash
run.bat
```

**Mac/Linux:**
```bash
./run.sh
```

**Login Screen Appears!** ✅

---

### 5️⃣ Login & Test (1 min)

```
Username: admin
Password: admin
```

Click Login → Dashboard appears!

---

## 🎮 Try These Actions

1. **Dashboard** appears with 6 buttons
2. Click **"Add Customer"** → Add: John Doe, 9876543210, john@email.com
3. Click **"Add Vehicle"** → Select John, Toyota, Camry, ABC123
4. Click **"New Service"** → Select vehicle, Oil Change, 2024-03-20, Pending, 500
5. Click **"View Records"** → See all data in tables
6. Update Service Status to "Completed"
7. Click **"Generate Bill"** → See invoice

---

## 📋 Core Features

| Feature | Where | What |
|---------|-------|------|
| 👤 Customers | Add Customer | Add/Edit/Delete customers |
| 🚗 Vehicles | Add Vehicle | Link vehicles to customers |
| 🛠️ Services | New Service | Track service jobs |
| 📊 Records | View Records | See all data in tables |
| 💵 Bills | Generate Bill | Print invoices |

---

## 🔑 Login Credentials

```
Username: admin
Password: admin
```

---

## 🗂️ File Organization

```
src/
├── gui/              (7 screen files)
├── model/            (3 data object files)
├── dao/              (4 database access files)
├── service/          (3 business logic files)
└── Main.java         (Start here)

lib/
└── mysql-connector-j-8.0.33.jar  (JDBC driver)

Docs:
├── README.md         (Full docs)
├── SETUP_GUIDE.md    (Detailed setup)
├── CONFIGURATION.md  (Config help)
└── FILE_INVENTORY.md (All files)
```

---

## ⚠️ Common Issues

### ❌ "Connection refused"
→ Start MySQL service: `net start MySQL80`

### ❌ "Database doesn't exist"
→ Run `database_schema.sql` in MySQL

### ❌ "Driver not found"
→ Place JDBC JAR in `lib/` folder

### ❌ "Cannot compile"
→ Check JDBC path in `compile.bat`

---

## 🎯 Database Tables

| Table | Purpose |
|-------|---------|
| customers | Store customer info |
| vehicles | Store vehicles linked to customers |
| services | Store service records |
| service_details | Optional: Service details |

---

## 📊 Architecture

```
GUI (Swing)
   ↓
Service Layer (Business Logic)
   ↓
DAO Layer (Database Access)
   ↓
Database (MySQL)
```

---

## 🔒 Security Features

✓ Login authentication
✓ SQL injection prevention
✓ Input validation
✓ Secure password handling

---

## 📱 Screen Functions

### 1. Login Screen
- Enter credentials
- Default: admin/admin

### 2. Dashboard
- 6 main buttons
- Navigate to all features

### 3. Customer Form
- Add/Edit/Delete customers
- Search by name
- View customer table

### 4. Vehicle Form
- Add vehicles per customer
- Search by registration
- View vehicle table

### 5. Service Form
- Create service jobs
- Update status
- Track costs

### 6. Billing Screen
- Generate bills
- Print directly
- Professional format

### 7. Records View
- Tabbed interface
- View customers
- View vehicles
- View services

---

## 💾 Data Storage

All data stored in MySQL database:
- ✅ Customers (name, phone, email, address)
- ✅ Vehicles (brand, model, registration)
- ✅ Services (type, date, status, cost)

---

## 🧪 Test Scenario

**Follow these steps to test:**

1. Login: `admin/admin`
2. Add Customer: "John Smith"
3. Add Vehicle: Toyota Camry, ABC123
4. Add Service: Oil Change, Pending, $100
5. Change Status: Completed
6. Generate Bill: View invoice

---

## 📝 Configuration

Default MySQL settings in `src/dao/DBConnection.java`:

```java
URL = "jdbc:mysql://localhost:3306/vehicle_service"
USER = "root"
PASSWORD = "root"
```

Change if your MySQL credentials differ.

---

## 🌐 Technology Stack

| Layer | Technology |
|-------|-----------|
| GUI | Java Swing |
| Backend | Java 8+ |
| Database | MySQL 5.7+ |
| Driver | JDBC (MySQL Connector/J) |

---

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| README.md | Complete overview |
| SETUP_GUIDE.md | Installation details |
| CONFIGURATION.md | Config reference |
| PROJECT_SUMMARY.md | Features list |
| FILE_INVENTORY.md | All files info |

---

## 💡 Pro Tips

1. **Add sample data first** to test features
2. **Complete services** before generating bills
3. **Use registration number** for vehicle search
4. **View records tab** to check all data
5. **Print bills** for records

---

## 🔄 Typical Workflow

```
1. Add Customer
   ↓
2. Add Vehicle for Customer
   ↓
3. Add Service for Vehicle
   ↓
4. Update Service Status to "Completed"
   ↓
5. Generate Bill
   ↓
6. Print or Save Bill
```

---

## 🎯 What's Included

- ✅ 17 Java source files
- ✅ Complete database schema
- ✅ 5 documentation guides
- ✅ Build scripts (Windows/Mac/Linux)
- ✅ Sample data
- ✅ Production-ready code

---

## 🚀 Beyond Quick Start

After getting comfortable:

1. Read **README.md** for full features
2. Check **PROJECT_SUMMARY.md** for architecture
3. Review **CONFIGURATION.md** for advanced setup
4. Explore code in **src/** folders

---

## 🆘 Need Help?

1. **Setup issues?** → Read SETUP_GUIDE.md
2. **Configuration?** → Check CONFIGURATION.md
3. **Code structure?** → See FILE_INVENTORY.md
4. **Features?** → Browse README.md

---

## ⏱️ Time Investment

```
Setup:           5 minutes
First test:      2 minutes
Understanding:   15-30 minutes
Full mastery:    1-2 hours
```

---

## 📞 Quick Checklist

Before running:

- [ ] Java installed and working
- [ ] MySQL server running
- [ ] Database created via database_schema.sql
- [ ] JDBC JAR in lib/ folder
- [ ] DBConnection.java configured
- [ ] Compilation successful

---

## 🎉 Ready to Go!

You now have a **fully functional Vehicle Service Management System** ready to use!

### Next Steps:
1. Run `compile.bat` (or `compile.sh`)
2. Run `run.bat` (or `run.sh`)
3. Login with admin/admin
4. Start managing vehicles!

---

**Happy Managing! 🚗**

> Questions? Refer to documentation files
> 
> Need more? Code is fully extensible!

---

**Version:** 1.0
**Status:** Production Ready ✅
**Last Updated:** 2024

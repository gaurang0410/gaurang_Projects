-- Reset database (recommended during development)
DROP DATABASE IF EXISTS vehicle_service_db;

CREATE DATABASE vehicle_service_db;
USE vehicle_service_db;

-- ================= USERS =================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role ENUM('ADMIN', 'CUSTOMER') DEFAULT 'CUSTOMER',
    customer_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================= CUSTOMERS =================
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Foreign Key
ALTER TABLE users 
ADD CONSTRAINT fk_user_customer 
FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
ON DELETE SET NULL;

-- ================= VEHICLE BRANDS =================
CREATE TABLE vehicle_brands (
    brand_id INT PRIMARY KEY AUTO_INCREMENT,
    brand_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO vehicle_brands (brand_name) VALUES 
('Toyota'), ('Honda'), ('Ford'), ('Hyundai'), ('Maruti Suzuki'), ('Tata Motors'), ('Mahindra');

-- ================= VEHICLE MODELS =================
CREATE TABLE vehicle_models (
    model_id INT PRIMARY KEY AUTO_INCREMENT,
    brand_id INT NOT NULL,
    model_name VARCHAR(50) NOT NULL,
    FOREIGN KEY (brand_id) REFERENCES vehicle_brands(brand_id)
    ON DELETE CASCADE
);

INSERT INTO vehicle_models (brand_id, model_name) VALUES 
(1, 'Corolla'), (1, 'Camry'), (1, 'Fortuner'),
(2, 'Civic'), (2, 'City'), (2, 'Accord'),
(3, 'Mustang'), (3, 'EcoSport'), (3, 'Endeavour'),
(4, 'Creta'), (4, 'Verna'), (4, 'i20'),
(5, 'Swift'), (5, 'Baleno'), (5, 'Dzire'),
(6, 'Nexon'), (6, 'Harrier'), (6, 'Safari'),
(7, 'Scorpio'), (7, 'Thar'), (7, 'XUV700');

-- ================= VEHICLES =================
CREATE TABLE vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    vehicle_category ENUM('ECONOMY', 'STANDARD', 'PREMIUM', 'SUPERCAR') DEFAULT 'STANDARD',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
    ON DELETE CASCADE
);

-- ================= SERVICES =================
CREATE TABLE services (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id INT NOT NULL,
    mechanic_id INT NULL,
    service_type VARCHAR(100) NOT NULL,
    service_date DATE NOT NULL,
    status VARCHAR(30) DEFAULT 'Pending',
    cost DECIMAL(10,2) NOT NULL,
    estimated_time VARCHAR(50) DEFAULT 'N/A',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id)
    ON DELETE CASCADE
);

-- ================= MECHANICS =================
CREATE TABLE mechanics (
    mechanic_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    phone VARCHAR(20),
    availability VARCHAR(30) DEFAULT 'Available',
    rating DOUBLE DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE
);

ALTER TABLE services
ADD CONSTRAINT fk_service_mechanic
FOREIGN KEY (mechanic_id) REFERENCES mechanics(mechanic_id)
ON DELETE SET NULL;

-- ================= SERVICE DETAILS =================
CREATE TABLE service_details (
    detail_id INT PRIMARY KEY AUTO_INCREMENT,
    service_id INT NOT NULL,
    description TEXT,
    parts_used VARCHAR(255),
    labor_cost DECIMAL(10,2),
    FOREIGN KEY (service_id) REFERENCES services(service_id)
    ON DELETE CASCADE
);

-- ================= SERVICE CATALOG =================
CREATE TABLE service_catalog (
    catalog_id INT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(100) NOT NULL,
    description TEXT,
    base_cost DECIMAL(10,2) NOT NULL,
    estimated_time VARCHAR(20) DEFAULT '2 hours',
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ================= INVENTORY =================
CREATE TABLE inventory (
    part_id INT PRIMARY KEY AUTO_INCREMENT,
    part_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) DEFAULT 0.00,
    stock INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ================= SERVICE JOB ITEMS =================
CREATE TABLE service_job_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    service_id INT NOT NULL,
    item_type ENUM('PART', 'SERVICE') DEFAULT 'SERVICE',
    item_name VARCHAR(100) NOT NULL,
    quantity INT DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    FOREIGN KEY (service_id) REFERENCES services(service_id)
    ON DELETE CASCADE
);

-- ================= FEEDBACK =================
CREATE TABLE feedback (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    service_id INT,
    customer_id INT,
    mechanic_id INT,
    rating INT,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    FOREIGN KEY (mechanic_id) REFERENCES mechanics(mechanic_id) ON DELETE SET NULL
);

-- ================= NOTIFICATIONS =================
CREATE TABLE notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL,
    title VARCHAR(120) NOT NULL,
    message VARCHAR(255) NOT NULL,
    type VARCHAR(40) DEFAULT 'INFO',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ================= AUDIT LOGS =================
CREATE TABLE audit_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    action VARCHAR(255),
    user_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);

-- ================= SERVICE COST HISTORY =================
CREATE TABLE service_price_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    service_id INT,
    old_price DOUBLE,
    new_price DOUBLE,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE
);

-- ================= DEFAULT ADMIN (ONLY FIXED DATA) =================
INSERT INTO users (username, email, password, full_name, role)
VALUES ('admin', 'admin@gmail.com', 'admin', 'Administrator', 'ADMIN');

-- ================= DEFAULT SERVICE CATALOG =================
INSERT INTO service_catalog (service_name, description, base_cost, category) VALUES
('General Service', 'Complete vehicle checkup and oil change', 1500.00, 'Maintenance'),
('Oil Change', 'Engine oil and filter replacement', 800.00, 'Maintenance'),
('Brake Repair', 'Brake pad replacement and system bleed', 1200.00, 'Repair'),
('AC Service', 'AC gas refill and filter cleaning', 1000.00, 'Maintenance'),
('Wheel Alignment', 'Precision wheel alignment and balancing', 600.00, 'Maintenance'),
('Engine Tuning', 'Full engine diagnostics and tuning', 2500.00, 'Repair');

-- ================= DEFAULT INVENTORY =================
INSERT INTO inventory (part_name, price, stock, low_stock_threshold) VALUES
('Engine Oil (4L)', 2500.00, 20, 5),
('Oil Filter', 450.00, 50, 10),
('Brake Pads (Front)', 1800.00, 15, 3),
('Air Filter', 600.00, 30, 8),
('Spark Plug', 250.00, 100, 20),
('Coolant (1L)', 350.00, 25, 5),
('Wiper Blades', 550.00, 12, 4);

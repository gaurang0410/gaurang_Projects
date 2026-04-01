-- Vehicle Service Management Database Schema

-- Create Database
CREATE DATABASE IF NOT EXISTS vehicle_service;

USE vehicle_service;

-- Users Table (Authentication)
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles Table
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- Services Table
CREATE TABLE IF NOT EXISTS services (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id INT NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    service_date VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    cost DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE CASCADE
);

-- Service Details Table (Optional)
CREATE TABLE IF NOT EXISTS service_details (
    detail_id INT PRIMARY KEY AUTO_INCREMENT,
    service_id INT NOT NULL,
    description TEXT,
    parts_used VARCHAR(255),
    labor_cost DECIMAL(10, 2),
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE
);

-- Insert Sample Data
INSERT INTO customers (name, phone, email, address) 
VALUES 
('John Doe', '555-1234', 'john@email.com', '123 Main St'),
('Jane Smith', '555-5678', 'jane@email.com', '456 Oak Ave'),
('Bob Johnson', '555-9012', 'bob@email.com', '789 Pine Rd');

INSERT INTO vehicles (customer_id, brand, model, registration_number)
VALUES 
(1, 'Toyota', 'Camry', 'ABC123'),
(1, 'Honda', 'Civic', 'XYZ789'),
(2, 'Ford', 'Mustang', 'DEF456'),
(3, 'BMW', 'X5', 'GHI012');

INSERT INTO services (vehicle_id, service_type, service_date, status, cost)
VALUES 
(1, 'Oil Change', '2024-01-15', 'Completed', 50.00),
(1, 'Tire Rotation', '2024-02-20', 'Completed', 75.00),
(2, 'Engine Checkup', '2024-03-10', 'In Progress', 150.00),
(3, 'Brake Service', '2024-03-15', 'Pending', 200.00);
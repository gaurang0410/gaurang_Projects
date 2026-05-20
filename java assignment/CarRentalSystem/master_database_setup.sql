-- =============================================================================
--  CAR RENTAL SYSTEM — MASTER DATABASE SETUP SCRIPT
--  Run this single file in MySQL Workbench to set up the entire database.
--
--  EXECUTION ORDER (all handled automatically below):
--    1. Create database + all 5 core tables
--    2. Add password reset tokens table
--    3. Add missing performance indexes
--    4. Insert default admin + customer users
--
--  ⚠️  BEFORE RUNNING:
--    - Make sure MySQL server is running
--    - Run PasswordUtil.main() in NetBeans first to generate real BCrypt hashes
--    - Replace the two [PASTE_BCRYPT_HASH_HERE] placeholders with your real hashes
--
--  ⚠️  WARNING: This script DROPS and RECREATES all tables.
--    All existing data will be wiped. For a fresh install only.
-- =============================================================================


-- =============================================================================
--  SECTION 1 — DATABASE
-- =============================================================================

CREATE DATABASE IF NOT EXISTS car_rental_system
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE car_rental_system;


-- =============================================================================
--  SECTION 2 — DROP EXISTING TABLES (safe reset)
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;


-- =============================================================================
--  SECTION 3 — CORE TABLES
-- =============================================================================

-- Table 1: users
CREATE TABLE users (
    id           INT          NOT NULL AUTO_INCREMENT,
    username     VARCHAR(50)  NOT NULL,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15)  NOT NULL,
    role         ENUM('ADMIN', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Table 2: cars
CREATE TABLE cars (
    id            INT            NOT NULL AUTO_INCREMENT,
    brand         VARCHAR(50)    NOT NULL,
    model         VARCHAR(50)    NOT NULL,
    category      VARCHAR(50)    NOT NULL,
    price_per_day DECIMAL(10,2)  NOT NULL,
    status        ENUM('AVAILABLE', 'BOOKED') NOT NULL DEFAULT 'AVAILABLE',
    image_url     TEXT           NOT NULL,
    fuel_type     VARCHAR(50)    NOT NULL DEFAULT 'Petrol',
    location      VARCHAR(100)   NOT NULL DEFAULT 'Mumbai',
    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Table 3: bookings
CREATE TABLE bookings (
    id              INT           NOT NULL AUTO_INCREMENT,
    customer_id     INT           NOT NULL,
    car_id          INT           NOT NULL,
    pickup_date     DATE          NOT NULL,
    return_date     DATE          NOT NULL,
    total_amount    DECIMAL(10,2) NOT NULL,
    status          ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
    booking_date    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pickup_location VARCHAR(100)  NOT NULL DEFAULT 'Mumbai',
    drop_location   VARCHAR(100)  NOT NULL DEFAULT 'Mumbai',
    gst_amount      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    invoice_id      VARCHAR(50)   DEFAULT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_bookings_invoice_id (invoice_id),
    KEY idx_bookings_customer_id (customer_id),
    KEY idx_bookings_car_id (car_id),
    CONSTRAINT fk_bookings_customer
        FOREIGN KEY (customer_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_bookings_car
        FOREIGN KEY (car_id) REFERENCES cars(id)
        ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT chk_booking_dates CHECK (return_date > pickup_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Table 4: payments
CREATE TABLE payments (
    id               INT           NOT NULL AUTO_INCREMENT,
    booking_id       INT           NOT NULL,
    amount           DECIMAL(10,2) NOT NULL,
    payment_method   VARCHAR(50)   NOT NULL,
    status           ENUM('SUCCESS', 'FAILED', 'PENDING') NOT NULL DEFAULT 'PENDING',
    transaction_id   VARCHAR(64)   DEFAULT NULL,
    transaction_date TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payments_transaction_id (transaction_id),
    KEY idx_payments_booking_id (booking_id),
    CONSTRAINT fk_payments_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id)
        ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Table 5: reviews
CREATE TABLE reviews (
    id          INT          NOT NULL AUTO_INCREMENT,
    booking_id  INT          NOT NULL,
    customer_id INT          NOT NULL,
    car_id      INT          NOT NULL,
    rating      INT          NOT NULL,
    comment     VARCHAR(500) DEFAULT NULL,
    status      ENUM('APPROVED', 'PENDING', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_review_booking_customer (booking_id, customer_id),
    KEY idx_reviews_customer_id (customer_id),
    KEY idx_reviews_car_id (car_id),
    CONSTRAINT fk_reviews_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id)
        ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_reviews_customer
        FOREIGN KEY (customer_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_reviews_car
        FOREIGN KEY (car_id) REFERENCES cars(id)
        ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
--  SECTION 4 — PASSWORD RESET TOKENS TABLE
--  (from migration_password_reset.sql — fixed for Error 3780)
-- =============================================================================

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    email      VARCHAR(100) NOT NULL,
    token      VARCHAR(255) NOT NULL,
    expiry     TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (email),
    CONSTRAINT fk_prt_email
        FOREIGN KEY (email) REFERENCES users(email)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
--  SECTION 5 — PERFORMANCE INDEXES
-- =============================================================================

-- Speeds up dashboard metrics and booking status filtering
CREATE INDEX idx_bookings_status ON bookings(status);

-- Speeds up review moderation page and approved review display on car pages
CREATE INDEX idx_reviews_status ON reviews(status);


-- =============================================================================
--  SECTION 6 — DEFAULT USERS
--
--  ⚠️  IMPORTANT — READ BEFORE RUNNING:
--
--  If your PasswordUtil.java uses BCrypt (recommended):
--    → Run PasswordUtil.main() in NetBeans first
--    → Copy the two printed hashes
--    → Replace [PASTE_BCRYPT_HASH_FOR_admin123_HERE] with the admin hash
--    → Replace [PASTE_BCRYPT_HASH_FOR_customer123_HERE] with the customer hash
--
--  If your PasswordUtil.java still uses plaintext or SHA-256 (not yet updated):
--    → Temporarily replace the hash values below with: 'admin123' and 'customer123'
--    → Update to BCrypt hashes after fixing PasswordUtil.java
-- =============================================================================
INSERT IGNORE INTO users (username, full_name, email, password, phone_number, role)
VALUES
(
    'admin',
    'Admin',
    'admin@carrental.com',
    '$2a$12$Gv0W1rG5cZdVDpruSK4jvui8PfdUsCMBSFajxxECHs9L594wlZr2W',
    '1234567890',
    'ADMIN'
),
(
    'aarav_sharma',
    'Aarav Sharma',
    'aarav.sharma@example.com',
    '$2a$12$fY4wSnafz5Mc3vnBYQukFO9OssZoQEjS.HxcHuz99KS1yg8YKJkWy',
    '9876500011',
    'CUSTOMER'
);



-- =============================================================================
--  SECTION 7 — VERIFY SETUP
--  Run this block after execution to confirm everything was created correctly.
-- =============================================================================

SELECT 'DATABASE' AS type, schema_name AS name
FROM information_schema.schemata
WHERE schema_name = 'car_rental_system'

UNION ALL

SELECT 'TABLE', table_name
FROM information_schema.tables
WHERE table_schema = 'car_rental_system'
ORDER BY type, name;

-- Expected output: 1 DATABASE row + 6 TABLE rows:
--   car_rental_system (DATABASE)
--   bookings, cars, password_reset_tokens, payments, reviews, users (TABLES)
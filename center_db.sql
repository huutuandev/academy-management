-- =============================================
-- DATABASE: CENTER MANAGEMENT SYSTEM
-- Mô tả: Hệ thống quản lý trung tâm giáo dục
-- Tác giả: Nguyễn Hữu Tuấn
-- Ngày tạo: 29/05/2026
-- =============================================

-- Tạo database
CREATE DATABASE IF NOT EXISTS center_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE center_db;

-- =============================================
-- 1. Bảng Role (Vai trò người dùng)
-- =============================================
CREATE TABLE roles (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL UNIQUE COMMENT 'Tên vai trò: ADMIN, TEACHER',
    description VARCHAR(255) COMMENT 'Mô tả vai trò',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT = 'Lưu các vai trò trong hệ thống';

-- =============================================
-- 2. Bảng User (Tài khoản người dùng)
-- =============================================
CREATE TABLE users (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    full_name    VARCHAR(100) NOT NULL,
    role_id      BIGINT       NOT NULL,
    phone        VARCHAR(20),
    email        VARCHAR(100),
    is_active    BOOLEAN      DEFAULT TRUE,
    created_by   BIGINT,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (role_id) REFERENCES roles(id),
    INDEX idx_username (username),
    INDEX idx_email (email)
) COMMENT = 'Bảng lưu thông tin tài khoản Admin và Teacher';

-- =============================================
-- 3. Bảng TeacherProfile (Giới thiệu giáo viên)
-- =============================================
CREATE TABLE teacher_profiles (
    user_id          BIGINT PRIMARY KEY,
    avatar           VARCHAR(500),
    bio              TEXT COMMENT 'Giới thiệu bản thân',
    qualifications   TEXT COMMENT 'Trình độ, bằng cấp',
    experience       TEXT COMMENT 'Kinh nghiệm giảng dạy',
    specialties      VARCHAR(500) COMMENT 'Chuyên môn (IELTS, Tiếng Anh giao tiếp...)',
    total_classes    INT DEFAULT 0,
    rating           DECIMAL(3,2) DEFAULT 0.0,
    total_students   INT DEFAULT 0,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT = 'Thông tin profile công khai của giáo viên';

-- =============================================
-- 4. Bảng Course (Khóa học)
-- =============================================
CREATE TABLE courses (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    code            VARCHAR(20)  NOT NULL UNIQUE,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    fee             DECIMAL(12,2),
    duration_weeks  INT,
    status          ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (created_by) REFERENCES users(id)
) COMMENT = 'Quản lý các khóa học lớn';

-- =============================================
-- 5. Bảng Class (Lớp học)
-- =============================================
CREATE TABLE classes (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    code              VARCHAR(20)  NOT NULL UNIQUE,
    name              VARCHAR(100) NOT NULL,
    course_id         BIGINT NOT NULL,
    teacher_id        BIGINT NOT NULL,
    start_date        DATE,
    end_date          DATE,
    max_students      INT DEFAULT 20,
    current_students  INT DEFAULT 0,
    room              VARCHAR(50),
    status            ENUM('UPCOMING', 'IN_PROGRESS', 'FINISHED', 'CANCELLED') DEFAULT 'UPCOMING',
    created_by        BIGINT,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (course_id)  REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
) COMMENT = 'Quản lý từng lớp học cụ thể';

-- =============================================
-- 6. Bảng Student (Học viên)
-- =============================================
CREATE TABLE students (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    code          VARCHAR(20)  NOT NULL UNIQUE,
    full_name     VARCHAR(100) NOT NULL,
    dob           DATE,
    gender        ENUM('MALE', 'FEMALE', 'OTHER'),
    phone         VARCHAR(20),
    email         VARCHAR(100),
    address       VARCHAR(255),
    parent_name   VARCHAR(100),
    parent_phone  VARCHAR(20),
    status        ENUM('ACTIVE', 'INACTIVE', 'DROPPED') DEFAULT 'ACTIVE',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT = 'Thông tin học viên';

-- =============================================
-- 7. Bảng ClassStudent (Học viên thuộc lớp)
-- =============================================
CREATE TABLE class_student (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id     BIGINT NOT NULL,
    student_id   BIGINT NOT NULL,
    joined_date  DATETIME DEFAULT CURRENT_TIMESTAMP,
    status       ENUM('ACTIVE', 'DROPPED', 'COMPLETED') DEFAULT 'ACTIVE',
    note         TEXT,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (class_id)   REFERENCES classes(id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    UNIQUE KEY unique_class_student (class_id, student_id)
) COMMENT = 'Liên kết học viên với lớp học';

-- =============================================
-- 8. Bảng Attendance (Điểm danh)
-- =============================================
CREATE TABLE attendances (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id      BIGINT NOT NULL,
    student_id    BIGINT NOT NULL,
    session_date  DATE NOT NULL,
    status        ENUM('PRESENT', 'ABSENT', 'LATE') DEFAULT 'PRESENT',
    note          TEXT,
    created_by    BIGINT NOT NULL,        -- Teacher điểm danh
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (class_id)   REFERENCES classes(id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    UNIQUE KEY unique_attendance (class_id, student_id, session_date)
) COMMENT = 'Bảng điểm danh theo buổi';

-- =============================================
-- 9. Bảng Registration (Đăng ký học từ profile)
-- =============================================
CREATE TABLE registrations (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id     BIGINT NOT NULL,
    class_id       BIGINT,
    student_name   VARCHAR(100) NOT NULL,
    student_phone  VARCHAR(20) NOT NULL,
    student_email  VARCHAR(100),
    message        TEXT,
    status         ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (class_id)   REFERENCES classes(id)
) COMMENT = 'Đăng ký học từ trang profile giáo viên';

-- =============================================
-- 10. Bảng Payment (Thanh toán)
-- =============================================
CREATE TABLE payments (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id      BIGINT NOT NULL,
    class_id        BIGINT,
    amount          DECIMAL(12,2) NOT NULL,
    payment_date    DATETIME DEFAULT CURRENT_TIMESTAMP,
    payment_method  ENUM('CASH', 'BANK_TRANSFER', 'MOMO', 'VNPAY', 'CARD'),
    note            TEXT,
    created_by      BIGINT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (class_id)   REFERENCES classes(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
) COMMENT = 'Lịch sử thanh toán học phí';

-- =============================================
-- 11. Bảng FeeRecord (Công nợ học phí)
-- =============================================
CREATE TABLE fee_records (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id   BIGINT NOT NULL,
    class_id     BIGINT NOT NULL,
    total_fee    DECIMAL(12,2) NOT NULL,
    paid_amount  DECIMAL(12,2) DEFAULT 0.00,
    due_amount   DECIMAL(12,2),
    due_date     DATE,
    status       ENUM('PAID', 'PARTIAL', 'UNPAID', 'OVERDUE') DEFAULT 'UNPAID',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (class_id)   REFERENCES classes(id)
) COMMENT = 'Quản lý công nợ học phí';

-- =============================================
-- Index để tối ưu query
-- =============================================
CREATE INDEX idx_class_teacher ON classes(teacher_id);
CREATE INDEX idx_class_course ON classes(course_id);
CREATE INDEX idx_attendance_date ON attendances(session_date);
CREATE INDEX idx_registration_teacher ON registrations(teacher_id);
# 🎓 Student Management System

## 📌 Giới thiệu

Đây là hệ thống **quản lý học viên** được xây dựng với mục tiêu:

* Quản lý học viên, lớp học
* Theo dõi học phí
* Làm nền tảng để mở rộng thành hệ thống học online (bài tập, thi online, AI)

---

## 🚀 Chức năng chính

### 🧑‍🎓 Quản lý học viên

* Thêm / sửa / xoá học viên
* Xem danh sách học viên
* Xem thông tin chi tiết

### 📚 Quản lý lớp học

* Tạo lớp học
* Sửa / xoá lớp
* Xem danh sách lớp

### 🔗 Gán học viên vào lớp

* Thêm học viên vào lớp
* Xem danh sách học viên theo lớp

### 🔐 Xác thực người dùng

* Đăng nhập
* Đăng xuất

### 💰 Quản lý học phí

* Nhập học phí cho học viên
* Trạng thái: Đã đóng / Chưa đóng

---

## 🧱 Công nghệ sử dụng

* Frontend: (HTML/CSS/JS hoặc React)
* Backend: (Node.js / Laravel / Django)
* Database: MySQL / PostgreSQL

---

## 🗄️ Cấu trúc database (cơ bản)

### students

* id
* name
* email
* phone

### classes

* id
* name
* schedule

### enrollments

* id
* student_id
* class_id

### payments

* id
* student_id
* amount
* status

### users

* id
* username
* password

---

## ⚙️ Cài đặt & chạy project

### 1. Clone project

```bash
git clone <your-repo>
cd project
```

### 2. Cài đặt dependencies

```bash
npm install
```

### 3. Cấu hình database

* Tạo database MySQL
* Import file SQL (nếu có)
* Cập nhật config trong `.env`

### 4. Chạy server

```bash
npm start
```

---

## 🔮 Hướng phát triển (Future)

### 📝 Bài tập

* Giáo viên giao bài
* Học viên nộp bài

### 💻 Thi online

* Làm bài trực tiếp trên web
* Chấm điểm tự động

### 🤖 AI

* Tạo đề thi tự động
* Gợi ý học tập

---

## 📌 Mục tiêu

* Xây dựng hệ thống đơn giản → mở rộng dần
* Dễ bảo trì, dễ nâng cấp

---

## 👨‍💻 Tác giả

* Your Name

---


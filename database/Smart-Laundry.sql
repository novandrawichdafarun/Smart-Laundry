DROP DATABASE IF EXISTS smart_laundry;
CREATE DATABASE IF NOT EXISTS smart_laundry;

USE smart_laundry;

-- Tabel Pelanggan
CREATE TABLE pelanggan (
    id_pelanggan INT AUTO_INCREMENT PRIMARY KEY,
    nama_lengkap VARCHAR(100),
    no_hp VARCHAR(15),
    alamat TEXT
);

-- Tabel Transaksi (Mencatat Status & Biaya)
CREATE TABLE transaksi (
    id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    id_pelanggan INT,
    jenis_layanan VARCHAR(50), -- Cuci Kering / Setrika
    berat_kg DOUBLE,
    tipe_paket VARCHAR(20), -- Reguler / Express
    total_biaya DOUBLE,
    status_cucian ENUM('Diterima', 'Dicuci', 'Selesai', 'Diambil'),
    tgl_masuk DATETIME DEFAULT CURRENT_TIMESTAMP,
    tgl_selesai_estimasi DATETIME,
    FOREIGN KEY (id_pelanggan) REFERENCES pelanggan(id_pelanggan)
);

-- Membuat tabel pengguna
CREATE TABLE users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'staff') DEFAULT 'staff'
);

-- Masukkan akun default (Username: admin, Password: 123)
INSERT INTO users (username, password, role) VALUES ('admin', '123', 'admin');
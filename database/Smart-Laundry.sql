DROP DATABASE IF EXISTS smart_laundry;
CREATE DATABASE IF NOT EXISTS smart_laundry;

USE smart_laundry;

-- Membuat tabel pengguna
CREATE TABLE IF NOT EXISTS users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('super_admin', 'kasir', 'pelanggan') DEFAULT 'pelanggan'
);

-- Tabel Pelanggan
CREATE TABLE IF NOT EXISTS pelanggan (
    id_pelanggan INT AUTO_INCREMENT PRIMARY KEY,
    nama_lengkap VARCHAR(100) NOT NULL,
    no_hp VARCHAR(15),
    alamat TEXT NOT NULL
);

-- Tabel Transaksi (Mencatat Status & Biaya)
CREATE TABLE IF NOT EXISTS transaksi (
    id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    id_pelanggan INT,
    id_user INT(11),
    jenis_layanan VARCHAR(50), -- Cuci Basah / Cuci Kering / Setrika
    berat_kg DOUBLE,
    tipe_paket VARCHAR(20), -- Reguler / Express
    total_biaya DOUBLE,
    status_cucian ENUM('Menunggu','Diterima', 'Dicuci', 'Selesai', 'Diambil'),
    tgl_masuk DATETIME DEFAULT CURRENT_TIMESTAMP,
    tgl_selesai_estimasi DATETIME,

    -- Foreign Key ke tabel pelanggan
    FOREIGN KEY (id_pelanggan) REFERENCES pelanggan(id_pelanggan),

    -- Foreign Key ke tabel users
    FOREIGN KEY (id_user) REFERENCES users(id_user)
);

-- Insert contoh data
INSERT IGNORE INTO users (id_user, username, password, role) VALUES 
(1, 'admin', 'admin123', 'super_admin'),
(2, 'kasir', 'kasir123', 'kasir'),
(3, 'pelanggan', 'pelanggan123', 'pelanggan');


INSERT IGNORE INTO pelanggan (id_pelanggan, nama_lengkap, no_hp, alamat) VALUES
(1, 'Otong Surotong', '081234567890', 'Jl. Kenangan No. 1'),
(2, 'Siti Ropeah', '089876543210', 'Jl. Jalan No. 5'),
(3, 'Bahlil Etanol', '085678901234', 'Jl. Minyak No. 10'),
(4, 'Jule Selingkuh', '081345678901', 'Jl. Tikung No. 3');

-- Insert Data Transaksi Dummy (7 Hari Terakhir)
DELETE FROM transaksi; -- Hapus data lama agar grafik bersih

-- HARI INI (Day 0)
INSERT INTO transaksi (id_pelanggan, id_user, jenis_layanan, berat_kg, tipe_paket, total_biaya, status_cucian, tgl_masuk) VALUES
(1, 2, 'Cuci Basah', 5.0, 'Reguler', 15000, 'Diterima', CURRENT_TIMESTAMP),
(2, 2, 'Cuci Kering', 3.0, 'Express', 27000, 'Dicuci', CURRENT_TIMESTAMP);

-- KEMARIN (Day -1)
INSERT INTO transaksi (id_pelanggan, id_user, jenis_layanan, berat_kg, tipe_paket, total_biaya, status_cucian, tgl_masuk) VALUES
(3, 2, 'Setrika', 10.0, 'Reguler', 35000, 'Selesai', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY)),
(4, 2, 'Cuci Basah', 4.5, 'Reguler', 13500, 'Diambil', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY));

-- 2 HARI LALU (Day -2)
INSERT INTO transaksi (id_pelanggan, id_user, jenis_layanan, berat_kg, tipe_paket, total_biaya, status_cucian, tgl_masuk) VALUES
(1, 2, 'Cuci Kering', 7.0, 'Reguler', 28000, 'Diambil', DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY));

-- 3 HARI LALU (Day -3)
INSERT INTO transaksi (id_pelanggan, id_user, jenis_layanan, berat_kg, tipe_paket, total_biaya, status_cucian, tgl_masuk) VALUES
(2, 2, 'Setrika', 5.0, 'Express', 25000, 'Diambil', DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY)),
(3, 2, 'Cuci Basah', 20.0, 'Reguler', 60000, 'Diambil', DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY));

-- 4 HARI LALU (Day -4)
INSERT INTO transaksi (id_pelanggan, id_user, jenis_layanan, berat_kg, tipe_paket, total_biaya, status_cucian, tgl_masuk) VALUES
(4, 2, 'Cuci Komplit', 6.0, 'Reguler', 30000, 'Diambil', DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY));

-- 5 HARI LALU (Day -5)
INSERT INTO transaksi (id_pelanggan, id_user, jenis_layanan, berat_kg, tipe_paket, total_biaya, status_cucian, tgl_masuk) VALUES
(1, 2, 'Cuci Basah', 3.0, 'Express', 19000, 'Diambil', DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY));

-- 6 HARI LALU (Day -6)
INSERT INTO transaksi (id_pelanggan, id_user, jenis_layanan, berat_kg, tipe_paket, total_biaya, status_cucian, tgl_masuk) VALUES
(2, 2, 'Setrika', 8.0, 'Reguler', 28000, 'Diambil', DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY));
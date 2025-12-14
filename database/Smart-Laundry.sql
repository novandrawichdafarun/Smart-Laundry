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
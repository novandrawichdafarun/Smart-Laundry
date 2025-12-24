package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import config.DBConnection;

public class TransaksiController {

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean simpanTransaksi(String nama, String hp, String alamat, String jenis, double berat, boolean isExpress, double total) {
        Connection con = DBConnection.getConnection();

        if (con == null) {
            return false;
        }

        try {
            con.setAutoCommit(false);

            String sqlPelanggan = "INSERT INTO pelanggan (nama_lengkap, no_hp, alamat) VALUES(?, ?, ?)";
            PreparedStatement psPelanggan = con.prepareStatement(sqlPelanggan, Statement.RETURN_GENERATED_KEYS);
            psPelanggan.setString(1, nama);
            psPelanggan.setString(2, hp);
            psPelanggan.setString(3, alamat);
            psPelanggan.executeUpdate();

            ResultSet rs = psPelanggan.getGeneratedKeys();
            int idPelanggan = 0;
            if (rs.next()) {
                idPelanggan = rs.getInt(1);
            }

            String sqlTrans = "INSERT INTO transaksi (id_pelanggan, jenis_layanan, berat_kg, tipe_paket, total_biaya, status_cucian) VALUES (?, ?, ?, ?, ?, 'Diterima')";
            PreparedStatement psTrans = con.prepareStatement(sqlTrans);
            psTrans.setInt(1, idPelanggan);
            psTrans.setString(2, jenis);
            psTrans.setDouble(3, berat);
            psTrans.setString(4, isExpress ? "Express" : "Reguler");
            psTrans.setDouble(5, total);

            psTrans.executeUpdate();

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
            }
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void loadData(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT t.id_transaksi, t.tgl_masuk, p.nama_lengkap, t.jenis_layanan, t.berat_kg, t.total_biaya, t.status_cucian "
                + "FROM transaksi t JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan ORDER BY t.id_transaksi DESC";

        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            //? Format tanggal
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            while (rs.next()) {
                String tanggal = "";
                if (rs.getTimestamp("tgl_masuk") != null) {
                    tanggal = sdf.format(rs.getTimestamp("tgl_masuk"));
                }
                model.addRow(new Object[]{
                    rs.getInt("id_transaksi"),
                    tanggal,
                    rs.getString("nama_lengkap"),
                    rs.getString("jenis_layanan"),
                    rs.getDouble("berat_kg"),
                    rs.getDouble("total_biaya"),
                    rs.getString("status_cucian")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void updateStatus(int idTransaksi, String currentStatus) {
        String nextStatus = switch (currentStatus) {
            case "Diterima" ->
                "Dicuci";
            case "Dicuci" ->
                "Selesai";
            case "Selesai" ->
                "Diambil";
            default ->
                "Diambil";
        };

        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE transaksi SET status_cucian = ? WHERE id_transaksi = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nextStatus);
            ps.setInt(2, idTransaksi);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean deleteTransaksi(int id) {
        String sql = "DELETE FROM transaksi WHERE id_transaksi = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public double[] getStatistikHarian() {
        double[] stats = {0, 0, 0};

        String sql = "SELECT SUM(total_biaya) as omset, COUNT(*) as jumlah, SUM(berat_kg) as berat "
                + "FROM transaksi WHERE DATE(tgl_masuk) = CURDATE()";
        try (Connection con = DBConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                stats[0] = rs.getDouble("omset");
                stats[1] = rs.getDouble("jumlah");
                stats[2] = rs.getDouble("berat");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Map<String, Double> getGrafikPenjualan() {
        Map<String, Double> data = new LinkedHashMap<>();

        // Query: Ambil tanggal dan total biaya, grup per hari, urutkan dari yang terlama
        String sql = "SELECT DATE(tgl_masuk) as tanggal, SUM(total_biaya) as total "
                + "FROM transaksi "
                + "WHERE tgl_masuk >= DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY) "
                + "GROUP BY DATE(tgl_masuk) "
                + "ORDER BY tanggal ASC";

        try (Connection con = DBConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String tanggal = rs.getString("tanggal");
                // Ambil tanggalnya saja (tgl-bulan) agar tidak kepanjangan
                String label = tanggal.substring(8, 10) + "/" + tanggal.substring(5, 7);
                double total = rs.getDouble("total");
                data.put(label, total);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void cariData(DefaultTableModel model, String keyword) {
        model.setRowCount(0);

        String sql = "SELECT t.id_transaksi, t.tgl_masuk, p.nama_lengkap, t.jenis_layanan, t.berat_kg, t.total_biaya, t.status_cucian "
                + "FROM transaksi t JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                + "WHERE p.nama_lengkap LIKE ? OR t.id_transaksi LIKE ? "
                + "ORDER BY t.id_transaksi DESC";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            String searchKey = "%" + keyword + "%";
            ps.setString(1, searchKey);
            ps.setString(2, searchKey);

            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                while (rs.next()) {
                    String tanggal = "";
                    if (rs.getTimestamp("tgl_masuk") != null) {
                        tanggal = sdf.format(rs.getTimestamp("tgl_masuk"));
                    }
                    model.addRow(new Object[]{
                        rs.getInt("id_transaksi"),
                        tanggal,
                        rs.getString("nama_lengkap"),
                        rs.getString("jenis_layanan"),
                        rs.getDouble("berat_kg"),
                        rs.getDouble("total_biaya"),
                        rs.getString("status_cucian")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

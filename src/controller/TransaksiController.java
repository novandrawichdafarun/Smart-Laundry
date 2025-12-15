package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import config.DBConnection;

public class TransaksiController {

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

    public void loadData(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT t.id_transaksi, p.nama_lengkap, t.jenis_layanan, t.berat_kg, t.total_biaya, t.status_cucian "
                + "FROM transaksi t JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan ORDER BY t.id_transaksi DESC";

        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_transaksi"),
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

    public boolean deleteTransaksi(int id) {
        String sql = "DELETE FROM transaksi WHERE id_transaksi = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();;
            return false;
        }
    }
}

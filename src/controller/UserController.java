package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import config.DBConnection;

public class UserController {

    @SuppressWarnings("CallToPrintStackTrace")
    public void loadData(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT * FROM users";

        try (Connection con = DBConnection.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean tambahUser(String user, String pass, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean editUser(int id, String user, String pass, String role) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id_user = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, role);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean hapusUser(int id) {
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}

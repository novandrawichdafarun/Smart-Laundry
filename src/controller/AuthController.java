package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.DBConnection;

public class AuthController {

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean login(String username, String password) {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return false;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

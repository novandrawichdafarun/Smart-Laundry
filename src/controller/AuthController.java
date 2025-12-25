package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.DBConnection;
import utils.UserSession;

public class AuthController {

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean login(String username, String password) {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return false;
        }

        String sql = "SELECT id_user, username, role FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_user");
                    String user = rs.getString("username");
                    String role = rs.getString("role");

                    // Set user session
                    UserSession.setUser(id, user, role);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}

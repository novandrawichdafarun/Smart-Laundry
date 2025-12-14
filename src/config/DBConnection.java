package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/smart_laundry";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection instance;

    public DBConnection() {
    }

    public static Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                instance = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Koneksi Baru Dibuat ke Database");
            }
            return instance;
        } catch (SQLException e) {
            System.err.println("Koneksi Gagal: " + e.getMessage());
            return null;
        }
    }
}

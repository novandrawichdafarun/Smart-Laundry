package utils;

public class UserSession {

    private static int id;
    private static String username;
    private static String role;

    public static void setUser(int userId, String userName, String userRole) {
        UserSession.id = userId;
        UserSession.username = userName;
        UserSession.role = userRole;
    }

    //? Getters
    public static int getId() {
        return id;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    //? check
    public static boolean isSuperAdmin() {
        return "super_admin".equalsIgnoreCase(role);
    }

    public static boolean isKasir() {
        return "kasir".equalsIgnoreCase(role);
    }

    public static boolean isPelanggan() {
        return "pelanggan".equalsIgnoreCase(role);
    }

    //? Clear session
    public static void logout() {
        id = 0;
        username = null;
        role = null;
    }
}

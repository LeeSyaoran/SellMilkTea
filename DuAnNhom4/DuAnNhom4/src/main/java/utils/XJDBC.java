package utils;

import java.sql.*;

/**
 * Tiện ích JDBC: quản lý kết nối và thực thi lệnh SQL.
 * Cấu hình URL/USER/PASS phù hợp CSDL của bạn.
 *
 * Ví dụ SQL Server:
 *  URL: jdbc:sqlserver://localhost:1433;databaseName=DuAnNhom4;encrypt=false;trustServerCertificate=true
 *  DRIVER: com.microsoft.sqlserver.jdbc.SQLServerDriver
 *
 * Ví dụ MySQL:
 *  URL: jdbc:mysql://localhost:3306/duannhom4?useSSL=false&serverTimezone=UTC
 *  DRIVER: com.mysql.cj.jdbc.Driver
 */
public class XJDBC {

    // Cấu hình kết nối
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=TraSuaDB;encrypt=false;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "leesyaoran1307";
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Không tìm thấy JDBC Driver: " + DRIVER, ex);
        }
    }

    // Lấy kết nối
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Tạo PreparedStatement (hỗ trợ truyền tham số varargs)
    private static PreparedStatement prepareStatement(Connection conn, String sql, Object... args) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        return ps;
    }

    // Thực thi INSERT/UPDATE/DELETE
    public static int update(String sql, Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement ps = prepareStatement(conn, sql, args)) {
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi thực thi update: " + ex.getMessage(), ex);
        }
    }

    // Thực thi SELECT, trả về ResultSet (caller phải đóng)
    public static ResultSet query(String sql, Object... args) {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = prepareStatement(conn, sql, args);
            return ps.executeQuery();
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi thực thi query: " + ex.getMessage(), ex);
        }
    }

    // Đóng tài nguyên an toàn
    public static void closeQuietly(AutoCloseable c) {
        if (c != null) {
            try { c.close(); } catch (Exception ignore) {}
        }
    }

    // Ví dụ helper truy vấn đơn giá trị (scalar)
    public static Object queryScalar(String sql, Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement ps = prepareStatement(conn, sql, args);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getObject(1);
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi thực thi queryScalar: " + ex.getMessage(), ex);
        }
    }
}
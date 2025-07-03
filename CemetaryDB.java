package Cemetary;

import java.sql.*;

public class CemetaryDB {
    private static final String DB_URL = "jdbc:sqlite:cemetery.db";

    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS burial_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "dob TEXT," +
                "dod TEXT," +
                "plot TEXT UNIQUE);";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("DB Init error: " + e.getMessage());
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static boolean isPlotOccupied(String plot) {
        String sql = "SELECT COUNT(*) FROM burial_records WHERE plot = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plot);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static String[] getPlotDetails(String plot) {
        String sql = "SELECT name, dob, dod FROM burial_records WHERE plot = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plot);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new String[]{rs.getString("name"), rs.getString("dob"), rs.getString("dod")};
            }
        } catch (SQLException e) {
            System.out.println("Get plot error: " + e.getMessage());
        }
        return new String[]{"Unknown", "?", "?"};
    }

    public static void insertRecord(String name, String dob, String dod, String plot) throws SQLException {
        String sql = "INSERT INTO burial_records(name, dob, dod, plot) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, dob);
            pstmt.setString(3, dod);
            pstmt.setString(4, plot);
            pstmt.executeUpdate();
        }
    }

    public static void deleteRecordByPlot(String plot) throws SQLException {
        String sql = "DELETE FROM burial_records WHERE plot = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plot);
            pstmt.executeUpdate();
        }
    }

    public static ResultSet getAllRecords() throws SQLException {
        Connection conn = connect();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM burial_records");
    }

    public static void deleteRecordById(int id) throws SQLException {
        String sql = "DELETE FROM burial_records WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

//To fetch a record by ID
public static String[] getRecordById(int id) {
 String sql = "SELECT name, dob, dod FROM burial_records WHERE id = ?";
 try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
     pstmt.setInt(1, id);
     ResultSet rs = pstmt.executeQuery();
     if (rs.next()) {
         return new String[]{rs.getString("name"), rs.getString("dob"), rs.getString("dod")};
     }
 } catch (SQLException e) {
     System.out.println("Get by ID error: " + e.getMessage());
 }
 return null;
}

//To update a record
public static void updateRecord(int id, String name, String dob, String dod) throws SQLException {
 String sql = "UPDATE burial_records SET name = ?, dob = ?, dod = ? WHERE id = ?";
 try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
     pstmt.setString(1, name);
     pstmt.setString(2, dob);
     pstmt.setString(3, dod);
     pstmt.setInt(4, id);
     pstmt.executeUpdate();
 }
}

public static ResultSet searchRecords(String keyword) throws SQLException {
    Connection conn = connect();
    String sql;

    if (keyword == null || keyword.isEmpty()) {
        sql = "SELECT * FROM burial_records";
        return conn.createStatement().executeQuery(sql);
    } else {
        sql = "SELECT * FROM burial_records WHERE LOWER(name) LIKE ? OR LOWER(plot) LIKE ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        String query = "%" + keyword.toLowerCase() + "%";
        pstmt.setString(1, query);
        pstmt.setString(2, query);
        return pstmt.executeQuery();
    }
}
}
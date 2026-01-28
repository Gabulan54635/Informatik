package KundenArtikelBeispiel;

import java.sql.*;

public class DBManager {

    private static final String DB_NAME = "kundenartikel";

    private static final String URL_DB =
            "jdbc:mysql://localhost:3306/" + DB_NAME +
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USER = "root";
    private static final String PASS = "M1necra4t!";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            initDatabase();
        } catch (Exception e) {
            throw new RuntimeException("DB-Initialisierung fehlgeschlagen", e);
        }
    }

    private static void initDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASS);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SHOW DATABASES LIKE '" + DB_NAME + "'");

            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
                System.out.println("Datenbank '" + DB_NAME + "' wurde erstellt.");
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_DB, USER, PASS);
    }
}

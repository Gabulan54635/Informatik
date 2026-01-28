package Basics;

import java.sql.*;

public class BasicsMain {

    private static final String URL = "jdbc:sqlite:test.db";
    private static Connection connection;

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);

            dropTableIfExists();
            Create();
            Insert();
            Select();
            Update();
            Delete();

            System.out.println("\nAlle Operationen erfolgreich abgeschlossen!");
        } catch (SQLException e) {
            System.err.println("Fehler beim Aufbau der Datenbankverbindung: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unerwarteter Fehler im Hauptprogramm: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private static void dropTableIfExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS COMPANY;");
        } catch (NullPointerException e) {
            System.err.println("Fehler: Verbindung ist null!");
        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen der Tabelle: " + e.getMessage());
        }
    }

    private static void Create() {
        String sql = """
                CREATE TABLE IF NOT EXISTS COMPANY (
                    ID INTEGER PRIMARY KEY NOT NULL,
                    NAME TEXT NOT NULL,
                    AGE INTEGER NOT NULL,
                    ADDRESS TEXT,
                    SALARY REAL
                );
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (IllegalArgumentException e) {
            System.err.println("Ungültiges SQL-Argument beim Erstellen der Tabelle!");
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Tabelle: " + e.getMessage());
        }
    }

    private static void Insert() {
        String sql = "INSERT INTO COMPANY (ID, NAME, AGE, ADDRESS, SALARY) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            Object[][] data = {
                    {1, "Paul", 32, "California", 20000.00},
                    {2, "Allen", 25, "Texas", 15000.00},
                    {3, "Teddy", 23, "Norway", 20000.00},
                    {4, "Mark", 25, "Rich-Mond", 65000.00}
            };
            for (Object[] row : data) {
                if (row[1] == null) throw new NullPointerException("Name darf nicht null sein!");
                if ((int) row[2] < 0) throw new IllegalArgumentException("Alter darf nicht negativ sein!");

                pstmt.setInt(1, (int) row[0]);
                pstmt.setString(2, (String) row[1]);
                pstmt.setInt(3, (int) row[2]);
                pstmt.setString(4, (String) row[3]);
                pstmt.setDouble(5, (double) row[4]);
                pstmt.executeUpdate();
            }
        } catch (NullPointerException e) {
            System.err.println("Fehler beim Einfügen: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Ungültige Eingabe beim Einfügen: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL-Fehler beim Einfügen: " + e.getMessage());
        }
    }

    private static void Select() {
        String sql = "SELECT * FROM COMPANY WHERE AGE <= ? OR AGE LIKE ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, 25);
            pstmt.setString(2, "2%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.printf("ID=%d | NAME=%s | AGE=%d | ADDRESS=%s | SALARY=%.2f%n",
                        rs.getInt("ID"), rs.getString("NAME"), rs.getInt("AGE"),
                        rs.getString("ADDRESS"), rs.getDouble("SALARY"));
            }

            rs.close();
        } catch (NullPointerException e) {
            System.err.println("Fehler: Kein gültiges ResultSet beim SELECT.");
        } catch (SQLException e) {
            System.err.println("Fehler bei SELECT: " + e.getMessage());
        }
    }

    private static void Update() {
        String sql = "UPDATE COMPANY SET SALARY = ? WHERE ID = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, 25000.00);
            pstmt.setInt(2, 1);
            pstmt.executeUpdate();
        } catch (IllegalArgumentException e) {
            System.err.println("Ungültiger Parameter beim UPDATE!");
        } catch (SQLException e) {
            System.err.println("Fehler bei UPDATE: " + e.getMessage());
        }
    }

    private static void Delete() {
        String sql = "DELETE FROM COMPANY WHERE ID = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, 2);
            pstmt.executeUpdate();
        } catch (NumberFormatException e) {
            System.err.println("Fehlerhafte ID beim DELETE.");
        } catch (SQLException e) {
            System.err.println("Fehler bei DELETE: " + e.getMessage());
        }
    }

    private static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Verbindung geschlossen.");
            } catch (SQLException e) {
                System.err.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
            }
        }
    }
}

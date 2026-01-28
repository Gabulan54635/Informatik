package ZufallszahlenHü;

import java.sql.*;
import java.util.Scanner;

public class ZufallszahlenMain {
    private static String URL = "jdbc:sqlite:test.db";
    private static Connection connection;
    private static String mode = "all"; 

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Verbindung hergestellt.");

            selectMode();
            createTable();
            clearTable();
            insertWithScanner();
            selectAll();
            sumEvenOddAndCount();
            deleteById();
            sumEvenOddAndCount();

        } catch (SQLException e) {
            System.err.println("Fehler: " + e.getMessage());
            System.exit(1);
        } finally {
            closeConnection();
        }
    }

    private static void selectMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welche Zahlen sollen angezeigt werden? (even/odd/all): ");
        String input = scanner.nextLine().trim().toLowerCase();

        switch (input) {
            case "even", "odd", "all" -> mode = input;
            default -> {
                System.out.println("Ungültige Eingabe, es werden alle Zahlen angezeigt.");
                mode = "all";
            }
        }
        System.out.println("Anzeigemodus gesetzt auf: " + mode);
    }

    private static void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS ZUFALLSZAHLEN (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    value INTEGER,
                    value2 INTEGER
                );
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabelle erstellt oder bereits vorhanden.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Tabelle: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void clearTable() {
        String sqlDelete = "DELETE FROM ZUFALLSZAHLEN";
        String sqlReset = "DELETE FROM sqlite_sequence WHERE name='ZUFALLSZAHLEN'";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlDelete);
            stmt.executeUpdate(sqlReset);
        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen der Tabelle: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void insertWithScanner() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Wie viele Zufallszahlen sollen eingefügt werden? ");
        int n = scanner.nextInt();

        String insertSQL = "INSERT INTO ZUFALLSZAHLEN (value, value2) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            for (int i = 0; i < n; i++) {
                int value = (int) (Math.random() * 10) + 1;
                int value2 = value % 2; 

                pstmt.setInt(1, value);
                pstmt.setInt(2, value2);
                pstmt.executeUpdate();
            }
            System.out.println(n + " Zufallszahlen eingefügt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Einfügen der Zufallszahlen: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void selectAll() {
        String sql = switch (mode) {
            case "even" -> "SELECT id, value, value2 FROM ZUFALLSZAHLEN WHERE value2 = 0";
            case "odd" -> "SELECT id, value, value2 FROM ZUFALLSZAHLEN WHERE value2 = 1";
            default -> "SELECT id, value, value2 FROM ZUFALLSZAHLEN";
        };

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nWerte in der Tabelle (" + mode + "):");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                int id = rs.getInt("id");
                int value = rs.getInt("value");
                int value2 = rs.getInt("value2");
                String evenodd = (value2 % 2 == 0) ? "gerade" : "ungerade";
                System.out.printf("ID: %d | Wert: %d (%s)%n", id, value, evenodd);
            }
            if (!hasRows) {
                System.out.println("Keine Daten im gewählten Modus gefunden.");
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Auswählen: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void sumEvenOddAndCount() {
        String sqlEven = "SELECT SUM(value), COUNT(*) FROM ZUFALLSZAHLEN WHERE value2 = 0";
        String sqlOdd = "SELECT SUM(value), COUNT(*) FROM ZUFALLSZAHLEN WHERE value2 = 1";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rsEven = stmt.executeQuery(sqlEven);
            int sumEven = 0, countEven = 0;
            if (rsEven.next()) {
                sumEven = rsEven.getInt(1);
                countEven = rsEven.getInt(2);
            }

            ResultSet rsOdd = stmt.executeQuery(sqlOdd);
            int sumOdd = 0, countOdd = 0;
            if (rsOdd.next()) {
                sumOdd = rsOdd.getInt(1);
                countOdd = rsOdd.getInt(2);
            }

            System.out.println("\n--- Statistik (" + mode + ") ---");
            switch (mode) {
                case "even" -> {
                    System.out.println("Summe der geraden Werte:   " + sumEven);
                    System.out.println("Anzahl der geraden Werte:  " + countEven);
                }
                case "odd" -> {
                    System.out.println("Summe der ungeraden Werte: " + sumOdd);
                    System.out.println("Anzahl der ungeraden Werte:" + countOdd);
                }
                default -> {
                    System.out.println("Summe der geraden Werte:   " + sumEven);
                    System.out.println("Anzahl der geraden Werte:  " + countEven);
                    System.out.println("Summe der ungeraden Werte: " + sumOdd);
                    System.out.println("Anzahl der ungeraden Werte:" + countOdd);
                }
            }

        } catch (SQLException e) {
            System.err.println("Fehler beim Berechnen der Summen: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void deleteById() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nMöchten Sie einen Datensatz löschen? (j/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (!answer.equals("j")) {
            return;
        }

        System.out.print("Bitte geben Sie die ID des zu löschenden Datensatzes ein: ");
        int id = scanner.nextInt();

        String selectSQL = "SELECT value, value2 FROM ZUFALLSZAHLEN WHERE id = ?";
        String deleteSQL = "DELETE FROM ZUFALLSZAHLEN WHERE id = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSQL);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {

            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int value = rs.getInt("value");
                int value2 = rs.getInt("value2");
                String parity = (value2 == 0) ? "gerade" : "ungerade";

                deleteStmt.setInt(1, id);
                deleteStmt.executeUpdate();

                System.out.printf("Datensatz mit ID %d (Wert: %d, %s) wurde gelöscht.%n", id, value, parity);
            } else {
                System.out.println("Keine Daten mit dieser ID gefunden!");
            }

        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("\nVerbindung geschlossen.");
            } catch (SQLException e) {
                System.err.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
                System.exit(1);
            }
        }
    }
}

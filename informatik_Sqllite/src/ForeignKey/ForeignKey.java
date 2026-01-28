package ForeignKey;

import java.sql.*;

public class ForeignKey {

    public static void main(String[] args) {
        try (Connection conn = connect()) {
            enableForeignKeys(conn);

            dropTables(conn);
            createTablesWithoutCascade(conn);

            insertData(conn);
            select(conn);

            System.out.println("\nAutor löschen OHNE CASCADE:");
            deleteAuthor(conn, 1);   
            select(conn);

            System.out.println("\nTabellen neu erstellen MIT CASCADE");
            dropTables(conn);
            createTablesWithCascade(conn);

            insertData(conn);
            select(conn);

            System.out.println("\nAutor löschen MIT CASCADE:");
            deleteAuthor(conn, 1);   
            select(conn);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:test.db");
    }

    private static void enableForeignKeys(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
    }

    private static void dropTables(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS books;");
            st.execute("DROP TABLE IF EXISTS authors;");
        }
    }

    private static void createTablesWithoutCascade(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE authors (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL
                );
            """);

            st.execute("""
                CREATE TABLE books (
                    id INTEGER PRIMARY KEY,
                    title TEXT NOT NULL,
                    author_id INTEGER,
                    FOREIGN KEY(author_id) REFERENCES authors(id)
                );
            """);

            System.out.println("Tabellen ohne CASCADE erstellt.");
        }
    }

    private static void createTablesWithCascade(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE authors (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL
                );
            """);

            st.execute("""
                CREATE TABLE books (
                    id INTEGER PRIMARY KEY,
                    title TEXT NOT NULL,
                    author_id INTEGER,
                    FOREIGN KEY(author_id) REFERENCES authors(id) ON DELETE CASCADE
                );
            """);

            System.out.println("Tabellen MIT CASCADE erstellt.");
        }
    }

    private static void insertData(Connection conn) throws SQLException {
        try (PreparedStatement ps1 = conn.prepareStatement("INSERT INTO authors VALUES (?, ?)");
             PreparedStatement ps2 = conn.prepareStatement("INSERT INTO books VALUES (?, ?, ?)")) {

            ps1.setInt(1, 1);
            ps1.setString(2, "J. R. R. Tolkien");
            ps1.executeUpdate();

            ps2.setInt(1, 1);
            ps2.setString(2, "Der Hobbit");
            ps2.setInt(3, 1);
            ps2.executeUpdate();

            System.out.println("Beispieldaten eingefügt.");
        }
    }

    private static void deleteAuthor(Connection conn, int id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM authors WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Autor mit ID " + id + " gelöscht.");
        } catch (SQLException e) {
            System.out.println("Löschen fehlgeschlagen: " + e.getMessage());
        }
    }

    private static void select(Connection conn) {
        System.out.println("\n--- Inhalt der Tabellen ---");

        String sqlAuthors = "SELECT id, name FROM authors";
        try (PreparedStatement ps = conn.prepareStatement(sqlAuthors);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Authors:");
            while (rs.next()) {
                System.out.println("  ID: " + rs.getInt("id") +
                                   ", Name: " + rs.getString("name"));
            }

        } catch (SQLException e) {
            System.out.println("Fehler beim Lesen der Authors: " + e.getMessage());
        }

        String sqlBooks = "SELECT id, title, author_id FROM books";
        try (PreparedStatement ps = conn.prepareStatement(sqlBooks);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Books:");
            while (rs.next()) {
                System.out.println("  ID: " + rs.getInt("id") +
                                   ", Titel: " + rs.getString("title") +
                                   ", Author-ID: " + rs.getInt("author_id"));
            }

        } catch (SQLException e) {
            System.out.println("Fehler beim Lesen der Books: " + e.getMessage());
        }
    }
}

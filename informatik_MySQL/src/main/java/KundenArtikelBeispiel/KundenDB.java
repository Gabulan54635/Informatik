package KundenArtikelBeispiel;

import java.sql.*;

public class KundenDB {

    public static void createKundenTabelle() {
        String sql = """
            CREATE TABLE IF NOT EXISTS kunden (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100),
                geburtsdatum DATE
            );
        """;

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertKunde(String name, String email, Date geburtsdatum) {
        if (name == null || name.isBlank()) {
            System.out.println("Name darf nicht leer sein!");
            return;
        }

        String sql = "INSERT INTO kunden(name, email, geburtsdatum) VALUES (?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            if (geburtsdatum != null) ps.setDate(3, geburtsdatum);
            else ps.setNull(3, Types.DATE);

            ps.executeUpdate();
            System.out.println("Kunde erfolgreich gespeichert!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void selectKunden() {
        String sql = "SELECT * FROM kunden";
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Kunden ---");
            while (rs.next()) {
                Date geb = rs.getDate("geburtsdatum");
                String gebStr = (geb != null) ? geb.toString() : "-";
                System.out.printf("%d | %s | %s | Geb.: %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        gebStr);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateKunde(int id, String name, String email, Date geburtsdatum) {
        if (!existsKunde(id)) {
            System.out.println("Kunde mit ID " + id + " existiert nicht!");
            return;
        }

        if (name == null || name.isBlank()) {
            System.out.println("Name darf nicht leer sein!");
            return;
        }

        String sql = "UPDATE kunden SET name=?, email=?, geburtsdatum=? WHERE id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            if (geburtsdatum != null) ps.setDate(3, geburtsdatum);
            else ps.setNull(3, Types.DATE);

            ps.setInt(4, id);
            ps.executeUpdate();
            System.out.println("Kunde aktualisiert!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteKunde(int id) {
        if (!existsKunde(id)) {
            System.out.println("Kunde mit ID " + id + " existiert nicht!");
            return;
        }

        String sql = "DELETE FROM kunden WHERE id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Kunde gelöscht!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean existsKunde(int id) {
        String sql = "SELECT id FROM kunden WHERE id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

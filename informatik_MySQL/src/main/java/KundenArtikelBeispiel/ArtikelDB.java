package KundenArtikelBeispiel;

import java.sql.*;

public class ArtikelDB {

    public static void createArtikelTabelle() {
        String sql = """
            CREATE TABLE IF NOT EXISTS artikel (
                id INT AUTO_INCREMENT PRIMARY KEY,
                bezeichnung VARCHAR(100) NOT NULL,
                preis DECIMAL(10,2) NOT NULL,
                lagerbestand INT NOT NULL,
                mindesthaltbar_bis DATE
            );
        """;

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertArtikel(String bez, double preis, int lager, Date mhd) {
        if (bez == null || bez.isBlank()) {
            System.out.println("Bezeichnung darf nicht leer sein!");
            return;
        }
        if (preis < 0) {
            System.out.println("Preis darf nicht negativ sein!");
            return;
        }
        if (lager < 0) {
            System.out.println("Lagerbestand darf nicht negativ sein!");
            return;
        }

        String sql = "INSERT INTO artikel(bezeichnung, preis, lagerbestand, mindesthaltbar_bis) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bez);
            ps.setDouble(2, preis);
            ps.setInt(3, lager);
            if (mhd != null) ps.setDate(4, mhd);
            else ps.setNull(4, Types.DATE);

            ps.executeUpdate();
            System.out.println("Artikel erfolgreich gespeichert!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void selectArtikel() {
        String sql = "SELECT * FROM artikel";
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Artikel ---");
            while (rs.next()) {
                Date mhd = rs.getDate("mindesthaltbar_bis");
                String mhdStr = (mhd != null) ? mhd.toString() : "-";
                System.out.printf("%d | %s | %.2f € | Lager: %d | MHD: %s%n",
                        rs.getInt("id"),
                        rs.getString("bezeichnung"),
                        rs.getDouble("preis"),
                        rs.getInt("lagerbestand"),
                        mhdStr);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateArtikel(int id, String bez, double preis, int lager, Date mhd) {
        if (!existsArtikel(id)) {
            System.out.println("Artikel mit ID " + id + " existiert nicht!");
            return;
        }
        if (bez == null || bez.isBlank()) {
            System.out.println("Bezeichnung darf nicht leer sein!");
            return;
        }
        if (preis < 0) {
            System.out.println("Preis darf nicht negativ sein!");
            return;
        }
        if (lager < 0) {
            System.out.println("Lagerbestand darf nicht negativ sein!");
            return;
        }

        String sql = "UPDATE artikel SET bezeichnung=?, preis=?, lagerbestand=?, mindesthaltbar_bis=? WHERE id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bez);
            ps.setDouble(2, preis);
            ps.setInt(3, lager);
            if (mhd != null) ps.setDate(4, mhd);
            else ps.setNull(4, Types.DATE);
            ps.setInt(5, id);

            ps.executeUpdate();
            System.out.println("Artikel aktualisiert!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteArtikel(int id) {
        if (!existsArtikel(id)) {
            System.out.println("Artikel mit ID " + id + " existiert nicht!");
            return;
        }

        String sql = "DELETE FROM artikel WHERE id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Artikel gelöscht!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean existsArtikel(int id) {
        String sql = "SELECT id FROM artikel WHERE id=?";
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

    public static boolean reduceLagerbestand(int id, int menge) {
        if (!existsArtikel(id)) return false;

        String sqlCheck = "SELECT lagerbestand FROM artikel WHERE id=?";
        String sqlUpdate = "UPDATE artikel SET lagerbestand=? WHERE id=?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
             PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {

            psCheck.setInt(1, id);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    int aktuell = rs.getInt("lagerbestand");
                    if (menge > aktuell) {
                        System.out.println("Nicht genug Lagerbestand!");
                        return false;
                    }
                    psUpdate.setInt(1, aktuell - menge);
                    psUpdate.setInt(2, id);
                    psUpdate.executeUpdate();
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showLageruebersicht() {
        String sql = "SELECT * FROM artikel";
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Lagerübersicht ---");
            while (rs.next()) {
                System.out.printf("ID: %d | %s | Lager: %d%n",
                        rs.getInt("id"),
                        rs.getString("bezeichnung"),
                        rs.getInt("lagerbestand"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

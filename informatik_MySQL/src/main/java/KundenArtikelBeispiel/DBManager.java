package KundenArtikelBeispiel;

import java.sql.*;

public class BestellungDB {

    public static void createBestelltabelle() {
        String sql = """
            CREATE TABLE IF NOT EXISTS bestellungen (
                id INT AUTO_INCREMENT PRIMARY KEY,
                kundenID INT NOT NULL,
                artikelID INT NOT NULL,
                anzahl INT NOT NULL,
                lieferdatum DATE,
                FOREIGN KEY (kundenID) REFERENCES kunden(id) ON DELETE CASCADE,
                FOREIGN KEY (artikelID) REFERENCES artikel(id) ON DELETE CASCADE
            );
        """;

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertBestellung(int kundenID, int artikelID, int anzahl, Date lieferdatum) {
        if (!KundenDB.existsKunde(kundenID)) {
            System.out.println("Kunde existiert nicht!");
            return;
        }
        if (!ArtikelDB.existsArtikel(artikelID)) {
            System.out.println("Artikel existiert nicht!");
            return;
        }
        if (anzahl <= 0) {
            System.out.println("Anzahl muss größer 0 sein!");
            return;
        }
        if (!ArtikelDB.reduceLagerbestand(artikelID, anzahl)) {
            return; 
        }

        String sql = "INSERT INTO bestellungen(kundenID, artikelID, anzahl, lieferdatum) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, kundenID);
            ps.setInt(2, artikelID);
            ps.setInt(3, anzahl);
            if (lieferdatum != null) ps.setDate(4, lieferdatum);
            else ps.setNull(4, Types.DATE);

            ps.executeUpdate();
            System.out.println("Bestellung gespeichert!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void selectBestellungenKunde(int kundenID) {
        String sql = """
            SELECT b.id, a.bezeichnung, b.anzahl, b.lieferdatum 
            FROM bestellungen b
            INNER JOIN artikel a ON b.artikelID = a.id
            WHERE b.kundenID = ?
        """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, kundenID);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- Bestellungen Kunde " + kundenID + " ---");
                boolean hasRows = false;
                while (rs.next()) {
                    hasRows = true;
                    Date liefer = rs.getDate("lieferdatum");
                    String lieferStr = (liefer != null) ? liefer.toString() : "-";
                    
                    System.out.printf("ID: %d | Artikel: %s | Anzahl: %d | Lieferdatum: %s%n",
                            rs.getInt("id"),
                            rs.getString("bezeichnung"), 
                            rs.getInt("anzahl"),         
                            lieferStr);
                }
                if (!hasRows) System.out.println("Keine Bestellungen gefunden.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteBestellung(int id) {
        String sql = "DELETE FROM bestellungen WHERE id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Bestellung gelöscht!");
            else System.out.println("Keine Bestellung mit dieser ID gefunden.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

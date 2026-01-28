package KundenArtikelBeispiel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;

public class Daten {

    public static void exportKundenJSON(String fileName) {
        JSONArray kundenListe = new JSONArray();
        String sql = "SELECT * FROM kunden";

        try (Connection conn = DBManager.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

        	while (rs.next()) {
                JSONObject kundenObj = new JSONObject();
                kundenObj.put("id", rs.getInt("id"));
                kundenObj.put("name", rs.getString("name"));
                kundenObj.put("email", rs.getString("email"));
                kundenObj.put("geburtsdatum", rs.getDate("geburtsdatum") != null ? rs.getDate("geburtsdatum").toString() : null);
                
                kundenListe.add(kundenObj); 
            }

            try (FileWriter file = new FileWriter(fileName)) {
                file.write(kundenListe.toJSONString());
                System.out.println("Kunden erfolgreich nach " + fileName + " exportiert.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void importKundenJSON(String fileName) {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            Object obj = parser.parse(reader);
            JSONArray kundenListe = (JSONArray) obj;

            for (Object item : kundenListe) {
                JSONObject kundenObj = (JSONObject) item;
                
                String name = (String) kundenObj.get("name");
                String email = (String) kundenObj.get("email");
                String gebStr = (String) kundenObj.get("geburtsdatum");
                Date geb = (gebStr != null) ? Date.valueOf(gebStr) : null;

                KundenDB.insertKunde(name, email, geb);
            }
            System.out.println("Import aus " + fileName + " abgeschlossen.");

        } catch (Exception e) {
            System.out.println("Fehler beim JSON-Import: " + e.getMessage());
        }
    }
}
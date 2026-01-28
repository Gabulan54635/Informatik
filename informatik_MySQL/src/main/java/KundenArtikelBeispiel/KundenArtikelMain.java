package KundenArtikelBeispiel;

import java.sql.Date;
import java.util.Scanner;

public class KundenArtikelMain {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        KundenDB.createKundenTabelle();
        ArtikelDB.createArtikelTabelle();
        BestellungDB.createBestelltabelle();

        while (true) {
            showMenu();
            int wahl = readInt("Auswahl: ");

            switch (wahl) {
            case 1 -> kundeAnlegen();
            case 2 -> artikelAnlegen();
            case 3 -> bestellungAufgeben();
            case 4 -> bestellungenAnzeigen();
            case 5 -> KundenDB.selectKunden();
            case 6 -> ArtikelDB.selectArtikel();
            case 7 -> kundeBearbeiten();
            case 8 -> kundeLoeschen();
            case 9 -> artikelBearbeiten();
            case 10 -> artikelLoeschen();
            case 11 -> ArtikelDB.showLageruebersicht();
            case 12 -> kundenExportierenJSON(); 
            case 13 -> kundenImportierenJSON(); 
            case 0 -> {
                System.out.println("Programm beendet.");
                sc.close();
                return;
            }
            default -> System.out.println("Ungültige Eingabe!");
        }
        }
    }

    private static void showMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1) Kunde anlegen");
        System.out.println("2) Artikel anlegen");
        System.out.println("3) Bestellung aufgeben");
        System.out.println("4) Bestellungen eines Kunden anzeigen");
        System.out.println("5) Alle Kunden anzeigen");
        System.out.println("6) Alle Artikel anzeigen");
        System.out.println("7) Kunde bearbeiten");
        System.out.println("8) Kunde löschen");
        System.out.println("9) Artikel bearbeiten");
        System.out.println("10) Artikel löschen");
        System.out.println("11) Lagerübersicht anzeigen");
        System.out.println("12) Kunden als JSON exportieren");
        System.out.println("13) Kunden aus JSON importieren");
        System.out.println("0) Beenden");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Zahl, bitte erneut eingeben!");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Zahl, bitte erneut eingeben!");
            }
        }
    }

    private static Date readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            if (input.isBlank()) return null;
            try {
                return Date.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ungültiges Datum! Format: YYYY-MM-DD");
            }
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private static void kundeAnlegen() {
        String name = readString("Name: ");
        String email = readString("Email: ");
        Date geb = readDate("Geburtsdatum (YYYY-MM-DD, optional): ");
        KundenDB.insertKunde(name, email, geb);
    }

    private static void artikelAnlegen() {
        String bez = readString("Bezeichnung: ");
        double preis = readDouble("Preis: ");
        int lager = readInt("Lagerbestand: ");
        Date mhd = readDate("Mindesthaltbar bis (YYYY-MM-DD, optional): ");
        ArtikelDB.insertArtikel(bez, preis, lager, mhd);
    }

    private static void bestellungAufgeben() {
        System.out.println("\n--- Kunden ---");
        KundenDB.selectKunden();
        int kundenID = readInt("Kunden-ID: ");

        System.out.println("\n--- Artikel ---");
        ArtikelDB.selectArtikel();
        int artikelID = readInt("Artikel-ID: ");

        int anzahl = readInt("Anzahl: ");
        Date lieferdatum = readDate("Lieferdatum (YYYY-MM-DD, optional): ");

        BestellungDB.insertBestellung(kundenID, artikelID, anzahl, lieferdatum);
    }

    private static void bestellungenAnzeigen() {
        int kundenID = readInt("Kunden-ID: ");
        BestellungDB.selectBestellungenVonKunde(kundenID);
    }

    private static void kundeBearbeiten() {
        KundenDB.selectKunden();
        int id = readInt("Kunden-ID: ");
        String name = readString("Neuer Name: ");
        String email = readString("Neue Email: ");
        Date geb = readDate("Neues Geburtsdatum (YYYY-MM-DD, optional): ");
        KundenDB.updateKunde(id, name, email, geb);
    }

    private static void kundeLoeschen() {
        KundenDB.selectKunden();
        int id = readInt("Kunden-ID löschen: ");
        KundenDB.deleteKunde(id);
    }

    private static void artikelBearbeiten() {
        ArtikelDB.selectArtikel();
        int id = readInt("Artikel-ID: ");
        String bez = readString("Neue Bezeichnung: ");
        double preis = readDouble("Neuer Preis: ");
        int lager = readInt("Neuer Lagerbestand: ");
        Date mhd = readDate("Neues Mindesthaltbarkeitsdatum (YYYY-MM-DD, optional): ");
        ArtikelDB.updateArtikel(id, bez, preis, lager, mhd);
    }

    private static void artikelLoeschen() {
        ArtikelDB.selectArtikel();
        int id = readInt("Artikel-ID löschen: ");
        ArtikelDB.deleteArtikel(id);
    }
    

    private static void kundenExportierenJSON() {
        String datei = readString("Dateiname für JSON-Export (z.B. kunden.json): ");
        Daten.exportKundenJSON(datei);
    }

    private static void kundenImportierenJSON() {
        String datei = readString("Dateiname für JSON-Import (z.B. kunden.json): ");
        Daten.importKundenJSON(datei);
    }
}

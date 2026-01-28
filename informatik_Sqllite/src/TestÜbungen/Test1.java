package TestÜbungen;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public class Test1 {
	public Connection connect() {
		Connection conn= null;
		try {
			conn = DriverManager.getConnection(null);
		} catch (SQLException e){
			System.exit(1);
		}
		
		return conn;
		
	}

	
	public void createTable(String Tabellenname) {
		String sql = "CREATE TABEL IF NOT EXISTS" + Tabellenname + "("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT"
				+ "name TEXT NOT NULL"
				+ "gehalt REAL NOT NULL"
				+ ");";
		try (Connection conn = connect(); Statement stmt = conn.createStatement()){
			stmt.execute(sql);
		} catch (SQLException e) {
			System.exit(1);
		}
	}

	public void mitarbeiterEinfügen (String name, double gehalt) {
		String sql = "INSERT INTO Mitarbeiter(name, gehalt) VALUES (?,?)";
		
		try (Connection conn = connect(); PreparedStatement  pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, name);
			pstmt.setDouble(2,  gehalt);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.exit(1);
		}
	}
	
	public void Main (String[] Args) {
		createTable("Mitarbeiter");
		
		mitarbeiterEinfügen("Müller", 55000);
		mitarbeiterEinfügen("Müller", 55000);
		mitarbeiterEinfügen("Müller", 55000);
	}
}


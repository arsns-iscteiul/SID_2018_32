package main;

import java.sql.*;

public class GUIBackend {

	private String databaseURL = "jdbc:mysql://localhost:3307/main";
	private String username;
	private String password;
	private Connection connection;
	private DatabaseMetaData content;

	public GUIBackend() {

		// Root or other user that has permissions to check credentials
		// username = "root";
		// password = "teste123";

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: Unable to load SQLServer JDBC Driver");
			e.printStackTrace();
			return;
		}

		// Connect to the main DB
//		System.out.println("Trying to get a connection to the database...");
//		try {
//			connection = DriverManager.getConnection(databaseURL, username, password);
//		} catch (SQLException e) {
//			System.out.println("ERROR: Unable to establish a connection with the database!");
//			e.printStackTrace();
//			return;
//		}

	}

	public ResultSet login(String username, String password) throws SQLException { // SQLException pq ERROR: Unable to
																					// establish a connection with the
																					// database! - Check
																					// Credentials/Internet Connection

		this.username = username;
		this.password = password;

		System.out.println("Trying to get a connection to the database...");
		connection = DriverManager.getConnection(databaseURL, username, password);
		content = connection.getMetaData();
		System.out.println("Connection established\n Welcome " + username);
		return content.getIndexInfo(null, null, "cultura", false, true);

	}

	public void logout() throws SQLException {
		connection.close();
		username = "";
		password = "";
	}

	public ResultSet getAllTables() throws SQLException {
		return content.getTables(null, null, "", null);
	}

	public ResultSet getTable(String tableName) throws SQLException {
		return content.getIndexInfo(null, null, tableName, false, true);
	}

	public void addRegisterToTable(String table, String fields[]) { // Fields necessita de estar ordenado pelos campos
																	// da
		// tabela respetiva
		
		try {
			ResultSet columns = content.getColumns(null, null, table, "");

			String query = "insert into " + table + " (";
			String query2 = " values (";

			for (int i = 0; i != fields.length-1; i++) {
				query = query + columns.getString(i+1) + ", ";
				query2 = query2 + fields[i] + ", ";
			}
			query = query + columns.getString(fields.length) + ")";
			query2 = query2 + fields[fields.length-1] + ")";
			query = query + query2;
			
			System.out.println(query);
			
			Statement statement =connection.createStatement();
			statement.executeQuery(query);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	
	public static void main(String args[]) throws InterruptedException, SQLException {
		GUIBackend b = new GUIBackend();
		String s[] = {"26", "Cultura-39", "Teste2", "6", "1"};
		b.login("root", "1221");
		Thread.currentThread().sleep(5000);
		b.addRegisterToTable("cutura", s);
		
	}

}

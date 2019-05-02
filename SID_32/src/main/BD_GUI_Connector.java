package main;

import java.sql.*;
import java.util.LinkedList;

public class BD_GUI_Connector {

	private String databaseURL = "jdbc:mysql://localhost:3307/main";
	private String username;
	private String password;
	private Connection connection;

	public BD_GUI_Connector() {

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

	public void login(String username, String password) throws SQLException { // SQLException pq ERROR: Unable to
																				// establish a connection with the
																				// database! - Check
																				// Credentials/Internet Connection

		this.username = username;
		this.password = password;

		System.out.println("Trying to get a connection to the database...");
		connection = DriverManager.getConnection(databaseURL, username, password);
		System.out.println("Connection established\n Welcome " + username);

	}

	public void logout() throws SQLException {
		connection.close();
		username = "";
		password = "";
	}

	public LinkedList<String> getAllTables() throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		ResultSet s = meta.getTables(null, null, "", null);
		LinkedList<String> tableNames = new LinkedList<>();
		while(s.next()) {
			tableNames.add(s.getString(3));
		}
		return tableNames;
		
	}

	public LinkedList<String> getTable(String tableName) throws SQLException {
		String query = "SELECT * FROM " + tableName;
		Statement s = connection.createStatement();
		ResultSet rs =  s.executeQuery(query);
		LinkedList<String> list = new LinkedList<String>();
		while(rs.next()){
			list.add(rs.getObject(2).toString());
		}
		return list;
		
	}

	public void addRegisterToTable(String table, String fields[]) { // Fields necessita de estar ordenado pelos campos
																	// da tabela respetiva
		try {
			
			ResultSetMetaData meta = connection.createStatement().executeQuery("SELECT * FROM " + table).getMetaData();
			
			
			String query = "INSERT INTO " + table + " (";
			String query2 = " VALUES (";
			
			for (int i = 0; i != fields.length - 1; i++) {
				query = query + meta.getColumnName(i+1) + ", ";
				query2 = query2 + fields[i] + ", ";
			}
			
			
			query = query + meta.getColumnName(fields.length) + ")";
			query2 = query2 + fields[fields.length - 1] + ")";
			query = query + query2;
			System.out.println(query);
			connection.createStatement().executeUpdate(query);
			

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
		BD_GUI_Connector b = new BD_GUI_Connector();
		String s[] = { "27", "\"iudsa\"", "\"Teste2\"", "6", "1" };
		b.login("root", "1221");
		Thread.currentThread().sleep(2000);
		LinkedList<String> list = b.getTable("cultura");
		for(int i =0; i!= list.size(); i++) {
			System.out.println(list.get(i));
		}
		//b.addRegisterToTable("cultura", s);

	}

}

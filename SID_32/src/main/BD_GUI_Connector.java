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
		ResultSet resultSet = connection.getMetaData().getTables(null, null, "", null);
		LinkedList<String> tableNames = new LinkedList<>();
		while (resultSet.next()) {
			tableNames.add(resultSet.getString(3));
		}
		return tableNames;

	}

	public LinkedList<String> getTableContentNames(String tableName) throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM " + tableName);
		LinkedList<String> list = new LinkedList<String>();
		while (resultSet.next()) {
			list.add(resultSet.getString(1));
		}
		return list;
	}

	public void addRegisterToTable(String table, String fields[], String operation) throws SQLException {
		// Fields need to be ordered by the columns of a given table
		// The exception is thrown when some1 who isn't allowed to add data tries to

		String sp = "EXEC <" + table + "[" + operation + "]> ";
		for(int i = 0; i!= fields.length-1; i++) {
			sp += "?,";
		}
		sp += "?";
		
		PreparedStatement sqlString = connection.prepareStatement(sp);
		sqlString.setEscapeProcessing(true);
		setParam(sqlString.getParameterMetaData(), sqlString, fields);

	}

	private void setParam(ParameterMetaData paramMeta, PreparedStatement sqlString, String fields[])
			throws SQLException {

		int paramSize = paramMeta.getParameterCount();

		for (int i = 0; i != paramSize; i++) {
			String type = paramMeta.getParameterTypeName(i);

			switch (type) {
			case "VARCHAR":
				sqlString.setString(i + 1, fields[i]);
				break;
			// Case for each types. Although we only use string and integer.
			default:
				sqlString.setInt(i + 1, Integer.parseInt(fields[i]));
			}
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
//		String s[] = { "iudsa", "Teste2", "6", "1" };
//		String s[] = {"oasdoajdia", "jasid@algo.pt", "Cat-0", "pwd"};
		b.login("root", "1221");
		Thread.currentThread().sleep(1000);
//		LinkedList<String> list = b.getTableContentNames("tipo_cultura");
//		for (int i = 0; i != list.size(); i++) {
//			System.out.println(list.get(i));
//		}
//		b.addRegisterToTable("investigador", s , "INSERT");
	}

}

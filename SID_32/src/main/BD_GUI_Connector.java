package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class BD_GUI_Connector {

	private final static String DATABASEURL = "jdbc:mysql://localhost:3307/main";
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
//			connection = DriverManager.getConnection(DATABASEURL, username, password);
//		} catch (SQLException e) {
//			System.out.println("ERROR: Unable to establish a connection with the database!");
//			e.printStackTrace();
//			return;
//		}

	}

	public void login(String username, String password) throws SQLException { // SQLException ERROR: Unable to
																				// establish a connection with the
																				// database! - Check
																				// Credentials/Internet Connection

		this.username = username;
		this.password = password;

		System.out.println("Trying to get a connection to the database...");
		connection = DriverManager.getConnection(DATABASEURL, username, password);
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

	public LinkedList<String> getTableColumn(String tableName, String columnName) throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT " + columnName + " FROM " + tableName);
		return (LinkedList<String>) convertResultSetColumnToALinkedList(resultSet, 1);
	}

	private LinkedList<?> convertResultSetColumnToALinkedList(ResultSet resultSet, int column) {

		LinkedList<String> list = new LinkedList<String>();
		try {

			while (resultSet.next()) {
				list.add(resultSet.getString(column));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;

	}

	public void changeContentOfATable(String table, String fields[], String operation) throws SQLException {
		// Fields need to be ordered by the columns of a given table
		// The exception is thrown when some1 who isn't allowed to add data tries to
		// SP needs to be the name of the table + [INSERT/UPDATE/DELETE]

		String sp = "EXEC <" + table + "[" + operation + "]> ";
		for (int i = 0; i != fields.length - 1; i++) {
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

}

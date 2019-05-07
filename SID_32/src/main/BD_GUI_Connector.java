package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * This class is the connector between the Database and the GUI.
 * 
 * @author Rúben Silva
 *
 */
public class BD_GUI_Connector {

	private final static String DATABASEURL = "jdbc:mysql://localhost:3307/main";
	private String username;
	private String password;
	private Connection connection;

	/**
	 * Constructor of the BD_GUI_Connector. - Loads SQLServer JDBC Driver
	 */
	public BD_GUI_Connector() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: Unable to load SQLServer JDBC Driver");
			e.printStackTrace();
			return;
		}

	}

	/**
	 * This function logins a user with the username and password. It can be an
	 * admin, investigator, root, etc.
	 * 
	 * @param username - username of the user
	 * @param password - password of the user
	 * @throws SQLException - this exception is thrown in case of no internet
	 *                      connection or bad login (wrong credentials)
	 */
	public void login(String username, String password) throws SQLException {

		this.username = username;
		this.password = password;

		connection = DriverManager.getConnection(DATABASEURL, username, password);

	}

	/**
	 * This function logs out a user and closes the connection that had been
	 * established.
	 * 
	 * @throws SQLException - if a user can't access the database.
	 */
	public void logout() throws SQLException {
		connection.close();
		username = "";
		password = "";
	}

	/**
	 * This function returns the name of all the tables that the logged user has
	 * access to.
	 * 
	 * @return a list containing all the tables of the user
	 * @throws SQLException - if a user can't access the database.
	 */
	public LinkedList<String> getAllTables() throws SQLException {
		ResultSet resultSet = connection.getMetaData().getTables(null, null, "", null);
		LinkedList<String> tableNames = new LinkedList<>();
		while (resultSet.next()) {
			tableNames.add(resultSet.getString(3));
		}
		return tableNames;

	}

	/**
	 * This function returns the rows of a given table and column
	 * 
	 * @param tableName  - the table name in mysql
	 * @param columnName - the column name in mysql
	 * @return list of the rows of a given table and column
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<String> getTableColumn(String tableName, String columnName) throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT " + columnName + " FROM " + tableName);
		return (LinkedList<String>) convertResultSetColumnToALinkedList(resultSet, 1);
	}

	/**
	 * This function transforms the rows of a given column and table, into a list.
	 * 
	 * @param resultSet - resultSet from the package java.sql, that has the
	 *                  information wanted to migrate to the list
	 * @param column    - the number of the column you want to migrate data
	 * @return a list that contains the rows of a given column and table
	 */
	private LinkedList<String> convertResultSetColumnToALinkedList(ResultSet resultSet, int column) {

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

	/**
	 * This function returns all the content of a given table
	 * 
	 * @param tableName - the name of the table in the db
	 * @return an array that each position is a column. Each position of the array
	 *         is a linkedlist<String>
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<String>[] allTableData(String tableName) throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM " + tableName);
		@SuppressWarnings("unchecked")
		LinkedList<String>[] table = (LinkedList<String>[]) new LinkedList<?>[resultSet.getMetaData().getColumnCount()];

		for (int i = 0; i != table.length; i++, resultSet.beforeFirst()) {
			table[i] = (LinkedList<String>) convertResultSetColumnToALinkedList(resultSet, i + 1);
		}

		return table;

	}

	/**
	 * This function allows you to execute stored procedures. These SPs are used to
	 * INSERT information, DELETE information or UPDATE information. The DB must
	 * have the SPs necessary to do so. These procedures have a certain format.
	 * (table[INSERT] or table[DELETE], ...)
	 * 
	 * @param table     - the name of a table in the db
	 * @param fields    - arguments of the SP that's being executed. These arguments
	 *                  need to be ordered. DON'T INCLUDE AUTO_INCREMENT FIELDS.
	 * @param operation - This operation is an enum that can only be "INSERT, DELETE
	 *                  or UPDATE"
	 * @throws SQLException - If a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void changeContentOfATable(String table, String fields[], String operation) throws SQLException {

		String sp = "EXEC <" + table + "[" + operation + "]> ";
		for (int i = 0; i != fields.length - 1; i++) {
			sp += "?,";
		}
		sp += "?";

		PreparedStatement sqlString = connection.prepareStatement(sp);
		sqlString.setEscapeProcessing(true);
		setParam(sqlString.getParameterMetaData(), sqlString, fields);

	}

	/**
	 * This function is used to set the parameters of a stored procedure.
	 * 
	 * @param paramMeta - MetaData of the parameters of a stored procedure.
	 * @param sqlString - the prepareStatement of the sp (example: EXEC
	 *                  <cultura[INSERT]>?,?,?)
	 * @param fields    - the arguments of the SP that is being executed. These
	 *                  arguments need to be ordered. DON'T INCLUDE AUTO_INCREMENT
	 *                  FIELDS.
	 * @throws SQLException - If a database access error occurs
	 */
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

	/**
	 * Getter of the users username
	 * 
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Getter of the users password
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}
	
}

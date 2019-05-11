package application.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import application.connector.objects.Cultura;
import application.connector.objects.Medicao;
import application.connector.objects.Variavel;
import application.connector.objects.VariavelMedida;

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
	 * This function returns all the content of the "Cultura" table
	 * 
	 * @return a List that each position is an object (Cultura).
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Cultura> getCulturaTable() throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Cultura");
		return createCultura(resultSet);
	}

	/**
	 * This function returns all the content of the "Variavel" table
	 * 
	 * @return a List that each position is an object (Variavel).
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Variavel> getVariavelTable() throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM variavel");
		return createVariavel(resultSet);
	}

	/**
	 * This function returns all the content of the "VariavelMedida" table
	 * 
	 * @return a List that each position is an object (VariavelMedida).
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<VariavelMedida> getVariavelMedidaTable() throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM variavel_medida");
		return createVariavelMedida(resultSet);
	}

	/**
	 * This function returns all the content of the "Medicao" table
	 * 
	 * @return a List that each position is an object (Medicao).
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Medicao> getMedicaoTable() throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM medicao");
		return createMedicoes(resultSet);
	}

	/**
	 * This function returns all the content of the "Variavel" table of a certain
	 * cultura
	 * 
	 * @param idCultura - id of a cultura u want to display its variables
	 * @return a List that each position is an object (Variavel).
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Variavel> getVariaveisCultura(int idCultura) throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery(
				"SELECT variavel.Id_Variavel, variavel.Nome_Variavel FROM variavel, variavel_medida WHERE variavel.Id_Variavel = variavel_medida.variavel_fk AND variavel_medida.cultura_fk ="
						+ idCultura);
		return createVariavel(resultSet);

	}

	/**
	 * This function returns all the content of the "Medicao" table
	 * 
	 * @param idCultura - id of a cultura u want to display its medições
	 * @return a List that each position is an object (Medicao).
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Medicao> getMedicoesCultura(int idCultura) throws SQLException {

		PreparedStatement sqlString = connection.prepareStatement("{call MedicaoSELECT(?)}");
		sqlString.setEscapeProcessing(true);
		sqlString.setInt(1, idCultura);
		ResultSet resultSet = sqlString.executeQuery();

		return createMedicoes(resultSet);
	}

	public void insertVariavelMedida(String fields[]) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call VariavelMedidaINSERT(?,?,?,?)}");
		ps.setEscapeProcessing(true);
		ps.setInt(1, Integer.parseInt(fields[0]));
		ps.setInt(2, Integer.parseInt(fields[1]));
		ps.setInt(3, Integer.parseInt(fields[2]));
		ps.setInt(4, Integer.parseInt(fields[3]));
		ps.executeQuery();
	}

	public void insertVariavel(String fields[]) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call VariavelINSERT(?)}");
		ps.setEscapeProcessing(true);
		ps.setString(1, fields[0]);
		ps.executeQuery();
	}

	public void insertMedicao(String fields[]) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call MedicaoINSERT(?,?,?)}");
		ps.setEscapeProcessing(true);
		ps.setString(1, fields[0]);
		ps.setInt(2, Integer.parseInt(fields[1]));
		ps.setString(3, fields[2]);
		ps.executeUpdate();
	}

	public void insertCultura(String fields[]) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call culturaINSERT(?,?,?,?)}");
		ps.setEscapeProcessing(true);
		ps.setString(1, fields[0]);
		ps.setString(2, fields[1]);
		ps.setInt(3, Integer.parseInt(fields[2]));
		ps.setInt(4, Integer.parseInt(fields[3]));
		ps.executeUpdate();
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
	 * Function that creates medicao objects from a resultset and places in a
	 * linkedlist<Medicao>.
	 * 
	 * @param resultSet each row correspondes with an object Medicao.
	 * @return linkedlist of objects medicao.
	 * @throws SQLException - If a database access error occurs-
	 */
	private LinkedList<Medicao> createMedicoes(ResultSet resultSet) throws SQLException {
		LinkedList<Medicao> list = new LinkedList<Medicao>();
		while (resultSet.next()) {
			list.add(new Medicao(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
					resultSet.getString(4)));
		}
		return list;
	}

	/**
	 * Function that creates Variavel objects from a resultset and places in a
	 * linkedlist<Variavel>.
	 * 
	 * @param resultSet each row correspondes with an object Variavel.
	 * @return linkedlist of objects Variavel.
	 * @throws SQLException - If a database access error occurs-
	 */
	private LinkedList<Variavel> createVariavel(ResultSet resultSet) throws SQLException {

		LinkedList<Variavel> list = new LinkedList<Variavel>();
		while (resultSet.next()) {
			list.add(new Variavel(resultSet.getString(1), resultSet.getString(2)));
		}
		return list;
	}

	/**
	 * Function that creates VariavelMedida objects from a resultset and places in a
	 * linkedlist<VariavelMedida>.
	 * 
	 * @param resultSet each row correspondes with an object VariavelMedida.
	 * @return linkedlist of objects VariavelMedida.
	 * @throws SQLException - If a database access error occurs.
	 */
	private LinkedList<VariavelMedida> createVariavelMedida(ResultSet resultSet) throws SQLException {
		LinkedList<VariavelMedida> table = new LinkedList<VariavelMedida>();
		while (resultSet.next()) {
			table.add(new VariavelMedida(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
					resultSet.getString(4), resultSet.getString(5)));
		}
		return table;
	}

	/**
	 * Function that creates Cultura objects from a resultset and places in a
	 * linkedlist<Cultura>.
	 * 
	 * @param resultSet each row correspondes with an object Cultura.
	 * @return linkedlist of objects Cultura.
	 * @throws SQLException - If a database access error occurs-
	 */
	private LinkedList<Cultura> createCultura(ResultSet resultSet) throws SQLException {
		LinkedList<Cultura> list = new LinkedList<Cultura>();
		while (resultSet.next()) {
			list.add(new Cultura(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
					resultSet.getString(4), resultSet.getString(5)));
		}
		return list;
	}

	/**
	 * This function returns all Medicao objects in database which are being
	 * measured by a Cultura, given it's id, separated by variable.
	 * 
	 * @param idCultura - id of a cultura u want to display its medições
	 * @return Medicao objects in database which are being measured by a Cultura,
	 *         given it's id, separated by variable
	 * @throws SQLException - if a user doesn't have permissions to execute the
	 *                      select query
	 */
	public LinkedList<LinkedList<Medicao>> getMedicoesCulturaByVariavel(int idCultura) throws SQLException {

		LinkedList<Variavel> variavelList = getVariaveisCultura(idCultura);
		LinkedList<LinkedList<Medicao>> list = new LinkedList<LinkedList<Medicao>>();
		for (int i = 0; i != variavelList.size(); i++) {
			ResultSet resultSet = connection.createStatement().executeQuery(
					"SELECT Medicao.Id_Medicao, Medicao.Data_Hora_Medicao, Medicao.Valor_Medicao, Medicao.Variavel_medida_fk FROM Medicao, variavel_medida, Variavel WHERE variavel_medida.cultura_fk="
							+ idCultura + "AND Variavel.Id_Variavel =" + variavelList.get(i).getId_variavel());

			while (resultSet.next()) {
				list.add(i, createMedicoes(resultSet));
			}
			resultSet.beforeFirst();
		}
		return null;
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

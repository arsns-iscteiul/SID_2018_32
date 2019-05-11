package application.connector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
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
					"SELECT Medicao.Id_Medicao, Medicao.Data_Hora_Medicao, Medicao.Valor_Medicao, Medicao.Variavel_medida_fk FROM medicao, variavel_medida WHERE medicao.Variavel_medida_fk = variavel_medida.Variavel_medida_ID AND variavel_medida.variavel_fk = "
							+ variavelList.get(i).getId_variavel() + " AND variavel_medida.cultura_fk = " + idCultura);

			while (resultSet.next()) {
				list.add(createMedicoes(resultSet));
			}
			resultSet.beforeFirst();
		}
		return list;
	}

	/**
	 * This Method creates and inserts a new VariavelMedida in the database
	 * 
	 * @param upperLimit - upperlimit of the VariavelMedida
	 * @param lowerLimit - lowerlimit of the VariavelMedida
	 * @param idVariavel - id of the Variavel you are measuring
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void insertVariavelMedida(int upperLimit, int lowerLimit, int idVariavel) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call VariavelMedidaINSERT(?,?,?)}");
		ps.setEscapeProcessing(true);
		ps.setInt(1, upperLimit);
		ps.setInt(2, lowerLimit);
		ps.setInt(3, idVariavel);
		ps.executeUpdate();
	}

	/**
	 * This Method creates and inserts a new Variavel in the database
	 * 
	 * @param name - name of the new variavel
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void insertVariavel(String name) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call VariavelINSERT(?)}");
		ps.setEscapeProcessing(true);
		ps.setString(1, name);
		ps.executeUpdate();
	}

	/**
	 * This Method creates and inserts a new Medicao in the database
	 * @param value - value of the new medicao you want to insert
	 * @param variavelMedidaFK - foreign key of the variavelMedida
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void insertMedicao(int value, int variavelMedidaFK) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call MedicaoINSERT(?,?,?)}");
		ps.setEscapeProcessing(true);
		ps.setInt(1, value);
		ps.setInt(2,variavelMedidaFK);
		ps.setTimestamp(3, new Timestamp(Calendar.getInstance().getTimeInMillis()));
		;
		ps.executeUpdate();
	}

	/**
	 * This Method creates and inserts a new Cultura in the database
	 * 
	 * @param nomeCultura     - Name of the cultura u want to insert
	 * @param description     - description of the cultura
	 * @param type            - type of the cultura
	 * @param id_investigador - investigadors id
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */

	public void insertCultura(String culturaName, String description, int type, int id_investigador)
			throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call culturaINSERT(?,?,?,?)}");
		ps.setEscapeProcessing(true);
		ps.setString(1, culturaName);
		ps.setString(2, description);
		ps.setInt(3, type);
		ps.setInt(4, id_investigador);
		ps.executeUpdate();
	}

	public void insertInvestigador(String nome, String email, String categoria, String pwd) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call investigadorINSERT(?,?,?,?)}");
		ps.setString(1, nome);
		ps.setString(2, email);
		ps.setString(3, categoria);
		ps.setString(4, pwd);
		ps.executeUpdate();
	}

	/**
	 * This method deletes a VariavelMedida from the database base on its ID.
	 * 
	 * @param idVariavelMedida - id of the VariavelMedida you want to delete.
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void deleteVariavelMedida(int idVariavelMedida) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call VariavelMedidaDELETE(?)}");
		ps.setInt(1, idVariavelMedida);
		ps.executeUpdate();
	}

	/**
	 * This method deletes a Variavel from the database base on its ID.
	 * 
	 * @param idVariavel - id of the Variavel you want to delete.
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void deleteVariavel(int idVariavel) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call VariavelDELETE(?)}");
		ps.setInt(1, idVariavel);
		ps.executeUpdate();

	}

	/**
	 * This method deletes a Medicao from the database base on its ID.
	 * 
	 * @param idMedicao - id of the Medicao you want to delete.
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void deleteMedicao(int idMedicao) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call MedicaoDELETE(?)}");
		ps.setInt(1, idMedicao);
		ps.executeUpdate();

	}

	/**
	 * This method deletes a Cultura from the database base on its ID.
	 * 
	 * @param idCultura - id of the Cultura you want to delete.
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void deleteCultura(int idCultura) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call CulturaDELETE(?)}");
		ps.setInt(1, idCultura);
		ps.executeUpdate();
	}

	public void deleteInvestigador(int idInvestigador) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call InvestigadorDELETE(?)}");
		ps.setInt(1, idInvestigador);
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

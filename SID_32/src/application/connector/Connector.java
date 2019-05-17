package application.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;

import application.connector.objects.AlertaSensor;
import application.connector.objects.AlertaVariavel;
import application.connector.objects.Cultura;
import application.connector.objects.Investigador;
import application.connector.objects.Log;
import application.connector.objects.Medicao;
import application.connector.objects.MedicaoLuminosidade;
import application.connector.objects.MedicaoTemperatura;
import application.connector.objects.Variavel;
import application.connector.objects.VariavelMedida;

/**
 * This class is the connector between the Database and the GUI.
 * 
 * @author Rúben Silva
 *
 */
public class Connector {

	private final static String MAIN_DATABASE_URL = "jdbc:mysql://localhost:3307/main";
	private final static String AUDITOR_DATABASE_URL = "jdbc:mysql://localhost:3307/auditor";
	private String username;
	private String password;
	private Connection connection;

	/**
	 * Constructor of the BD_GUI_Connector. - Loads SQLServer JDBC Driver
	 */
	public Connector() {

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
	public String login(String database, String username, String password) throws SQLException {

		this.username = username + "@localhost";
		this.password = password;

		if (database.equals("auditor")) {
			connection = DriverManager.getConnection(AUDITOR_DATABASE_URL, "root", "");
		} else if (database.equals("main")) {
			connection = DriverManager.getConnection(MAIN_DATABASE_URL, "root", "");
		}

		if (username.equals("auditor") && password.equals("auditor")) {
			return "auditor";
		} else if (username.equals("admin") && password.equals("admin")) {
			return "admin";
		}

		for (Investigador investigador : getInvestigadorTable()) {
			if (investigador.getEmail_investigador().equals(this.username) && investigador.getPwd().equals(password)) {
				return investigador.getId_investigador();
			}
		}
		return null;
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
	 * This function returns all the content of the "Investigador" table
	 * 
	 * @return a List that each position is an object (Investigador).
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Investigador> getInvestigadorTable() throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Investigador");
		return createInvestigador(resultSet);
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
	 * This function returns all the content of the "Variavel" table of a certain
	 * cultura
	 * 
	 * @param idCultura - id of a cultura u want to display its variables
	 * @return a List that each position is an object (Variavel).
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Cultura> getCulturasInvestigador(int idInvestigador) throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery(
				"SELECT cultura.Id_Cultura, cultura.Nome_Cultura, cultura.Descricao_cultura, cultura.Tipo_Cultura, cultura.investigador_fk FROM investigador, cultura WHERE cultura.investigador_fk = investigador.Id_Investigador AND investigador.Id_Investigador = "
						+ idInvestigador);
		return createCultura(resultSet);

	}

	public LinkedList<MedicaoTemperatura> getMedicoesTemperatura() throws SQLException {
		LinkedList<String>[] medicoes_temperatura = allTableData("medicao_temperatura");
		LinkedList<MedicaoTemperatura> list = new LinkedList<>();
		for (int i = 0; i != medicoes_temperatura[0].size(); i++) {
			list.add(new MedicaoTemperatura(medicoes_temperatura[0].get(i), medicoes_temperatura[1].get(i),
					medicoes_temperatura[2].get(i)));
		}
		return list;
	}

	public LinkedList<MedicaoLuminosidade> getMedicoesLuminosidade() throws SQLException {
		LinkedList<String>[] medicoes_luminosidade = allTableData("medicao_luminosidade");
		LinkedList<MedicaoLuminosidade> list = new LinkedList<>();
		for (int i = 0; i != medicoes_luminosidade[0].size(); i++) {
			list.add(new MedicaoLuminosidade(medicoes_luminosidade[0].get(i), medicoes_luminosidade[1].get(i),
					medicoes_luminosidade[2].get(i)));
		}
		return list;
	}

	/**
	 * This function returns all Medicao objects in database which are being
	 * measured by a Cultura, given it's id
	 *
	 * @return list of Medicao objects
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Medicao> getMedicoesDaCultura(String cultura_id) throws SQLException {
		LinkedList<Medicao> list = new LinkedList<>();
		LinkedList<String> variaveis_medidas_id = new LinkedList<>();
		for (VariavelMedida vm : getVariaveisMedidas()) {
			if (vm.getCultura_fk().equalsIgnoreCase(cultura_id)) {
				variaveis_medidas_id.add(vm.getVariavel_fk());
			}
		}
		for (Medicao m : getMedicoes()) {
			if (variaveis_medidas_id.contains(m.getVariavel_medida_fk())) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * This function returns all VariavelMedida objects in database on table
	 * "variavel_medida"
	 *
	 * @return list of the VariavelMedida objects
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<VariavelMedida> getVariaveisMedidas() throws SQLException {
		LinkedList<String>[] variaveis_medidas = allTableData("variavel_medida");
		LinkedList<VariavelMedida> list = new LinkedList<>();
		for (int i = 0; i != variaveis_medidas[0].size(); i++) {
			String variavel_medida_id = variaveis_medidas[0].get(i);
			String cultura_fk = variaveis_medidas[1].get(i);
			String variavel_fk = variaveis_medidas[2].get(i);
			String limite_superior = variaveis_medidas[3].get(i);
			String limite_inferior = variaveis_medidas[4].get(i);
			String limiteInferiorLaranja = variaveis_medidas[5].get(i);
			String limiteInferiorVermelho = variaveis_medidas[6].get(i);
			String limiteSuperiorLaranja = variaveis_medidas[7].get(i);
			String limiteSuperiorVermelho = variaveis_medidas[8].get(i);
			list.add(new VariavelMedida(variavel_medida_id, cultura_fk, variavel_fk, limite_superior, limite_inferior,
					limiteInferiorLaranja, limiteInferiorVermelho, limiteSuperiorLaranja, limiteSuperiorVermelho));
		}
		return list;
	}

	/**
	 * This function returns all Medicao objects
	 *
	 * @return list of the Medicao objects in database on table "medicao"
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Medicao> getMedicoes() throws SQLException {
		LinkedList<String>[] variaveis = allTableData("medicao");
		LinkedList<Medicao> list = new LinkedList<>();
		for (int i = 0; i != variaveis[0].size(); i++) {
			String id_medicao = variaveis[0].get(i);
			String data_hora_medicao = variaveis[1].get(i);
			String valor_medicao = variaveis[2].get(i);
			String variavel_medida_fk = variaveis[3].get(i);
			list.add(new Medicao(id_medicao, data_hora_medicao, valor_medicao, variavel_medida_fk));
		}
		return list;
	}

	/**
	 * This function returns all Medicao objects in database which are being
	 * measured by a Cultura, given it's id, separated by variable
	 *
	 * @return array of lists of Medicao objects separated by variable
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<LinkedList<Medicao>> getMedicoesDaCulturaByVariable(String cultura_id) throws SQLException {
		LinkedList<LinkedList<Medicao>> list = new LinkedList<LinkedList<Medicao>>();
		LinkedList<String> variaveis_medidas_ids = new LinkedList<>();
		for (VariavelMedida vm : getVariaveisMedidas()) {
			if (vm.getCultura_fk().equalsIgnoreCase(cultura_id)) {
				variaveis_medidas_ids.add(vm.getVariavel_fk());
			}
		}

		for (Variavel v : getVariaveisDaCultura(cultura_id)) {
			LinkedList<Medicao> list_medicaoes = new LinkedList<>();
			for (Medicao m : getMedicoes()) {
				if (variaveis_medidas_ids.contains(m.getVariavel_medida_fk())
						&& variaveis_medidas_ids.contains(v.getId_variavel())) {
					m.setMore_info(v.getNome_variavel());
					list_medicaoes.add(m);
				}
			}
			list.add(list_medicaoes);
		}
		System.out.println(list.size());
		return list;
	}

	/**
	 * This function returns all Variavel objects in database which are being
	 * monitorized by a Cultura, given it's id
	 *
	 * @return list of Variavel objects
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Variavel> getVariaveisDaCultura(String cultura_id) throws SQLException {
		LinkedList<Variavel> list = new LinkedList<>();
		LinkedList<String> variaveis_medidas_id = new LinkedList<>();
		for (VariavelMedida vm : getVariaveisMedidas()) {
			if (vm.getCultura_fk().equalsIgnoreCase(cultura_id)) {
				variaveis_medidas_id.add(vm.getVariavel_fk());
			}
		}
		for (Variavel v : getVariaveis()) {
			if (variaveis_medidas_id.contains(v.getId_variavel())) {
				list.add(v);
			}
		}
		return list;
	}

	/**
	 * This function returns all Variavel objects
	 *
	 * @return list of the Variavel objects in database on table "variavel"
	 * @throws SQLException - if a user doesn't have permissions to execute a select
	 *                      query in a given table
	 */
	public LinkedList<Variavel> getVariaveis() throws SQLException {
		LinkedList<String>[] variaveis = allTableData("variavel");
		LinkedList<Variavel> list = new LinkedList<>();
		for (int i = 0; i != variaveis[0].size(); i++) {
			String id_variavel = variaveis[0].get(i);
			String nome_variavel = variaveis[1].get(i);
			list.add(new Variavel(id_variavel, nome_variavel));
		}
		return list;
	}

	/**
	 * This Method creates and inserts a new VariavelMedida in the database
	 * 
	 * @param idCultura  - culturasID of the variavelMedida
	 * @param upperLimit - upperlimit of the VariavelMedida
	 * @param lowerLimit - lowerlimit of the VariavelMedida
	 * @param idVariavel - id of the variavel
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void insertVariavelMedida(int idCultura, int idVariavel, int upperLimit, int lowerLimit, int id_investigador)
			throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call `VariavelMedida[INSERT]` (?,?,?,?,?)}");
		ps.setEscapeProcessing(true);
		ps.setInt(1, idCultura);
		ps.setInt(2, idVariavel);
		ps.setInt(3, upperLimit);
		ps.setInt(4, lowerLimit);
		ps.setInt(5, id_investigador);
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
		PreparedStatement ps = connection.prepareStatement("{call `Variavel[INSERT]` (?)}");
		ps.setEscapeProcessing(true);
		ps.setString(1, name);
		ps.executeUpdate();
	}

	/**
	 * This Method creates and inserts a new Medicao in the database
	 * 
	 * @param value            - value of the new medicao you want to insert
	 * @param variavelMedidaFK - foreign key of the variavelMedida
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void insertMedicao(int value, int variavelMedidaFK, int investigador) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call `Medicao[INSERT]` (?,?,?,?)}");
		ps.setEscapeProcessing(true);
		ps.setInt(1, value);
		ps.setInt(2, variavelMedidaFK);
		ps.setTimestamp(3, new Timestamp(Calendar.getInstance().getTimeInMillis()));
		ps.setInt(4, investigador);
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
		PreparedStatement ps = connection.prepareStatement("{call `Cultura[INSERT]` (?,?,?,?)}");
		ps.setEscapeProcessing(true);
		ps.setString(1, culturaName);
		ps.setString(2, description);
		ps.setInt(3, type);
		ps.setInt(4, id_investigador);
		ps.executeUpdate();
	}

	public void insertInvestigador(String nome, String email, String categoria, String pwd) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call `Investigador[INSERT]` (?,?,?,?)}");
		ps.setString(1, nome);
		ps.setString(2, email);
		ps.setString(3, categoria);
		ps.setString(4, pwd);
		ps.executeUpdate();
	}

	public void updateAlerta(int tempoDePico, float temperaturaSuperiorVermelho, float temperaturaInferiorVermelho,
			float temperaturaSuperiorAmarelo, float temperaturaInferiorAmarelo, float luminosidadeSuperiorVermelho,
			float luminosidadeInferiorVermelho, float luminosidadeSuperiorAmarelo, float luminosidadeInferiorAmarelo,
			int investigador) throws SQLException {

		PreparedStatement ps = connection.prepareStatement("{call `PerfilUser[UPDATE]` (?,?,?,?,?,?,?,?,?,?)}");
		ps.setInt(1, tempoDePico);
		ps.setFloat(2, temperaturaSuperiorVermelho);
		ps.setFloat(3, temperaturaInferiorVermelho);
		ps.setFloat(4, temperaturaSuperiorAmarelo);
		ps.setFloat(5, temperaturaInferiorAmarelo);
		ps.setFloat(6, luminosidadeSuperiorVermelho);
		ps.setFloat(7, luminosidadeInferiorVermelho);
		ps.setFloat(8, luminosidadeSuperiorAmarelo);
		ps.setFloat(9, luminosidadeInferiorAmarelo);
		ps.setInt(10, investigador);
		ps.executeUpdate();
	}

	public void updateVariableMesuredInfo(int id, float limite_Superior_Lar, float limite_Inferior_Lar,
			float limite_Superior_Ver, float limite_Inferior_Ver) throws SQLException {

		PreparedStatement ps = connection.prepareStatement("{call `VariavelMedidaPreferencias[UPDATE]` (?,?,?,?,?)}");
		ps.setInt(1, id);
		ps.setFloat(2, limite_Superior_Lar);
		ps.setFloat(3, limite_Inferior_Lar);
		ps.setFloat(4, limite_Superior_Ver);
		ps.setFloat(5, limite_Inferior_Ver);
		ps.executeUpdate();
	}

	/**
	 * This method deletes a VariavelMedida from the database base on its ID.
	 * 
	 * @param idVariavelMedida - id of the VariavelMedida you want to delete.
	 * @throws SQLException - if a database access error occurs or this method is
	 *                      called on a closed connection
	 */
	public void deleteVariavelMedida(int idCultura, int idVariavel) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call `VariavelMedida[DELETE]`(?,?)}");
		ps.setInt(1, idCultura);
		ps.setInt(2, idVariavel);
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
		PreparedStatement ps = connection.prepareStatement("{call `Variavel[DELETE]`(?)}");
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
		PreparedStatement ps = connection.prepareStatement("{call `Medicao[DELETE]`(?)}");
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
		PreparedStatement ps = connection.prepareStatement("{call `Cultura[DELETE]`(?)}");
		ps.setInt(1, idCultura);
		ps.executeUpdate();
	}

	public void deleteInvestigador(int idInvestigador) throws SQLException {
		deletePerfil(idInvestigador);
		PreparedStatement ps = connection.prepareStatement("{call `Investigador[DELETE]`(?)}");
		ps.setInt(1, idInvestigador);
		ps.executeUpdate();
	}

	private void deletePerfil(int idInvestigador) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("{call `PerfilUser[DELETE]`(?)}");
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
	 * This function returns the log columns of all the log tables that the logged
	 * user has access to.
	 * 
	 * @return a list containing all the log tables of the user
	 * @throws SQLException - if a user can't access the database.
	 */
	public LinkedList<Log> getLogColumns(String tableName) throws SQLException {
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT " + tableName + ".idlog, " + tableName
				+ ".utilizador, " + tableName + ".dataLog, " + tableName + ".operacao FROM " + tableName);
		return createLog(resultSet);
	}

	/**
	 * Function that creates Log objects from a resultset and places in a
	 * linkedlist<Log>.
	 * 
	 * @param resultSet each row correspondes with an object Log.
	 * @return linkedlist of objects Log.
	 * @throws SQLException - If a database access error occurs-
	 */
	private LinkedList<Log> createLog(ResultSet resultSet) throws SQLException {
		LinkedList<Log> list = new LinkedList<Log>();
		while (resultSet.next()) {
			list.add(new Log(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
					resultSet.getString(4)));
		}
		return list;
	}

	/**
	 * Function that creates Cultura objects from a resultset and places in a
	 * linkedlist<Cultura>.
	 * 
	 * @param resultSet each row correspondes with an object Cultura.
	 * @return linkedlist of objects Cultura.
	 * @throws SQLException - If a database access error occurs-
	 */
	private LinkedList<Investigador> createInvestigador(ResultSet resultSet) throws SQLException {
		LinkedList<Investigador> list = new LinkedList<Investigador>();
		while (resultSet.next()) {
			list.add(new Investigador(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
					resultSet.getString(4), resultSet.getString(5)));
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
					resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7),
					resultSet.getString(8), resultSet.getString(9)));
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

	private LinkedList<AlertaSensor> createAlertaSensor(ResultSet resultSet) throws SQLException {
		LinkedList<AlertaSensor> list = new LinkedList<AlertaSensor>();
		while (resultSet.next()) {
			list.add(new AlertaSensor(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
					resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7),
					resultSet.getString(8), resultSet.getString(9)));
		}
		return list;
	}

	private LinkedList<AlertaVariavel> createAlertaVariavel(ResultSet resultSet) throws SQLException {
		LinkedList<AlertaVariavel> list = new LinkedList<AlertaVariavel>();
		while (resultSet.next()) {
			list.add(new AlertaVariavel(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
					resultSet.getInt(4), resultSet.getString(5), resultSet.getInt(6)));
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

	public LinkedList<AlertaSensor> getSensorAlertTable(int id_investigador) throws SQLException {
		ResultSet resultSet = connection.createStatement()
				.executeQuery("SELECT * FROM  alerta_sensor WHERE idInvestigador = " + id_investigador);
		return createAlertaSensor(resultSet);
	}

	public LinkedList<AlertaVariavel> getVariableAlertTable(int id_investigador) throws SQLException {
		ResultSet resultSet = connection.createStatement()
				.executeQuery("SELECT * FROM  alerta_variavel WHERE id_investigador = " + id_investigador);
		return createAlertaVariavel(resultSet);
	}

}

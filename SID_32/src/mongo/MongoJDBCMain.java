package mongo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class MongoJDBCMain {

	double LITemperatura;
	double LSTemperatura;
	double LILuminosidade;
	double LSLuminosidade;
	double valorXtemperatura;
	double valorXluminosidade;
	double valorAntigoTemperatura =0;
	double valorAntigoLuminosidade=0;
	double valorAnomaloTemperatura=0;
	double valorAnomaloLuminosidade=0;
	boolean alertaAmareloTemperatura = false;
	boolean alertaAmareloLuminosidade = false;
	boolean alertaLaranjaTemperatura = false;
	boolean alertaLaranjaLuminosidade = false;
	boolean alertaVermelhoTemperatura = false;
	boolean alertaVermelhoLuminosidade = false;
	boolean picoTemperatura = false;
	boolean picoLuminosidade = false;
	int count = 0;
	int time = 20000;
	Statement stmt = null;
	Connection connection = null;
	Statement statement = null;
	ResultSet result = null;
	
	
	
	public MongoJDBCMain() {
		try {
			migracao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void migracao() throws SQLException {
		try {
			// ligar-se ao mongo
			MongoClient mongoClient = new MongoClient();
			mongoClient.getDatabaseNames().forEach(System.out::println);
			DB db = mongoClient.getDB("sensores");
			DBCollection collection = db.getCollection("sensor");

			// ligar-se � base de dados Main
			String database_url = "jdbc:mysql://localhost:3307/main";
			String username = "root";
			String password = "teste123";
			

			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR: Unable to load SQLServer JDBC Driver");
				e.printStackTrace();
				return;
			}
			System.out.println("MainDB JDBC Driver has been registered...");

			System.out.println("Trying to get a connection to the database...");
			try {
				connection = DriverManager.getConnection(database_url, username, password);
			} catch (SQLException e) {
				System.out.println("ERROR: Unable to establish a connection with the database!");
				e.printStackTrace();
				return;
			}

			if (connection != null) {
				DatabaseMetaData metadata = connection.getMetaData();
				System.out.println("Connection to the database has been established...");
				System.out.println("JDBC Driver Name : " + metadata.getDriverName());
				System.out.println("JDBC Driver Version : " + metadata.getDriverVersion());

				// depois de liga��o feita com sucesso vai buscar os limites do sistema
				Statement stmt = null;
				stmt = connection.createStatement();
				ObterLimites(stmt);
				
				DBCursor cursor = collection.find();
				while (cursor.hasNext()) {
					BasicDBObject theObj = (BasicDBObject) cursor.next();
					String content = theObj.toString();
					System.out.println(content);
					String DataHora = (theObj).getString("dat") + " " + (theObj).getString("tim");
					String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					SimpleDateFormat soDia = new SimpleDateFormat("dd-MM-yyyy");
					Date parsedDate;
					Date diaDate;
					// verificar se a data � valida
					try {

						diaDate = sdf.parse(DataHora);
						System.out.println("Dia" + soDia.format(diaDate));
						parsedDate = sdf.parse(DataHora);
						SimpleDateFormat print = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						DataHora=print.format(parsedDate);
						if (!timeStamp.contains(soDia.format(diaDate))) {
							System.out.println("timestamp:" + timeStamp);
							System.out.println("erro na data");
							DataHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(Calendar.getInstance().getTime());
						}
						int luminosidade=0;
						if(content.contains("cell")){
								luminosidade = Integer.parseInt((theObj).getString("cell"));
						}
						double temperatura = Double.parseDouble((theObj).getString("tmp"));
						String id = (theObj).getString("_id");
						int foiExportado = Integer.parseInt((theObj).getString("exported"));
						// se ainda nao foi exportado, foiExportado=0
						if (foiExportado == 0) {
							// criar a medicacao_luminosidade
							if (luminosidade != 0) {
								String query1 = " insert into medicao_luminosidade (Data_Hora_Medicao, Valor_Medicao_Luminosidade)"
										+ " values (?, ?)";

								// create the mysql insert preparedstatement
								PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
								preparedStmt1.setString(1, DataHora);
								preparedStmt1.setDouble(2, luminosidade);

								// execute the preparedstatement
								preparedStmt1.execute();
							}
							// criar a medicacao_temperatura
							String query2 = " insert into medicao_temperatura (Data_Hora_Medicao, Valor_Medicao_Temperatura)"
									+ " values (?, ?)";
							// create the mysql insert preparedstatement
							PreparedStatement preparedStmt2 = connection.prepareStatement(query2);
							preparedStmt2.setString(1, DataHora);
							preparedStmt2.setDouble(2, temperatura);

							// execute the preparedstatement
							preparedStmt2.execute();

							// colocar foiExportado=1
							BasicDBObject newDocument = new BasicDBObject();
							newDocument.append("$set", new BasicDBObject().append("exported", 1));
							BasicDBObject searchQuery = new BasicDBObject().append("_id", new ObjectId(id));
							collection.update(searchQuery, newDocument);
							System.out.println("Valor da temperatura:" + temperatura);
							System.out.println("Valor do if:" + (LITemperatura + LITemperatura * 0.4));

							// decidir se cria o alerta
							criaAlertaTemperatura(temperatura, DataHora, connection, stmt);
							if(luminosidade!=0){
								criaAlertaLuminosidade(luminosidade, DataHora, connection, stmt);
							}
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} else {
				System.out.println("ERROR: Unable to make a database connection!");
			}

			System.out.println("Trying to get a list of all entrys in sensor collection...");
			try {
				statement = connection.createStatement();

			} finally {
				System.out.println("Closing all open resources...");
				if (result != null)
					result.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			}

		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException {
		new MongoJDBCMain();
	}

	private void ObterLimites(Statement stmt) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "SELECT Limite_Inferior_Temperatura,Limite_Superior_Temperatura, Limite_Inferior_Luminosidade,Limite_Superior_Luminosidade FROM Sistema";
		ResultSet rs = stmt.executeQuery(sql);
		// STEP 5: Extract data from result set
		while (rs.next()) {
			// Retrieve by column name
			LITemperatura = rs.getDouble("Limite_Inferior_Temperatura");
			LSTemperatura = rs.getDouble("Limite_Superior_Temperatura");
			LILuminosidade = rs.getDouble("Limite_Inferior_Luminosidade");
			LSLuminosidade = rs.getDouble("Limite_Superior_Luminosidade");
			valorXtemperatura = (LSTemperatura - LITemperatura) / 2;
			valorXluminosidade = (LSLuminosidade - LILuminosidade) / 2;

			// Display values
			System.out.print("LITemperatura: " + LITemperatura);
			System.out.print(", LSTemperatura: " + LSTemperatura);
			System.out.print(", LILuminosidade: " + LILuminosidade);
			System.out.println(", LSLuminosidade: " + LSLuminosidade);
		}
		rs.close();
	}

	private void criaAlertaTemperatura(double temperatura, String date, Connection connection, Statement stmt)
			throws SQLException {
		// alerta Vermelho Temperatura
		String existeAlertaVermelhoTemp = "SELECT id FROM alerta_sensor WHERE intensidade='vermelho' and tipo='temp' AND datahoraalerta > DATE_ADD( \""
				+ date + "\" , interval 1 minute)";
		ResultSet rsVermelhoTemp = stmt.executeQuery(existeAlertaVermelhoTemp);
		if (rsVermelhoTemp.next() == false
				&& ((temperatura <= (LITemperatura + LITemperatura * 0.4)) || (temperatura >= LSTemperatura * 0.9))) {
			alertaVermelhoTemperatura = true;
			insertAlerta("temp","vermelho",date,temperatura,"O valor da temperatura aproxima-se criticamente dos limites",LITemperatura,LSTemperatura);
			String investigadores = "SELECT * FROM main.investigador";
			ResultSet inves = stmt.executeQuery(investigadores);
			while (inves.next()) {
				String invest = inves.getNString("Email_Investigador");
				System.out.println("Investigador" + invest);
				sendEmails(invest, "Alerta Vermelho Temperatura", "Valor da temperatura" + temperatura);
			}
			inves.close();

		} else {
			alertaVermelhoTemperatura = false;
		}

		// alerta Laranja Temperatura
		String existeAlertaLaranjaTemp = "SELECT id FROM alerta_sensor WHERE intensidade='laranja' and tipo='temp' AND datahoraalerta > DATE_ADD( \""
				+ date + "\" , interval 1 minute)";
		ResultSet rsLaranjaTemp = stmt.executeQuery(existeAlertaLaranjaTemp);
		if (rsLaranjaTemp.next() == false && (temperatura <= (LITemperatura + LITemperatura * 0.8))
				&& (temperatura > (LITemperatura + LITemperatura * 0.4))
				|| (temperatura >= (LSTemperatura * 0.8) && temperatura < LSTemperatura * 0.9)) {
			alertaLaranjaTemperatura = true;
			insertAlerta("temp","laranja",date,temperatura,"O valor da temperatura aproxima-se dos limites",LITemperatura,LSTemperatura);
			String investigadores = "SELECT * FROM main.investigador";
			ResultSet inves = stmt.executeQuery(investigadores);
			while (inves.next()) {
				String invest = inves.getNString("Email_Investigador");
				System.out.println("Investigador" + invest);
				sendEmails(invest, "Alerta Laranja Temperatura", "Valor da temperatura" + temperatura);
			}
			inves.close();

		} else {
			alertaLaranjaTemperatura = false;
		}

		// Detetar picos
		if (Math.abs(temperatura - valorAntigoTemperatura) > valorXtemperatura && picoTemperatura == false
				&& valorAntigoTemperatura!=0) {
			picoTemperatura = true;
			valorAnomaloTemperatura = temperatura;
			new Thread() {
				
				@Override
				public void run() {
					try {
						sleep(time);
						String mysql = "SELECT * FROM medicao_temperatura where Id_Medicao_Temperatura =(Select MAX(Id_Medicao_Temperatura) from medicao_temperatura)";
						ResultSet rs = stmt.executeQuery(mysql);
						Double valorAtualTemperatura = rs.getDouble("Valor_Medicao_Temperatura");
						rs.close();
						if (Math.abs(valorAtualTemperatura - valorAntigoTemperatura) > valorXtemperatura) {
							// alerta Vermelho Temperatura
							String existeAlertaVermelhoTemp = "SELECT id FROM alerta_sensor WHERE intensidade='vermelho' and tipo='temp' AND datahoraalerta > DATE_ADD( \""
									+ date + "\" , interval 1 minute)";
							ResultSet rsVermelhoTemp = stmt.executeQuery(existeAlertaVermelhoTemp);
							if (rsVermelhoTemp.next() == false
									&& ((valorAtualTemperatura <= (LITemperatura + LITemperatura * 0.4)) || (valorAtualTemperatura >= LSTemperatura * 0.9))) {
								alertaVermelhoTemperatura = true;
								insertAlerta("temp","vermelho",date,valorAtualTemperatura,"O valor da temperatura aproxima-se criticamente dos limites",LITemperatura,LSTemperatura);
								String investigadores = "SELECT * FROM main.investigador";
								ResultSet inves = stmt.executeQuery(investigadores);
								while (inves.next()) {
									String invest = inves.getNString("Email_Investigador");
									System.out.println("Investigador" + invest);
									sendEmails(invest, "Alerta Vermelho Temperatura", "Valor da temperatura" + valorAtualTemperatura);
								}
								inves.close();

							} else {
								alertaVermelhoTemperatura = false;
							}

							// alerta Laranja Temperatura
							String existeAlertaLaranjaTemp = "SELECT id FROM alerta_sensor WHERE intensidade='laranja' and tipo='temp' AND datahoraalerta > DATE_ADD( \""
									+ date + "\" , interval 1 minute)";
							ResultSet rsLaranjaTemp = stmt.executeQuery(existeAlertaLaranjaTemp);
							if (rsLaranjaTemp.next() == false && (temperatura <= (LITemperatura + LITemperatura * 0.8))
									&& (valorAtualTemperatura > (LITemperatura + LITemperatura * 0.4))
									|| (valorAtualTemperatura >= (LSTemperatura * 0.8) && valorAtualTemperatura < LSTemperatura * 0.9)) {
								alertaLaranjaTemperatura = true;
								insertAlerta("temp","laranja",date,valorAtualTemperatura,"O valor da temperatura aproxima-se dos limites",LITemperatura,LSTemperatura);
								String investigadores = "SELECT * FROM main.investigador";
								ResultSet inves = stmt.executeQuery(investigadores);
								while (inves.next()) {
									String invest = inves.getNString("Email_Investigador");
									System.out.println("Investigador" + invest);
									sendEmails(invest, "Alerta Laranja Temperatura", "Valor da temperatura" + valorAtualTemperatura);
								}
								inves.close();

							} else {
								alertaLaranjaTemperatura = false;
							}
							if(alertaLaranjaTemperatura==false && alertaVermelhoTemperatura==false) {
								insertAlerta("temp","amarelo",date,valorAtualTemperatura,"Ocorreu um pico de temperatura",LITemperatura,LSTemperatura);
								String investigadores = "SELECT * FROM main.investigador";
								ResultSet inves = stmt.executeQuery(investigadores);
								while (inves.next()) {
									String invest = inves.getNString("Email_Investigador");
									System.out.println("Investigador" + invest);
									sendEmails(invest, "Alerta Amarelo Temperatura", "Valor da temperatura" + valorAtualTemperatura);
								}
								inves.close();
							}
						}else{
							picoTemperatura = false;
						}
						
					} catch (InterruptedException e) {
						System.out.println("esta tudo bem ;)");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}else{
			valorAntigoTemperatura=temperatura;
		}

	}

	private void criaAlertaLuminosidade(double luminosidade, String date, Connection connection, Statement stmt)
			throws SQLException {
		// alerta Vermelho Luminosidade
		String existeAlertaVermelhoLum = "SELECT id FROM alerta_sensor WHERE intensidade='vermelho' and tipo='lum' AND datahoraalerta > DATE_ADD( \""
				+ date + "\" , interval 1 minute)";
		ResultSet rsVermelhoLum = stmt.executeQuery(existeAlertaVermelhoLum);
		if (rsVermelhoLum.next() == false && (luminosidade <= (LILuminosidade + LILuminosidade * 0.4))
				|| (luminosidade >= LSLuminosidade * 0.9)) {
			alertaVermelhoLuminosidade = true;
			insertAlerta("lum","vermelho",date,luminosidade,"O valor da luminosidade aproxima-se criticamente dos limites",LILuminosidade,LSLuminosidade);
			String investigadores = "SELECT * FROM main.investigador";
			ResultSet inves = stmt.executeQuery(investigadores);
			while (inves.next()) {
				String invest = inves.getNString("Email_Investigador");
				System.out.println("Investigador" + invest);
				sendEmails(invest, "Alerta Vermelho Luminosidade", "Valor da luminosidade" + luminosidade);
			}
			inves.close();

		} else {
			alertaVermelhoLuminosidade = false;
		}

		// alerta Laranja Luminosidade
		String existeAlertaLaranjaLum = "SELECT id FROM alerta_sensor WHERE intensidade='laranja' and tipo='lum' AND datahoraalerta > DATE_ADD( \""
				+ date + "\" , interval 1 minute)";
		ResultSet rsLaranjaLum = stmt.executeQuery(existeAlertaLaranjaLum);
		if (rsLaranjaLum.next() == false && (luminosidade <= (LILuminosidade + LILuminosidade * 0.8))
				&& (luminosidade > (LILuminosidade + LILuminosidade * 0.4))
				|| (luminosidade >= (LSLuminosidade * 0.8) && luminosidade < LSLuminosidade * 0.9)) {
			alertaLaranjaLuminosidade = true;

			insertAlerta("lum","laranja",date,luminosidade,"O valor da luminosidade aproxima-se dos limites",LILuminosidade,LSLuminosidade);
			String investigadores = "SELECT * FROM main.investigador";
			ResultSet inves = stmt.executeQuery(investigadores);
			while (inves.next()) {
				String invest = inves.getNString("Email_Investigador");
				System.out.println("Investigador" + invest);
				sendEmails(invest, "Alerta Laranja Luminosidade", "Valor da luminosidade" + luminosidade);
			}
			inves.close();

		} else {
			alertaLaranjaLuminosidade = false;
		}

		// Detetar picos
		if (Math.abs(luminosidade - valorAntigoLuminosidade) > valorXluminosidade && picoLuminosidade == false
				&& valorAntigoLuminosidade != 0) {
			picoLuminosidade = true;
			valorAnomaloLuminosidade = luminosidade;
			
			new Thread() {
				@Override
				public void run() {
					try{
						sleep(time);
						String mysql = "SELECT * FROM medicao_luminosidade where Id_Medicao_Luminosidade =(Select MAX(Id_Medicao_Luminosidade) from medicao_luminosidade)";
						ResultSet rs = stmt.executeQuery(mysql);
						Double valorAtualLuminosidade = rs.getDouble("Valor_Medicao_Luminosidade");
						rs.close();
						if (Math.abs(valorAtualLuminosidade - valorAntigoLuminosidade) > valorXluminosidade) {
							// alerta Vermelho Luminosidade
							String existeAlertaVermelhoLum = "SELECT id FROM alerta_sensor WHERE intensidade='vermelho' and tipo='lum' AND datahoraalerta > DATE_ADD( \""
									+ date + "\" , interval 1 minute)";
							ResultSet rsVermelhoLum = stmt.executeQuery(existeAlertaVermelhoLum);
							if (rsVermelhoLum.next() == false && (luminosidade <= (LILuminosidade + LILuminosidade * 0.4))
									|| (luminosidade >= LSLuminosidade * 0.9)) {
								alertaVermelhoLuminosidade = true;
								insertAlerta("lum","vermelho",date,valorAtualLuminosidade,"O valor da luminosidade aproxima-se criticamente dos limites",LILuminosidade,LSLuminosidade);
								String investigadores = "SELECT * FROM main.investigador";
								ResultSet inves = stmt.executeQuery(investigadores);
								while (inves.next()) {
									String invest = inves.getNString("Email_Investigador");
									System.out.println("Investigador" + invest);
									sendEmails(invest, "Alerta Vermelho Luminosidade", "Valor da luminosidade" + luminosidade);
								}
								inves.close();

							} else {
								alertaVermelhoLuminosidade = false;
							}

							// alerta Laranja Luminosidade
							String existeAlertaLaranjaLum = "SELECT id FROM alerta_sensor WHERE intensidade='laranja' and tipo='lum' AND datahoraalerta > DATE_ADD( \""
									+ date + "\" , interval 1 minute)";
							ResultSet rsLaranjaLum = stmt.executeQuery(existeAlertaLaranjaLum);
							if (rsLaranjaLum.next() == false && (luminosidade <= (LILuminosidade + LILuminosidade * 0.8))
									&& (luminosidade > (LILuminosidade + LILuminosidade * 0.4))
									|| (luminosidade >= (LSLuminosidade * 0.8) && luminosidade < LSLuminosidade * 0.9)) {
								alertaLaranjaLuminosidade = true;

								insertAlerta("lum","laranja",date,valorAtualLuminosidade,"O valor da luminosidade aproxima-se dos limites",LILuminosidade,LSLuminosidade);
								String investigadores = "SELECT * FROM main.investigador";
								ResultSet inves = stmt.executeQuery(investigadores);
								while (inves.next()) {
									String invest = inves.getNString("Email_Investigador");
									System.out.println("Investigador" + invest);
									sendEmails(invest, "Alerta Laranja Luminosidade", "Valor da luminosidade" + luminosidade);
								}
								inves.close();

							} else {
								alertaLaranjaLuminosidade = false;
							}
							if (alertaLaranjaLuminosidade == false && alertaVermelhoLuminosidade== false) {
								insertAlerta("lum","amarelo",date,valorAtualLuminosidade,"Pico de luminosidade dentro dos limites",LILuminosidade,LSLuminosidade);
								String investigadores = "SELECT * FROM main.investigador";
								ResultSet inves = stmt.executeQuery(investigadores);
								while (inves.next()) {
									String invest = inves.getNString("Email_Investigador");
									System.out.println("Investigador" + invest);
									sendEmails(invest, "Alerta Amarelo Luminosidade", "Valor da luminosidade" + valorAtualLuminosidade);
								}
								inves.close();
									
								}
							} else {
								picoLuminosidade=false;
							}

						} catch (InterruptedException e){
							System.out.println("esta tudo bem ;)");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
			}.start();
		}else {
			valorAntigoLuminosidade=luminosidade;
		}

		

	}
	
	public void insertAlerta(String tipo, String intensidade, String datahora, double medicao, String descricao, double li, double ls) throws SQLException{
		String alerta = " insert into alerta_sensor (tipo, intensidade, datahoraalerta, valormedicao,descricao,limiteinferior,limitesuperior)"
				+ " values (?, ?, ?, ?, ?, ?, ?)";
		// create the mysql insert preparedstatement
		PreparedStatement preparedStmt = connection.prepareStatement(alerta);
		preparedStmt.setString(1, tipo);
		preparedStmt.setString(2, intensidade);
		preparedStmt.setString(3, datahora);
		preparedStmt.setDouble(4, medicao);
		preparedStmt.setString(5, descricao);
		preparedStmt.setDouble(6, li);
		preparedStmt.setDouble(7, ls);

		// execute the preparedstatement
		preparedStmt.execute();
	}

	public boolean sendEmails(String to, String sub, String text) {
		System.out.println(to + sub + text);
		// Sender's email ID needs to be mentioned

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "outlook.office365.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("es1g1@outlook.com", "grupo1grupo");
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("es1g1@outlook.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(sub);
			message.setText(text);

			Transport.send(message);

			System.out.println("Done");
			System.out.println("Sent message successfully....");
			return true;
		} catch (MessagingException mex) {
			mex.printStackTrace();
			return false;
		}
	}
}

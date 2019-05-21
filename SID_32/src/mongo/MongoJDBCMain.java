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
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;

public class MongoJDBCMain {

	private double LITemperatura;
	private double LSTemperatura;
	private double LILuminosidade;
	private double LSLuminosidade;
	private double valorXtemperatura;
	private double valorXluminosidade;
	
	private double valorAntigoTemperatura ;
	private double valorAntigoLuminosidade ;
	private boolean alertaLaranjaTemperatura ;
	private boolean alertaLaranjaLuminosidade;
	private boolean alertaVermelhoTemperatura;
	private boolean alertaVermelhoLuminosidade;
	private boolean picoTemperatura;
	private boolean picoLuminosidade;
	private Statement stmt = null;
	private Connection connection = null;
	private DBCollection collection;
	Conf cfg;

	public MongoJDBCMain(Conf c) {
		valorAntigoTemperatura = 0;
		valorAntigoLuminosidade = 0;
		alertaLaranjaTemperatura = false;
		alertaLaranjaLuminosidade = false;
		alertaVermelhoTemperatura = false;
		alertaVermelhoLuminosidade = false;
		picoTemperatura = false;
		picoLuminosidade = false;	
		
		
		cfg =c;
		try {
			connect();
			if (connection != null) {
				migracao();
			}else {
				System.out.println("ERROR: Unable to make a database connection!");
			}

			System.out.println("Trying to get a list of all entrys in sensor collection...");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void connect() {
		try {
			// ligar-se ao mongo
			MongoClient mongoClient = new MongoClient(
					new MongoClientURI("mongodb+srv://"+cfg.getProperty("user_mongo")+":"+cfg.getProperty("password_mongo")+cfg.getProperty("cluster_mongo")));
			mongoClient.getDatabaseNames().forEach(System.out::println);
			DB db = mongoClient.getDB("sensores");
			collection = db.getCollection("sensor");

			// ligar-se ï¿½ base de dados Main
			String database_url = "jdbc:mysql://localhost:"+cfg.getProperty("port_sql")+"/"+cfg.getProperty("databasename_sql");
			String username = cfg.getProperty("user_sql");
			String password = cfg.getProperty("password_sql");

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
		} catch (MongoException e) {
			e.printStackTrace();
		}

	}
	
	public boolean verificarData(String dataHora) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date diaDate = sdf.parse(dataHora);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}

	@SuppressWarnings({ "resource", "deprecation" })
	public void migracao() throws SQLException {			
				DatabaseMetaData metadata = connection.getMetaData();
				System.out.println("Connection to the database has been established...");
				System.out.println("JDBC Driver Name : " + metadata.getDriverName());
				System.out.println("JDBC Driver Version : " + metadata.getDriverVersion());

				// depois de ligaï¿½ï¿½o feita com sucesso vai buscar os limites do sistema
				stmt = connection.createStatement();
				ObterLimites(stmt);

				DBCursor cursor = collection.find();
				while (cursor.hasNext()) {
					BasicDBObject theObj = (BasicDBObject) cursor.next();
					String content = theObj.toString();
					
					
					int foiExportado = Integer.parseInt((theObj).getString("exported"));
					// se ainda nao foi exportado, foiExportado=0
					if (foiExportado == 0) {
						String dataHora = (theObj).getString("dateTime");
						
						if(!verificarData(dataHora))
							continue;
						
						int luminosidade = 0;
						if (content.contains("cell")) {
							luminosidade = Integer.parseInt((theObj).getString("cell"));
						}
						
						double temperatura = Double.parseDouble((theObj).getString("tmp"));
						String id = (theObj).getString("_id");
						// criar a medicacao_luminosidade
						if (luminosidade != 0) {
							String query1 = " insert into medicao_luminosidade (Data_Hora_Medicao, Valor_Medicao_Luminosidade)"
									+ " values (?, ?)";

							// create the mysql insert preparedstatement
							PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
							preparedStmt1.setString(1, dataHora);
							preparedStmt1.setDouble(2, luminosidade);

							// execute the preparedstatement
							preparedStmt1.execute();
						}
						// criar a medicacao_temperatura
						String query2 = " insert into medicao_temperatura (Data_Hora_Medicao, Valor_Medicao_Temperatura)"
								+ " values (?, ?)";
						// create the mysql insert preparedstatement
						PreparedStatement preparedStmt2 = connection.prepareStatement(query2);
						preparedStmt2.setString(1, dataHora);
						preparedStmt2.setDouble(2, temperatura);

						// execute the preparedstatement
						preparedStmt2.execute();

						// colocar foiExportado=1
						BasicDBObject newDocument = new BasicDBObject();
						newDocument.append("$set", new BasicDBObject().append("exported", 1));
						BasicDBObject searchQuery = new BasicDBObject().append("_id", new ObjectId(id));
						collection.update(searchQuery, newDocument);
						System.out.println("Valor da temperatura:" + temperatura);

						// decidir se cria o alerta
						Statement stmt3 =connection.createStatement();
						String investigadores = "SELECT * FROM main.investigador";
						ResultSet inves = stmt3.executeQuery(investigadores);
						while (inves.next()) { //ciclo para percorrer investigadores
							//obter email
							String investEmail = inves.getNString("Email_Investigador");  
							investEmail.replaceAll("@localhost","");
							//obter id e perfil de utilizador
							int idI = inves.getInt("Id_Investigador");
							System.out.println("id:" +idI);
							String perfUti= "Select * FROM main.perfil_user where investigador =+ "+ inves.getInt("Id_Investigador"); 
							Statement stmt2 =connection.createStatement();
							ResultSet invesPerf = stmt2.executeQuery(perfUti);
							if(invesPerf.next()) {
								//obter valores do perfil de utilizador
								int tempoDePico = invesPerf.getInt("tempoDePico");
								Float vermelho_sup_T = invesPerf.getFloat("vermelhoSupTemp");
								Float vermelho_inf_T = invesPerf.getFloat("vermelhoInfTemp");
								Float laranja_sup_T = invesPerf.getFloat("laranjaSupTemp");
								Float laranja_inf_T = invesPerf.getFloat("laranjaInfTemp");
								Float vermelho_sup_L = invesPerf.getFloat("vermelhoSupLum");							
								Float vermelho_inf_L = invesPerf.getFloat("vermelhoInfLum");
								Float laranja_sup_L = invesPerf.getFloat("laranjaSupLum");							
								Float laranja_inf_L = invesPerf.getFloat("laranjaInfLum");


								criaAlertaTemperatura(temperatura, dataHora, connection, stmt, idI,investEmail, 
										tempoDePico,vermelho_sup_T, vermelho_inf_T,  laranja_sup_T, laranja_inf_T);
								if (luminosidade != 0) {
									criaAlertaLuminosidade(luminosidade, dataHora, connection, stmt, idI,
											investEmail,  tempoDePico,vermelho_sup_L, vermelho_inf_L,  laranja_sup_L, laranja_inf_L );

								}
							}
							invesPerf.close();
							
						}
						inves.close();
						
					}

					

				}
	
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

	private void criaAlertaTemperatura_Vermelho(double temperatura, String date, Connection connection, Statement stmt, int id, String email, int TempoDePico, Float vermelhoSup, Float vermelhoInf, Float laranjaSup, Float laranjaInf) throws SQLException {
		// alerta Vermelho Temperatura
				String existeAlertaVermelhoTemp = "SELECT id FROM alerta_sensor WHERE intensidade='vermelho' and tipo='temp' AND datahoraalerta > DATE_ADD( \""
						+ date + "\" , interval -1 minute)  and idInvestigador=" +id ;
				ResultSet rsVermelhoTemp = stmt.executeQuery(existeAlertaVermelhoTemp);
				
				if (!rsVermelhoTemp.isBeforeFirst() 
						&& ((temperatura <= (LITemperatura + LITemperatura * (1-vermelhoInf))) || (temperatura >= LSTemperatura * vermelhoSup))) {
					alertaVermelhoTemperatura = true;
					String s = "O valor da temperatura aproxima-se criticamente dos limites";
					if (temperatura <= LITemperatura||temperatura >= LSTemperatura) {
						s="O valor da temperatura ultrapassou os limites";
					}
					insertAlerta("temp", "vermelho", date, temperatura, s, LITemperatura, LSTemperatura, id);
					sendEmails(email, " Alerta Vermelho Temperatura ", "Valor da temperatura: " + temperatura);
				

				} else {
					alertaVermelhoTemperatura = false;
				}
	}
	
	private void criaAlertaTemperatura_laranja(double temperatura, String date, Connection connection, Statement stmt, int id, String email, int TempoDePico, Float vermelhoSup, Float vermelhoInf, Float laranjaSup, Float laranjaInf)
			throws SQLException {
		// alerta Laranja Temperatura
				String existeAlertaLaranjaTemp = "SELECT id FROM alerta_sensor WHERE intensidade='laranja' and tipo='temp' AND datahoraalerta > DATE_ADD( \""
						+ date + "\" , interval -1 minute)  and idInvestigador=" +id ;
				ResultSet rsLaranjaTemp = stmt.executeQuery(existeAlertaLaranjaTemp);
				System.out.println(!rsLaranjaTemp.isBeforeFirst() + " data " + date);
				if (!rsLaranjaTemp.isBeforeFirst()  && ((temperatura <= (LITemperatura + LITemperatura *(1- laranjaInf)))
						&& (temperatura > (LITemperatura + LITemperatura * (1-vermelhoInf)))
						|| (temperatura >= (LSTemperatura * laranjaSup) && temperatura < LSTemperatura * vermelhoSup))) {
					
					alertaLaranjaTemperatura = true;
					insertAlerta("temp", "laranja", date, temperatura, "O valor da temperatura aproxima-se dos limites",
							LITemperatura, LSTemperatura,id);
						sendEmails(email, "Alerta Laranja Temperatura", "Valor da temperatura" + temperatura);


				} else {
					System.out.println("aqui não ha alerta laranja  t:" + temperatura);
					alertaLaranjaTemperatura = false;
				}

	}

	
	private void criaAlertaTemperatura(double temperatura, String date, Connection connection, Statement stmt, int id, String email, int TempoDePico, Float vermelhoSup, Float vermelhoInf, Float laranjaSup, Float laranjaInf)
			throws SQLException {
		// alerta Vermelho Temperatura
		criaAlertaTemperatura_Vermelho( temperatura,  date,  connection,  stmt,  id,  email,  TempoDePico,  vermelhoSup,  vermelhoInf,  laranjaSup,  laranjaInf);
		// alerta Laranja Temperatura
		criaAlertaTemperatura_laranja( temperatura,  date,  connection,  stmt,  id,  email,  TempoDePico,  vermelhoSup,  vermelhoInf,  laranjaSup,  laranjaInf);
		// Detetar picos
		if (Math.abs(temperatura - valorAntigoTemperatura) > valorXtemperatura && picoTemperatura == false
				&& valorAntigoTemperatura != 0) {
			picoTemperatura = true;
			new Thread() {
				@Override
				public void run() {
					try {
						//esperar o tempo definido pelo investigador para ver se considera o pico anomalia ou alerta
						sleep(TempoDePico);
						String mysql = "SELECT Valor_Medicao_Temperatura FROM medicao_temperatura where Id_Medicao_Temperatura "
								+ "=(Select MAX(Id_Medicao_Temperatura) from medicao_temperatura)";
						ResultSet rs = stmt.executeQuery(mysql);
						Double valorAtualTemperatura=0.0;
						if (rs.next()) {
							valorAtualTemperatura=rs.getDouble("Valor_Medicao_Temperatura");
						}
						rs.close();
						//verifica se foi de facto uma anomalia ou se a condição ainda se mantêm
						if (Math.abs(valorAtualTemperatura - valorAntigoTemperatura) > valorXtemperatura) {
							// alerta Vermelho Temperatura, verifica se já existe um alerta no ultimo minuto para esse investigador
							criaAlertaTemperatura_Vermelho( valorAtualTemperatura,  date,  connection,  stmt,  id,  email,  TempoDePico,  vermelhoSup,  vermelhoInf,  laranjaSup,  laranjaInf);

							// alerta Laranja Temperatura
							criaAlertaTemperatura_Vermelho( valorAtualTemperatura,  date,  connection,  stmt,  id,  email,  TempoDePico,  vermelhoSup,  vermelhoInf,  laranjaSup,  laranjaInf);

							if (alertaLaranjaTemperatura == false && alertaVermelhoTemperatura == false) {
								insertAlerta("temp", "amarelo", date, valorAtualTemperatura,
										"Ocorreu um pico de temperatura", LITemperatura, LSTemperatura,id);
									sendEmails(email, "Alerta Amarelo Temperatura",
											"Valor da temperatura" + valorAtualTemperatura);

							}
						} else {
							System.out.println(" afinal era so um pico");
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
		} else {
			valorAntigoTemperatura = temperatura;
		}

	}

	private void criaAlertaLuminosidade_vermelho(double luminosidade, String date, Connection connection, Statement stmt,int id, String email, int tempoDePico, Float vermelhoSup, Float vermelhoInf, Float laranjaSup, Float laranjaInf)
			throws SQLException {
		String existeAlertaVermelhoLum = "SELECT id FROM alerta_sensor WHERE intensidade='vermelho' and tipo='lum' AND datahoraalerta > DATE_ADD( \""
				+ date + "\" , interval -1 minute) and idInvestigador=" +id ;
		ResultSet rsVermelhoLum = stmt.executeQuery(existeAlertaVermelhoLum);
		System.out.println(!rsVermelhoLum.isBeforeFirst() + " ************************************************--------------");
		if (!rsVermelhoLum.isBeforeFirst() && ((luminosidade <= (LILuminosidade + LILuminosidade * (1-vermelhoInf)))|| (luminosidade >= LSLuminosidade * vermelhoSup))) {
			System.out.println("alerta vermlho de luminosidade!!!!!!!!");
			alertaVermelhoLuminosidade = true;
			String s="O valor da luminosidade aproxima-se criticamente dos limites";
			if (luminosidade<=LILuminosidade|| luminosidade >= LSLuminosidade) {
			s="O valor da luminosidade ultrapassou os limites";
			}
			insertAlerta("lum", "vermelho", date, luminosidade, s , LILuminosidade, LSLuminosidade,id);

				sendEmails(email, "Alerta Vermelho Luminosidade", "Valor da luminosidade" + luminosidade);


		} else {
			System.out.println("aqui não ha alerta vermelho l:" + luminosidade);
			alertaVermelhoLuminosidade = false;
		}
	}
	
	private void criaAlertaLuminosidade_laranja(double luminosidade, String date, Connection connection, Statement stmt,int id, String email, int tempoDePico, Float vermelhoSup, Float vermelhoInf, Float laranjaSup, Float laranjaInf)
			throws SQLException {
		String existeAlertaLaranjaLum = "SELECT id FROM alerta_sensor WHERE intensidade='laranja' and tipo='lum' AND datahoraalerta > DATE_ADD( \""
				+ date + "\" , interval -1 minute) and idInvestigador=" +id ;
		ResultSet rsLaranjaLum = stmt.executeQuery(existeAlertaLaranjaLum);
		if (!rsLaranjaLum.isBeforeFirst() && (luminosidade <= (LILuminosidade + LILuminosidade * (1-laranjaInf)))
				&& ((luminosidade > (LILuminosidade + LILuminosidade * (1-vermelhoInf)))
				|| (luminosidade >= (LSLuminosidade * laranjaSup) && luminosidade < LSLuminosidade * vermelhoSup))) {
			System.out.println("alerta laranja luz!!!!!!!!!!!");
			alertaLaranjaLuminosidade = true;

			insertAlerta("lum", "laranja", date, luminosidade, "O valor da luminosidade aproxima-se dos limites",
					LILuminosidade, LSLuminosidade,id);
				sendEmails(email, "Alerta Laranja Luminosidade", "Valor da luminosidade" + luminosidade);

		} else {
			System.out.println("aqui não ha alerta laranja + l:" + luminosidade);
			alertaLaranjaLuminosidade = false;
		}
	}
	
	
	private void criaAlertaLuminosidade(double luminosidade, String date, Connection connection, Statement stmt,int id, String email, int tempoDePico, Float vermelhoSup, Float vermelhoInf, Float laranjaSup, Float laranjaInf)
			throws SQLException {
		// alerta Vermelho Luminosidade
		criaAlertaLuminosidade_vermelho( luminosidade,  date,  connection,  stmt,  id,  email,  tempoDePico,  vermelhoSup,  vermelhoInf,  laranjaSup,  laranjaInf);

		// alerta Laranja luminosidade
		criaAlertaLuminosidade_laranja( luminosidade,  date,  connection,  stmt,  id,  email,  tempoDePico,  vermelhoSup,  vermelhoInf,  laranjaSup,  laranjaInf);



		// Detetar picos
		if (Math.abs(luminosidade - valorAntigoLuminosidade) > valorXluminosidade && picoLuminosidade == false
				&& valorAntigoLuminosidade != 0) {
			picoLuminosidade = true;

			new Thread() {
				@Override
				public void run() {
					try {
						sleep(tempoDePico);
						System.out.println("**********verificar se é um pico de luz **********");
						String mysql = "SELECT * FROM medicao_luminosidade where Id_Medicao_Luminosidade =(Select MAX(Id_Medicao_Luminosidade) from medicao_luminosidade)";
						ResultSet rs = stmt.executeQuery(mysql);
						Double valorAtualLuminosidade =0.0;
						if(rs.next()) {
							valorAtualLuminosidade = rs.getDouble("Valor_Medicao_Luminosidade");
						}
						rs.close();
						if (Math.abs(valorAtualLuminosidade - valorAntigoLuminosidade) > valorXluminosidade) {
							// alerta Vermelho Luminosidade
							criaAlertaLuminosidade_vermelho( valorAtualLuminosidade,  date,  connection,  stmt,  id,  email,  tempoDePico,  vermelhoSup,  vermelhoInf,  laranjaSup,  laranjaInf);


							// alerta Laranja Luminosidade
							criaAlertaLuminosidade_laranja( valorAtualLuminosidade,  date,  connection,  stmt,  id,  email,  tempoDePico,  vermelhoSup,  vermelhoInf,  laranjaSup,  laranjaInf);


							if (alertaLaranjaLuminosidade == false && alertaVermelhoLuminosidade == false) {
								insertAlerta("lum", "amarelo", date, valorAtualLuminosidade,
										"Pico de luminosidade dentro dos limites", LILuminosidade, LSLuminosidade,id);
									sendEmails(email, "Alerta Amarelo Luminosidade",
											"Valor da luminosidade" + valorAtualLuminosidade);
				
							}
						} else {
							System.out.println("afinal era so um pico (luz)");
							picoLuminosidade = false;
						}

					} catch (InterruptedException e) {
						System.out.println("esta tudo bem ;)");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();
		} else {
			valorAntigoLuminosidade = luminosidade;
		}

	}

	public void insertAlerta(String tipo, String intensidade, String datahora, double medicao, String descricao,
			double li, double ls, int id) throws SQLException {
		System.out.println("a inserir alerta na tabela!");
		String alerta = " insert into alerta_sensor (tipo, intensidade, datahoraalerta, valormedicao,descricao,limiteinferior,limitesuperior,idInvestigador )"
				+ " values (?, ?, ?, ?, ?, ?, ?,?)";
		// create the mysql insert preparedstatement
		PreparedStatement preparedStmt = connection.prepareStatement(alerta);
		preparedStmt.setString(1, tipo);
		preparedStmt.setString(2, intensidade);
		preparedStmt.setString(3, datahora);
		preparedStmt.setDouble(4, medicao);
		preparedStmt.setString(5, descricao);
		preparedStmt.setDouble(6, li);
		preparedStmt.setDouble(7, ls);
		preparedStmt.setInt(8, id);

		// execute the preparedstatement
		preparedStmt.execute();
	}

	public boolean sendEmails(String to, String sub, String text) {
/*		System.out.println(to + sub + text);
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
		
		
*/
		return false;
	}

	
	
	public Connection getConnection() {
		return connection;
	}

}

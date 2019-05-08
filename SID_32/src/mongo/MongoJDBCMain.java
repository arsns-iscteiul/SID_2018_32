package mongo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class MongoJDBCMain {
	
	static double LITemperatura;
	static double LSTemperatura;
	static double LILuminosidade;
	static double LSLuminosidade;
	static double valorXtemperatura;
	static double valorXluminosidade;
	static double valorAntigoTemperatura;
	static double valorAntigoLuminosidade;
	static double valorAnomaloTemperatura;
	static double valorAnomaloLuminosidade;
	static boolean alertaAmareloTemperatura=false;
	static boolean alertaAmareloLuminosidade=false;
	static boolean alertaLaranjaTemperatura=false;
	static boolean alertaLaranjaLuminosidade=false;
	static boolean alertaVermelhoTemperatura=false;
	static boolean alertaVermelhoLuminosidade=false;
	static int count =0;
	
	public void export() throws SQLException {
		
		try {
		//ligar-se ao mongo
		MongoClient mongoClient = new MongoClient();
		mongoClient.getDatabaseNames().forEach(System.out::println);
		DB db = mongoClient.getDB("sensores");
		DBCollection collection = db.getCollection("sensor");
			
		//ligar-se à base de dados Main
		String database_url = "jdbc:mysql://localhost:3307/main";
		String username = "root";
		String password = "teste123";
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;

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
		
		//depois de ligação feita com sucesso vai buscar os limites do sistema
		 Statement stmt = null;
		  stmt = connection.createStatement();
		  ObterLimites(stmt);
		
		DBCursor cursor = collection.find();
		while(cursor.hasNext()) {
			BasicDBObject theObj = (BasicDBObject) cursor.next();
			String content = theObj.toString();
			String DataHora = (theObj).getString("dat") + " " +(theObj).getString("tim");
			System.out.println(content);
			System.out.println(DataHora);
			int luminosidade = Integer.parseInt((theObj).getString("cell"));
			double temperatura = Double.parseDouble((theObj).getString("tmp"));
			System.out.println(luminosidade);
			String id = (theObj).getString("_id");
			int foiExportado = Integer.parseInt((theObj).getString("exported"));
			System.out.println(id);
			// se ainda nao foi exportado, foiExportado=0
			if(foiExportado==0) {
				
				//criar a medicacao_luminosidade
				if(luminosidade !=0) {
			  String query1 = " insert into medicao_luminosidade (Data_Hora_Medicao, Valor_Medicao_Luminosidade)"
				        + " values (?, ?)";

				      // create the mysql insert preparedstatement
				      PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
				      preparedStmt1.setString (1, DataHora);
				      preparedStmt1.setDouble(2, luminosidade);

				      // execute the preparedstatement
				      preparedStmt1.execute();
				}
				    //criar a medicacao_temperatura
			      String query2 = " insert into medicao_temperatura (Data_Hora_Medicao, Valor_Medicao_Temperatura)"
					        + " values (?, ?)";
					      // create the mysql insert preparedstatement
					      PreparedStatement preparedStmt2 = connection.prepareStatement(query2);
					      preparedStmt2.setString (1, DataHora);
					      preparedStmt2.setDouble(2,  temperatura);

					      // execute the preparedstatement
					      preparedStmt2.execute();
					      
					      //colocar foiExportado=1
					      BasicDBObject newDocument = new BasicDBObject();
					  	newDocument.append("$set", new BasicDBObject().append("exported", 1));
					  	BasicDBObject searchQuery = new BasicDBObject().append("_id", new ObjectId(id));
					  	collection.update(searchQuery, newDocument);
					  	System.out.println("Valor da temperatura:" +temperatura);
					  	System.out.println("Valor do if:" +(LITemperatura + LITemperatura*0.4));
					  	
					  	//decidir se cria o alerta
					  	//alerta Vermelho Temperatura
					  	if( (temperatura <= (LITemperatura + LITemperatura*0.4)) || (temperatura >= LSTemperatura*0.9)){
					  		alertaVermelhoTemperatura=true;
					  		 String alertaVermelhoTemperatura = " insert into alerta_sensor (tipo, intensidade, datahoraalerta, valormedicao,descricao,limiteinferior,limitesuperior)"
								        + " values (?, ?, ?, ?, ?, ?, ?)";
								      // create the mysql insert preparedstatement
								      PreparedStatement preparedStmt3 = connection.prepareStatement(alertaVermelhoTemperatura);
								      preparedStmt3.setString(1, "temp");
								      preparedStmt3.setString(2,  "vermelho");
								      preparedStmt3.setString (3, DataHora);
								      preparedStmt3.setDouble(4,  temperatura);
								      preparedStmt3.setString (5, "O valor da temperatura aproxima-se criticamente dos limites");
								      preparedStmt3.setDouble(6,  LITemperatura);
								      preparedStmt3.setDouble(7, LSTemperatura);

								      // execute the preparedstatement
								      preparedStmt3.execute();
								      
					  	}else {
					  		alertaVermelhoTemperatura=false;
					  	}
					  //alerta Vermelho Luminosidade
					  	if( (luminosidade <= (LILuminosidade + LILuminosidade*0.4)) || (luminosidade >= LSLuminosidade*0.9) ){
					  		alertaVermelhoLuminosidade=true;
					  		String alertaVermelhoLuminosidade = " insert into alerta_sensor (tipo, intensidade, datahoraalerta, valormedicao,descricao,limiteinferior,limitesuperior)"
								        + " values (?, ?, ?, ?, ?, ?, ?)";
								      // create the mysql insert preparedstatement
								      PreparedStatement preparedStmt3 = connection.prepareStatement(alertaVermelhoLuminosidade);
								      preparedStmt3.setString(1, "lum");
								      preparedStmt3.setString(2,  "vermelho");
								      preparedStmt3.setString (3, DataHora);
								      preparedStmt3.setDouble(4,  luminosidade);
								      preparedStmt3.setString (5, "O valor da luminosidade aproxima-se criticamente dos limites");
								      preparedStmt3.setDouble(6,  LILuminosidade);
								      preparedStmt3.setDouble(7, LSLuminosidade);
   
								      // execute the preparedstatement
								      preparedStmt3.execute();
								      
					  	}else{
					  		alertaVermelhoLuminosidade=false;
					  	}
					  //alerta Laranja Temperatura
					  	if( (temperatura <= (LITemperatura + LITemperatura*0.8)) &&  (temperatura > (LITemperatura + LITemperatura*0.4)) || (temperatura >= (LSTemperatura*0.8) && temperatura < LSTemperatura*0.9) ){
					  		alertaLaranjaTemperatura=true; 
					  		String alertaLaranjaTemperatura = " insert into alerta_sensor (tipo, intensidade, datahoraalerta, valormedicao,descricao,limiteinferior,limitesuperior)"
								        + " values (?, ?, ?, ?, ?, ?, ?)";
								      // create the mysql insert preparedstatement
								      PreparedStatement preparedStmt3 = connection.prepareStatement(alertaLaranjaTemperatura);
								      preparedStmt3.setString(1, "temp");
								      preparedStmt3.setString(2,  "laranja");
								      preparedStmt3.setString (3, DataHora);
								      preparedStmt3.setDouble(4,  temperatura);
								      preparedStmt3.setString (5, "O valor da temperatura aproxima-se dos limites");
								      preparedStmt3.setDouble(6,  LITemperatura);
								      preparedStmt3.setDouble(7, LSTemperatura);

								      // execute the preparedstatement
								      preparedStmt3.execute();
								      
					  	}else {
					  		alertaLaranjaTemperatura=false; 
					  	}
					  //alerta Laranja Luminosidade
					  	if( (luminosidade <= (LILuminosidade + LILuminosidade*0.8)) &&  (luminosidade > (LILuminosidade + LILuminosidade*0.4)) || (luminosidade >= (LSLuminosidade*0.8) && luminosidade < LSLuminosidade*0.9) ){
					  		alertaLaranjaLuminosidade=true; 
					  		String alertaLaranjaLuminosidade = " insert into alerta_sensor (tipo, intensidade, datahoraalerta, valormedicao,descricao,limiteinferior,limitesuperior)"
								        + " values (?, ?, ?, ?, ?, ?, ?)";
								      // create the mysql insert preparedstatement
								      PreparedStatement preparedStmt3 = connection.prepareStatement(alertaLaranjaLuminosidade);
								      preparedStmt3.setString(1, "lum");
								      preparedStmt3.setString(2,  "laranja");
								      preparedStmt3.setString (3, DataHora);
								      preparedStmt3.setDouble(4, luminosidade);
								      preparedStmt3.setString (5, "O valor da luminosidade aproxima-se dos limites");
								      preparedStmt3.setDouble(6,  LILuminosidade);
								      preparedStmt3.setDouble(7, LSLuminosidade);

								      // execute the preparedstatement
								      preparedStmt3.execute();
								      
					  	}else{
					  		alertaLaranjaLuminosidade=false; 
					  	}
					  	//alerta Amarelo temperatura
						  	if(Math.abs(temperatura - valorAntigoTemperatura) > valorXtemperatura && alertaAmareloTemperatura==false && alertaVermelhoTemperatura==false && alertaLaranjaTemperatura==false) {
						  		alertaAmareloTemperatura=true;
						  		valorAnomaloTemperatura=temperatura;
						  	}
					  	
					  	if(alertaAmareloTemperatura==true && valorAntigoTemperatura!=0) {
					  		count++;
					  		if(count==2) {
						  		if(Math.abs(temperatura -valorAntigoTemperatura) >valorXtemperatura){
						  			 String alertaAmareloTemperatura = " insert into alerta_sensor (tipo, intensidade, datahoraalerta, valormedicao,descricao,limiteinferior,limitesuperior)"
										        + " values (?, ?, ?, ?, ?, ?, ?)";
										      // create the mysql insert preparedstatement
										      PreparedStatement preparedStmt3 = connection.prepareStatement(alertaAmareloTemperatura);
										      preparedStmt3.setString(1, "temp");
										      preparedStmt3.setString(2,  "Amarelo");
										      preparedStmt3.setString (3, DataHora);
										      preparedStmt3.setDouble(4,  temperatura);
										      preparedStmt3.setString (5, "Ocorreu um pico de temperatura");
										      preparedStmt3.setDouble(6,  LITemperatura);
										      preparedStmt3.setDouble(7, LSTemperatura);
	
										      // execute the preparedstatement
										      preparedStmt3.execute();
						  		}else {
						  			alertaAmareloTemperatura =false;
						  			count=0;
						  		}
					  		}
					  	}else {
					  		valorAntigoTemperatura=temperatura;
					  	}
						
					  //alerta Amarelo Luminosidade
					  	
					  	if(Math.abs(temperatura - valorAntigoTemperatura) > valorXtemperatura && alertaAmareloTemperatura==false && alertaVermelhoTemperatura==false && alertaLaranjaTemperatura==false) {
					  		alertaAmareloTemperatura=true;
					  		valorAnomaloTemperatura=temperatura;
					  	}
				  	
				  	if(alertaAmareloLuminosidade==true && valorAntigoLuminosidade!=0) {
				  		count++;
				  		if(count==2) { 
					  		if(Math.abs(luminosidade -valorAntigoLuminosidade) >valorXluminosidade){
					  			 String alertaAmareloLuminosidade = " insert into alerta_sensor (tipo, intensidade, datahoraalerta, valormedicao,descricao,limiteinferior,limitesuperior)"
									        + " values (?, ?, ?, ?, ?, ?, ?)";
									      // create the mysql insert preparedstatement
									      PreparedStatement preparedStmt3 = connection.prepareStatement(alertaAmareloLuminosidade);
									      preparedStmt3.setString(1, "lum");
									      preparedStmt3.setString(2,  "Amarelo");
									      preparedStmt3.setString (3, DataHora);
									      preparedStmt3.setDouble(4,  luminosidade);
									      preparedStmt3.setString (5, "Ocorreu um pico de luminosidade");
									      preparedStmt3.setDouble(6,  LILuminosidade);
									      preparedStmt3.setDouble(7, LSLuminosidade);

									      // execute the preparedstatement
									      preparedStmt3.execute();
					  		}else {
					  			alertaAmareloLuminosidade =false;
					  			count=0;
					  		}
				  		}
				  	}else {
				  		valorAntigoLuminosidade=luminosidade;
				  	}
					  
					  	
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
		if (result != null) result.close();
		if (statement != null) statement.close();
		if (connection != null) connection.close();
		}
		
		
	} catch (MongoException e) {
		e.printStackTrace();
	}
	}
	
	
	private static void ObterLimites(Statement stmt) throws SQLException {
		// TODO Auto-generated method stub
		 String sql = "SELECT Limite_Inferior_Temperatura,Limite_Superior_Temperatura, Limite_Inferior_Luminosidade,Limite_Superior_Luminosidade FROM Sistema";
	      ResultSet rs = stmt.executeQuery(sql);
	      //STEP 5: Extract data from result set
	      while(rs.next()){
	         //Retrieve by column name
	         LITemperatura = rs.getDouble("Limite_Inferior_Temperatura");
	         LSTemperatura = rs.getDouble("Limite_Superior_Temperatura");
	         LILuminosidade = rs.getDouble("Limite_Inferior_Luminosidade");
	         LSLuminosidade = rs.getDouble("Limite_Superior_Luminosidade");
	         valorXtemperatura= (LSTemperatura- LITemperatura)/2;
	         valorXluminosidade= (LSLuminosidade -LILuminosidade)/2;

	         //Display values
	         System.out.print("LITemperatura: " +  LITemperatura);
	         System.out.print(", LSTemperatura: " + LSTemperatura);
	         System.out.print(", LILuminosidade: " + LILuminosidade);
	         System.out.println(", LSLuminosidade: " + LSLuminosidade);
	      }
	      rs.close();
	}
	

}

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
	public static void main(String[] args) throws SQLException {
		
		try {

			MongoClient mongoClient = new MongoClient();
			mongoClient.getDatabaseNames().forEach(System.out::println);
			DB db = mongoClient.getDB("sensores");
			DBCollection collection = db.getCollection("sensor");
			
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
		System.out.println("MongoDB JDBC Driver has been registered...");

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
		
		
		DBCursor cursor = collection.find();
		while(cursor.hasNext()) {
		
			DBObject theObj = cursor.next();
			String content = theObj.toString();
			System.out.println(content);
			String[] tokens = content.split(",|:");
			//System.out.println(tokens[8]);
			String DataHora = tokens[8] +  tokens[10] + ":" + tokens[11] + ":" +tokens[12];
			System.out.println(DataHora);
			String luminosidade = tokens[14];
			String temperatura = tokens[4];
			luminosidade = luminosidade.replace(" ", "");
			temperatura = temperatura.replace(" ", "");
			System.out.println(luminosidade);
			String id =tokens[2];
			id = id.replace(" ", "");
			id = id.replace("}", "");
			id = id.substring(1);
			id = id.substring(0, id.length() - 1);
			// falta um if() para verificar se o foiExportado esta a um
			  String query1 = " insert into medicao_luminosidade (Data_Hora_Medicao, Valor_Medicao_Luminosidade)"
				        + " values (?, ?)";

				      // create the mysql insert preparedstatement
				      PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
				      preparedStmt1.setString (1, DataHora);
				      preparedStmt1.setInt(2, Integer.parseInt(luminosidade));

				      // execute the preparedstatement
				      preparedStmt1.execute();

				      
			      String query2 = " insert into medicao_temperatura (Data_Hora_Medicao, Valor_Medicao_Temperatura)"
					        + " values (?, ?)";
					      // create the mysql insert preparedstatement
					      PreparedStatement preparedStmt2 = connection.prepareStatement(query2);
					      preparedStmt2.setString (1, DataHora);
					      preparedStmt2.setDouble(2,  Double.parseDouble(temperatura));

					      // execute the preparedstatement
					      preparedStmt2.execute();
					      BasicDBObject newDocument = new BasicDBObject();
					  	newDocument.append("$set", new BasicDBObject().append("foiExportado", 1));
					  			
					  	BasicDBObject searchQuery = new BasicDBObject().append("_id", new ObjectId(id));

					  	collection.update(searchQuery, newDocument);
					  	
		  }
		
		} else {
		System.out.println("ERROR: Unable to make a database connection!");
		}

		System.out.println("Trying to get a list of all employees in employee collection...");
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
	

}

package mongo;

import java.sql.SQLException;

public class ExportingThread extends Thread{
	
	private MongoJDBCMain exporter;
	
	public ExportingThread() {
		this.exporter = new MongoJDBCMain();
	}

	public void run() {
		while(true) {
			try { 
				sleep(7000);
				if (exporter.getConnection() != null) {
					exporter.migracao();
				}else {
					System.out.println("ERROR: Unable to make a database connection!");
					break;
				}
				
			} catch (InterruptedException | SQLException e) { 
				e.printStackTrace();  
			}
		}
	} 

}   

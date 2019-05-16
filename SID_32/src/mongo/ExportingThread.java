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
					System.out.println("vou migrar");
					exporter.migracao(); 
					System.out.println("migracao acabada");
				}else {
					System.out.println("ERROR: Unable to make a database connection!");
					break;
				
				}

				System.out.println("Trying to get a list of all entrys in sensor collection...");
				
			} catch (InterruptedException | SQLException e) { 
				e.printStackTrace();  
			}
		}
	} 

}   

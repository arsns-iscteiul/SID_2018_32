package mongo;

import java.sql.SQLException;

public class ExportingThread extends Thread{
	
	private MongoJDBCMain exporter = new MongoJDBCMain();
	
	public void run() {
		while(true) {
			try { 
				sleep(7000);
				exporter.export();
			} catch (InterruptedException | SQLException e) {
				e.printStackTrace();  
			}
		}
	}

}

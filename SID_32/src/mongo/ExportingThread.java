package mongo;

import java.sql.SQLException;

public class ExportingThread extends Thread{
	
	private MongoJDBCMain exporter;
	private Conf cfg;
	private final int time;
	
	public ExportingThread() {
		cfg = new Conf("mongo/conf1.properties");
		this.exporter = new MongoJDBCMain(cfg);
		time=Integer.parseInt(cfg.getProperty("periodicity")) * 1000;
		System.out.println(time + "is the time imma gonna sleep");
	}

	public void run() {
		while(true) {
			try { 
				sleep(time);
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

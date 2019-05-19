package mongo;

import java.util.Properties;

public class Conf {
	

   Properties configFile;
   
   public Conf(String filename)   {
	   configFile = new java.util.Properties();
	   try {

		   configFile.load(this.getClass().getClassLoader().getResourceAsStream(filename));
	   }catch(Exception eta){
		   eta.printStackTrace();
	   }
   }
 
   public String getProperty(String key)
   {
	   String value = this.configFile.getProperty(key);
	   return value;
   }

}

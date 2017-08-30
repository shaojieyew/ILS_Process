package application.configurable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This class manage the config.properties file for the application
 * 
 * @author YEW SHAO JIE
 */
public class AppProperty {
	private static final Properties PROP = new Properties();
	private static final String CONFIG_FIlE="config.properties";
	
	 /**
	  * Get the value of an application property. Initialize new config.properties file if file not exist.
	  * 
	  * @param  property  the property of application
	  * @return      the value
	  */
	public static String getValue(String property) {
		String value ="";
		InputStream input = null;
		try {
			File varTmpDir = new File(CONFIG_FIlE);
			boolean exists = varTmpDir.exists();
			if(!exists){
				initialize();
			}
			input = new FileInputStream(CONFIG_FIlE);
			PROP.load(input);
			value=PROP.getProperty(property);
			
		} catch (IOException ex) {
				ex.printStackTrace();
		} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}
		return value;
	}

	 
	 /**
	  * Set the value of an application property 
	  * 
	  * @param  property  the property of application
	  * @param  value  value to set for the application property
	  */
	 public static void setValue(String property, String value) {
		 if(value==null){
			 value="";
		 }
		 if(property==null||property.length()==0){
			 return;
		 }
		OutputStream output = null;
		try {
			output = new FileOutputStream("config.properties");
			PROP.setProperty(property, value);
			PROP.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	 

	 /**
	  * Create a new config.properties file for the application, initialized with default value for application properties 
	  */
	 public static void initialize() {
			OutputStream output = null;
			try {
				output = new FileOutputStream("config.properties");
				PROP.setProperty("input", Paths.get("").toAbsolutePath().toString());
				PROP.setProperty("output", Paths.get("").toAbsolutePath().toString());
				PROP.setProperty("report_summary", "");
				PROP.setProperty("report_summary_sheet", "");
				PROP.setProperty("multi_thread", "2");
				PROP.setProperty("debug", "false");
				PROP.store(output, null);

			} catch (IOException io) {
				io.printStackTrace();
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
	}
}

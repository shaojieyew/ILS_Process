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
	public static final String PROP_input = "input";
	public static final String PROP_output = "output";
	public static final String PROP_report_summary = "report_summary";
	public static final String PROP_report_summary_sheet = "report_summary_sheet";
	public static final String PROP_multi_thread = "multi_thread";
	public static final String PROP_debug = "debug";
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
			output = new FileOutputStream(CONFIG_FIlE);
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
				output = new FileOutputStream(CONFIG_FIlE);
				PROP.setProperty(PROP_input, Paths.get("").toAbsolutePath().toString());
				PROP.setProperty(PROP_output, Paths.get("").toAbsolutePath().toString());
				PROP.setProperty(PROP_report_summary, "");
				PROP.setProperty(PROP_report_summary_sheet, "");
				PROP.setProperty(PROP_multi_thread, "2");
				PROP.setProperty(PROP_debug, "false");
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

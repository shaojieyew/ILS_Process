package application.configurable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class AppProperty {
	 public static final Properties PROP = new Properties();
	 public static final String CONFIG_FIlE="config.properties";
	
	 
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
	 
	 public static void initialize() {
			OutputStream output = null;
			try {
				output = new FileOutputStream("config.properties");
				PROP.setProperty("input", Paths.get("").toAbsolutePath().toString());
				PROP.setProperty("output", Paths.get("").toAbsolutePath().toString());
				PROP.setProperty("report_summary", "");
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
	 
	 public static void setValue(String property, String value) {
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
}

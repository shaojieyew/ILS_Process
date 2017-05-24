package application.configurable;

/*
 * Class for managing the output configuration 
 */

public final class OutputConfiguration {
	//singleton
	private static final OutputConfiguration INSTANCE = new OutputConfiguration();
	public static OutputConfiguration getInstance() {
        return INSTANCE;
    }
	
	//out folder configuration of the application
	private String outputDirectory=AppProperty.getValue("output");
	

	public void setDirectory(String newDir){
		AppProperty.setValue("output", newDir);
		outputDirectory=newDir;
	}
	public String getDirectory(){
		return outputDirectory;
	}

}

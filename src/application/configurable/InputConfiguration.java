package application.configurable;

import java.util.ArrayList;

/**
 * Class for managing the input configuration, such as the input folder or loading of summary file
 * 
 * @author YEW SHAO JIE
 */
public final class InputConfiguration {
	//Singleton
	private static final InputConfiguration INSTANCE = new InputConfiguration();
	/**
	 * This method return instance of this class to ensure singleton when accessing this class.
	 * 
	 * @return instance of this class
	 */
	public static InputConfiguration getInstance() {
        return INSTANCE;
    }

	
	/**
	 * A type of event change to the input configuration that is use for selective subscribing and publishing
	 */
	public static final String LISTEN_ReportSummaryFile="reportSummaryFile";

	/**
	 * A type of event change to the input configuration that is use for selective subscribing and publishing
	 */
	public static final String LISTEN_InputDirectory="input";
	
	//readable file types for the application
	private String fileType[] = {"pdf","png","html","htm","jpeg","jpg","tiff","bmp","gif"};
	//input folder of the application configuration
	private String inputDirectory =AppProperty.getValue("input");
	private String reportSummaryFile =AppProperty.getValue("report_summary");
	private String reportSummaryFile_sheet =AppProperty.getValue("report_summary_sheet");
	
	//List of listener listening to changes of this class's instance
	private ArrayList<InputChangeListener> inputChangeListeners = new ArrayList<InputChangeListener>();
	private ArrayList<InputChangeListener> reportSummaryChangeListeners = new ArrayList<InputChangeListener>();

	//notify changes to listener
	/**
	 * This method publish the content to the subscribe by selective.
	 * Only listener that subscribe to the type of event change will be notify of the corresponding type of event change.  
	 * 
	 * @param type A type of event change to the input configuration that is use for selective subscribing and publishing
	 */
	public void notifyChange(String type){
		ArrayList<InputChangeListener> listeners = null;
		switch(type){
		case LISTEN_InputDirectory:
			listeners=inputChangeListeners;
			break;
		case LISTEN_ReportSummaryFile:
			listeners=reportSummaryChangeListeners;
			break;
		}
		if(listeners!=null){
			for(int i =0;i<listeners.size();i++){
				listeners.get(i).onUpdateInput(this, type);	
			}
		}
	}

	//add new listener
	/**
	 * This method allow instances to subscribe to Input configuration, so that any new event change will notify the subscribed instance.
	 * The list of subscribe is stored in list for keeping track of them.
	 * 
	 * @param listener the instance to subscribe to the change event of input configuration.
	 * @param type A type of event change to the input configuration that is use for selective subscribing and publishing
	 */
	public void listenToChange(InputChangeListener listener, String type){
		switch(type){
		case LISTEN_InputDirectory:
			inputChangeListeners.add(listener);
			break;
		case LISTEN_ReportSummaryFile:
			reportSummaryChangeListeners.add(listener);
			break;
		}
	}
	//remove listener
	/**
	 * This method allow instances to remove subscription from Input configuration, so that any new event change will not notify instance.
	 * The list of subscribe is stored in list for keeping track of them.
	 * the instance of subscriber is removed from the list. 
	 * 
	 * @param listener the instance to subscribe to the change event of input configuration.
	 * @param type A type of event change to the input configuration that is use for selective subscribing and publishing
	 */
	public void unlistenToChange(InputChangeListener listener, String type){
		switch(type){
		case LISTEN_InputDirectory:
			inputChangeListeners.remove(listener);
			break;
		case LISTEN_ReportSummaryFile:
			reportSummaryChangeListeners.remove(listener);
			break;
		}
	}
	
	/**
	 * This method set the input folder.
	 * 
	 * @param newDir the directory of the input folder.
	 */
	public void setDirectory(String newDir){
		inputDirectory=newDir;
		AppProperty.setValue("input", newDir);
		notifyChange(LISTEN_InputDirectory);
	}

	/**
	 * This method get the directory of the input folder.
	 * 
	 * @return return the directory of the input folder
	 */
	public String getDirectory(){
		return inputDirectory;
	}

	/**
	 * This method get the list of file type that is compatible for the application input
	 * 
	 * @return return array list of file extension type.
	 */
	public String[] getFileType(){
		return fileType;
	}
	
	/**
	 * This method gets the absolute path of the summary file that is loaded to the application 
	 * 
	 * @return return absolute path of the summary file from the application property
	 */
	public String getReportSummaryFile() {
		return reportSummaryFile;
	}
	/**
	 * This method sets the absolute path of the summary file that is loaded to the application 
	 * 
	 * @param reportSummaryFile absolute path of the summary file from the application property
	 */
	public void setReportSummaryFile(String reportSummaryFile) {
		this.reportSummaryFile = reportSummaryFile;
		if(!reportSummaryFile.equals(reportSummaryFile)||reportSummaryFile==null||reportSummaryFile.length()==0){
			setReportSummaryFile_sheet("");
		}
		AppProperty.setValue("report_summary", reportSummaryFile);
		notifyChange(LISTEN_ReportSummaryFile);
	}


	/**
	 * This method gets the selected sheet for the summary file. This method is used for excel workbook 
	 * 
	 * @param return the selected sheet from the application property
	 */
	public String getReportSummaryFile_sheet() {
		return reportSummaryFile_sheet;
	}

	public void setReportSummaryFile_sheet(String reportSummaryFile_sheet) {
		this.reportSummaryFile_sheet = reportSummaryFile_sheet;
		AppProperty.setValue("report_summary_sheet", reportSummaryFile_sheet);
	}


}

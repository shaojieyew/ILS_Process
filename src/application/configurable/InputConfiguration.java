package application.configurable;

import java.util.ArrayList;

/*
 * Class for managing the input configuration 
 */

public final class InputConfiguration {
	//Singleton
	private static final InputConfiguration INSTANCE = new InputConfiguration();
	public static InputConfiguration getInstance() {
        return INSTANCE;
    }

	public static final String LISTEN_ReportSummaryFile="reportSummaryFile";
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
	

	public void setDirectory(String newDir){
		inputDirectory=newDir;
		AppProperty.setValue("input", newDir);
		notifyChange(LISTEN_InputDirectory);
	}
	public String getDirectory(){
		return inputDirectory;
	}

	public String[] getFileType(){
		return fileType;
	}
	

	public String getReportSummaryFile() {
		return reportSummaryFile;
	}

	public void setReportSummaryFile(String reportSummaryFile) {
		this.reportSummaryFile = reportSummaryFile;
		if(!reportSummaryFile.equals(reportSummaryFile)||reportSummaryFile==null||reportSummaryFile.length()==0){
			setReportSummaryFile_sheet("");
		}
		AppProperty.setValue("report_summary", reportSummaryFile);
		notifyChange(LISTEN_ReportSummaryFile);
	}

	public String getReportSummaryFile_sheet() {
		return reportSummaryFile_sheet;
	}

	public void setReportSummaryFile_sheet(String reportSummaryFile_sheet) {
		this.reportSummaryFile_sheet = reportSummaryFile_sheet;
		AppProperty.setValue("report_summary_sheet", reportSummaryFile_sheet);
	}


}

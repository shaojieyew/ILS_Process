package reportProcessor;

import java.io.File;
import javafx.collections.ObservableList;
import report.Report;
import reportSummary.ReportSummary;
import reportSummary.ReportSummaryFactory;

/*
 * Main Process for managing multiple threads for processing
 */
public class SummaryProcessor extends Processor implements Runnable{
	private static SummaryProcessor INSTANCE;
	
	private ObservableList<Report> reports;
	private String outputDirectory;
	private File destFile;
	private Object summaryFile;
	private String processName;
	private int writeType;
	
	//constructor setup list of reports to process
	public SummaryProcessor(ObservableList<Report> data, String outputDirectory, File destFile, Object summaryFile,String processName, int writeType){

		this.reports=data;
		this.outputDirectory= outputDirectory;
		this.destFile = destFile;
		this.summaryFile = summaryFile;
		this.processName = processName;
		this.writeType = writeType;
	}
	
	//singleton method
	public static SummaryProcessor getInstance(ObservableList<Report> data, String outputDirectory, File destFile, Object summaryFile,String processName, int writeType) {
		INSTANCE = new SummaryProcessor(data,outputDirectory,destFile,summaryFile,processName,writeType);
        return INSTANCE;
    }
	
	@Override
	public void run() {
		started();
		boolean result = true;
		ReportSummary reportSmmary = ReportSummaryFactory.createInstance(summaryFile);
		if(reportSmmary!=null){
			summaryFile = reportSmmary.process(reports,processName,writeType);
			result = reportSmmary.save(destFile); 
		}
		if(result){
			completed();
		}else{
			failed();
		}
	}
	
	public ObservableList<Report> getReports() {
		return reports;
	}

	public void setReports(ObservableList<Report> reports) {
		this.reports = reports;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public File getDestFile() {
		return destFile;
	}

	public void setDestFile(File destFile) {
		this.destFile = destFile;
	}

	public Object getSummaryFile() {
		return summaryFile;
	}

	public void setSummaryFile(Object summaryFile) {
		this.summaryFile = summaryFile;
	}


}

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
	
	//constructor setup list of reports to process
	public SummaryProcessor(ObservableList<Report> data, String outputDirectory, File destFile, Object summaryFile){

		this.reports=data;
		this.outputDirectory= outputDirectory;
		this.destFile = destFile;
		this.summaryFile = summaryFile;
	}
	
	//singleton method
	public static SummaryProcessor getInstance(ObservableList<Report> data, String outputDirectory, File destFile, Object summaryFile) {
		INSTANCE = new SummaryProcessor(data,outputDirectory,destFile,summaryFile);
        return INSTANCE;
    }
	
	@Override
	public void run() {
		started();
		/*
		String output="";
		for(Report report : reports){
			if(report.getStatus().equals(Report.STATUS_COMPLETED)){
				output = output+"===============Name and ILS Attributes==============";
				 output = output+System.getProperty("line.separator").toString();
				 output = output+ "NAME: "+report.getAuthor_name()+ System.getProperty("line.separator").toString();
				 for(AttributeIndex ai : report.getAttributes()){
					 output = output + ai.getAttribute()+": "+ai.getIndex()+System.getProperty("line.separator").toString();
				 }
			}
		}
		File folder = new File(outputDirectory);
		if(!folder.exists()){
			try {
				FileUtils.forceMkdir(folder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileUtility.writeWordsToText(output,outputDirectory+"\\ILS_Output.txt");
		*/
		
		ReportSummary reportSmmary = ReportSummaryFactory.createInstance(summaryFile);
		if(reportSmmary!=null){
			reportSmmary.process(reports);
			reportSmmary.save(destFile); //slow
		}
		completed();
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

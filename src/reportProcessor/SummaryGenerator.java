package reportProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.FileUtils;

import application.AttributeIndex;
import application.configurable.AppProperty;
import javafx.collections.ObservableList;
import report.Report;
import report.ReportObservable;
import reportSummary.ReportSummary;
import reportSummary.ReportSummaryExcelLayout;
import reportSummary.ReportSummaryFactory;
import util.FileUtility;

/*
 * Main Process for managing multiple threads for processing
 */
public class SummaryGenerator extends Processor implements Runnable{
	private static SummaryGenerator INSTANCE;
	
	private ObservableList<Report> reports;
	private String outputDirectory;
	private File destFile;
	private Object summaryFile;
	
	//constructor setup list of reports to process
	public SummaryGenerator(ObservableList<Report> data, String outputDirectory, File destFile, Object summaryFile){

		this.reports=data;
		this.outputDirectory= outputDirectory;
		this.destFile = destFile;
		this.summaryFile = summaryFile;
	}
	
	//singleton method
	public static SummaryGenerator getInstance(ObservableList<Report> data, String outputDirectory, File destFile, Object summaryFile) {
		INSTANCE = new SummaryGenerator(data,outputDirectory,destFile,summaryFile);
        return INSTANCE;
    }
	
	@Override
	public void run() {
		started();
		System.out.println("@@@@@@@@start generator");
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
			System.out.println("@@@@@@@@start generator process");
			reportSmmary.process(reports);
			System.out.println("@@@@@@@@start generator save");
			reportSmmary.save(destFile);
		}
		System.out.println("@@@@@@@@start generator DONE");
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

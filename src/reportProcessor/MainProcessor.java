package reportProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.FileUtils;

import application.AttributeIndex;
import application.Report;
import application.ReportObservable;
import application.configurable.AppProperty;
import javafx.collections.ObservableList;
import reportSummary.ReportSummary;
import reportSummary.ReportSummaryExcelLayout;
import reportSummary.ReportSummaryFactory;
import util.FileUtility;

/*
 * Main Process for managing multiple threads for processing
 */
public class MainProcessor implements Runnable{
	private static MainProcessor INSTANCE;
	private ObservableList<Report> reports;
	//variable keep track of the progress
	private int totalCount = 0 ;
	private boolean cancelProcess = false;
	private boolean reprocessCompletedFile = false;
	
	/*
	private int completedCount = 0 ;
	//get process of the processing
	public int getPercentageCompleted(){
		return (int)(((float)completedCount/(float)totalCount)*100f);
	}
	*/
	
	//constructor setup list of reports to process
	public MainProcessor(ObservableList<Report> data, int numberOfThread, boolean reprocessCompletedFile){
		if(numberOfThread<=0){
			numberOfThread=1;
		}
		this.count_thread = new Semaphore(numberOfThread);
		this.reports=data;
		this.totalCount=data.size();
		this.reprocessCompletedFile = reprocessCompletedFile;
	}
	
	//singleton method
	public static MainProcessor getInstance(ObservableList<Report> data, int numberOfThread, boolean reprocessCompletedFile) {
		INSTANCE = new MainProcessor(data,numberOfThread,reprocessCompletedFile);
        return INSTANCE;
    }
	
	//setup counting semaphore for multithreading
	private Semaphore count_thread;
	public void releaseSemaphore(){
		count_thread.release();
	}
	
	public void forceStopProcess(){
		cancelProcess=true;
	}
	
	List<Thread> listOfThreads = new ArrayList<Thread>();
	
	@Override
	public void run() {
		cancelProcess = false;
		listOfThreads.clear();
		for(int i =0;i<totalCount;i++){
			try {
				count_thread.acquire();
				if(cancelProcess){
					count_thread.release();
					break;
				}
				//start a sub-thread to process a report
				Thread thread1 = new Thread(new ReportProcessor(this,reports.get(i),i,reprocessCompletedFile));
				thread1.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean isCancelProcess() {
		return cancelProcess;
	}
	
	public void generateReport(){
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
		File folder = new File(AppProperty.getValue("output"));
		if(!folder.exists()){
			try {
				FileUtils.forceMkdir(folder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileUtility.writeWordsToText(output,AppProperty.getValue("output")+"\\ILS_Output.txt");
		
		
		ReportSummary reportSmmary = ReportSummaryFactory.getInstance();
		if(reportSmmary!=null){
			reportSmmary.process(reports);
		}
	}

}

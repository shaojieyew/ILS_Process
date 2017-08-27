package reportProcessor;

import java.io.File;

import application.configurable.AppProperty;
import report.AttributeIndex;
import report.Report;
import reportProcessor.analysis.DataCorrection;
import reportProcessor.analysis.ReportDataReader;
import reportProcessor.analysis.ReportDataReaderBySplit;
import reportProcessor.extraction.DataExtract;
import reportProcessor.extraction.DataExtractFactory;
import util.FileUtility;
/*
 * Class for processing the report
 * Make call to data extraction, data correction and reading
 */
public class DataExtractProcessor extends Processor implements Runnable{
	private static final boolean DEBUG=false;
	
	private boolean reprocessCompletedFile=false;
	private MainDataExtractProcessor mainProcess;  //the parent thread that create the thread of this class.
	private int index; //index of which the report is in the tableview
	private Report report;
	
	//constructor
	public DataExtractProcessor(MainDataExtractProcessor mainProcess, Report report, int index, boolean reprocessCompletedFile){
		this.mainProcess =mainProcess;
		this.index = index;
		this.report = report;
		this.reprocessCompletedFile = reprocessCompletedFile;
	}
	
	//start thread
	@Override
	public void run() {
		started();
		if((!report.getStatus().equals(Report.STATUS_INVALID_FILE))&&(!report.getStatus().equals(Report.STATUS_COMPLETED)||reprocessCompletedFile)){
			preProcess();
			runProcess();
			postProcess();
		}
		mainProcess.releaseSemaphore();
		completed();
	}

	
	
	private void preProcess() {
		report.setStatus(Report.STATUS_IN_PROCESSING);
		report.getAttributes().clear();
		report.setAuthor_name(null);
	}

	private void postProcess() {
		if(new File(report.getPath()).exists()){
			if(report.validateFile()){
				if(report.validateData()){
					report.setStatus(Report.STATUS_COMPLETED);
				}else{
					report.setStatus(Report.STATUS_FAILED);
				}
			}else{
				report.setStatus(Report.STATUS_INVALID_FILE);
			}
		}else{
			report.setStatus(Report.STATUS_NOT_FOUND);
		}
	}
	public void runProcess(){
		 try {
			 //Extract data from report
			 File file = new File(report.getPath());
			 DataExtract dataExtract =  DataExtractFactory.getInstance(file);
			 dataExtract.processFile();
			 String text = dataExtract.getText();
			 String org=text;
			 
			 //Correct data due to text from low quality or unclear image file
			 DataCorrection dc = new DataCorrection(text);
			 text=dc.getCorrectedText(DataCorrection.STRICTNESS_VERY_STRICT,DataCorrection.STRICTNESS_STRICT);
				
			 
			 //Read the specific data from the extracted data
			 ReportDataReader rdr = new ReportDataReaderBySplit(text,report);
			 report = rdr.getReport();
			 //Check if the data is good enough
			 boolean retry=false;
			 int nonZeroCount=0;
			 int zeroCount=0;
			 	//check if author name exist
			 if(report.getAuthor_name()==null||report.getAuthor_name().length()==0){
				 retry=true;
			 } 
			 for(AttributeIndex ai : report.getAttributes()){
				 if(ai.getIndex()==0){
					 zeroCount++;
				 }else{
					 nonZeroCount++;
				 }
			 }
			 	//check if learning index have scores for 4 attributes
			 if(zeroCount!=4&&nonZeroCount!=4){
				 retry=true;
			 }
			 
			 if(retry){
				 //re-extract data with other method; reProcessFile()
				 dataExtract.reProcessFile();
				 text = dataExtract.getText();
				 org=text;
				 if(text!=null&&text.length()>0){
					 /*re-correct data*/
					 dc = new DataCorrection(text);
					 text=dc.getCorrectedText(DataCorrection.STRICTNESS_LESS_STRICT,DataCorrection.STRICTNESS_STRICT);
					 /*re-read data*/
					 rdr = new ReportDataReaderBySplit(text,report);
					 report = rdr.getReport();
				 }
			 }
			 
			 if(DEBUG){
			 //===============print out for debug===========
			 String output="";
			 
			 output = output+"==================Data Extracted====================";
			 output = output+System.getProperty("line.separator").toString();
			 output=output+org;
			 output = output+System.getProperty("line.separator").toString();
			 output = output+"==================Data Corrected====================";
			 output = output+System.getProperty("line.separator").toString();
			 output=output+text;
			 output = output+System.getProperty("line.separator").toString();
			 
			 output = output+"===============Name and ILS Attributes==============";
			 output = output+System.getProperty("line.separator").toString();
			 output = output+ "NAME: "+report.getAuthor_name()+ System.getProperty("line.separator").toString();
			 for(AttributeIndex ai : report.getAttributes()){
				 output = output + ai.getAttribute()+": "+ai.getIndex()+System.getProperty("line.separator").toString();
			 }
			 FileUtility.writeWordsToText(output,AppProperty.getValue("output")+"\\ILS_"+report.getAuthor_name()+".txt");
			 
			 //============================================
			 }
		} catch (Exception e) {
			//e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	

}

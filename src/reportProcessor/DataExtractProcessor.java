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
		if(!report.getStatus().equals(Report.STATUS_COMPLETED)||reprocessCompletedFile){
			preProcess();
			runProcess();
		}
		postProcess();
		completed();
	}

	private void preProcess() {
		report.setStatus(Report.STATUS_IN_PROCESSING);
	}

	private boolean validateDataExtracted(){
		int zeroCount = 0;
		int nonZeroCount = 0;
		boolean fail= false;
		if(report.getAuthor_name()==null||report.getAuthor_name().length()==0){
			 fail=true;
			//report.setAuthor_name(report.getFileName());
		} 
		for(int i =0;i<AttributeIndex.LEFT_ILS_INDEX.length;i++){
			AttributeIndex leftIndex =report.getAttributeIndexByAttribute(AttributeIndex.LEFT_ILS_INDEX[i]);
			AttributeIndex rightIndex =report.getAttributeIndexByAttribute(AttributeIndex.RIGHT_ILS_INDEX[i]);
			int leftIndexValue=0,rightIndexValue=0;
			if(leftIndex!=null){
				leftIndexValue = leftIndex.getIndex();
			}
			if(rightIndex!=null){
				rightIndexValue = rightIndex.getIndex();
			}
			
			if(leftIndexValue==0&&rightIndexValue==0){
				 fail=true;
				 break;
			}
			if(leftIndexValue!=0&&rightIndexValue!=0){
				 fail=true;
				 break;
			}
			if(leftIndexValue!=0){
				if(leftIndexValue%2==0||leftIndexValue<0||leftIndexValue>11){
					 fail=true;
					 break;
				}
			}
			if(rightIndexValue!=0){
				if(rightIndexValue%2==0||rightIndexValue<0||rightIndexValue>11){
					 fail=true;
					 break;
				}
			}
		}
		return !fail;
	}
	
	private boolean validateFile(){
		boolean no_name=false;
		if(report.getAuthor_name()==null||report.getAuthor_name().length()==0){
			no_name=true;
		}
		int totalIndex = 0;
		for(AttributeIndex ai : report.getAttributes()){
			totalIndex = totalIndex+ai.getIndex();
		}
		if(totalIndex==0 && no_name){
			return false;
		}
		return true;
	}
	
	private void postProcess() {
		mainProcess.releaseSemaphore();
		
		
		/*report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_ACTIVE);
		for(AttributeIndex ai : report.getAttributes()){
			 if(ai.getIndex()%2==0||ai.getIndex()<0||ai.getIndex()>11){
				 fail=true;
				 break;
			 }else{
				 
			 }
		}*/
		 
		if(new File(report.getPath()).exists()){
			if(validateFile()){
				if(validateDataExtracted()){
					report.setStatus(Report.STATUS_COMPLETED);
				}else{
					report.setStatus(Report.STATUS_FAILED);
				}
			}else{
				report.setStatus(Report.STATUS_FAILED);
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
			 ReportDataReader rdr = new ReportDataReaderBySplit(text);
			 report.setAuthor_name(rdr.getReport_name());
			 report.setAttributes(rdr.getAttributeList());
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
					 rdr = new ReportDataReaderBySplit(text);
					 report.setAuthor_name(rdr.getReport_name());
					 report.setAttributes(rdr.getAttributeList());
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

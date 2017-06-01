package application.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.MainApplication;
import application.configurable.AppProperty;
import application.configurable.InputChangeListener;
import application.configurable.InputConfiguration;
import application.configurable.OutputConfiguration;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import javafx.stage.FileChooser.ExtensionFilter;
import report.Report;
import report.ReportChangeListener;
import report.ReportObservable;
import reportProcessor.MainProcessor;
import reportProcessor.Processor;
import reportProcessor.ProcessorListener;
import reportProcessor.SummaryProcessor;
import reportSummary.ReportSummaryFactory;
import util.AppDialog;
import util.FilesChooser;
import util.FolderChooser;

/*
 * Controller class for Main.fxml in application.css
 */

public class MainController extends FXMLController implements Initializable,InputChangeListener,ReportChangeListener {
	@FXML
	private TextField inputTextfield;
	@FXML
	private TextField outputTextfield;
	@FXML
	private TableView<Report> tableview;
	@FXML
	private Button cancelBtn;
	@FXML
	private Button startBtn;
	@FXML
	private Label progressLabel;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private BorderPane rootPane;
	private Node leftPane=null;

	public static final String defaultFileName = "ILS-Result";
	
	public MainProcessor mainProcessor;
	
	public MainController() {
		addListener(InputConfiguration.LISTEN_InputDirectory);
		addListener(InputConfiguration.LISTEN_ReportSummaryFile);
		addReportProcessListener();
    }

	
	@FXML
	public void initialize(URL fxmlurl, ResourceBundle arg1) {
		//setup initial input folder
		String inputPath = InputConfiguration.getInstance().getDirectory();
		inputTextfield.setText(inputPath);
		//setup initial output folder
		String outputPath = OutputConfiguration.getInstance().getDirectory();
		outputTextfield.setText(outputPath);
		//initialize tableview 
		ReportTableViewFactory.getInstance(tableview).updateListByInputDirectory();
		updateProgressBar("");
		
		//initialize leftPane;
		try {
	        FXMLLoader sideBarLoader = new FXMLLoader();
	        sideBarLoader.setLocation(MainApplication.class.getResource("gui/SideBar.fxml"));
	        Node sideBarRootLayout = sideBarLoader.load();
	        FXMLController controller = (FXMLController)sideBarLoader.getController();
	        controller.setStageAndSetupListeners(getStage());
	        leftPane=sideBarRootLayout;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputConfiguration.getInstance().setReportSummaryFile(InputConfiguration.getInstance().getReportSummaryFile());
	}

	public Node getLeftPane() {
		return leftPane;
	}
	public void setLeftPane(Node leftPane) {
		this.leftPane = leftPane;
	}
	
	@FXML
	public void selectInputAction(){
		//select input directory
		File f = FolderChooser.show(getStage(), "Select Input Directory",inputTextfield.getText());
		if(f!=null){
			InputConfiguration.getInstance().setDirectory(f.getPath());
			completedCount=0;
			failedCount = 0;
			inProcessCount = 0;
			updateProgressBar("");
		}
	}
	@FXML
	public void selectOutputAction(){
		//select output directory
		File f = FolderChooser.show(getStage(), "Select Output Directory",outputTextfield.getText());
		if(f!=null){
			OutputConfiguration.getInstance().setDirectory(f.getPath());
			outputTextfield.setText(f.getPath());
		}
	}
	

	//process the list of reports in the tableview
	@FXML
	public void startProcess(){
		int result = 1;
		boolean rerunProcess=false;
		if(completedCount>0){
			String[] buttonNames = {"Re-process","Skip"};
			 result = AppDialog.multiButtonDialog(buttonNames , "Re-process completed file?", "");
			if(result==-1){
				return;
			}
			if(result==0){
				rerunProcess=true;
			}
		}
		
		//disable all control will processing
		diableAllControls(true);
		//get report from the tableview
		completedCount = 0;
		failedCount = 0;
		inProcessCount = 0;
		ObservableList<Report> data = tableview.getItems();
		int multi_thread_count = 1;
		try{
			multi_thread_count = Integer.parseInt(AppProperty.getValue("multi_thread"));
		}catch(Exception ex){
			
		}
		mainProcessor = MainProcessor.getInstance(data,multi_thread_count,rerunProcess);
		mainProcessor.addListener(new ProcessorListener(){
			@Override
			public void onComplete(Processor process) {
				generateSummary();
			}

			@Override
			public void onStart(Processor process) {
			}
		});
		Thread mainProcessorThread = new Thread(mainProcessor);
		mainProcessorThread.start();
		cancelBtn.setDisable(false);
	}
	
	private void diableAllControls(boolean disable){
		startBtn.getParent().setDisable(disable);
		leftPane.setDisable(disable);
		rootPane.getTop().setDisable(disable);
		cancelBtn.setDisable(disable);
	}
	
	@FXML
	public void cancelProcess(){
		cancelBtn.setDisable(true);
		if(mainProcessor!=null){
			updateProgressBar("Cancelling, please wait.");
			mainProcessor.forceStopProcess();
		}
		//finish touch up and generate report
		//if(mainProcessor==null||nonInProcess){
		//	diableAllControls(false);
		//	cancelBtn.setDisable(true);
		//}
	}
	

	private void generateSummary() {
		File destFile = null;
		XSSFWorkbook book;
		XSSFSheet sheet = null;
		
		if(InputConfiguration.getInstance().getReportSummaryFile().length()>0){
			
			File file = new File(InputConfiguration.getInstance().getReportSummaryFile());
			if(!file.exists()){
				InputConfiguration.getInstance().setReportSummaryFile("");
				InputConfiguration.getInstance().setReportSummaryFile_sheet("");
			}else{
				destFile=file;
				String sheetName = InputConfiguration.getInstance().getReportSummaryFile_sheet();
				
					FileInputStream fis;
					try {
						fis = new FileInputStream(file.getAbsolutePath());
						book = new XSSFWorkbook(fis);
						if(sheetName.length()>0){
							sheet = book.getSheet(sheetName);
							if(sheet==null){
								book.createSheet(sheetName);
								sheet=book.getSheet(sheetName);
							}
						}else{
							sheet = book.createSheet();
							sheetName = sheet.getSheetName();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} 
			}
		}
		if(InputConfiguration.getInstance().getReportSummaryFile().length()<=0){

			book = new XSSFWorkbook();	
			sheet = book.createSheet();	
			destFile = new File(OutputConfiguration.getInstance().getDirectory()+"\\"+defaultFileName+".xlsx");
			int count = 1;
			while(destFile.exists()){
				destFile = new File(OutputConfiguration.getInstance().getDirectory()+"\\"+defaultFileName+" ("+count+").xlsx");
				count++;
			}
		}
		SummaryProcessor summaryProcess = new SummaryProcessor(tableview.getItems(), OutputConfiguration.getInstance().getDirectory(),destFile, sheet);
		Thread thread1 = new Thread(summaryProcess);
		thread1.start();
		summaryProcess.addListener(new ProcessorListener(){
			@Override
			public void onComplete(Processor process) {
				Platform.runLater(new Runnable() {
	                 @Override public void run() {
	                		InputConfiguration.getInstance().setReportSummaryFile_sheet(((XSSFSheet)((SummaryProcessor)process).getSummaryFile()).getSheetName());
	                		InputConfiguration.getInstance().setReportSummaryFile(((SummaryProcessor)process).getDestFile().getAbsolutePath());
	        				diableAllControls(false);
	        				cancelBtn.setDisable(true);
	        				updateProgressBar("Complete");
	                 }
				});
			}

			@Override
			public void onStart(Processor process) {

				Platform.runLater(new Runnable() {
	                 @Override public void run() {
	     				updateProgressBar("Generating Summary");
	                 }
				});
			}
		});
	}


	//on input configuration changed
	@Override
	public void onUpdateInput(InputConfiguration inputDirectory, String type) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if(type.equals(InputConfiguration.LISTEN_InputDirectory)){
					inputTextfield.setText(inputDirectory.getDirectory());
				}
				if(type.equals(InputConfiguration.LISTEN_ReportSummaryFile)){
					if(inputDirectory.getReportSummaryFile()!=null&&inputDirectory.getReportSummaryFile().length()>0){
				        if(new File(inputDirectory.getReportSummaryFile()).exists()){
							rootPane.setLeft(leftPane);
				        }else{
				        	inputDirectory.setReportSummaryFile("");
				        }
					}else{
				        rootPane.setLeft(null);
				        ReportSummaryFactory.deleteInstance();
					}
				}
			}
			
		});
	}

	//Subscribe to new changes in input configuration
	@Override
	public void addListener(String type) {
		InputConfiguration.getInstance().listenToChange(this,type);
	}
	
	//Un-subscribe to new changes in input configuration
	@Override
	public void removeListener(String type) {
		InputConfiguration.getInstance().unlistenToChange(this,type);
	}

	
	@Override
	public void addReportProcessListener() {
		ReportObservable.listenToChange(this);
	}

	@Override
	public void removeReportProcessListener() {
		ReportObservable.unlistenToChange(this);
	}

	private static final Semaphore lockStatCounter = new Semaphore(1);
	
	public int completedCount = 0;
	public int failedCount = 0;
	public int inProcessCount = 0;
	@Override
	public void onUpdateReport(ReportObservable reportObservable) {
		try {
			lockStatCounter.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			//update UI
			if((Report.STATUS_IN_PROCESSING).equals(reportObservable.getStatus())){
				inProcessCount++;
			}
			if((Report.STATUS_COMPLETED).equals(reportObservable.getStatus())){
				inProcessCount--;
				completedCount++;
			}
			if((Report.STATUS_FAILED).equals(reportObservable.getStatus())||(Report.STATUS_NOT_FOUND).equals(reportObservable.getStatus())){
				inProcessCount--;
				failedCount++;
			}
			lockStatCounter.release();
			Platform.runLater(new Runnable() {
                 @Override public void run() {
         			tableview.getColumns().get(0).setVisible(false);
         			tableview.getColumns().get(0).setVisible(true);
     			
         				if((Report.STATUS_COMPLETED).equals(reportObservable.getStatus())||(Report.STATUS_FAILED).equals(reportObservable.getStatus())||(Report.STATUS_NOT_FOUND).equals(reportObservable.getStatus())){
    	         			double percentageProcess= updateProgressBar("");
    	         			if(percentageProcess==1||mainProcessor.isCancelProcess()){
    	             			cancelProcess();
    	         			}
         				}
         				
                 }
             });
			
	}
	
	public double updateProgressBar(String message){
		int reportToProcess = ReportTableViewFactory.getInstance(tableview).getTotalGetReport();
		double percentageProcess = (float)(completedCount+failedCount)/(float)reportToProcess;
		progressBar.setProgress(percentageProcess);
		progressLabel.setText("("+(completedCount+failedCount)+"/"+reportToProcess + ") "+message);
		return percentageProcess;
	}
	

	@FXML
	public void onImportExcel(){
		ExtensionFilter filters[] = {FilesChooser.FORMAT_EXCEL};
		File file = FilesChooser.show(getStage(), "Select Excel file", OutputConfiguration.getInstance().getDirectory(), filters);
		//setImportedFile(file);
		if(file!=null){
			InputConfiguration.getInstance().setReportSummaryFile(file.getAbsolutePath());
		}
	}

	@FXML
	public void onClickNewExcel(){
		ExtensionFilter [] filters = {FilesChooser.FORMAT_EXCEL};
		File file = FilesChooser.save(getStage(), "Save new excel file",OutputConfiguration.getInstance().getDirectory(),filters );
		if(file != null){
			 try {
				 XSSFWorkbook workbook = new XSSFWorkbook();
				 XSSFSheet sheet = workbook.createSheet();
				 String sheetName = sheet.getSheetName();
				 //ReportSummaryExcelLayout.createNewLayout(sheet);
			     FileOutputStream out =  new FileOutputStream(file.getAbsoluteFile());
			     workbook.write(out);
			     out.close();
				 InputConfiguration.getInstance().setReportSummaryFile(file.getAbsolutePath());
				 InputConfiguration.getInstance().setReportSummaryFile_sheet(sheetName);
		        } catch (IOException ex) {
		    }
		}
	}
}

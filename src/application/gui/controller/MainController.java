package application.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser.ExtensionFilter;
import report.Report;
import report.ReportChangeListener;
import report.ReportObservable;
import reportProcessor.MainDataExtractProcessor;
import reportProcessor.Processor;
import reportProcessor.ProcessorListener;
import reportProcessor.SummaryProcessor;
import reportSummary.ReportSummaryFactory;
import util.AppDialog;
import util.FileUtility;
import util.FilesChooser;
import util.FolderChooser;

/*
 * Controller class for Main.fxml in application.css
 */

public class MainController extends FXMLController implements Initializable,InputChangeListener,ReportChangeListener {
	@FXML
	private TextField inputTextfield;
	@FXML
	private TextField textField_outputFile;
	@FXML 
	private GridPane importedFilePane;
	
	@FXML
	private Button removeInvalidBtn;
	@FXML
	private Button refreshBtn;
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
	@FXML
	private Button importBtn;
	@FXML
	private ComboBox<String> comboBoxSheets;

	public static final String defaultFileName = "ILS-Result";
	
	public MainDataExtractProcessor mainProcessor;

	public String getImportedFile() {
		return InputConfiguration.getInstance().getReportSummaryFile();
	}

	public void setImportedFile(String importedFile) {
        textField_outputFile.setText(importedFile);
		File f = new File(importedFile);
		if(importedFile!=null&&importedFile.length()>0&&f.exists()){
			/* LOL
			rootPane.setLeft(leftLayout);
			*/
			importedFilePane.visibleProperty().set(true);
			loadExcelSheetToComboBox();
		}else{
			/* LOL
	        rootPane.setLeft(null);
	        */
			importedFilePane.visibleProperty().set(false);
	        //textField_outputFile.setText("");
	        //ReportSummaryFactory.deleteInstance();
		}
	}

	
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
		
		//initialize tableview 
		ReportTableViewFactory.getInstance(tableview).updateListByInputDirectory(InputConfiguration.getInstance());
		updateProgressBar("");
		textField_outputFile.setText(getImportedFile());
		InputConfiguration.getInstance().setReportSummaryFile(getImportedFile());
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
		mainProcessor = MainDataExtractProcessor.getInstance(data,multi_thread_count,rerunProcess);
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
	
	TableViewSelectionModel<Report> tableviewSelectionModel = null;
	private void diableAllControls(boolean disable){
		startBtn.getParent().setDisable(disable);
		removeInvalidBtn.setDisable(disable);
		refreshBtn.setDisable(disable);
		if(tableview.getSelectionModel()!=null){
			tableviewSelectionModel=tableview.getSelectionModel();
		}
		if(disable){
			tableview.setSelectionModel(null);
		}else{
			tableview.setSelectionModel(tableviewSelectionModel);
		}
		if(rootPane.getRight()!=null){
			if(rootPane.getRight().getId().equals(SidebarUpdateReportController.PANE_ID)){
				rootPane.getRight().setDisable(disable);
			}
		}
		importedFilePane.setDisable(disable);
		cancelBtn.setDisable(disable);
	}
	
	@FXML
	public void onCancelProcess(){
		updateProgressBar("Cancelling, please wait.");
		cancelProcess();
	}
	
	private void cancelProcess(){
		cancelBtn.setDisable(true);
		if(mainProcessor!=null){
			mainProcessor.forceStopProcess();
		}
	}
	
	

	private void generateSummary() {
		File destFile = null;
		XSSFWorkbook book;
		XSSFSheet sheet = null;
		String importedFilePath= getImportedFile();
		
		if(importedFilePath.length()>0){
			
			File file = new File(importedFilePath);
			if(!file.exists()){
				book = new XSSFWorkbook();	
				sheet = book.createSheet();	
				destFile = new File(importedFilePath);
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
		}else{
			if(importedFilePath.length()<=0){
				book = new XSSFWorkbook();	
				sheet = book.createSheet();	
				destFile = new File(OutputConfiguration.getInstance().getDirectory()+"\\"+defaultFileName+".xlsx");
				int count = 1;
				while(destFile.exists()){
					destFile = new File(OutputConfiguration.getInstance().getDirectory()+"\\"+defaultFileName+" ("+count+").xlsx");
					count++;
				}
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
					
					setImportedFile(inputDirectory.getReportSummaryFile());
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
		completedCount = 0;
		failedCount = 0;
		inProcessCount = 0;
		for(Report r : tableview.getItems()){
			if((Report.STATUS_IN_PROCESSING).equals(r.getStatus())){
				inProcessCount++;
			}
			if((Report.STATUS_COMPLETED).equals(r.getStatus())){
				completedCount++;
			}
			if((Report.STATUS_FAILED).equals(r.getStatus())||(Report.STATUS_NOT_FOUND).equals(r.getStatus())||(Report.STATUS_INVALID_FILE).equals(r.getStatus())){
				failedCount++;
			}
		}
		lockStatCounter.release();
		Platform.runLater(new Runnable() {
             @Override 
             public void run() {
        		tableview.getColumns().get(0).setVisible(false);
        		tableview.getColumns().get(0).setVisible(true);
         		//if((Report.STATUS_COMPLETED).equals(reportObservable.getStatus())||(Report.STATUS_FAILED).equals(reportObservable.getStatus())||(Report.STATUS_NOT_FOUND).equals(reportObservable.getStatus())){
    	        	double percentageProcess= updateProgressBar("");
    	        	if(mainProcessor!=null){
    	        		if(percentageProcess==1||mainProcessor.isCancelProcess()){
    	        			cancelProcess();
    	        		}
    	        	}
         		//}		
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
		File f = new File(getImportedFile());
		File file = null;
		if(f.exists()){
			 file = FilesChooser.show(getStage(), "Select Excel file", f.getParent(), filters);
		}else{
			 file = FilesChooser.show(getStage(), "Select Excel file", OutputConfiguration.getInstance().getDirectory(), filters);
		}
		//setImportedFile(file);
		if(file!=null){
			InputConfiguration.getInstance().setReportSummaryFile(file.getAbsolutePath());
			textField_outputFile.setText(file.getAbsolutePath());
		}
	}

	@FXML
	public void onClickNewExcel(){
		ExtensionFilter [] filters = {FilesChooser.FORMAT_EXCEL};
		File f = new File(getImportedFile());
		File file = null;
		if(f.exists()){
			 file = FilesChooser.save(getStage(), "Save new excel file",f.getParent(),filters );
		}else{
			 file = FilesChooser.save(getStage(), "Save new excel file",OutputConfiguration.getInstance().getDirectory(),filters );
		}
		
		if(file != null){
			 InputConfiguration.getInstance().setReportSummaryFile(file.getAbsolutePath());
			
			/* try {
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
		    }*/
		}
	}
	
	@FXML
	public void openImportedFile(){
		if(getImportedFile()!=null&&getImportedFile().length()>0){
			FileUtility.openFile(getImportedFile());
		}
	}
	
	@FXML
	public void viewSummary(){
		new SidebarSummaryLoader(rootPane, tableview.getItems());
	}
	@FXML
	public void refreshList(){	
		InputConfiguration.getInstance().setDirectory(InputConfiguration.getInstance().getDirectory());
	}
	@FXML
	public void removeInvalidFile(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String formattedDate = formatter.format(new Date());
		int count =0;
		String movedToDir = "";
		ObservableList<Report> data = tableview.getItems();
		for(Report report : data){
			if(report.getStatus().equals(Report.STATUS_INVALID_FILE)){
				  File afile =new File(report.getPath());
				  movedToDir = afile.getParent()+"\\"+formattedDate+"_INVALID_FILE";
				  FileUtility.moveFiles(afile, movedToDir);
				  count++;
			}
		}
		if(count>0){
			String []buttons = {"Open Folder"};
			if(0==AppDialog.multiButtonDialog(buttons, "Problem Files Removed", "The problematic files are moved to "+movedToDir+".")){
				FileUtility.openFile(movedToDir);
			}
		}
		InputConfiguration.getInstance().setDirectory(InputConfiguration.getInstance().getDirectory());
	}
	
	@FXML
	public void loadExcelSheetToComboBox(){
		//ReportSummaryFactory.deleteInstance();
		if(getImportedFile()!=null&&getImportedFile().length()>0){
			FileInputStream fis;
			try {
				if(new File(getImportedFile()).exists()){
					fis = new FileInputStream(getImportedFile());
					XSSFWorkbook book = new XSSFWorkbook(fis); 
					int totalSheet = book.getNumberOfSheets();
					comboBoxSheets.getItems().clear();
					comboBoxSheets.getItems().add("***New Sheet***");
					String sheetName = InputConfiguration.getInstance().getReportSummaryFile_sheet();
					int pos = 0;
					for(int i=0;i<totalSheet;i++){
						String sheetname = book.getSheetAt(i).getSheetName();
						comboBoxSheets.getItems().add(sheetname);
						if(sheetname.equals(sheetName)){
							pos=i+1;
						}
					}
					if(pos==0){
						InputConfiguration.getInstance().setReportSummaryFile_sheet("");
					}
					comboBoxSheets.getSelectionModel().select(pos);
				}
			} catch ( IOException e) {
				e.printStackTrace();
			} 
		}
	}

	@FXML
	public void onComboBoxSheetsSelectedChange(){
		if(getImportedFile()!=null){
			try {
				FileInputStream fis = new FileInputStream(getImportedFile());
				XSSFWorkbook book = new XSSFWorkbook(fis); 
				int totalSheet = book.getNumberOfSheets();
				
				String selectedsheet="";
				if(comboBoxSheets.getValue()!=null){
					if(comboBoxSheets.getSelectionModel().getSelectedIndex()>0){	
						selectedsheet=comboBoxSheets.getValue();
					}
					InputConfiguration.getInstance().setReportSummaryFile_sheet(selectedsheet);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	/*
	@FXML
	public void onClickNewSheet(){
		if(getImportedFile()!=null){
			try {
				FileInputStream fis = new FileInputStream(getImportedFile());
				XSSFWorkbook book = new XSSFWorkbook(fis); 
				int totalSheet = book.getNumberOfSheets();
				
				//create sheet and layout for imported file;
				XSSFSheet sheet=book.createSheet();
				//ReportSummaryExcelLayout.createNewLayout(sheet);

				//save file
				try{
					FileOutputStream out =  new FileOutputStream(getImportedFile());
					sheet.getWorkbook().write(out);
					out.close();

					//update combo box
					comboBoxSheets.getItems().add(sheet.getSheetName());
					comboBoxSheets.setValue(sheet.getSheetName());
				}catch(FileNotFoundException ex){
					//labelVerifyFormat.setText(ex.getMessage());
					AppDialog.alert("Cannot create new sheet!",ex.getMessage());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	*/

	/*
	@FXML
	public void onRemoveFile(){
		InputConfiguration.getInstance().setReportSummaryFile("");
	}
	*/
	
}

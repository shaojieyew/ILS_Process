package application.gui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import application.Report;
import application.ReportObservable;
import application.configurable.InputChangeListener;
import application.configurable.InputConfiguration;
import application.configurable.OutputConfiguration;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import reportProcessor.MainProcessor;
import reportProcessor.ReportChangeListener;
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

	public MainProcessor mainProcessor;
	
	public MainController() {
		addInputListener();
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
		updateProgressBar();
	}

	@FXML
	public void selectInputAction(){
		//select input directory
		File f = FolderChooser.show(getStage(), "Select Input Directory",inputTextfield.getText());
		if(f!=null){
			InputConfiguration.getInstance().setDirectory(f.getPath());
			processedCount=0;
			updateProgressBar();
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
		//get report from the tableview
		startBtn.getParent().setDisable(true);
		processedCount = 0;
		ObservableList<Report> data = tableview.getItems();
		mainProcessor = MainProcessor.getInstance(data);
		Thread mainProcessorThread = new Thread(mainProcessor);
		mainProcessorThread.start();
		cancelBtn.setDisable(false);
	}
	@FXML
	public void cancelProcess(){
		boolean nonInProcess = true;
		cancelBtn.setDisable(true);
		if(mainProcessor!=null){
			mainProcessor.forceStopProcess();
			ObservableList<Report> data = tableview.getItems();
			for(Report r: data){
				if(r.getStatus().equals(Report.STATUS_IN_PROCESSING)){
					nonInProcess=false;
					break;
				}
			}
		}
		//finish touch up and generate report
		if(mainProcessor==null||nonInProcess){
			if(mainProcessor!=null)
				mainProcessor.generateReport();
			startBtn.getParent().setDisable(false);
		}
	
	}

	//on input configuration changed
	@Override
	public void onUpdateInput(InputConfiguration inputDirectory) {
		inputTextfield.setText(inputDirectory.getDirectory());
	}

	//Subscribe to new changes in input configuration
	@Override
	public void addInputListener() {
		InputConfiguration.getInstance().listenToChange(this);
	}
	
	//Un-subscribe to new changes in input configuration
	@Override
	public void removeInputListener() {
		InputConfiguration.getInstance().unlistenToChange(this);
	}

	
	@Override
	public void addReportProcessListener() {
		ReportObservable.listenToChange(this);
	}

	@Override
	public void removeReportProcessListener() {
		ReportObservable.unlistenToChange(this);
	}

	public int processedCount = 0;
	@Override
	public void onUpdateReport(ReportObservable reportObservable) {
			//update UI
			Platform.runLater(new Runnable() {
                 @Override public void run() {
         			tableview.getColumns().get(0).setVisible(false);
         			tableview.getColumns().get(0).setVisible(true);
         			if((Report.STATUS_COMPLETED).equals(reportObservable.getStatus())||(Report.STATUS_FAILED).equals(reportObservable.getStatus())){
         				processedCount++;
	         			double percentageProcess= updateProgressBar();
	         			if(percentageProcess==1||mainProcessor.isCancelProcess()){
	             			cancelProcess();
	         			}
         			}
                 }
             });
	}
	
	public double updateProgressBar(){
		int reportToProcess = ReportTableViewFactory.getInstance(tableview).getTotalGetReport();
		String progressText = processedCount+"/"+reportToProcess;
		double percentageProcess = (float)processedCount/(float)reportToProcess;
		progressBar.setProgress(percentageProcess);
		progressLabel.setText(progressText);
		return percentageProcess;
	}
}

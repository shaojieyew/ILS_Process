package application.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import reportProcessor.MainProcessor;
import reportProcessor.ReportChangeListener;
import reportSummary.ReportSummary;
import reportSummary.ReportSummaryExcel;
import reportSummary.ReportSummaryExcelLayout;
import reportSummary.ReportSummaryFactory;
import util.FileUtility;
import util.FilesChooser;
import util.FolderChooser;

/*
 * Controller class for Main.fxml in application.css
 */

public class SideBarController extends FXMLController implements Initializable, InputChangeListener {
	@FXML
	private Button importBtn;
	@FXML
	private ComboBox<String> comboBoxSheets;
	@FXML
	private TextField labelImportedFile;
	@FXML
	private Label labelVerifyFormat;
	
	public static String importedFile = null;

	public SideBarController() {
		addListener(InputConfiguration.LISTEN_ReportSummaryFile);
    }

	public String getImportedFile() {
		return importedFile;
	}

	public void setImportedFile(String importedFile) {
		SideBarController.importedFile = importedFile;
		labelImportedFile.setText(importedFile);
		loadExcelSheetToComboBox();
	}

	@FXML
	public void initialize(URL fxmlurl, ResourceBundle arg1) {
	}

	@FXML
	public void openImportedFile(){
		if(importedFile!=null){
			FileUtility.openFile(importedFile);
		}
	}

	@FXML
	public void loadExcelSheetToComboBox(){
		if(importedFile!=null&&importedFile.length()>0){
			FileInputStream fis;
			try {
				fis = new FileInputStream(importedFile);
				XSSFWorkbook book = new XSSFWorkbook(fis); 
				int totalSheet = book.getNumberOfSheets();
				comboBoxSheets.getItems().clear();
				for(int i=0;i<totalSheet;i++){
					String sheetname = book.getSheetAt(i).getSheetName();
					comboBoxSheets.getItems().add(sheetname);
				}
			} catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	@FXML
	public void onComboBoxSheetsSelectedChange(){
		if(importedFile!=null){
			try {
				FileInputStream fis = new FileInputStream(importedFile);
				XSSFWorkbook book = new XSSFWorkbook(fis); 
				int totalSheet = book.getNumberOfSheets();
				XSSFSheet sheet=book.getSheet(comboBoxSheets.getValue());

				ReportSummary rs = ReportSummaryFactory.createInstance(sheet);
				if(rs.verify()){
					labelVerifyFormat.setText("Valid Format");
				}else{
					labelVerifyFormat.setText("Invalid Format");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@FXML
	public void onClickNewSheet(){
		if(importedFile!=null){
			try {
				FileInputStream fis = new FileInputStream(importedFile);
				XSSFWorkbook book = new XSSFWorkbook(fis); 
				int totalSheet = book.getNumberOfSheets();
				
				//create sheet and layout for imported file;
				book.createSheet("Sheet"+(totalSheet+1));
				XSSFSheet sheet=book.getSheet("Sheet"+(totalSheet+1));
				ReportSummaryExcelLayout.createNewLayout(sheet);

				//save file
				FileOutputStream out =  new FileOutputStream(importedFile);
				sheet.getWorkbook().write(out);
				out.close();
				
				//update combo box
				comboBoxSheets.getItems().add(sheet.getSheetName());
				comboBoxSheets.setValue(sheet.getSheetName());
				ReportSummary rs = ReportSummaryFactory.createInstance(sheet);
				if(rs.verify()){
					labelVerifyFormat.setText("Valid Format");
				}else{
					labelVerifyFormat.setText("Invalid Format");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	public void openRemoveFile(){
		importedFile="";
		InputConfiguration.getInstance().setReportSummaryFile(importedFile);
	}
	
	@Override
	public void onUpdateInput(InputConfiguration inputDirectory, String type) {
		if(type.equals(InputConfiguration.LISTEN_ReportSummaryFile)){
			setImportedFile(inputDirectory.getReportSummaryFile());
		}
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
}

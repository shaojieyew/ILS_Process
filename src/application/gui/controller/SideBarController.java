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

public class SideBarController extends FXMLController implements Initializable {
	@FXML
	private Button importBtn;
	@FXML
	private ComboBox<String> comboBoxSheets;
	@FXML
	private Label labelImportedFile;
	@FXML
	private Label labelVerifyFormat;
	
	public static File importedFile = null;
	
	public File getImportedFile() {
		return importedFile;
	}

	public void setImportedFile(File importedFile) {
		SideBarController.importedFile = importedFile;
		if(importedFile!=null){
			labelImportedFile.setText(importedFile.getName());
			InputConfiguration.getInstance().setReportSummaryFile(importedFile.getAbsolutePath());
		}
		loadExcelSheetToComboBox();
	}

	public SideBarController() {
    }

	@FXML
	public void initialize(URL fxmlurl, ResourceBundle arg1) {
		String path = InputConfiguration.getInstance().getReportSummaryFile();
		if(path!=null){
			File f = new File(path);
			if(f!=null)
				setImportedFile(f);
		}
	}

	@FXML
	public void onImportExcel(){
		ExtensionFilter filters[] = {FilesChooser.FORMAT_EXCEL};
		setImportedFile(FilesChooser.show(getStage(), "Select Excel file", OutputConfiguration.getInstance().getDirectory(), filters));
		
	}

	@FXML
	public void openImportedFile(){
		if(importedFile!=null){
			FileUtility.openFile(importedFile.getPath());
		}
	}

	@FXML
	public void loadExcelSheetToComboBox(){
		if(importedFile!=null){
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
				FileOutputStream out =  new FileOutputStream(importedFile.getAbsoluteFile());
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
	public void onClickNewExcel(){
		ExtensionFilter [] filters = {FilesChooser.FORMAT_EXCEL};
		File file = FilesChooser.save(getStage(), "Save new excel file",OutputConfiguration.getInstance().getDirectory(),filters );
		if(file != null){
			 try {
				 XSSFWorkbook workbook = new XSSFWorkbook();
				 XSSFSheet sheet = workbook.createSheet("Sheet1");
				 ReportSummaryExcelLayout.createNewLayout(sheet);
			     FileOutputStream out =  new FileOutputStream(file.getAbsoluteFile());
			     workbook.write(out);
			     out.close();
			     setImportedFile( file);
		        } catch (IOException ex) {
		        }
		}
	}
}

package application.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.configurable.InputChangeListener;
import application.configurable.InputConfiguration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import reportSummary.ReportSummary;
import reportSummary.ReportSummaryFactory;
import util.AppDialog;
import util.FileUtility;

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
		File f = new File(importedFile);
		labelImportedFile.setText(f.getName());
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
		//ReportSummaryFactory.deleteInstance();
		if(importedFile!=null&&importedFile.length()>0){
			FileInputStream fis;
			try {
				if(new File(importedFile).exists()){
					fis = new FileInputStream(importedFile);
					XSSFWorkbook book = new XSSFWorkbook(fis); 
					int totalSheet = book.getNumberOfSheets();
					comboBoxSheets.getItems().clear();
					for(int i=0;i<totalSheet;i++){
						String sheetname = book.getSheetAt(i).getSheetName();
						comboBoxSheets.getItems().add(sheetname);
					}
					String sheetName = InputConfiguration.getInstance().getReportSummaryFile_sheet();
					if(sheetName!=null&&sheetName.length()>0){
						if(comboBoxSheets.getItems().contains(sheetName)){
							comboBoxSheets.setValue(sheetName);
							InputConfiguration.getInstance().setReportSummaryFile_sheet(sheetName);
						}else{
							InputConfiguration.getInstance().setReportSummaryFile_sheet("");
						}
					}
				}
			} catch ( IOException e) {
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
				
				if(comboBoxSheets.getValue()!=null){
					XSSFSheet sheet=book.getSheet(comboBoxSheets.getValue());
					//ReportSummary rs = ReportSummaryFactory.createInstance(sheet);
					InputConfiguration.getInstance().setReportSummaryFile_sheet(comboBoxSheets.getValue());
				}
				/*else{
					//InputConfiguration.getInstance().setReportSummaryFile_sheet("");
					//ReportSummaryFactory.deleteInstance();
				}*/
				/*
				if(rs.verify()){
					labelVerifyFormat.setText("Valid Format");
				}else{
					labelVerifyFormat.setText("Invalid Format");
				}*/
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
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
				XSSFSheet sheet=book.createSheet();
				//ReportSummaryExcelLayout.createNewLayout(sheet);

				//save file
				try{
					FileOutputStream out =  new FileOutputStream(importedFile);
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

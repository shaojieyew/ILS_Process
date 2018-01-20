package application.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.MainApplication;
import application.configurable.InputConfiguration;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.AppDialog;

public class CustomDialogNewSheet {

	private static String getSheetName(){
		//File destFile = null;
		XSSFWorkbook book;
		XSSFSheet sheet = null;
		String importedFilePath= InputConfiguration.getInstance().getReportSummaryFile();
		
		if(importedFilePath.length()>0){
			File file = new File(importedFilePath);
			if(file.exists()){
				String sheetName = InputConfiguration.getInstance().getReportSummaryFile_sheet();
				
					FileInputStream fis;
					try {
						fis = new FileInputStream(file.getAbsolutePath());
						book = new XSSFWorkbook(fis);
						if(sheetName.length()>0){
							sheet = book.getSheet(sheetName);
							if(sheet!=null){
								return sheetName;
							}
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} 
			}
		}
		return null;
	}
	private static boolean checkIfSheetExist(String sheetName){
		//File destFile = null;
		XSSFWorkbook book;
		XSSFSheet sheet = null;
		String importedFilePath= InputConfiguration.getInstance().getReportSummaryFile();
		
		if(importedFilePath.length()>0){
			File file = new File(importedFilePath);
			if(file.exists()){
				//String sheetName = InputConfiguration.getInstance().getReportSummaryFile_sheet();
				
					FileInputStream fis;
					try {
						fis = new FileInputStream(file.getAbsolutePath());
						book = new XSSFWorkbook(fis);
						if(sheetName.length()>0){
							sheet = book.getSheet(sheetName);
							if(sheet!=null){
								return true;
							}
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} 
			}
		}
		return false;
	}
	static boolean cancel = false;
	public static String[] showCustomDialog(){
			cancel = false;
			final Stage dialog = new Stage();
	        dialog.setTitle("Enter Processing Details: ");
	        Button yes = new Button("Continue");
	        Button no = new Button("Cancel");

	        BorderPane borderPane = new BorderPane();
	        dialog.initModality(Modality.APPLICATION_MODAL);

	        
	        HBox hbox = new HBox();
	        hbox.setAlignment(Pos.CENTER_RIGHT);
	        hbox.getChildren().addAll(yes,no);
	        hbox.setSpacing(10);
	        VBox vbox = new VBox();
	        
	        HBox hbox1 = new HBox();
	        hbox1.setAlignment(Pos.TOP_RIGHT);
	        TextField textField_sheetName = new TextField();
	        if(getSheetName()==null){
	        	textField_sheetName.setFont(Font.font(null, FontWeight.NORMAL, 14));
		        Label label_sheetName = new Label("Sheet Name: ");
		        label_sheetName.setFont(Font.font(null, FontWeight.NORMAL, 14));
		        hbox1.getChildren().addAll(label_sheetName,textField_sheetName);
	        }else{
	        	textField_sheetName.setVisible(false);
	        }
	        
	        hbox1.setSpacing(10);
	        

	        HBox hbox2 = new HBox();
	        hbox2.setAlignment(Pos.TOP_RIGHT);
	        TextField textField_batchName = new TextField();
	        textField_batchName.setFont(Font.font(null, FontWeight.NORMAL, 14));
	        Label label_batchName = new Label("Batch Name: ");
	        label_batchName.setFont(Font.font(null, FontWeight.NORMAL, 14));
	        hbox2.getChildren().addAll(label_batchName,textField_batchName);
	        hbox2.setSpacing(10);
	        
	        vbox.getChildren().addAll(hbox1,hbox2,hbox);
	        yes.addEventHandler(MouseEvent.MOUSE_CLICKED,
	                new EventHandler<MouseEvent>() {
	                    @Override
	                    public void handle(MouseEvent e) {
	                    	if(textField_sheetName.getText().length()>0||textField_sheetName.isVisible()==false){
		                    	if(!checkIfSheetExist(textField_sheetName.getText())||textField_sheetName.isVisible()==false){
		                    		if(textField_batchName.getText().length()>0){
			                    		dialog.close();
			                    	}
			                    	else{
			                    		AppDialog.alert("Invalid batch name", "Invalid batch name");
			                    	}	
		                    	}else{
		                    		AppDialog.alert("Sheet name already exist", "Sheet name already exist");
		                    	}
	                    	}
	                    	else{
	                    		AppDialog.alert("Invalid sheet name", "Invalid sheet name");
	                    	}
	                    }
	                });
	        no.addEventHandler(MouseEvent.MOUSE_CLICKED,
	                new EventHandler<MouseEvent>() {
	                    @Override
	                    public void handle(MouseEvent e) {
	                        dialog.close();
	                        cancel =true;
	                    }
	                });
	        
	        vbox.setSpacing(10);
	        borderPane.setCenter(vbox);
	        borderPane.setPadding(new Insets(20));
	        Scene dialogScene = new Scene(borderPane, 350, 150);
	        dialogScene.getStylesheets().add(MainApplication.class.getResource("gui/application.css").toExternalForm());
	        dialogScene.getStylesheets().add(MainApplication.class.getResource("gui/bootstrap.css").toExternalForm());
	        dialog.setResizable(false);
	        dialog.setScene(dialogScene);
	        dialog.showAndWait();
	        if(cancel){
			       return null;
	        }else{
	        	//InputConfiguration.getInstance().setReportSummaryFile_sheet("aaaaaaaa");
				String[] result={textField_sheetName.getText(),textField_batchName.getText()};
	        	return result;
	        }
	        
	}
}

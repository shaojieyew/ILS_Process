package application.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import report.AttributeIndex;
import report.Report;

public class SidebarUpdateReportController implements Initializable {
	
	@FXML
	private BorderPane rootPane;
	@FXML
	private TextField textfield_name;
	@FXML
	private TextField textfield_ILS_Active;
	@FXML
	private TextField textfield_ILS_Sensing;
	@FXML
	private TextField textfield_ILS_Visual;
	@FXML
	private TextField textfield_ILS_Sequential;
	@FXML
	private TextField textfield_ILS_Reflective;
	@FXML
	private TextField textfield_ILS_Intuitive;
	@FXML
	private TextField textfield_ILS_Verbal;
	@FXML
	private TextField textfield_ILS_Global;
	@FXML
	private Button button_close;
	
    
	public Report report;
	public void setReport(Report report) {
		this.report = report;
		textfield_name.setText(report.getAuthor_name());
		textfield_name.textProperty().addListener((observable, oldValue, newValue) -> {
			report.setAuthor_name(newValue);
			System.out.println(newValue);
		});
		TextField attributesTextField[] = {textfield_ILS_Active,
				textfield_ILS_Sensing,
				textfield_ILS_Visual,
				textfield_ILS_Sequential,
				textfield_ILS_Reflective,
				textfield_ILS_Intuitive,
				textfield_ILS_Verbal,
				textfield_ILS_Global};
		String attributesName[] = {AttributeIndex.KEYWORD_ILS_ACTIVE,
				AttributeIndex.KEYWORD_ILS_SENSING,
				AttributeIndex.KEYWORD_ILS_VISUAL,
				AttributeIndex.KEYWORD_ILS_SEQUENTIAL,
				AttributeIndex.KEYWORD_ILS_REFLECTIVE,
				AttributeIndex.KEYWORD_ILS_INTUITIVE,
				AttributeIndex.KEYWORD_ILS_VERBAL,
				AttributeIndex.KEYWORD_ILS_GLOBAL};
		for(int i =0;i<attributesTextField.length;i++){
			AttributeIndex attributeIndex = report.getAttributeIndexByAttribute(attributesName[i]);
			if(attributeIndex!=null){
				attributesTextField[i].setText(attributeIndex.getIndex()+"");
			}else{
				attributesTextField[i].setText("0");
			}
			
			attributesTextField[i].textProperty().addListener((observable, oldValue, newValue) -> {
				attributeIndex.setIndex(Integer.parseInt(newValue));
			});
		}
	
	}

	public SidebarUpdateReportController() {
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@FXML
	public void closeSidebar(){
		new SidebarLoader((BorderPane) rootPane.getParent(), null);
	}
}

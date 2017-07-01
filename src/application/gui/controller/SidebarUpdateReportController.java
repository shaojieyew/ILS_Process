package application.gui.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.MainApplication;
import application.configurable.InputConfiguration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import report.AttributeIndex;
import report.Report;
import report.ReportChangeListener;
import report.ReportObservable;
import util.FileUtility;

public class SidebarUpdateReportController implements Initializable, ReportChangeListener {
	public final static String PANE_ID = "updateReportPane";
	@FXML
	private BorderPane updateReportPane;
	@FXML
	private ComboBox<String> combobox_status;
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
	private Label filename_label;
	

	public SidebarUpdateReportController() {
		 addReportProcessListener();
	}
	public Report report;
	public void setReport(Report report) {
		this.report = report;
		filename_label.setText(report.getFileName());
		filename_label.setTooltip(new Tooltip("Open "+report.getPath()));
		textfield_name.setText(report.getAuthor_name());
		textfield_name.textProperty().addListener((observable, oldValue, newValue) -> {
			report.setAuthor_name(newValue);
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
			}
			
			attributesTextField[i].textProperty().addListener((observable, oldValue, newValue) -> {
				try{
					newValue = newValue.replaceAll("[^\\d.]", "");
					if(newValue.length()==0||Integer.parseInt(newValue)<0){
						newValue="0";
					}
					attributeIndex.setIndex(Integer.parseInt(newValue));
					if(attributeIndex.getIndex()>0){
						AttributeIndex ai =null;
						switch(attributeIndex.getAttribute()){
						case AttributeIndex.KEYWORD_ILS_ACTIVE:
							ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_REFLECTIVE);
							break;
						case AttributeIndex.KEYWORD_ILS_SENSING:
							ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_INTUITIVE);
							break;
						case AttributeIndex.KEYWORD_ILS_VISUAL:
							ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_VERBAL);
							break;
						case AttributeIndex.KEYWORD_ILS_SEQUENTIAL:
							ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_GLOBAL);
							break;
						case AttributeIndex.KEYWORD_ILS_REFLECTIVE:
							ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_ACTIVE);
							break;
						case AttributeIndex.KEYWORD_ILS_INTUITIVE:
							ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_SENSING);
							break;
						case AttributeIndex.KEYWORD_ILS_VERBAL:
							ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_VISUAL);
							break;
						case AttributeIndex.KEYWORD_ILS_GLOBAL:
							ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_SEQUENTIAL);
							break;
						}
						if(ai!=null){
							if(ai.getIndex()>0){
								ai.setIndex(0);
								setReport(report);
							}
						}
					}
				}catch(Exception ex){
					
				}
			});
		}
		combobox_status.setValue(report.getStatus());
		if(combobox_status.getSelectionModel().getSelectedIndex()<0){	
			combobox_status.setValue(report.STATUS_NOT_PROCESSED);
		}
	}

	@FXML
	public void onComboBoxSelectedChange(){
		if(combobox_status.getValue()!=null){
			if(combobox_status.getSelectionModel().getSelectedIndex()>=0){	
				report.setStatus(combobox_status.getValue());
				setReport(report);
			}
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		combobox_status.getItems().add(Report.STATUS_COMPLETED);
		combobox_status.getItems().add(Report.STATUS_NOT_PROCESSED);
	}

	@FXML
	public void closeSidebar(){
		new SidebarLoader((BorderPane) updateReportPane.getParent(), null);
	}
	
	@FXML
	public void openFile(){
        FileUtility.openFile(report.getPath());
	}
	
	@Override
	public void onUpdateReport(ReportObservable reportObservable) {

		Platform.runLater(new Runnable() {
             @Override 
             public void run() {
				if(report.equals((Report)reportObservable)){
					setReport((Report)reportObservable);
				}
             }
		});
	}

	@Override
	public void addReportProcessListener() {
		ReportObservable.listenToChange(this);
	}

	@Override
	public void removeReportProcessListener() {
		ReportObservable.unlistenToChange(this);
	}
}

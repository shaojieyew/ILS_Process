package application.gui.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.MainApplication;
import application.configurable.InputConfiguration;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private ChangeListener indexChangeListeners[] = new ChangeListener[8];
	private ChangeListener<String> nameChangeListeners = null;
	
	public SidebarUpdateReportController() {
		 addReportProcessListener();
	}
	public Report report;
	public void setReport(Report report) {
		this.report = report;
		filename_label.setText(report.getFileName());
		filename_label.setTooltip(new Tooltip("Open "+report.getPath()));
		if(report.getAuthor_name()==null||!report.getAuthor_name().equals(textfield_name.getText())){
			textfield_name.setText(report.getAuthor_name());
		}
		if(nameChangeListeners!=null){
			textfield_name.textProperty().removeListener(nameChangeListeners);
		}
		nameChangeListeners = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				report.setAuthor_name(newValue);
				validate_form(report);
				verify_data(report);
			}
			
		};
		textfield_name.textProperty().addListener(nameChangeListeners);
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
			if(indexChangeListeners[i]!=null)
				attributesTextField[i].textProperty().removeListener(indexChangeListeners[i]);
			indexChangeListeners[i] = new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					try{
						newValue = newValue.replaceAll("[^\\d.]", "");
						if(!newValue.equals("11")){
							newValue=newValue.replaceAll("0", "");
							if(newValue.length()>0)
								newValue=newValue.charAt(0)+"";
						}
						if(newValue.length()==0||Integer.parseInt(newValue)<0||Integer.parseInt(newValue)%2==0||Integer.parseInt(newValue)>11){
							newValue="0";
						}			
						newValue= Integer.parseInt(newValue)+"";
						System.out.println(newValue);
						TextField changedTextField = null;
						TextField oppositeTextField = null;
						//if(attributeIndex.getIndex()>0){
							AttributeIndex ai =null;
							switch(attributeIndex.getAttribute()){
							case AttributeIndex.KEYWORD_ILS_ACTIVE:
								ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_REFLECTIVE);
								changedTextField = textfield_ILS_Active;
								oppositeTextField = textfield_ILS_Reflective;
								break;
							case AttributeIndex.KEYWORD_ILS_SENSING:
								ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_INTUITIVE);
								changedTextField = textfield_ILS_Sensing;
								oppositeTextField = textfield_ILS_Intuitive;
								break;
							case AttributeIndex.KEYWORD_ILS_VISUAL:
								ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_VERBAL);
								changedTextField = textfield_ILS_Visual;
								oppositeTextField = textfield_ILS_Verbal;
								break;
							case AttributeIndex.KEYWORD_ILS_SEQUENTIAL:
								ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_GLOBAL);
								changedTextField = textfield_ILS_Sequential;
								oppositeTextField = textfield_ILS_Global;
								break;
							case AttributeIndex.KEYWORD_ILS_REFLECTIVE:
								ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_ACTIVE);
								changedTextField = textfield_ILS_Reflective;
								oppositeTextField = textfield_ILS_Active;
								break;
							case AttributeIndex.KEYWORD_ILS_INTUITIVE:
								ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_SENSING);
								changedTextField = textfield_ILS_Intuitive;
								oppositeTextField = textfield_ILS_Sensing;
								break;
							case AttributeIndex.KEYWORD_ILS_VERBAL:
								ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_VISUAL);
								changedTextField = textfield_ILS_Verbal;
								oppositeTextField = textfield_ILS_Visual;
								break;
							case AttributeIndex.KEYWORD_ILS_GLOBAL:
								ai = report.getAttributeIndexByAttribute(AttributeIndex.KEYWORD_ILS_SEQUENTIAL);
								changedTextField = textfield_ILS_Global;
								oppositeTextField = textfield_ILS_Sequential;
								break;
							}

							attributeIndex.setIndex(Integer.parseInt(newValue));
							if(attributeIndex.getIndex()!=0){
								ai.setIndex(0);
							}
							validate_form(report);
							verify_data(report);
							
					}catch(Exception ex){
						System.out.println(ex.getMessage());
					}
				}
	        };
			attributesTextField[i].textProperty().addListener(indexChangeListeners[i]);
		}
		combobox_status.setValue(report.getStatus());
		if(combobox_status.getSelectionModel().getSelectedIndex()<0){	
			combobox_status.setValue(report.STATUS_NOT_PROCESSED);
		}
		validate_form(report);
	}

	public void verify_data(Report report){
		if(!report.validateData()){
			if(!report.getStatus().equals(Report.STATUS_FAILED)&&!report.getStatus().equals(Report.STATUS_NOT_FOUND)){
				report.setStatus(Report.STATUS_NOT_PROCESSED);
			}else{
				report.setStatus(report.getStatus());
			}
		}else{
			report.setStatus(Report.STATUS_COMPLETED);
		}
	}
	
	public void validate_form(Report report){
		String invalid = "invalid";
		String zero_input = "zero_input";
		String valid = "valid";
		String classes []= {invalid,valid,zero_input};
		textfield_name.getStyleClass().removeAll(classes);
		if(textfield_name.getText()==null||textfield_name.getText().length()==0){
            textfield_name.getStyleClass().add(invalid); 
		}else{
            textfield_name.getStyleClass().add(valid); 
		}
		TextField attributesTextField[] = {textfield_ILS_Active,	textfield_ILS_Sensing,	textfield_ILS_Visual,	textfield_ILS_Sequential,		textfield_ILS_Reflective,	textfield_ILS_Intuitive,	textfield_ILS_Verbal,textfield_ILS_Global};
		for(int i =0;i<4;i++){
			TextField tf1 = attributesTextField[i];
			TextField tf2 = attributesTextField[i+4];
			tf1.getStyleClass().removeAll(classes);
			tf2.getStyleClass().removeAll(classes);
			tf1.getStyleClass().add(zero_input);
			tf2.getStyleClass().add(zero_input);
			if(((tf1.getText().equals("0") && tf2.getText().equals("0")) ||( !tf1.getText().equals("0") && !tf2.getText().equals("0")))){
				tf1.getStyleClass().add(invalid); 
				tf2.getStyleClass().add(invalid); 
			}else{
				if(!tf1.getText().equals("0")){
					int index = Integer.parseInt(tf1.getText());
					if(index%2!=1||index>11||index<0){
						tf1.getStyleClass().add(invalid); 
					}else{
						tf1.getStyleClass().add(valid); 
					}
				}
				if(!tf2.getText().equals("0")){
					int index = Integer.parseInt(tf2.getText());
					if(index%2!=1||index>11||index<0){
						tf2.getStyleClass().add(invalid); 
					}else{
						tf2.getStyleClass().add(valid); 
					}
				}
			}
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
		combobox_status.getItems().add(Report.STATUS_FAILED);
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

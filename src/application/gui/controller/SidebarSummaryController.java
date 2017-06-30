package application.gui.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.MainApplication;
import application.configurable.InputConfiguration;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import report.AttributeIndex;
import report.Report;
import report.ReportChangeListener;
import report.ReportObservable;
import util.FileUtility;

public class SidebarSummaryController implements Initializable, ReportChangeListener {
	
	@FXML
	private BorderPane rootPane;
	@FXML
	private Label label_summary;
	@FXML
	private Label label_summary1;
	@FXML
	private BorderPane borderPane_graphic;
	
	private ObservableList<Report> observableList;
	
	public SidebarSummaryController() {
		 addReportProcessListener();
	}

	public void setReportList(ObservableList<Report> observableList) {
		this.observableList= observableList;
		String summary = "";
		String status[] = {Report.STATUS_NOT_PROCESSED, Report.STATUS_IN_PROCESSING,Report.STATUS_COMPLETED,Report.STATUS_FAILED,Report.STATUS_NOT_FOUND};
		int countStatus[] = {0,0,0,0,0};
		for(Report r: observableList){
			if(r.getStatus().equals(Report.STATUS_COMPLETED)){
				countStatus[2]++;
			}
			if(r.getStatus().equals(Report.STATUS_FAILED)){
				countStatus[3]++;
			}
			if(r.getStatus().equals(Report.STATUS_NOT_FOUND)){
				countStatus[4]++;
			}
			if(r.getStatus().equals(Report.STATUS_NOT_PROCESSED)){
				countStatus[0]++;
			}
			if(r.getStatus().equals(Report.STATUS_IN_PROCESSING)){
				countStatus[1]++;
			}
		}
		for(int i=0;i<countStatus.length;i++){
			if(countStatus[i]>0){
				summary=summary+status[i]+": "+ countStatus[i]+"\n";
			}
		}
		label_summary.setText(summary);
		label_summary1.setText("Total ILS Report: "+ observableList.size());
		

		 //Filled rectangle
        BorderPane rect1 = new SummaryGUI(observableList);
        borderPane_graphic.setCenter(rect1);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@FXML
	public void closeSidebar(){
		new SidebarLoader((BorderPane) rootPane.getParent(), null);
	}
	
	
	static  Semaphore mutex = new Semaphore(1);
	@Override
	public void onUpdateReport(ReportObservable reportObservable) {

		Platform.runLater(new Runnable() {
             @Override 
             public void run() {
            	 try {
					mutex.acquire();
			        BorderPane rect1 = new SummaryGUI(observableList);
			        borderPane_graphic.setCenter(rect1);
	            	mutex.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

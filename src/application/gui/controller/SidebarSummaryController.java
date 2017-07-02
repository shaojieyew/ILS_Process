package application.gui.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.MainApplication;
import application.configurable.InputConfiguration;
import application.configurable.OutputConfiguration;
import application.gui.controller.SummaryGUI.StatMode;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import report.AttributeIndex;
import report.Report;
import report.ReportChangeListener;
import report.ReportObservable;
import util.FileUtility;
import util.FilesChooser;

public class SidebarSummaryController implements Initializable, ReportChangeListener {
	
	@FXML
	private BorderPane summaryPane;
	@FXML
	private TextFlow label_summary;
	@FXML
	private TextFlow label_summary1;
	@FXML
	private BorderPane borderPane_graphic;
	
	private SummaryGUI summary_graphic = new SummaryGUI();
	
	private ObservableList<Report> observableList;
	
	public SidebarSummaryController() {
		 addReportProcessListener();
	}

	public void setReportList(ObservableList<Report> observableList) {
		this.observableList= observableList;
		//String summary = "";
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
		label_summary.getChildren().clear();
		for(int i=0;i<countStatus.length;i++){
			if(countStatus[i]>0){
				Text text1=new Text(status[i]+": "+ countStatus[i]+"\n");
				text1.setFill(Color.BLACK);
				if(status[i].equals(Report.STATUS_COMPLETED)){
					text1.setStyle("-fx-font-weight: bold");
					text1.setFill(Color.GREEN);
				}
				if(status[i].equals(Report.STATUS_FAILED)||status[i].equals(Report.STATUS_NOT_FOUND)){
					text1.setStyle("-fx-font-weight: bold");
					text1.setFill(Color.RED);
				}
				
	            label_summary.getChildren().addAll(text1);
			}
		}
		label_summary1.getChildren().clear();
		Text text2=new Text("Total ILS Report: "+ observableList.size());
		label_summary1.getChildren().addAll(text2);
		

		 //Filled rectangle
		summary_graphic.setObservableList(observableList);
        borderPane_graphic.setCenter(summary_graphic);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	
	@FXML
	public void closeSidebar(){
		new SidebarLoader((BorderPane) summaryPane.getParent(), null);
	}
	@FXML
	public void onclick_mean(){
		summary_graphic.setStatMode(StatMode.MEAN);
		
	}
	@FXML
	public void onclick_median(){
		summary_graphic.setStatMode(StatMode.MEDIAN);
	}

	Stage saveSummaryStage = new Stage();
	@FXML
	public void onclick_save(){
		BorderPane bp = new BorderPane();
		SummaryGUI summaryGUI = new SummaryGUI();
		summaryGUI.setObservableList(observableList);
		summaryGUI.setSelectorsLoc(summary_graphic.getSelectorsLoc());
		bp.setCenter(summaryGUI);
		Scene scene2 = new Scene(bp, 800, 500);
		saveSummaryStage.setScene(scene2);

		ExtensionFilter [] filters = {FilesChooser.FORMAT_PNG};
		File file = FilesChooser.save(saveSummaryStage, "Save Summary Image", OutputConfiguration.getInstance().getDirectory(), filters);
		if(file!=null){
			WritableImage snapshot = summaryGUI.snapshot(new SnapshotParameters(), null);
			saveImage(snapshot,file);
		}
	}
	
	private void saveImage(WritableImage snapshot, File file) {
		BufferedImage bufferedImage = new BufferedImage(550, 400, BufferedImage.TYPE_INT_ARGB);

	    BufferedImage image;
	    image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
	    try {
	        Graphics2D gd = (Graphics2D) image.getGraphics();
	        gd.translate(summary_graphic.getWidth(), summary_graphic.getHeight());
	        ImageIO.write(image, "png", file);
	    } catch (IOException ex) {
	    };
	  }
	static  Semaphore mutex = new Semaphore(1);
	@Override
	public void onUpdateReport(ReportObservable reportObservable) {

		Platform.runLater(new Runnable() {
             @Override 
             public void run() {
            	 try {
					mutex.acquire();
					setReportList(observableList);
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

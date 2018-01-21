package application.gui.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.MainApplication;
import application.configurable.InputConfiguration;
import application.configurable.OutputConfiguration;
import application.gui.controller.SummaryGUI.StatMode;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.stage.FileChooser.ExtensionFilter;
import report.AttributeIndex;
import report.Report;
import report.ReportChangeListener;
import report.ReportObservable;
import report.ReportCurve;
import util.AppDialog;
import util.FileUtility;
import util.FilesChooser;

public class SidebarSummaryController implements Initializable, ReportChangeListener {
	@FXML
	private TextArea text_area;
	@FXML
	private BorderPane summaryPane;
	@FXML
	private Slider thresholdSlider;
	@FXML
	private TextFlow label_summary;
	@FXML
	private TextFlow label_summary1;
	@FXML
	private BorderPane borderPane_graphic;
	@FXML
	private ListView list_view_curve;
	
	private SummaryGUIMultiCurve summary_graphic = new SummaryGUIMultiCurve();
	
	private List<Report> reportList;
	
	private String selectedCurve = null;
	public SidebarSummaryController() {
		 addReportProcessListener();
	}

	public void setReportList(List<Report> reportList) {
		this.reportList= reportList;
		//String summary = "";
		String status[] = {Report.STATUS_NOT_PROCESSED, Report.STATUS_IN_PROCESSING,Report.STATUS_COMPLETED,Report.STATUS_FAILED,Report.STATUS_NOT_FOUND};
		int countStatus[] = {0,0,0,0,0};

		if(reportList!=null){
			for(Report r: reportList){
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
		}

		label_summary.getChildren().clear();
		if(reportList instanceof ObservableList){
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
		}
		
		if(reportList!=null){
			Text text2=new Text("Total ILS Report: "+ reportList.size());
			label_summary.getChildren().addAll(text2);
		}
		

		 //Filled rectangle
		summary_graphic.setReportList(reportList);
        borderPane_graphic.setCenter(summary_graphic);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		thresholdSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            summary_graphic.setShadingThreshold((float) thresholdSlider.getValue());
            summary_graphic.drawCurve();
        });
        
        list_view_curve.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();

        		List<ReportCurve> appliedRc = summary_graphic.getCurves();
        		for(ReportCurve rc: appliedRc){
        			if(rc.name.equals(item)){
                        observable.set(true);
                        break;
        			}
        		}
                observable.addListener((obs, wasSelected, isNowSelected) -> updateGUICurve(obs, wasSelected, isNowSelected,item));
                return observable ;
            }

			private Object updateGUICurve(ObservableValue<? extends Boolean> obs, Boolean wasSelected,
					Boolean isNowSelected, String item) {
				ReportCurve rc = ReportCurve.getCurve(item);
				if(rc!=null){
					if(isNowSelected&&!wasSelected){
						summary_graphic.drawCurve(rc);
					}
					if(!isNowSelected&&wasSelected){
						summary_graphic.removeCurve(rc);
					}
				}
				return null;
			}
        }));
        initCurveListView();
	}
	
	
	private void initCurveListView(){
        list_view_curve.getItems().clear();
		List<ReportCurve> list= ReportCurve.getAll();
		if(list!=null){
	        for (ReportCurve rc:list){
	        	list_view_curve.getItems().add(rc.getName());
	        }
			if(selectedCurve!=null){
				list_view_curve.getSelectionModel().select(selectedCurve);
			}else{
				list_view_curve.getSelectionModel().clearSelection();
			}
		}

	}
	
	@FXML
	public void onclick_mean(){
		//setSelectedCurve(StatMode.MEAN);
		summary_graphic.setStatMode(StatMode.MEAN);
		
	}
	@FXML
	public void onclick_median(){
		//setSelectedCurve(StatMode.MEDIAN);
		summary_graphic.setStatMode(StatMode.MEDIAN);
	}
	@FXML
	public void onclick_mode(){
		//setSelectedCurve(StatMode.MODE);
		summary_graphic.setStatMode(StatMode.MODE);
	}
	
	

	Stage saveSummaryStage = new Stage();
	@FXML
	public void onclick_save(){
		BorderPane bp = new BorderPane();
		SummaryGUIMultiCurve summaryGUI = new SummaryGUIMultiCurve();
		summaryGUI.setReportList(reportList);
		//summaryGUI.setSelectorsLoc(summary_graphic.getSelectorsLoc());
		summaryGUI.setShadingThreshold((float) thresholdSlider.getValue());
		bp.setCenter(summaryGUI);
		Scene scene2 = new Scene(bp, 1000, 625);
		saveSummaryStage.setScene(scene2);
		summaryGUI.drawCurves(summary_graphic.getCurves());
		
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
		if(reportList instanceof ObservableList){
			Platform.runLater(new Runnable() {
	             @Override 
	             public void run() {
	            	 try {
						mutex.acquire();
						setReportList(reportList);
		            	mutex.release();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	             }
			});
		}
	}

	
	
	
	@FXML
	public void onclick_save_curve(){
		//BorderPane bp = new BorderPane();
		SummaryGUI summaryGUI = new SummaryGUI();
		summaryGUI.setReportList(reportList);
		float[][][]  curve = summaryGUI.getMeanMedianSelector();
		summaryGUI.setSelectorsLoc(curve[0]);
		summaryGUI.setShadingThreshold((float) thresholdSlider.getValue());
		
		boolean result = CustomDialogCurve.showCustomDialog("Save current statistic as curve", "Enter curve name",summaryGUI, "");
		if(result){
			initCurveListView();
		}
	}
	
	
	
	@FXML
	public void onclick_delete_curve(){
		String buttons[] = {"Confirm"};
		if(selectedCurve!=null){
			int result = AppDialog.multiButtonDialog(buttons, "Confirmation", "Confirm Deletion of Curve?");
			if(result>-1){
				ReportCurve.deleteCurve(selectedCurve);

		        initCurveListView();
				selectedCurve = null;
			}
		}else{
			AppDialog.alert("No curve selected", "No curve selected to be delete.");
		}
	}
	
	@FXML
	public void on_curve_change(){
		if(list_view_curve.getSelectionModel().getSelectedIndex()>-1){
			if(selectedCurve==null||!selectedCurve.equals(list_view_curve.getSelectionModel().getSelectedItem().toString())){
				selectedCurve = list_view_curve.getSelectionModel().getSelectedItem().toString();
			}else{
				list_view_curve.getSelectionModel().clearSelection();
				selectedCurve=null;
			}
		}else{
			selectedCurve=null;
		}
		
		//setSelectedCurve(StatMode.MODE);
	}
	
	
	
	public void setSelectedCurve(StatMode stateType){
		if(selectedCurve==null){
			summary_graphic.removeSelectors();
			return;
		}
		ReportCurve rc  =ReportCurve.getCurve(selectedCurve);
		selectedCurve = list_view_curve.getSelectionModel().getSelectedItem().toString();
		//String s = "[[[7.0, 0.8065469], [4.0, 0.6410792], [4.0, 1.25259], [6.0, 1.1446764]], [[7.0, 0.49], [4.0, 0.49], [3.0, 0.49], [6.0, 0.49]]]";
		float[][][]  curve = new float [3][4][2];
		String[]  results= rc.getCurve().split("],");
		for(int i =0;i<results.length;i++){
			String str = results[i];
			str = str.replace("[", "");
			str = str.replace("]", "");
			curve[i/4][i%4][0] = Float.parseFloat(str.split(",")[0]);
			curve[i/4][i%4][1] = Float.parseFloat(str.split(",")[1]);
		}

		if(stateType.equals(StatMode.MEDIAN)){
			summary_graphic.setSelectorsLoc(curve[2]);
		}
		if(stateType.equals(StatMode.MEAN)){
			summary_graphic.setSelectorsLoc(curve[1]);
		}
		if(stateType.equals(StatMode.MODE)){
			summary_graphic.setSelectorsLoc(curve[0]);
		}
		
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

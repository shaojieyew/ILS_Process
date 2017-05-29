package application.gui.controller;

import java.util.ArrayList;

import java.awt.Desktop;
import java.io.File;
import application.Report;
import application.ReportFinder;
import application.configurable.InputChangeListener;
import application.configurable.InputConfiguration;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import util.FileUtility;

/*
 * Class for managing the tableview.
 * Listen to inputConfiguration with common interface; InputChangeListener, and update tableview according to input configuration.
 */

public  class ReportTableView implements InputChangeListener{
	private TableView<Report> tableview;
	private ObservableList<Report> data;
	private int totalGetReport=0;
	private Report selectedReport = null;
	public ReportTableView(TableView<Report> inTableview){
		addListener(InputConfiguration.LISTEN_InputDirectory);
		tableview = inTableview;
		
		tableview.setOnMousePressed(new EventHandler<MouseEvent>() {
		    @Override 
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		            Node node = ((Node) event.getTarget()).getParent();
		            TableRow row;
		            if (node instanceof TableRow) {
		                row = (TableRow) node;
		            } else {
		                row = (TableRow) node.getParent();
		            }
		            Report report = (Report) row.getItem();
		            FileUtility.openFile(report.getPath());
		        }
		    }
		});
		 
		tableview.setSelectionModel(null);
		tableview.setRowFactory(tv -> new TableRow<Report>() {
		    @Override
		    public void updateItem(Report item, boolean empty) {
		        super.updateItem(item, empty) ;
	            this.getStyleClass().clear();
		        if(item!=null){
			        switch(item.getStatus()){
			        case Report.STATUS_NOT_PROCESSED:
			            this.getStyleClass().add("ready"); 
			        	break;
			        case Report.STATUS_IN_PROCESSING:
			            this.getStyleClass().add("processing"); 
			        	break;
			        case Report.STATUS_COMPLETED:
			            this.getStyleClass().add("complete"); 
			        	break;
			        case Report.STATUS_FAILED:
			            this.getStyleClass().add("fail"); 
			        	break;
			        case Report.STATUS_NOT_FOUND:
			            this.getStyleClass().add("fail"); 
			        	break;
			        default:
			        	break;
			        }
		        }
		    }
		});
		
		
	}
	
	
	//update the list in the tableview
	public void updateListByInputDirectory(){
		data=tableview.getItems();
        data.removeAll(data);
        ReportFinder reportFinder = new ReportFinder();
        ArrayList<Report> reports = reportFinder.findAllReport();
        for(Report report : reports){
            data.add(report);
        }
        totalGetReport=data.size();
	}

	
	//listen to input configuration change
	@Override
	public void addListener(String type) {
		InputConfiguration.getInstance().listenToChange(this,type);
	}

	@Override
	public void removeListener(String type) {
		InputConfiguration.getInstance().unlistenToChange(this,type);
	}

	@Override
	public void onUpdateInput(InputConfiguration inputDirectory, String type) {
		if(type.equals(InputConfiguration.LISTEN_InputDirectory)){
			updateListByInputDirectory();
		}
	}

	public int getTotalGetReport() {
		return totalGetReport;
	}

}

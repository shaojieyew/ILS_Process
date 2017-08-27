package application.gui.controller;

import java.util.ArrayList;
import application.configurable.InputChangeListener;
import application.configurable.InputConfiguration;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import report.Report;
import util.FileUtility;

/*
 * Class for managing the tableview.
 * Listen to inputConfiguration with common interface; InputChangeListener, and update tableview according to input configuration.
 */

public  class ReportTableView implements InputChangeListener{
	private TableView<Report> tableview;
	private ObservableList<Report> data;
	private int totalGetReport=0;
	public ReportTableView(TableView<Report> inTableview){
		addListener(InputConfiguration.LISTEN_InputDirectory);
        tableview = inTableview;
        
		/*
		TableColumn col_action = new TableColumn<>("Edit");
		tableview.getColumns().add(3,col_action);
        
        col_action.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Report, Boolean>, 
                ObservableValue<Boolean>>() {

            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Report, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });

        //Adding the Button to the cell
        col_action.setCellFactory(
                new Callback<TableColumn<Report, Boolean>, TableCell<Report, Boolean>>() {

            @Override
            public TableCell<Report, Boolean> call(TableColumn<Report, Boolean> p) {
                return new ButtonCell();
            }
        
        });*/
        tableview.setTooltip(new Tooltip("Single click to view processed information, Double click to open file"));
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
		            if(report!=null){
			            FileUtility.openFile(report.getPath());
		            }
		        }
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 1&tableview.getSelectionModel()!=null) {
		            Node node = ((Node) event.getTarget()).getParent();
		            TableRow row;
		            if (node instanceof TableRow) {
		                row = (TableRow) node;
		            } else {
		                row = (TableRow) node.getParent();
		            }
		            Report report = (Report) row.getItem();
		            if(report!=null){
		            	new SidebarUpdateReportLoader((BorderPane) tableview.getParent(),report);
		          
		            }
		        }
		     
		    }
		});
		//tableview.setSelectionModel(null);
		tableview.setRowFactory(tv -> new TableRow<Report>() {
		    @Override
		    public void updateItem(Report item, boolean empty) {
		        super.updateItem(item, empty) ;
	            this.getStyleClass().clear();
	            this.getStyleClass().add("rowStyleSelected"); 
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
			        case Report.STATUS_INVALID_FILE:
			            this.getStyleClass().add("fail"); 
			        	break;
			        default:
			        	break;
			        }
		        }
		    }
		});
		
		
	}
	private class ButtonCell extends TableCell<Report, Boolean> {
        final Button cellButton = new Button("Edit");
        ButtonCell(){
        	//Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                	Report report = (Report) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                	
                	//data.remove(report);
                	new SidebarUpdateReportLoader((BorderPane) tableview.getParent(),report);
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }else{
                setGraphic(null);
            }
        }
    }
	
	//update the list in the tableview
	public void updateListByInputDirectory(InputConfiguration inputDirectory){
		data=tableview.getItems();
		ObservableList<Report> backup = FXCollections.observableArrayList(data);
        data.removeAll(data);
		System.out.println(backup.size());
        ArrayList<Report> reports = Report.findAllReport(inputDirectory.getDirectory(),inputDirectory.getFileType());
        for(Report report : reports){
            data.add(report);
            for(Report oldReport : backup){
            	if(oldReport.getPath().equals(report.getPath())){
            		report.setAttributes(oldReport.getAttributes());
            		report.setAuthor_name(oldReport.getAuthor_name());
            		report.setStatus(oldReport.getStatus());
            		backup.remove(oldReport);
            		break;
            	}
            }
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
			updateListByInputDirectory(inputDirectory);
		}
	}

	public int getTotalGetReport() {
		return totalGetReport;
	}
}

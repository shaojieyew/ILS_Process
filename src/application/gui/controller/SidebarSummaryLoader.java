package application.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.MainApplication;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import report.Report;

public class SidebarSummaryLoader extends SidebarContentLoader   {

	public SidebarSummaryLoader(Parent parent, List<Report> observableList, String title) {
		super((BorderPane) parent,getSidePane(observableList),title);
		//report.setStatus(Report.STATUS_COMPLETED);
	}
	
	private static BorderPane getSidePane(List<Report> observableList){
		 FXMLLoader sidebarLoader = new FXMLLoader();
		 BorderPane sidebar =null;
	     sidebarLoader.setLocation(MainApplication.class.getResource("gui/SidebarSummary.fxml"));
	     try {
			 sidebar = (BorderPane) sidebarLoader.load();
	     } catch (IOException e) {
			e.printStackTrace();
	     }
	     SidebarSummaryController controller = (SidebarSummaryController)sidebarLoader.getController();
	     controller.setReportList(observableList);
	     return sidebar;
	}
}

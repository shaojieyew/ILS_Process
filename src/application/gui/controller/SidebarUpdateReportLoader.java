package application.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import report.Report;

public class SidebarUpdateReportLoader extends SidebarContentLoader   {

	public SidebarUpdateReportLoader(Parent parent, Report report) {
		super((BorderPane) parent,getSidePane(report),"ILS Data");
		//report.setStatus(Report.STATUS_COMPLETED);
	}
	
	private static BorderPane getSidePane(Report report){
		 FXMLLoader sidebarLoader = new FXMLLoader();
		 BorderPane sidebar =null;
	     sidebarLoader.setLocation(MainApplication.class.getResource("gui/SidebarUpdateReport.fxml"));
	     try {
			 sidebar = (BorderPane) sidebarLoader.load();
	     } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	     }
	     SidebarUpdateReportController controller = (SidebarUpdateReportController)sidebarLoader.getController();
	     controller.setReport(report);
	     return sidebar;
	}
}

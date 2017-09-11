package application.gui.controller;

import java.io.IOException;

import application.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class SidebarContentLoader extends FXMLController{
	public SidebarContentLoader(BorderPane pane, BorderPane sidebarContent, String title) {
		BorderPane sidebarFrame=null;
		if(sidebarContent!=null){
			 FXMLLoader sidebarLoader = new FXMLLoader();
		     sidebarLoader.setLocation(MainApplication.class.getResource("gui/Sidebar.fxml"));
		     try {
		    	 sidebarFrame = (BorderPane) sidebarLoader.load();
		    	 SidebarController sidebarController = sidebarLoader.getController();
		    	 sidebarController.setContent(sidebarContent, title);
		     } catch (IOException e) {
				e.printStackTrace();
		     }
		}
	     //sidebarFrame.setCenter(sidebarContent);
		pane.setRight(sidebarFrame);
	}
}

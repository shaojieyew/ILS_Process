package application.gui.controller;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class SidebarLoader extends FXMLController{
	public SidebarLoader(BorderPane pane, BorderPane sidebar) {
		pane.setRight(sidebar);
	}
}

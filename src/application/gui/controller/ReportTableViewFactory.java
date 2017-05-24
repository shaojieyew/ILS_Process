package application.gui.controller;

import application.Report;
import javafx.scene.control.TableView;

public class ReportTableViewFactory {

	private static ReportTableView INSTANCE;

	//Singleton method
	public static ReportTableView getInstance(TableView<Report> tableview) {
		if(INSTANCE==null){
			INSTANCE = new ReportTableView(tableview);
		}
        return INSTANCE;
    }
}

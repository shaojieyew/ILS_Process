package application.gui.controller;

import javafx.scene.control.TableView;
import report.Report;

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

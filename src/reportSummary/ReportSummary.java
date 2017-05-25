package reportSummary;

import application.Report;
import javafx.collections.ObservableList;

public interface  ReportSummary {

	public void process(ObservableList<Report> reports);

	public  boolean verify();
}

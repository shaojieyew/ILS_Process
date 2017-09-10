package reportSummary;

import java.io.File;

import javafx.collections.ObservableList;
import report.Report;

public interface  ReportSummary {

	public void process(ObservableList<Report> reports);
	public  boolean save(File destFile);
	public boolean verify();
}

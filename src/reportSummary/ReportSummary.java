package reportSummary;

import java.io.File;

import javafx.collections.ObservableList;
import report.Report;

public interface  ReportSummary {

	public void process(ObservableList<Report> reports, String processName);
	public  boolean save(File destFile);
	public boolean verify();
	public String getReportSheetName();
	public String getBatchName();
}

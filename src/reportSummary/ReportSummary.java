package reportSummary;

import java.io.File;

import javafx.collections.ObservableList;
import report.Report;

public interface  ReportSummary {

	public Object process(ObservableList<Report> reports, String processName, int writeType);
	public  boolean save(File destFile);
	public boolean verify();
	public String getReportSheetName();
	public String getBatchName();
}

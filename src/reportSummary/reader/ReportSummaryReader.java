package reportSummary.reader;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import report.Report;

public interface  ReportSummaryReader {
	public ArrayList<Report> read();
}

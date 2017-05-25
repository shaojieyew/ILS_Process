package reportSummary;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import application.Report;
import javafx.collections.ObservableList;

public class ReportSummaryExcel implements ReportSummary {
	
	public ReportSummaryExcel(XSSFSheet o) {
		
	}

	@Override
	public void process(ObservableList<Report> reports) {
		// TODO Auto-generated method stub
		System.out.println("Writing data to file");
	}
}

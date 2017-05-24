package reportProcessor;

import application.ReportObservable;
public interface ReportChangeListener {
	void onUpdateReport(ReportObservable reportObservable);
	void addReportProcessListener();
	void removeReportProcessListener();
}

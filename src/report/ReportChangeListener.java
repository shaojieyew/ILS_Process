package report;

public interface ReportChangeListener {
	void onUpdateReport(ReportObservable reportObservable);
	void addReportProcessListener();
	void removeReportProcessListener();
}

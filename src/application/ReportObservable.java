package application;

import java.util.ArrayList;

import reportProcessor.ReportChangeListener;

public abstract class ReportObservable {
	//List of listener listening to changes of this class's instance
	private static ArrayList<ReportChangeListener> reportChangeListener = new ArrayList<ReportChangeListener>();

	//notify changes to listener
	public void notifyChange(){
		for(int i =0;i<reportChangeListener.size();i++){
			reportChangeListener.get(i).onUpdateReport(this);	
		}
	}

	//add new listener
	public static void listenToChange(ReportChangeListener listener){
		reportChangeListener.add(listener);
	}
	//remove listener
	public static void unlistenToChange(ReportChangeListener listener){
		reportChangeListener.remove(listener);
	}

	public abstract String  getStatus();
}

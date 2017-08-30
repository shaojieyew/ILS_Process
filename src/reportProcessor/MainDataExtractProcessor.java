package reportProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import application.configurable.AppProperty;
import javafx.collections.ObservableList;
import report.Report;

/*
 * Main Process for managing multiple threads for processing
 */
public class MainDataExtractProcessor extends Processor implements Runnable{
	private static MainDataExtractProcessor INSTANCE;
	private ObservableList<Report> reports;
	//variable keep track of the progress
	private int totalCount = 0 ;
	private boolean cancelProcess = false;
	private boolean reprocessCompletedFile = false;
	/*
	private int completedCount = 0 ;
	//get process of the processing
	public int getPercentageCompleted(){
		return (int)(((float)completedCount/(float)totalCount)*100f);
	}
	*/

	//constructor setup list of reports to process
	public MainDataExtractProcessor(ObservableList<Report> data, int numberOfThread, boolean reprocessCompletedFile){
		if(numberOfThread<=0){
			numberOfThread=1;
		}
		this.count_thread = new Semaphore(numberOfThread);
		this.reports=data;
		this.totalCount=data.size();
		this.reprocessCompletedFile = reprocessCompletedFile;
	}
	
	//singleton method
	public static MainDataExtractProcessor getInstance(ObservableList<Report> data, int numberOfThread, boolean reprocessCompletedFile) {
		INSTANCE = new MainDataExtractProcessor(data,numberOfThread,reprocessCompletedFile);
        return INSTANCE;
    }
	
	//setup counting semaphore for multithreading
	private Semaphore count_thread;
	public void releaseSemaphore(){
		count_thread.release();
	}
	
	public void forceStopProcess(){
		cancelProcess=true;
	}
	
	List<Thread> listOfThreads = new ArrayList<Thread>();
	private final Semaphore lock = new Semaphore(1);
	
	@Override
	public void run() {
		started();
		Semaphore token = new Semaphore((totalCount-1)*-1);
		cancelProcess = false;
		listOfThreads.clear();
		List<Processor> runningProcessors = new ArrayList<Processor>();
		int count =0;
		for(int i =0;i<totalCount;i++){
			try {
				count_thread.acquire();
				if(cancelProcess){
					for(int x=0;x<(totalCount-count);x++){
						token.release();
					}
					count_thread.release();
					break;
				}
				//start a sub-thread to process a report
				DataExtractProcessor rp = new DataExtractProcessor(this,reports.get(i),i,reprocessCompletedFile);
				rp.addListener(new ProcessorListener(){
					@Override
					public void onComplete(Processor processor) {
						try {
							lock.acquire();
							runningProcessors.remove(processor);
							token.release();
							lock.release();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					@Override
					public void onStart(Processor processor) {
						try {
							lock.acquire();
							runningProcessors.add(processor);
							lock.release();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
				Thread thread1 = new Thread(rp);
				thread1.start();
				count++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			token.acquire();
			completed();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isCancelProcess() {
		return cancelProcess;
	}
	

}

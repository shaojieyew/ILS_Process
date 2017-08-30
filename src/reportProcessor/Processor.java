package reportProcessor;

import java.util.ArrayList;

import application.configurable.DebugClass;


public abstract class Processor extends DebugClass {
	private ArrayList<ProcessorListener> listeners = new ArrayList<ProcessorListener>();
	public static  final String TYPE_ON_COMPLETE="Completed";
	public static  final String TYPE_ON_START="Started";
	
	//notify changes to listener
		public void notifyChange(String type){
			if(listeners!=null&&listeners.size()>0){
				for(int i =0;i<listeners.size();i++){
					switch(type){
					case TYPE_ON_COMPLETE:
						listeners.get(i).onComplete(this);	
						break;
					case TYPE_ON_START:
						listeners.get(i).onStart(this);	
						break;
					}
				}
			}
		}

		//add new listener
		public void addListener(ProcessorListener listener){
			listeners.add(listener);
		}
		//remove listener
		public void removeListener(ProcessorListener listener){
			listeners.remove(listener);
		}
		

		protected void completed() {
			notifyChange(TYPE_ON_COMPLETE);
		}

		protected void started() {
			notifyChange(TYPE_ON_START);
		}
}

package application.configurable;

import java.util.ArrayList;

/*
 * Class for managing the input configuration 
 */

public final class InputConfiguration {
	//Singleton
	private static final InputConfiguration INSTANCE = new InputConfiguration();
	public static InputConfiguration getInstance() {
        return INSTANCE;
    }

	
	//readable file types for the application
	private String fileType[] = {"pdf","png","html","htm","jpeg","jpg","tiff","bmp","gif"};
	//input folder of the application configuration
	private String inputDirectory =AppProperty.getValue("input");
	
	//List of listener listening to changes of this class's instance
	private ArrayList<InputChangeListener> inputChangeListeners = new ArrayList<InputChangeListener>();

	//notify changes to listener
	public void notifyChange(){
		for(int i =0;i<inputChangeListeners.size();i++){
			inputChangeListeners.get(i).onUpdateInput(this);	
		}
	}

	//add new listener
	public void listenToChange(InputChangeListener listener){
		inputChangeListeners.add(listener);
	}
	//remove listener
	public void unlistenToChange(InputChangeListener listener){
		inputChangeListeners.remove(listener);
	}
	

	public void setDirectory(String newDir){
		inputDirectory=newDir;
		AppProperty.setValue("input", newDir);
		notifyChange();
	}
	public String getDirectory(){
		return inputDirectory;
	}

	public String[] getFileType(){
		return fileType;
	}

}

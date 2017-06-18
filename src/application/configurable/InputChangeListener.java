package application.configurable;

/**
 * Interface for listening to changes to input configuration of the application
 * 
 * @author YEW SHAO JIE
 */
public interface InputChangeListener {
	/**
	 * This method is called when new changes from the Input Configuration of the application
	 * 
	 * @param inputDirectory the content of the InputConfiguration
	 * @param type the type of change event from the InputConfiguration
	 */
	void onUpdateInput(InputConfiguration inputDirectory, String type);

	/**
	 * This method can be used by the listener to listen to InputConfiguration by 
	 * <p/>InputConfiguration.getInstance().listenToChange(this,type);
	 * 
	 * @param type the type of change event from the InputConfiguration
	 */
	void addListener(String type);
	
	/**
	 * This method can be used by the listener to unlisten to InputConfiguration by 
	 * <p/>InputConfiguration.getInstance().unlistenToChange(this,type);
	 * 
	 * @param type the type of change event from the InputConfiguration
	 */
	void removeListener(String type);
}

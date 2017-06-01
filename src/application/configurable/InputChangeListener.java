package application.configurable;

/*
 * Interface for listening to changes to input folder configuration
 */

public interface InputChangeListener {
	void onUpdateInput(InputConfiguration inputDirectory, String type);
	void addListener(String type);
	void removeListener(String type);
}

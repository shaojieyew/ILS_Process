package application.configurable;

/*
 * Interface for listening to changes to input folder configuration
 */

public interface InputChangeListener {
	void onUpdateInput(InputConfiguration inputDirectory);
	void addInputListener();
	void removeInputListener();
}

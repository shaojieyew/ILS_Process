package reportProcessor;

public interface ProcessorListener {
	void onComplete(Processor processor);
	void onStart(Processor processor);
}

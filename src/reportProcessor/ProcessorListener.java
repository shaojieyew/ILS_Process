package reportProcessor;

public interface ProcessorListener {
	void onComplete(Processor processor);
	void onStart(Processor processor);
	void onFail(Processor processor);
	void onInterrupt(Processor processor);
}

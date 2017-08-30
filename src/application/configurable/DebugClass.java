package application.configurable;

public abstract class DebugClass {
	private static boolean debug=false;

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean input) {
		debug = input;
	}
}

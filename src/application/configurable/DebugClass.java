package application.configurable;

public abstract class DebugClass {
	private static boolean debug=false;

	public static boolean isDebug() {
		//return true;
		//DebugClass.setDebug(AppProperty.getValue(AppProperty.PROP_debug).equals("true")?true:false);
		return AppProperty.getValue(AppProperty.PROP_debug).equals("true");
	}

	public static void setDebug(boolean input) {
		if(input){
			AppProperty.setValue(AppProperty.PROP_debug, "true");
		}else{
			AppProperty.setValue(AppProperty.PROP_debug, "false");
		}
		//debug = input;
	}
}

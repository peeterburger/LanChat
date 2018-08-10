package util;

public class Debugger {
	public static void println(int layer, String subject, String object, String text) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < layer; i++) {
			sb.append("\t");
		}
		sb.append("[" + subject + "]");
		if(object != null) {
			sb.append("{" + object + "}");
		}
		sb.append(" " + text);
		System.out.println(sb.toString());
	}
}
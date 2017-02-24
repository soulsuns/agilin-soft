package diff.util;

public class StringUtil {

	
	public static String nvl(String str) {
		if(str == null)
			return "";
		
		return str;
	}
	
	public static String parseLastSlash(String str) {
		
		if(nvl(str).endsWith("/")) {
			str = str.substring(0, str.length()-1);
		}
		
		return str;
	}
}

package ist.meic.pa;

import java.util.HashMap;
import java.util.Map;

public class Parser {
	
	public static Map<String, String> parseKeywordArgs(String s) {
		
		if(s.length() > 0) {
			
			Map<String, String> result = new HashMap<String, String>();
			
			return result;
		}
		else
			return null;
			
	}
	
	public static Map<String, String> parseConstructorArgs(String s) {
		
		if(s.length() > 0) {
			
			Map<String, String> result = new HashMap<String, String>();
			
			return result;
		}
		else
			return null;
			
	}
}

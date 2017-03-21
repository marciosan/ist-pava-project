package ist.meic.pa;

import java.util.HashMap;
import java.util.Map;

public class Parser {
	
	public static Map<String, String> parseKeywordArgs(String s) {
		
		if(s.length() > 0) {
			
			Map<String, String> result = new HashMap<String, String>();
			
			String[] tokens = s.split(",");
			
			for(String token : tokens) {
				
				String[] pair = token.split("=");
				result.put(pair[0], pair[1]);
			}
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

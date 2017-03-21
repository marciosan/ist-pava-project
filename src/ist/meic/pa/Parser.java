package ist.meic.pa;

import java.util.HashMap;
import java.util.Map;

public class Parser {
	
	//parse dos atributos da anotação no construtor
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
	
	//parse dos atributos na inicialização
	public static Map<String, Object> parseInitializatorArgs(Object ...args) {
		
		if(args.length > 0) {
			
			Map<String, Object> result = new HashMap<String, Object>();
			
						
			for (int i=0;i<args.length;i++) {
				
				String key = (String) args[i];
				Object value = args[++i];

				result.put(key,value);				
				
			}
			
//			System.out.println("args init:");
//			for(String s : result.keySet())
//			System.out.println(s + " " + result.get(s));
//			System.out.println("args init end");
			
			return result;
			
		}
		
		else
			return null;
			
	}
}

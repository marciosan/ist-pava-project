package ist.meic.pa;

import java.util.*;

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
	
	public static String[] splitBy(String line, char splitter){
		// this function splits by ',' or '=' depending on [splitter]
		// it must not split a [splitter] inside the following expressions: () {} "" ''
		
		// tested with the following values
		// String [] inputs = {"a=1,b=2", "a=func(1,2,3),b=5", "a={1,2,3}", "a=\"1,2,3\",b=5","a=\',\',b=7","a=\"a,\\\",b \""};
		
		ArrayList<String> strList = new ArrayList<String>();
		int nParenthesis = 0;
		int nBrackets = 0;
		boolean insideString = false;	
		boolean insideChar = false;	
		
		StringBuilder sb = new StringBuilder("");
		
		char[] charArr = line.toCharArray();
		for(int i = 0; i < line.length(); i++){
			char c = charArr[i];
			
			if( c == splitter && nParenthesis == 0 && nBrackets == 0 
				&& !insideString && !insideChar){
				
				strList.add(sb.toString());
				sb.setLength(0);
			}
			else if( c == '('){
				nParenthesis++;
				sb.append(c);
			}
			else if( c == ')'){
				nParenthesis--;
				sb.append(c);
			}
			else if( c == '{'){
				nBrackets++;
				sb.append(c);
			}
			else if( c == '}'){
				nBrackets--;
				sb.append(c);
			}
			else if( c == '\"' && charArr[i-1] != '\\'){
				insideString = !insideString;
				sb.append(c);
			}
			else if( c == '\''){
				insideChar = !insideChar;
				sb.append(c);
			}
			else sb.append(c);
		}
		
		String s = sb.toString();
		if(! s.isEmpty() ){
			strList.add(s);
		}
		
		return strList.toArray(new String[strList.size()]);
	}
}

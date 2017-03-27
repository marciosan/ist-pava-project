package ist.meic.pa;

import java.util.*;

public class Parser {

	/**
	 * Splitter for keyword annotations.
	 * This handles edge cases where split should not be done, such as inside :
	 * - strings:		"a=\",,,\",b=\"===\""
	 * - parenthesis:	"a=2,n=Math.max(2,3)"
	 * - brackets:		"a={1,2,3}"
	 *
	 * @param line		the line we want to split
	 * @param splitter	the char used as an expression to split by, should be a ',' or '='
	 */
	public static String[] splitBy(String line, char splitter){

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

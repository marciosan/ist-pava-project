package ist.meic.pa;

import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javassist.*;

public class KeywordTranslator implements Translator {
	
	List<String> annotAttribs;
	
	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
	
		annotAttribs = new ArrayList<String>();
	}
	
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
		
		try {
			checkAnnotations(pool, className);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void checkAnnotations(ClassPool pool, String className) throws NotFoundException, CannotCompileException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		
		CtClass ctClass = pool.get(className);
		for(CtConstructor ctConstructor: ctClass.getDeclaredConstructors()) {
			
			if(ctConstructor.hasAnnotation("ist.meic.pa.KeywordArgs")) {
				System.out.println("Constructor found: " + ctConstructor.getName());
				
				Object annotation = ctConstructor.getAnnotation(KeywordArgs.class);
				KeywordArgs ka = (KeywordArgs)annotation;
				System.out.println(ka.value());
				
				
				// FIXME: <string, string>
				Map<String,String> argsMap = annotationToMap(ka.value(), className);
				makeConstructor(ctClass, ctConstructor, argsMap);
				annotAttribs.clear();
				
			}
		}
	}
	
	private Map<String,String> annotationToMap(String anotStr, String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		HashMap<String,String> map = new HashMap<String,String>();
		String[] keyVals = anotStr.split(",");
		
		for(String kv: keyVals){
			String[] keyValues = kv.trim().split("="); 
			
			// save attribute name 
			//System.out.println("found annot " + keyValues[0]);
			this.annotAttribs.add(keyValues[0]);
			
			if(keyValues.length !=2){
				continue;
			}
			
			String key = keyValues[0];
			String value = keyValues[1];
			
			if(key == null || value == null){
				continue;
			}
			
			map.put(key,value);
		}
		return map;
	}
	
	private void makeConstructor(CtClass ctClass, CtConstructor ctConstructor, Map<String,String> argsMap) throws CannotCompileException, ClassNotFoundException, NoSuchFieldException{
		
		String body = "{\n";
		for(String k : argsMap.keySet()){
			body+= String.format("\tthis.%s = %s;\n", k, argsMap.get(k));
		}
		body+=" \tObject[] args = $1 ;\n";
		body+= "\tfor(int i = 0; i < args.length; i+= 2) {\n";
		body+= "\t\tObject o = args[i];\n";
		body+= "\t\tObject value = args[i+1];\n";
		//~ body+= "\t\tSystem.out.println(o);\n";
		Class c = Class.forName(ctClass.getName());
		for(String attrib: annotAttribs){
			Class fieldClass = c.getField(attrib).getType();
			String className = fieldClass.getName();
			
			if(fieldClass.isPrimitive()){
				String attribution = primitiveCasting(className);
				body += String.format("\t\tif (((String) o).equals(\"%s\")) this.%s = %s\n", attrib, attrib, attribution);
				
				continue;
			}
			
			body+= String.format("\t\tif (((String) o).equals(\"%s\")) this.%s = (%s) value;\n", attrib, attrib, className);
		}
		body+="\t}\n"; // end of for }
		body+="\n}";
		
		System.out.println("BODY #########");
		System.out.println(body);
		System.out.println("BODY #########");
		ctConstructor.setBody(body);		
	}
	
	public Object createPrimitive(Class myClass, String value) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException{
		String fieldType = myClass.getName();				
		Object obj = null;
		Class myField = null;
		Constructor c = null;	
		
		// ordered by byte, short, int, long, float, double, char, boolean
		if (fieldType.equals("byte")){					
			obj = new Byte(value).byteValue();
		}
		else if (fieldType.equals("short")){					
			obj = new Short(value).shortValue();
		}
		else if (fieldType.equals("int")){					
			obj = new Integer(value).intValue();
		}
		else if (fieldType.equals("long")){					
			obj = new Long(value).longValue();
		}
		else if (fieldType.equals("float")){					
			obj = new Float(value).floatValue();
		}
		else if (fieldType.equals("double")){					
			obj = new Double(value).doubleValue();
		}
		else if (fieldType.equals("char")){					
			char[] chars = new char[1];
			value.getChars(0,0,chars,0); // FIXME: check if str has 1 char only???
			char myChar = chars[0];
			
			obj = myChar;
		}
		else if (fieldType.equals("boolean")){					
			obj = new Boolean(value).booleanValue();
		}
		return obj;
	}

	public String primitiveCasting(String className){
		String str = "";
		
		if (className.equals("byte")){	
			str = "((Byte) value).byteValue();";				
		}
		else if (className.equals("short")){	
			str = "((Short) value).shortValue();";				
		}
		else if (className.equals("int")){					
			str = "((Integer) value).intValue();";
		}
		else if (className.equals("long")){
			str = "((Long) value).longValue();";				
		}
		else if (className.equals("float")){					
			str = "((Float) value).floatValue();"; // TODO: is double ok?
		}
		else if (className.equals("double")){
			str = "((Double) value).doubleValue();";				
		}
		else if (className.equals("char")){
			str = "((Character) value).charValue();";				
		}
		else if (className.equals("boolean")){
			str = "((Boolean) value).booleanValue();";
		}
		
		return str;
	}
}

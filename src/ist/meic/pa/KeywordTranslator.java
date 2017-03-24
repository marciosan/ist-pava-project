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
				Map<String,Object> argsMap = annotationToMap(ka.value(), className);
				makeConstructor(ctClass, ctConstructor, argsMap);
				
			}
		}
	}
	
	private Map<String,Object> annotationToMap(String anotStr, String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		HashMap<String,Object> map = new HashMap<String,Object>();
		String[] keyVals = anotStr.split(",");
		
		for(String kv: keyVals){
			String[] keyValues = kv.trim().split("="); 
			
			if(keyValues.length !=2){
				continue;
			}
			
			String key = keyValues[0];
			String value = keyValues[1];
			
			if(key == null || value == null){
				continue;
			}
			
			// save attribute name 
			this.annotAttribs.add(key);
			
			Class<?> fieldClass = Class.forName(className).getField(key).getType();
			
			Constructor<?> c = null;
			Object obj = null;
			
			if(fieldClass.isPrimitive()){
				obj = createPrimitive(fieldClass, value);
			}
			
			else {			
			c = fieldClass.getConstructor(String.class);
			obj= c.newInstance(value);			
			}
			
			// check for wrapper types before inserting in map
			
			switch(fieldClass.getSimpleName()) {
				
				//case ("Integer"): {
					
					//System.out.println("it's an integer");
					//System.out.println(fieldClass.getSimpleName());
					
					//int temp;
					//temp = obj.intValue();						
					
				//}
			
			}
				map.put(key,obj);
			
		}
		return map;
		
	}
	
	private void makeConstructor(CtClass ctClass, CtConstructor ctConstructor, Map<String,Object> argsMap) throws CannotCompileException{
		
		String body = "{\n";
		for(String k : argsMap.keySet()){
			body+= String.format("\tthis.%s = %s;\n", k, argsMap.get(k));
		}
		body+=" \tObject[] args = $1 ;\n";
		body+= "\tfor(int i = 0; i < args.length; i++) {\n";
		body+= "\t\tObject o = args[i];\n";
		//~ body+= "\t\tSystem.out.println(o);\n";
		for(String attrib: annotAttribs){
			body+= String.format("\t\tif (((String) o).equals(\"%s\")) this.%s = (String)args[++i];\n", attrib, attrib);
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
}

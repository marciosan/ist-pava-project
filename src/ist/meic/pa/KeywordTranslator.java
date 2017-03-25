package ist.meic.pa;

import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Field;

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
				
				Map<String,String> argsMap = annotationToMap(ka.value(), className);
				makeConstructor(ctClass, ctConstructor, argsMap);
				makeEmptyConstructor(ctClass);
				annotAttribs.clear();
			}
		}
	}
	
	private Map<String,String> annotationToMap(String anotStr, String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		HashMap<String,String> map = new HashMap<String,String>();
		
		// empty annotation
		if(anotStr.trim().isEmpty())
			return map;
		
		String[] keyVals = Parser.splitByComma(anotStr);
		
		for(String kv: keyVals){
			String[] keyValues = kv.trim().split("="); 
			
			if(keyValues.length < 1 && keyValues[0] != null){ 
				// should produce array with 1 or 2 indexes (1 without default, 2 with default)
				continue;
			}
			
			String key = keyValues[0];

			// save attribute name
			if(! classHasAttribute(Class.forName(className), key)){
				continue;
			}
			this.annotAttribs.add(key);
			
			if(keyValues.length !=2){
				continue;
			}
			
			String value = keyValues[1];
			if(key == null || value == null){
				continue;
			}
			
			map.put(key,value);
		}
		return map;
	}
	
	private void makeEmptyConstructor(CtClass ctClass) throws CannotCompileException{
		
		CtConstructor ct = CtNewConstructor.defaultConstructor(ctClass);
		ctClass.addConstructor(ct);
		
		CtConstructor[] classCons = ctClass.getConstructors(); // TODO delete
		System.out.println("Number of constrcutors is " + classCons.length); // TODO delete
	}
	
	private void makeConstructor(CtClass ctClass, CtConstructor ctConstructor, Map<String,String> argsMap) throws CannotCompileException, ClassNotFoundException, NoSuchFieldException{
		
		String body = "{\n";
		for(String k : argsMap.keySet()){
			body+= String.format("\tthis.%s = %s;\n", k, argsMap.get(k));
		}
		body+=" \tObject[] args = $1 ;\n";
		body+="\n\n";
		
		body += "\tjava.util.ArrayList attributes = new java.util.ArrayList();\n";
		
		// create arraylist with attributes
		// constructor will need this at runtime to process parameters
		Class c = Class.forName(ctClass.getName());
		for(String attrib: annotAttribs){
			body+= String.format("\tattributes.add(\"%s\");\n", attrib); 
			
			if(! classHasAttribute(c, attrib)){
				continue;
			}
		}
		
		body+="\n";

		// LOOP IN JAVASSIST
		body+= "\tfor(int i = 0; i < args.length; i+= 2) {\n";
		body+= "\t\tObject o = args[i];\n";
		body+= "\t\tObject value = args[i+1];\n\n";
		
		body+="\n\n";
			
		for(String attrib: annotAttribs){
			
			Class fieldClass = c.getField(attrib).getType();
			String className = fieldClass.getName();
			
			body += "\t\tif(! attributes.contains((String)o)){ " +
				"throw new RuntimeException(\"Unrecognized keyword: \" + (String) o); }\n";
			
			if(fieldClass.isPrimitive()){
				String attribution = primitiveCasting(className);
				body += String.format("\t\tif (((String) o).equals(\"%s\")) this.%s = %s\n\n", attrib, attrib, attribution);
				
				continue;
			}
			
			body+= String.format("\t\tif (((String) o).equals(\"%s\")) this.%s = (%s) value;\n\n", attrib, attrib, className);
		}
		body+="\t}\n"; // end of for }
		body+="\n}";
		
		System.out.println("BODY #########");
		System.out.println(body);
		System.out.println("BODY #########");
		ctConstructor.setBody(body);		
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

	// FIXME: need to also check superclasses
	public boolean classHasAttribute(Class<?> aClass, String attribute) throws NoSuchFieldException{
		 if (aClass == null){ return false; }
		 
		 Field f = null;
		 try{
			 f = aClass.getField(attribute);
		 }
		 catch (NoSuchFieldException e){
			throw new RuntimeException ("Unrecognized keyword: " + attribute); 
		 }
		 
		 return f == null? false : true;
	}
}

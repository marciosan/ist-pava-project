package ist.meic.pa;

import java.util.Map;
import java.util.Set;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
				
				Map<String,String> argsMap = annotationToMap(ka.value(), className, new HashMap<String,String>());
				makeConstructor(ctClass, ctConstructor, argsMap);
				makeEmptyConstructor(ctClass);
				annotAttribs.clear();
			}
		}
	}
	
	private Map<String,String> annotationToMap(String anotStr, String className, Map<String, String> map) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{

		System.out.println(">>>>>>>entering class " + className);
		System.out.println(">>>>>>>annot: " + anotStr);
		for(String s : annotAttribs)
			System.out.println("Annot when entering class " + className + ": " + s);
		// empty annotation
		if(anotStr.trim().isEmpty())
			return map;
		
		String[] keyVals = Parser.splitBy(anotStr, ',');
		
		for(String kv: keyVals){
			String[] keyValues = Parser.splitBy(kv.trim(), '=');
			
			if(keyValues.length < 1 && keyValues[0] != null){ 
				// should produce array with 1 or 2 indexes (1 without default, 2 with default)
				continue;
			}
			
			String key = keyValues[0];
			System.out.println("Found key: " + key);

			// save attribute name
			if(! classHasAttribute(Class.forName(className), key)){
				continue;
			}
			this.annotAttribs.add(key);
			System.out.println("Added key: " + key);
			
			if(keyValues.length !=2){
				continue;
			}
			
			String value = keyValues[1];
			if(key == null || value == null){
				continue;
			}
			
			if(!map.containsKey(key)) {
				map.put(key,value);
				System.out.println("map.put " + key + "," + value);
			}
		}
		
		// collect annotations from the superclass
		Class<?> superClass = Class.forName(className).getSuperclass();
		if(superClass != null) {
			
			Constructor<?>[] cons = superClass.getDeclaredConstructors();
			KeywordArgs annot = (KeywordArgs) cons[0].getAnnotation(KeywordArgs.class);
			
			if(Class.forName(className).equals("Object") || annot == null)
				return map;
			
			String superClassName = superClass.getName();
			annotationToMap(annot.value(), superClassName, map);
		}

		// remove duplicates from the annotations list
		Set<String> hs = new HashSet<String>();
		hs.addAll(annotAttribs);
		annotAttribs.clear();
		annotAttribs.addAll(hs);
		for(String s : annotAttribs)
			System.out.println("Annot when exiting class " + className + ": " + s);
		System.out.println("-----------------------");
					
		return map;
	}
	
	private void makeEmptyConstructor(CtClass ctClass) throws CannotCompileException{
		
		CtConstructor ct = CtNewConstructor.defaultConstructor(ctClass);
		ctClass.addConstructor(ct);
	}
	
	private void makeConstructor(CtClass ctClass, CtConstructor ctConstructor, Map<String,String> argsMap) throws CannotCompileException, ClassNotFoundException, NoSuchFieldException{
		
		String body = "{\n";
		
		for(String k : argsMap.keySet()){
			String v = argsMap.get(k);
			
			if(argsMap.get(v) == null){ // a normal field: int a = 3, float b = Math.PI
				body+= String.format("\tthis.%s = %s;\n", k, v);
			}
			else { // a param that is equal to another field: int a = b, int b = a
				body+= String.format("\tthis.%s = %s;\n", k, argsMap.get(v));
			}
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
		
		Class fieldClass = null;
		String className = null;
		for(String attrib: annotAttribs){
			
			fieldClass = getFieldClass(c, attrib);
			className = fieldClass.getName();
			
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
			str = "((Number) value).byteValue();";				
		}
		else if (className.equals("short")){	
			str = "((Number) value).shortValue();";				
		}
		else if (className.equals("int")){					
			str = "((Integer) value).intValue();";
		}
		else if (className.equals("long")){
			str = "((Number) value).longValue();";			
		}
		else if (className.equals("float")){					
			str = "((Number) value).floatValue();"; // TODO: is double ok?
		}
		else if (className.equals("double")){
			str = "((Number) value).doubleValue();";				
		}
		else if (className.equals("char")){
			str = "((Character) value).charValue();";				
		}
		else if (className.equals("boolean")){
			str = "((Boolean) value).booleanValue();";
		}
		
		return str;
	}

	public boolean classHasAttribute(Class<?> aClass, String attribute) throws NoSuchFieldException{
		 if(aClass == null){
			 return false;
		 }
		 
		 Field[] fields = aClass.getDeclaredFields();
		 boolean hasAttribute = false;
		 
		 for(Field f : fields) {
			 if(f.getName().equals(attribute)) { // class has attribute
				 hasAttribute = true;
			 	 break;
			 }
		 }
		 
		 if(!hasAttribute) {// attribute not in this class; look in super classes
			 Class<?> superClass = aClass.getSuperclass();
			 
//			 if(superClass.getSimpleName().equals("Object"))
//			 	hasAttribute = false;
//			 else
		 		hasAttribute = classHasAttribute(superClass, attribute);
		}
		 
		 return hasAttribute;
	}
	
	public Class<?> getFieldClass(Class<?> aClass, String attribute) {
		
		Class<?> fieldClass = null;
		boolean hasAttribute = false;
		
		 if(aClass == null){
			 return null;
		 }
		 
		 Field[] fields = aClass.getDeclaredFields();
		 
		 for(Field f : fields) {
			 if(f.getName().equals(attribute)) { // class has attribute
				 hasAttribute = true;
				 fieldClass = f.getType();
			 	 break;
			 }
		 }
		 
		 if(!hasAttribute) {// attribute not in this class; look in super classes
			 Class<?> superClass = aClass.getSuperclass();
			 
			 if(superClass != null)
//			 if(superClass.getSimpleName().equals("Object"))
//			 	hasAttribute = false;
//			 else
		 		fieldClass = getFieldClass(superClass, attribute);
		}
		 
		if(fieldClass == null)
			throw new RuntimeException("Unrecognized keyword: " + attribute);
		else
			return fieldClass;
	}
}

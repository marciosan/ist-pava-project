package ist.meic.pa;

import java.util.*;
import java.lang.reflect.*;
import javassist.*;

public class KeywordTranslator implements Translator {
	
	List<String> annotAttribs; // collection of all attributes in a class (possibly superclasses too)
	static final boolean DEBUG = false;

	/** 
	 * Loader invokes this for initialization when the translator is attached to the loader
	 * 
	 * @param pool - the pool to be used by the translator
	*/
	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
		annotAttribs = new ArrayList<String>();
	}

	
	/** 
	 * Invoked by a loader when a class is loaded
	 * 
	 * @param pool - the pool to be used by the translator
	 * @param className - the name of the class being loaded
	*/
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {

		try {
			processClass(pool, className);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	/** 
	 * Process a class before it is loaded to the JVM
	 * 
	 * @param pool - the pool to be used by the translator
	 * @param className - the name of the class being loaded
	*/
	private void processClass(ClassPool pool, String className) throws NotFoundException, CannotCompileException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {

		CtClass ctClass = pool.get(className);
		for(CtConstructor ctConstructor: ctClass.getDeclaredConstructors()) {

			if(ctConstructor.hasAnnotation("ist.meic.pa.KeywordArgs")) {
				KeywordTranslator.debug("Constructor found: " + ctConstructor.getName());

				Object annotation = ctConstructor.getAnnotation(KeywordArgs.class);
				KeywordArgs ka = (KeywordArgs) annotation;

				// collect attributes and their values
				Map<String,String> argsMap = annotationToMap(ka.value(), className, new HashMap<String,String>());
				
				// modify the constructor
				makeConstructor(ctClass, ctConstructor, argsMap);
				
				// an empty constructor is needed when handing inheritance
				// could be problematic if we handled multiple annotated constructors (we do not, as per the specification)
				makeEmptyConstructor(ctClass);
				
				// clear attributes list for this class
				annotAttribs.clear();
			}
		}
	}

	
	/** 
	 * Transforms a string annotation of type attribute=defaultValue into a Map<attribute,defaultValue>
	 * Also saves all annotated attributes (even without default value) into the array annotAttribs (that's
	 * because all annotated attributes are needed in the new constructor body, as it will need them to process
	 * its parameters (Obj... args)
	 * 
	 * @param annotStr - the annotation string
	 * @param className - the class name
	 * @param map - container for pairs (attribute, value)
	 * 
	 * @return map with pairs (attribute, value)
	*/
	private Map<String,String> annotationToMap(String anotStr, String className, Map<String, String> map) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{

		/* start of debug */
		debug("Entering class " + className + ", annotation " + anotStr);
		for(String s : annotAttribs)
			debug("Annotation when entering class " + className + ": " + s);
		/* end of debug */
		
		// empty annotation
		if(anotStr.trim().isEmpty())
			return map;

		String[] keyVals = Parser.splitBy(anotStr, ','); // split annotation to get string attribute=defaultValue

		for(String kv: keyVals) {
			String[] keyValues = Parser.splitBy(kv.trim(), '='); // split attribute=defaultValue

			if(keyValues.length < 1 && keyValues[0] != null) {
				// should produce array with 1 or 2 indexes (1 without default, 2 with default)
				continue;
			}

			String key = keyValues[0];
			debug("Found key: " + key);

			if(!classHasAttribute(Class.forName(className), key)) {
				continue;
			}
			
			// save attribute name (only name, without default value)
			if(!annotAttribs.contains(key)) { // don't add duplicate attributes
				this.annotAttribs.add(key);
				debug("Added key: " + key);
			}

			if(keyValues.length !=2) { // no default value to save
				continue;
			}

			String value = keyValues[1];
			if(key == null || value == null){
				continue;
			}

			// save attribute and default value
			if(!map.containsKey(key)) { // don't add duplicate attributes
				map.put(key,value);
				debug("Saved pair (" + key + "," + value + ")");
			}
		}

		// check annotations of the superclass
		Class<?> superClass = Class.forName(className).getSuperclass();
		
		if(superClass != null) {

			Constructor<?>[] cons = superClass.getDeclaredConstructors();
			KeywordArgs annot = (KeywordArgs) cons[0].getAnnotation(KeywordArgs.class);

			if(Class.forName(className).equals("Object") || annot == null) // no more work to do
				return map;

			String superClassName = superClass.getName();
			annotationToMap(annot.value(), superClassName, map); // recursively collect attributes and values
		}

		return map;
	}


	/** 
	 * Creates a constructor that handles annotations. It can be divided in two parts:
	 *   1. Initialize object with default values (at load time)
	 *   2. Create a mechanism to process arguments from (Object ... args) and set attributes (at run time)
	 *   
	 * @param ctClass - the compile time class being modified
	 * @param ctConstructor - the annotated constructor
	 * @param argsMap - map with pairs (attribute, defaultValue)
	*/
	private void makeConstructor(CtClass ctClass, CtConstructor ctConstructor, Map<String,String> argsMap) throws CannotCompileException, ClassNotFoundException, NoSuchFieldException{

		String body = "{\n";
		
		body += makeAnnotationsProcessor(argsMap);
		body += "\n";
		body += makeParametersProcessor(ctClass);
		body += "}";

		// add body to constructor
		ctConstructor.setBody(body);
		KeywordTranslator.debug(String.format("BODY #########\n%s\n#########", body));
	}

	
	/**
	 * Builds part of the constructor body, processing attributes with default values
	 * 
	 * @param argsMap - map with pairs (attribute, defaultValue)
	 * 
	 * @return the assembled part of the body
	 */
	private String makeAnnotationsProcessor(Map<String,String> argsMap) {
		
		String body = "";

		// process pairs (attribute, defaultValue)
		for(String k : argsMap.keySet()) {
			String v = argsMap.get(k);

			if(argsMap.get(v) == null) { // a normal field: int a = 3, float b = Math.PI
				body+= String.format("\tthis.%s = %s;\n", k, v);
			}
			else { // a field that references another field: int a = b, int b = a
				body+= String.format("\tthis.%s = %s;\n", k, argsMap.get(v));
			}
		}

		return body;
	}

	
	/**
	 * Builds part of the constructor body, which processes the parameters array (Object... args)
	 * 
	 * @param ctClass - the compile time class being modified
	 * 
	 * @return the assembled part of the body
	 */
	private String makeParametersProcessor(CtClass ctClass) throws ClassNotFoundException, NoSuchFieldException{
		
		String body = "";
		
		body += " \tObject[] args = $1;\n";
		body += "\n";

		// add array list with attributes to the constructor
		// it will be needed at runtime to process parameters
		body += "\tjava.util.ArrayList attributes = new java.util.ArrayList();\n";
		body += "\n";
		
		Class<?> c = Class.forName(ctClass.getName());
		
		// add every attribute to the array list
		for(String attrib: annotAttribs){
			
			body += String.format("\tattributes.add(\"%s\");\n", attrib);

			if(! classHasAttribute(c, attrib)){
				continue;
			}
		}
		body += "\n";

		// loop in the constructor body to set attributes
		body += "\tfor(int i = 0; i < args.length; i+= 2) {\n\n";
		body += "\t\tObject o = args[i];\n"; // get attribute
		body += "\t\tObject value = args[i+1];\n"; // get value
		body +="\n";
		
		// unrecognized keyword raises exception
		body += "\t\tif(!attributes.contains((String) o)) {\n" +
				"\t\t\tthrow new RuntimeException(\"Unrecognized keyword: \" + (String) o); \n}\n";
		body += "\n";
		
		Class<?> fieldClass = null;
		String className = null;
		
		// for each attribute, set value
		for(String attrib: annotAttribs) {

			fieldClass = getFieldType(c, attrib);
			className = fieldClass.getName();

			// if field class is primitive, casting is needed
			if(fieldClass.isPrimitive()) {
				String attribution = primitiveCasting(className);
				
				body += String.format("\t\tif (((String) o).equals(\"%s\")) this.%s = %s\n\n", attrib, attrib, attribution);
				continue;
			}
			// field not of a primitive type
			body += String.format("\t\tif (((String) o).equals(\"%s\")) this.%s = (%s) value;\n\n", attrib, attrib, className);
		}
		
		body += "\t}\n"; // end of loop in the constructor body
		
		return body;
	}

	
	/**
	 * Add a default constructor to the class (required to handle inheritance)
	 * 
	 * @param ctClass - the compile time class being modified
	*/
	private void makeEmptyConstructor(CtClass ctClass) throws CannotCompileException{

		CtConstructor ct = CtNewConstructor.defaultConstructor(ctClass);
		ctClass.addConstructor(ct);
	}
	
	
	/** 
	 * Process primitive types, which are handled differently than objects
	 * 
	 * Primitives for numbers (byte, float, etc.) are upcast to number and then the correct value is extracted
	 *   - Advantage: the types are very permissive and properly cast by the constructor
	 *   - Disadvantage: loss of precision, as int n = Math.PI becomes int n =((Number) Math.PI).intValue() which evaluates to 3.
	 * 
	 * @param className - the class name
	 * 
	 * @return part of the body with the casting
	*/
	public String primitiveCasting(String className) {
		
		String str = "";

		if(className.equals("byte")) {
			str = "((Number) value).byteValue();";
		}
		else if(className.equals("short")) {
			str = "((Number) value).shortValue();";
		}
		else if(className.equals("int")) {
			str = "((Number) value).intValue();";
		}
		else if(className.equals("long")) {
			str = "((Number) value).longValue();";
		}
		else if(className.equals("float")) {
			str = "((Number) value).floatValue();"; // TODO: is double ok?
		}
		else if(className.equals("double")) {
			str = "((Number) value).doubleValue();";
		}
		else if(className.equals("char")) {
			str = "((Character) value).charValue();";
		}
		else if(className.equals("boolean")) {
			str = "((Boolean) value).booleanValue();";
		}

		return str;
	}

	
	/**
	 * Check if a class has a given attribute
	 * 
	 * @param aClass - a class
	 * @param attribute - the name of the attribute
	 * 
	 * @return true if class has attribute; false otherwise
	*/
	public boolean classHasAttribute(Class<?> aClass, String attribute) throws NoSuchFieldException{
		
		 if(aClass == null) {
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

		 if(!hasAttribute) { // attribute not in this class; look in super classes
			 Class<?> superClass = aClass.getSuperclass();
			 hasAttribute = classHasAttribute(superClass, attribute);
		}

		 return hasAttribute;
	}

	
	/** Get the type of a specific field from a given class
	 * 
	 * @param aClass - a class
	 * @param attribute - the name of the field
	 * 
	 * @return the type of the field
	 * 
	 * @exception RuntimeException if class has no field with the specified name
	*/
	public Class<?> getFieldType(Class<?> aClass, String field) {

		Class<?> fieldClass = null;
		boolean hasAttribute = false;

		 if(aClass == null) {
			 return null;
		 }

		 Field[] fields = aClass.getDeclaredFields();

		 for(Field f : fields) {
			 if(f.getName().equals(field)) { // class has attribute
				 hasAttribute = true;
				 fieldClass = f.getType();
			 	 break;
			 }
		 }

		 if(!hasAttribute) {// attribute not in this class; look in super classes
			 Class<?> superClass = aClass.getSuperclass();

			 if(superClass != null)
		 		fieldClass = getFieldType(superClass, field);
		}

		if(fieldClass == null)
			throw new RuntimeException("Unrecognized keyword: " + field);
		else
			return fieldClass;
	}
	
	/** debif purposes */
	public static void debug(String s) {
		/** Print messages during tests.
		 */
		if(KeywordTranslator.DEBUG){
			System.err.println(s);
		}
	}
}

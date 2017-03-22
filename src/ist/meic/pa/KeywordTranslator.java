package ist.meic.pa;

import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javassist.*;

public class KeywordTranslator implements Translator {

	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

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
			
			Class fieldClass = Class.forName(className).getField(key).getType();
			Constructor c = fieldClass.getConstructor(String.class);
			Object obj= c.newInstance(value);
			map.put(key,obj);
		}
		return map;
		
	}
	
	private void makeConstructor(CtClass ctClass, CtConstructor ctConstructor, Map<String,Object> argsMap) throws CannotCompileException{
		
		String [] annotAttribs = {"name", "description"};
		
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
}

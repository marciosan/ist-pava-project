package ist.meic.pa;

import java.util.Map;
import java.util.HashMap;

import javassist.*;

public class KeywordTranslator implements Translator {

	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

	}
	
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
		
		CtClass ctClass = pool.get(className);
		
		try {
			checkAnnotations(ctClass);
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void checkAnnotations(CtClass ctClass) throws NotFoundException, CannotCompileException, ClassNotFoundException {
		
		for(CtConstructor ctConstructor: ctClass.getDeclaredConstructors()) {
			
			if(ctConstructor.hasAnnotation("ist.meic.pa.KeywordArgs")) {
				System.out.println("Constructor found: " + ctConstructor.getName());
				
				Object annotation = ctConstructor.getAnnotation(KeywordArgs.class);
				KeywordArgs ka = (KeywordArgs)annotation;
				System.out.println(ka.value());
				
				
				// FIXME: <string, string>
				Map<String,String> argsMap = annotationToMap(ka.value());
				makeConstructor(ctClass, ctConstructor, argsMap);
				
			}
		}
	}
	
	private Map<String,String> annotationToMap(String anotStr){
		HashMap<String,String> map = new HashMap<String,String>();
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
			
			map.put(key,value);
		}
		return map;
		
	}
	
	private void makeConstructor(CtClass ctClass, CtConstructor ctConstructor, Map<String,String> argsMap) throws CannotCompileException{
		
		String body = "{\n";
		for(String k : argsMap.keySet()){
			body+= String.format("\tthis.%s = \"%s\";\n", k, argsMap.get(k));
		}
		body+= "Object[] args = $1;";
		body+= "\t this.description = args[1].toString();";
		//body+= "this. = args[1].toString();\n";
		//body+= "String.format(\"\t%s = %s\", (String)args[0], (String)args[1]);";
		body+="}";
		
		System.out.println("BODY #########");
		System.out.println(body);
		System.out.println("BODY #########");
		ctConstructor.setBody(body);
		
	}
}

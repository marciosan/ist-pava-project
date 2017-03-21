package ist.meic.pa;

import java.util.Map;

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
				
				// parse arguments on the annotation
				Map<String, String> argsMap = Parser.parseKeywordArgs(ka.value());
//				System.out.println("args:");
//				for(String s : argsMap.keySet())
//					System.out.println(s + " " + argsMap.get(s));
				
				makeConstructor(ctClass, ctConstructor, argsMap);
				
			}
		}
	}
	
	private void makeConstructor(CtClass ctClass, CtConstructor ctConstructor, Map<String,String>argsMap) throws CannotCompileException{
		
		/* estrategia:
		 * 1. usar os valores das anotacoes para atribuir valores aos atributos
		 * 		- fazer construtor.setbody("this." + map.key + "=" + map.value + ";")
		 * 2. usar os argumentos do constructor (Object[] ...args) - estao na variavel $args
		 * 		- para cada par em $args fazer constrcutor.insertAfter("this.args[0] = $args[1];")
		 * 		- basicamente fazer um for para todos os argumentos
		*/ 
		
	}
}

package ist.meic.pa;

import javassist.*;

public class KeywordTranslator implements Translator {

	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

	}
	
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
		
		CtClass ctClass = pool.get(className);
		
		try {
			test(ctClass);
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void test(CtClass ctClass) throws NotFoundException, CannotCompileException, ClassNotFoundException {
		
		for(CtConstructor ctConstructor: ctClass.getDeclaredConstructors()) {
			
			if(ctConstructor.hasAnnotation("ist.meic.pa.KeywordArgs")) {
				System.out.println("Constructor found: " + ctConstructor.getName());
				
				Object annotation = ctConstructor.getAnnotation(KeywordArgs.class);
				KeywordArgs ka = (KeywordArgs)annotation;
				System.out.println(ka.value());
			}
		}
	}
}

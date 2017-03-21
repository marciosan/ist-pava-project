package ist.meic.pa;

import javassist.*;

public class KeywordTranslator implements Translator {

	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

	}
	
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
		
		System.out.println("in");
		CtClass ctClass = pool.get(className);
		System.out.println("out");
		
		try {
			test(ctClass);
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void test(CtClass ctClass) throws NotFoundException, CannotCompileException, ClassNotFoundException {
		System.out.println(ctClass.getName());
	}
}

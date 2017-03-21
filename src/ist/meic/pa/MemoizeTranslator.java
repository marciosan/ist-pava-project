package pt.ist.ap.labs;

import javassist.*;
import pt.ist.ap.labs.Memoize;


public class MemoizeTranslator implements Translator {

	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

	}
	
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
	
		CtClass ctClass = pool.get(className);
		
		try {
			memoizeMethods(ctClass);
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void memoizeMethods(CtClass ctClass) throws NotFoundException, CannotCompileException, ClassNotFoundException {
	
		for(CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			
			Object[] annotations = ctMethod.getAnnotations();
			
			if((annotations.length == 1) && annotations[0] instanceof Memoize) {
				MemoizeAndRun.memoize(ctClass, ctMethod);
			}
		}
	}
}

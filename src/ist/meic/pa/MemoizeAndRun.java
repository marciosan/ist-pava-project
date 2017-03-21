package pt.ist.ap.labs;

import javassist.*;

public class MemoizeAndRun {
	
	public static void main(String[] args) throws Throwable {
		
		if(args.length < 1) {
			System.out.println("Usage: java MemoizeAndRun <class>");
			System.exit(1);
		}
		else {
			Translator translator = new MemoizeTranslator();
			ClassPool pool = ClassPool.getDefault();
			Loader classLoader = new Loader();
			classLoader.addTranslator(pool, translator);
			
			String[] restArgs = new String[args.length -1];
			System.arraycopy(args, 1, restArgs, 0, restArgs.length);
			classLoader.run(args[0], restArgs);
		}
	}
	
	
	public static void memoize(CtClass ctClass, CtMethod ctMethod) throws NotFoundException, CannotCompileException {
		
		// create field and add it to the class
		CtField ctField = CtField.make("static java.util.Hashtable cachedResults = " + "new java.util.Hashtable();", ctClass);
		ctClass.addField(ctField);
		
		// create new method from the original and add it to the class
		String name = ctMethod.getName();
		ctMethod.setName(name + "$original");
		ctMethod = CtNewMethod.copy(ctMethod, name, ctClass, null);
		ctMethod.setBody("{" +
						 "  Object result = cachedResults.get($1);" + 
						 "  if(result == null) {" +
						 "  System.out.println(\"Not in cache!\"); " + 
						 "    result = " + name + "$original($$);" +
						 "    cachedResults.put($1, result);" + 
						 "  }" +
						 "  return ($r)result;" +
						 "}");
		ctClass.addMethod(ctMethod);
	}
}


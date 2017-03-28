package ist.meic.pa;

import javassist.*;

public class KeyConstructors {
	
	public static void main(String[] args) throws Throwable {

		if(args.length < 1) {
			System.out.println("Usage: java KeyConstructors <class>");
			System.exit(1);
		}
		else {
			Translator translator = new KeywordTranslator();
			ClassPool pool = ClassPool.getDefault();
			Loader classLoader = new Loader();
			classLoader.addTranslator(pool, translator);

			// call test class (possibly with arguments)
			String[] restArgs = new String[args.length -1];
			System.arraycopy(args, 1, restArgs, 0, restArgs.length);
			classLoader.run(args[0], restArgs);
		}
	}
}

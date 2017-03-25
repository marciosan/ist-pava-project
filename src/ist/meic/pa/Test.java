package ist.meic.pa;

public class Test{
	
	public static void main(String[] args){
//		Widget w0 = new Widget();
//		System.out.println(w0);
		
//		Widget w1 = new Widget("description", "overriden description");
//		System.out.println(w1);
		
		//~ Widget w2 = new Widget("name", "bad params on widget", "weight", 3.0);
		//~ System.out.println(w2);
		
//		Primitives p1 = new Primitives();
//		System.out.println(p1);
		
		//~ Primitives p2 = new Primitives("byt", (byte) 11, "shrt", 
			//~ (short) 12, "i", 13, "l", (long) 14444444, "f", (float) 15.55, "d", 
			//~ (double) 10 + Math.PI, "c", 'k', "bool", true);
		//~ System.out.println(p2);
		
//		ExtendedWidget ex1 = new ExtendedWidget();
//		System.out.println(ex1);
		
//		ExtendedWidget ex2 = new ExtendedWidget("ratio", 10000);
//		System.out.println(ex2);
		
		
		//~ System.out.println(new BadClass());
		
		// test A
		System.err.println(new Widget());
		System.err.println(new Widget("width", 80));
		System.err.println(new Widget("height", 30));
		System.err.println(new Widget("margin", 2));
		System.err.println(new Widget("width", 8, "height", 13, "margin", 21));
	}
}

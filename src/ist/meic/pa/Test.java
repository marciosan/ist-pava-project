package ist.meic.pa;

public class Test{
	
	public static void main(String[] args){
		//~ Widget w0 = new Widget();
		//~ System.out.println(w0);
		
		//~ Widget w1 = new Widget("description", "overriden description");
		//~ System.out.println(w1);
		
		//~ Widget w2 = new Widget("name", "uniq widget", "description", "args are working", "height", 99, "width", 3.14);
		//~ System.out.println(w2);
		
		Primitives p = new Primitives("byt", (byte) 11, "shrt", (short) 12, "i", 13, "l", (long) 14444444, "f", (float) 15.55, "d", (double) 16.66, "bool", true);
		System.out.println(p);
		
	}
}

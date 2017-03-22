package ist.meic.pa;

public class Test{
	
	public static void main(String[] args){
		Widget w0 = new Widget();
		System.out.println(w0);
		
		Widget w1 = new Widget("description", "overriden description");
		System.out.println(w1);
		
		Widget w2 = new Widget("name", "uniq widget", "description", "args are working");
		System.out.println(w2);
	}
}

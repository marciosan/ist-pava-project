package ist.meic.pa;

public class Test{
	
	public static void main(String[] args){
		System.out.println("im main");
	}
	
	private String _name;
	
	public Test(Object ...args){
	}
	
	@Override
	public String toString(){
		return _name;
	}
}

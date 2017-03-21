package ist.meic.pa;

public class Widget{
	String name;
	
	@KeywordArgs("name=widget,")
	public Widget(Object... args){
		
		Parser.parseInitializatorArgs(args);
		
	}
	

	
	@Override
	public String toString(){
		return String.format("Widget: %s", name);
	}
}

package ist.meic.pa;

public class Widget{
	String name;
	
	@KeywordArgs("name=widget,")
	public Widget(Object... args){}
	
	@Override
	public String toString(){
		return String.format("Widget: %s", name);
	}
}

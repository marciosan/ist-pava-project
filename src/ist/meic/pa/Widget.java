package ist.meic.pa;

public class Widget{
	int width;
	
	@KeywordArgs("width=10")
	public Widget(Object... args){}
	
	@Override
	public String toString(){
		return String.format("Widget - width:%d", width);
	}
}

package ist.meic.pa;

public class Widget{
	private String name;
	private String description;
	
	@KeywordArgs("name=Default Widget,description=default widget from annotations")
	public Widget(Object... args){
		
		Parser.parseInitializatorArgs(args);
		
	}
	

	
	@Override
	public String toString(){
		return String.format("Widget (%s) is a (%s).", name, description);
	}
}

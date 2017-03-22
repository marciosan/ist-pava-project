package ist.meic.pa;

import java.util.Map;

public class Widget{
	private String name;
	private String description;
	
	@KeywordArgs("name=Default Widget,description=default widget from annotations")
	public Widget(Object... args){
		
		//~ Map<String,Object> map = Parser.parseInitializatorArgs(args);
		//~ for(String s : map.keySet()) System.out.println(s + " " + map.get(s));
	}
	

	
	@Override
	public String toString(){
		return String.format("Widget (%s) is a (%s).", name, description);
	}
}

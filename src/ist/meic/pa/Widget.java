package ist.meic.pa;

import java.util.Map;

public class Widget{
	public String name;
	public String description;
	public int height;
	
	@KeywordArgs("name=\"Default Widget\",description=\"default widget from annotations\", height=100")
	public Widget(Object... args){
	}
	
	@Override
	public String toString(){
		return String.format("Widget (%s) is a (%s). Height = (%s)", name, description, height);		
	}
}

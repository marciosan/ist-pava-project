package ist.meic.pa;

import java.util.Map;

public class Widget{
	public String name;
	public String description;
	public int height;
	public float width;
	
	@KeywordArgs("name=\"Default Widget\",description=\"default widget from annotations\",height,width")
	public Widget(Object... args){
	}
	
	@Override
	public String toString(){
		return String.format("Widget (%s) is a (%s). Height = (%s), Width = (%s)", name, description, height, width);		
	}
}

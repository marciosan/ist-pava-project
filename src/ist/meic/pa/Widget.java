package ist.meic.pa;


public class Widget{
	public String name;
	public String description;
	public int height;
	public float width;
	
	@KeywordArgs("")
	public Widget(Object... args){
	}
	
	@Override
	public String toString(){
		return String.format("Widget (%s) is a (%s). Height = (%s), Width = (%s),", name, description, height, width);		
	}
}

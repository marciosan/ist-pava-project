package ist.meic.pa;


public class Widget{
	public String name;
	public String description;
	public int height;
	public float width;
	
	@KeywordArgs("name=\"bond,james bond\",width=(float)Math.max(2,3)")
	public Widget(Object... args){
	}
	
	@Override
	public String toString(){
		return String.format("Widget (%s) is a (%s). Height = (%s), Width = (%s),", name, description, height, width);		
	}
}

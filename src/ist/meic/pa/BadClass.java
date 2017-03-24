package ist.meic.pa;


public class BadClass{
	public String name;

	
	@KeywordArgs("")
	public BadClass(Object... args){
	}
	
	@Override
	public String toString(){
		return String.format(name);		
	}
}

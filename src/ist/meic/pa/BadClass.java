package ist.meic.pa;


public class BadClass{
	public String name;

	
	@KeywordArgs("nnnname")
	public BadClass(Object... args){
	}
	
	@Override
	public String toString(){
		return String.format(name);		
	}
}

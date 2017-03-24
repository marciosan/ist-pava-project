package ist.meic.pa;

public class ExtendedWidget extends Widget {

	public int ratio;
	
	@KeywordArgs("ratio=10")
	public ExtendedWidget(Object... args) {
		
	}
	
	@Override
	public String toString(){
		String res = super.toString();
		res += String.format(" Ratio = (%d).", ratio);
		return res;
	}
}

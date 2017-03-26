package ist.meic.pa;

class ComplexStrings extends Widget {
	String a,b;
	char c;
	float d;
	int[] e;

	@KeywordArgs("a=\"Bond\" + \" , \" + \"James Bond\",b=\"a=b\",c=\'=\',d=Math.max(1.0,Math.min(Math.PI, Math.E))")
	public ComplexStrings(Object... args) {}

	public String toString() {
		String s= "";
		s+= String.format("- %s\n- %s\n- %c\n", a, b, c);
		s+= d + "\n";

		//~ for(int i: e){
			//~ s+= i + ", ";
		//~ }

		return s;

	}
}

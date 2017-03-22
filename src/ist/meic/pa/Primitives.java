package ist.meic.pa;

public class Primitives{
	// ordered by byte, short, int, long, float, double, char, boolean
	public byte byt;
	public short shrt;
	public int i;
	public long l;
	public float f;
	public double d;
	public char c;
	public boolean bool;
	
	public String name;
	public String description;
	
	@KeywordArgs("byt=1,shrt=2,i=3,l=4444444,f=5.55,d=6.66,bool=True")
	public Primitives(Object... args){}
	
	@Override 
	public String toString(){
		java.lang.StringBuilder builder = new java.lang.StringBuilder();
		builder.append("Primitives: \n");
		builder.append("byte:\t" + byt + "\n");
		builder.append("short:\t" + shrt + "\n");
		builder.append("int:\t" + i + "\n");
		builder.append("long:\t" + l + "\n");
		builder.append("float:\t" + f + "\n");
		builder.append("double:\t" + d + "\n");
		builder.append("char:\t" + c + "\n");
		builder.append("bool:\t" + bool + "\n");
		
		return builder.toString();
	}
}

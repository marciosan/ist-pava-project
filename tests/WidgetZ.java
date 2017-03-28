import ist.meic.pa.KeywordArgs;

class WidgetZ {
	int a;
	int b;

	@KeywordArgs("a=100,b=100,a=50,b=50")
	public WidgetZ(Object... args) {}

	public String toString() {
		return String.format("a:%s,b:%s",
				a, b);
	}
}

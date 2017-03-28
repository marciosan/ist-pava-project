import ist.meic.pa.KeywordArgs;

class WidgetX {
	int a;
	int b;

	@KeywordArgs("a=100,b=a")
	public WidgetX(Object... args) {}

	public String toString() {
		return String.format("a:%s,b:%s",
				a, b);
	}
}

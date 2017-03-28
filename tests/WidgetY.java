import ist.meic.pa.KeywordArgs;

class WidgetY {
	int a;
	int b;

	@KeywordArgs("a=b,b=100")
	public WidgetY(Object... args) {}

	public String toString() {
		return String.format("a:%s,b:%s",
				a, b);
	}
}

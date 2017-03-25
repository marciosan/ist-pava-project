package ist.meic.pa;


class Widget {
	float width;
	float height;
	float margin;
	long longNumber;

	@KeywordArgs("width=100,height=50,margin=5,longNumber")
	public Widget(Object... args) {}

	public String toString() {
		return String.format("width:%s,height:%s,margin:%s",
				width, height, margin);
	}
}

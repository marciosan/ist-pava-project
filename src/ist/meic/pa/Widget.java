package ist.meic.pa;


class Widget {
	float width;
	float height;
	float margin;
	long longNumber;

	@KeywordArgs("width=height,height=5,margin=height,longNumber=height")
	public Widget(Object... args) {}

	public String toString() {
		return String.format("width:%s,height:%s,margin:%s",
				width, height, margin);
	}
}

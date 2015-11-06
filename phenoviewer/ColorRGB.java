package phenoviewer;

public class ColorRGB {
	private float r;
	private float g;
	private float b;

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public String toString() {
		return "ColorRGB [r=" + r + ", g=" + g + ", b=" + b + "]";
	}

  public String toCSV() {
    return ""+r+","+g+","+b+"";
  }

  public ColorRGB toRelRGB() {
    float total = r + g + b;
    return new ColorRGB(r/total*100,g/total*100,b/total*100);
  }

	public ColorRGB()
	{
		setR(0);
		setG(0);
		setB(0);
	}

	public ColorRGB(float r, float g, float b)
	{
		setR(r);
		setG(g);
		setB(b);
	}
}

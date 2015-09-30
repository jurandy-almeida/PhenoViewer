package imageviewer;

/**
 * Object for pixels of mask
 * @author EPHENOLOGY
 *
 */
public class Pixel {
	
	public Pixel() {
		setX(0);
		setY(0);
	}

	int x;
	
	int y;
	
	
	public void setX(int x){
		this.x = x;
	}
	
	public int getX(){
		return x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public int getY(){
		return y;
	}
	
}

package phenoviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class MeanH extends TimeSeriesOperator<ArrayList<Float>>{

	ArrayList<Float> hMean;

		public MeanH(ArrayList<File> imageList, File mask) {
		super(imageList, mask);
		hMean = new ArrayList<Float>();
		}

	public void calcMeanH(File f) {
		try {
			BufferedImage biCalc = ImageIO.read(f);
			int sumH = 0;
			for (Pixel p : maskPixels) {
        int h = (int) (255.0 * hsv[0]);

				sumH = sumH + h;
			}

			float meanH = (float) sumH/(maskPixels.size());
			hMean.add(meanH);
			biCalc.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Float> process(){
		for(File i : imageList)
			this.calcMeanH(i);
		return hMean;
	}
}

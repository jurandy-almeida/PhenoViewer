package phenoviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;

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
        int i = p.getX();
				int j = p.getY();
				int rgb = biCalc.getRGB(i, j);
				int r = (rgb & 0xFF0000) >> 16;
				int g = (rgb & 0xFF00) >> 8;
				int b = (rgb & 0xFF);
				float[] hsv = Color.RGBtoHSB(r, g, b, null);
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

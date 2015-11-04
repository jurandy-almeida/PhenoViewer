package phenoviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class ExcGreen extends TimeSeriesOperator<ArrayList<Float>>{

	ArrayList<Float> greenMean;

		public ExcGreen(ArrayList<File> imageList, File mask) {
		super(imageList, mask);
		greenMean = new ArrayList<Float>();
		}

	public void calcExcGreen(File f) {
		try {
			BufferedImage biCalc = ImageIO.read(f);
			int sumR = 0;
			int sumG = 0;
			int sumB = 0;
			int sumExG = 0;
			for (Pixel p : maskPixels) {
				int i = p.getX();
				int j = p.getY();
				int rgb = biCalc.getRGB(i, j);
				int r = (rgb & 0xFF0000) >> 16;
				int g = (rgb & 0xFF00) >> 8;
				int b = (rgb & 0xFF);

				sumR = sumR + r;
				sumG = sumG + g;
				sumB = sumB + b;
				sumExG = sumExG + g + g -b -r;
			}

			float excG = (float) sumExG/(maskPixels.size());
			greenMean.add(excG);
			biCalc.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Float> process(){
		for(File i : imageList)
			this.calcExcGreen(i);
		return greenMean;
	}
}

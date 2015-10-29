package phenoviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

public class VisualRhythm extends TimeSeriesOperator<BufferedImage>{

	private TreeMap<Double, Integer> maskIndex;
	private ArrayList<Integer> visualRhythm;

	public VisualRhythm(ArrayList<File> imageList, File mask) {
		super(imageList, mask);
		maskIndex = new TreeMap<Double, Integer>();
		visualRhythm = new ArrayList<Integer>();
	}

	public void indexingMask() {
		// find the center
		int center_x = 0;
		int center_y = 0;
		for (Pixel p : maskPixels) {
			center_x += p.getX();
			center_y += p.getY();
		}
		center_x /= maskPixels.size();
		center_y /= maskPixels.size();
		// convert to polar coordinates
		ArrayList<Double> rhoList = new ArrayList<Double>();
		ArrayList<Double> thetaList = new ArrayList<Double>();
		for (Pixel p : maskPixels) {
			int dx = p.getX() - center_x;
			int dy = p.getY() - center_y;
			double rho = Math.sqrt(dx * dx + dy * dy);
			double theta = Math.atan2(dy, dx);
			rhoList.add(rho);
			thetaList.add(theta);
		}
		// compute the orientation histogram
		int OriBins = 36;
		int[] hist = new int[OriBins];
		for (int i = 0; i < OriBins; i++)
			hist[i]= 0;
	    for (Double angle : thetaList) {
	        int bin = (int) (OriBins * (angle + Math.PI + 0.001) / (2.0 * Math.PI));
	        assert(bin >= 0 && bin <= OriBins);
	        bin = Math.min(bin, OriBins - 1);
	        hist[bin]++;
	    }
	    // find maximum value in the histogram
		double maxval = 0;
		int maxloc = 0;
	    for (int i = 0; i < OriBins; i++)
	        if (hist[i] > maxval) {
	            maxval = hist[i];
	            maxloc = i;
	        }
		double angle = 2.0 * Math.PI * (maxloc + 0.5) / OriBins - Math.PI;
	    for (int i = 0; i < thetaList.size(); i++)
			thetaList.set(i, thetaList.get(i) - angle);
		for (int i = 0; i < thetaList.size(); i++)
			maskIndex.put(2 * Math.PI * rhoList.get(i) + thetaList.get(i), i);
	}

	public void calcVisualRhythm(File f) {
		try {
			BufferedImage biCalc = ImageIO.read(f);
			for (Map.Entry<Double, Integer> entry : maskIndex.entrySet()) {
				Pixel p = maskPixels.get(entry.getValue());
				int i = p.getX();
				int j = p.getY();
				int rgb = biCalc.getRGB(i, j);
				visualRhythm.add(rgb);
			}
			biCalc.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getVisualRhythmImage() {
		int width = maskPixels.size();
		int height = visualRhythm.size() / maskPixels.size();

		BufferedImage vr = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		int p = 0;
		for (int j = 0; j < vr.getHeight(); j++)
			for (int i = 0; i < vr.getWidth(); i++)	{
				vr.setRGB(i, j, visualRhythm.get(p));
				p++;
			}
		return vr;
	}

	public BufferedImage process() {
		this.indexingMask();
		for(File i : imageList)
			calcVisualRhythm(i);
		return this.getVisualRhythmImage();
	}
}

package phenoviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class AvgRgb extends TimeSeriesOperator<ArrayList<ColorRGB>>{

  ArrayList<ColorRGB> avgRgb;

  public AvgRgb(ArrayList<File> imageList, File mask) throws IOException {
    super(imageList, mask);
    avgRgb = new ArrayList<ColorRGB>();
  }

  public void calcAvgRGB(File f) {
    BufferedImage biCalc = ImageFunctions.load(f);
    int sumR = 0;
    int sumG = 0;
    int sumB = 0;
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
    }

    float meanR = (float) sumR/maskPixels.size();
    float meanG = (float) sumG/maskPixels.size();
    float meanB = (float) sumB/maskPixels.size();
    ColorRGB newpoint = new ColorRGB(meanR, meanG, meanB);
    avgRgb.add(newpoint);

    biCalc.flush();
  }

  public ArrayList<ColorRGB> process() {
    for(File i : imageList)
      this.calcAvgRGB(i);
    return avgRgb;
  }

}

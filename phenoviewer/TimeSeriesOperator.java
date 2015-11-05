package phenoviewer;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



import javax.imageio.ImageIO;

public abstract class TimeSeriesOperator<T>{
  protected ArrayList<File> imageList;
  private File mask;
  protected ArrayList<Pixel> maskPixels;
  private boolean changed;

  public abstract T process();

  public TimeSeriesOperator() throws IOException{
    setImageListAndMask(null, null);
  }

  public TimeSeriesOperator(ArrayList<File> imageList, File mask) {
    maskPixels = new ArrayList<Pixel>();
    setImageListAndMask(imageList, mask);
  }

  public ArrayList<File> getImageList(){
    return imageList;
  }

  public File getMask(){
    return mask;
  }

  public void setMask(File mask){
    setImageListAndMask(this.imageList, mask);
  }

  public void setImageList(ArrayList<File> imageList) {
    setImageListAndMask(imageList, this.mask);
  }

  @SuppressWarnings("unused")
  private boolean isChanged(){
    return changed;
  }

  private void setImageListAndMask(ArrayList<File> imageList, File mask) {
    this.imageList = imageList;
    if(this.mask != mask)
    {
      getMaskPixels(mask);
      this.mask = mask;
    }
    this.changed = true;
  }

  private void getMaskPixels(File mask) {
    BufferedImage maskImage = null;
    try {
      maskImage = ImageIO.read(mask);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int width  = maskImage.getWidth();
    int height = maskImage.getHeight();

    Raster maskRaster = maskImage.getRaster();
    if (maskRaster.getNumBands() == 1) {

      for (int i = 0; i < width; i++)
        for (int j = 0; j < height; j++) {
        Pixel pixel = new Pixel();
        if (maskRaster.getSample(i, j, 0) == 1) {
          pixel.setX(i);
          pixel.setY(j);
          maskPixels.add(pixel);
        }
      }
    } else if (maskRaster.getNumBands() == 3) {

      for (int i = 0; i < width; i++)
        for (int j = 0; j < height; j++) {
        Pixel pixel = new Pixel();
        int r = maskRaster.getSample(i, j, 0);
        int g = maskRaster.getSample(i, j, 1);
        int b = maskRaster.getSample(i, j, 2);
        if ((r == 255) && (g == 255) && (b == 255)) {
          pixel.setX(i);
          pixel.setY(j);
          maskPixels.add(pixel);
        }
      }
    }
  }
}

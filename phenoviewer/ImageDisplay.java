package phenoviewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.rmi.server.ExportException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * This class has functions of processing images - view
 * @author EPHENOLOGY
 *
 */
@SuppressWarnings("serial")
public class ImageDisplay extends JLabel {
  // red band Matrix
  static final float RED_BAND_MATRIX[][] = { { 1.0f, 0.0f, 0.0f },
  { 0.0f, 0.0f, 0.0f }, { 0.0f, 0.0f, 0.0f } };

  // green band Matrix
  static final float GREEN_BAND_MATRIX[][] = { { 0.0f, 0.0f, 0.0f },
  { 0.0f, 1.0f, 0.0f }, { 0.0f, 0.0f, 0.0f } };

  // blue band Matrix
  static final float BLUE_BAND_MATRIX[][] = { { 0.0f, 0.0f, 0.0f },
  { 0.0f, 0.0f, 0.0f }, { 0.0f, 0.0f, 1.0f } };

  // Matrix that inverts all the bands
  // the nagative of the image.
  static final float INVERSE_BAND_MATRIX[][] = { { -1.0f, 0.0f, 0.0f },
  { 0.0f, -1.0f, 0.0f }, { 0.0f, 0.0f, -1.0f } };

  // Matrix that reduces the intensities of all bands
  static final float AVERAGE_BAND_MATRIX[][] = { { 0.5f, 0.0f, 0.0f },
  { 0.0f, 0.5f, 0.0f }, { 0.0f, 0.0f, 0.5f } };

  boolean isImageLoaded;

  // greice
  boolean isMaskLoaded;
  //

  Image displayImage;

  //greice
  Image displayImageMask;
  //

  Histogram redHistogram, greenHistogram, blueHistogram;

  ArrayList<Float> redMean, greenMean, blueMean, hMean, totalMean, excG;

  ArrayList<Integer> visualRhythm;

  String colorModel = "RGB";

  int imgWidth, imgHeight;

  double imgScale = 0.0;

  double imgAngle = 0.0;

  boolean imgInvX = false;

  boolean imgInvY = false;

  // The source and destination images
  BufferedImage biSrc;

  BufferedImage biDest;

  BufferedImage biShow;

  // greice
  BufferedImage biMask;

  // The source and destination rasters
  Raster srcRaster;

  WritableRaster dstRaster;

  BufferedImage bi;

  //greice
  BufferedImage biM;
  //

  Graphics2D big;

  //greice
  ArrayList<Pixel> maskPixels;
  //

  TreeMap<Double, Integer> maskIndex;

  ImageDisplay() {
    setBackground(Color.black);
    redHistogram = new Histogram("Red", "", 16, 0, 256);
    greenHistogram = new Histogram("Green", "", 16, 0, 256);
    blueHistogram = new Histogram("Blue", "", 16, 0, 256);
    maskPixels = new ArrayList<Pixel>();
    maskIndex = new TreeMap<Double, Integer>();
    redMean = new ArrayList<Float>();
    greenMean = new ArrayList<Float>();
    blueMean = new ArrayList<Float>();
    hMean = new ArrayList<Float>();
    totalMean = new ArrayList<Float>();
    excG = new ArrayList<Float>();
    visualRhythm = new ArrayList<Integer>();
    isImageLoaded = false;
    isMaskLoaded = false;// greice
  }

  public boolean isImageLoaded() {
    return isImageLoaded;
  }

  // greice
  public boolean isMaskLoaded() {
    return isMaskLoaded;
  }
  //

  public Histogram getRedHistogram() {
    return redHistogram;
  }

  public Histogram getGreenHistogram() {
    return greenHistogram;
  }

  public Histogram getBlueHistogram() {
    return blueHistogram;
  }

  public List<Float> getRedMean() {
    return redMean;
  }

  public List<Float> getGreenMean() {
    return greenMean;
  }

  public List<Float> getBlueMean() {
    return blueMean;
  }

  public List<Float> getHMean() {
    return hMean;
  }

  public List<Float> getTotalMean() {
    return totalMean;
  }

  public List<Float> getExcessGreen() {
    return excG;
  }

  public List<Integer> getVisualRhythmPixels() {
    return visualRhythm;
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

  public void setColorModel(String colorModel) {
    if (this.colorModel != colorModel) {
      this.colorModel = colorModel;
      if (colorModel.equals("RGB")) {
        redHistogram.setTitle("Red");
        greenHistogram.setTitle("Green");
        blueHistogram.setTitle("Blue");
      } else if (colorModel.equals("HSB")) {
        redHistogram.setTitle("Hue");
        greenHistogram.setTitle("Saturation");
        blueHistogram.setTitle("Brightness");
      }
      if (isImageLoaded)
        computeColorHistograms();
    }
  }

  public void loadFile(String fileName) {
    displayImage = Toolkit.getDefaultToolkit().getImage(fileName);
    MediaTracker mt = new MediaTracker(this);
    mt.addImage(displayImage, 1);
    try {
      mt.waitForAll();
    } catch (Exception e) {
      System.out.println("Exception while loading.");
    }

    isImageLoaded = true;

    originalSize();

    setAngle(0.0);

    imgInvX = false;
    imgInvY = false;

    createBufferedImages();

    if(biShow!=null) biShow.flush();
    biShow = biSrc;

    redHistogram.setFilterBin(-1);
    greenHistogram.setFilterBin(-1);
    blueHistogram.setFilterBin(-1);

    computeColorHistograms();
  }

  public void computeColorHistograms() {
    int width = biShow.getWidth();
    int height = biShow.getHeight();

    boolean grayLevel = (biShow.getColorModel().getNumColorComponents() == 1);

    redHistogram.clear();
    greenHistogram.clear();
    blueHistogram.clear();

    Raster raster = biShow.getRaster();

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (grayLevel) {
          int c = raster.getSample(i, j, 0);
          if (c > 0)
            redHistogram.add((double) c);
        } else {
          int r = raster.getSample(i, j, 0);
          int g = raster.getSample(i, j, 1);
          int b = raster.getSample(i, j, 2);
          if (colorModel.equals("RGB")) {
            if ((r > 0) && (r < 255))
              redHistogram.add((double) r);
            if ((g > 0) && (g < 255))
              greenHistogram.add((double) g);
            if ((b > 0) && (b < 255))
              blueHistogram.add((double) b);
          } else if (colorModel.equals("HSB")) {
            float[] hsv = Color.RGBtoHSB(r, g, b, null);
            int h = (int) (255.0 * hsv[0]);
            int s = (int) (255.0 * hsv[1]);
            int v = (int) (255.0 * hsv[2]);
            redHistogram.add((double) h);
            greenHistogram.add((double) s);
            if ((v > 0) && (v < 255))
              blueHistogram.add((double) v);
          }
        }
      }
    }
  }

  public boolean isFlippedHorizontally() {
    return imgInvX;
  }

  public boolean flipHorizontally() {
    if (isImageLoaded) {
      imgInvX = !imgInvX;
      return true;
    }
    return false;
  }

  public boolean isFlippedVertically() {
    return imgInvY;
  }

  public boolean flipVertically() {
    if (isImageLoaded) {
      imgInvY = !imgInvY;
      return true;
    }
    return false;
  }

  public double getAngle() {
    return imgAngle;
  }

  public boolean setAngle(double value) {
    if (isImageLoaded) {
      if (imgAngle != value) {
        imgAngle = value;
        return true;
      }
    }
    return false;
  }

  public double getScale() {
    return imgScale;
  }

  public boolean setScale(double value) {
    if (isImageLoaded) {
      if (imgScale != value) {
        imgScale = value;
        imgWidth = (int) (imgScale * displayImage.getWidth(this));
        imgHeight = (int) (imgScale * displayImage.getHeight(this));
        return true;
      }
    }
    return false;
  }

  public boolean originalSize() {
    return setScale(1.0);
  }

  public boolean fitToSize(int winWidth, int winHeight) {
    if (isImageLoaded) {
      int oriWidth = displayImage.getWidth(this);
      int oriHeight = displayImage.getHeight(this);

      float scaleWidth = (float) winWidth / oriWidth;
      float scaleHeight = (float) winHeight / oriHeight;

      if (scaleWidth <= scaleHeight)
        return setScale(scaleWidth);
      else
        return setScale(scaleHeight);
    }
    return false;
  }

  public void createBufferedImages() {
    int width = displayImage.getWidth(this);
    int height = displayImage.getHeight(this);

    if(biSrc!=null) biSrc.flush();
    biSrc = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    Graphics2D g2D = biSrc.createGraphics();
    g2D.drawImage(displayImage, 0, 0, width, height, this);
    g2D.dispose();

    srcRaster = biSrc.getRaster();

    if(biDest!=null) biDest.flush();
    biDest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    dstRaster = (WritableRaster) biDest.getRaster();

  }

  public void bandCombine(float[][] bandCombineMatrix) {
    if (isImageLoaded) {
      BandCombineOp bandCombineOp = new BandCombineOp(bandCombineMatrix,
                                                      null);
      bandCombineOp.filter(srcRaster, dstRaster);
      biShow = biDest;
      computeColorHistograms();
    }
  }

  public void prepare() {
    if (isImageLoaded) {
      double sin = Math.abs(Math.sin(imgAngle));
      double cos = Math.abs(Math.cos(imgAngle));
      int newImgWidth = (int) Math
        .floor(imgWidth * cos + imgHeight * sin);
      int newImgHeight = (int) Math.floor(imgHeight * cos + imgWidth
                                          * sin);
      if(bi!=null) bi.flush();
      bi = new BufferedImage(newImgWidth, newImgHeight,
                             BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2D = bi.createGraphics();
      if (imgInvX && imgInvY) {
        g2D.scale(-1, -1);
        g2D.translate(-newImgWidth, -newImgHeight);
      } else if (imgInvX) {
        g2D.scale(-1, 1);
        g2D.translate(-newImgWidth, 0);
      } else if (imgInvY) {
        g2D.scale(1, -1);
        g2D.translate(0, -newImgHeight);
      }
      g2D.translate(0.5 * (newImgWidth - imgWidth),
                    0.5 * (newImgHeight - imgHeight));
      g2D.rotate(imgAngle, 0.5 * imgWidth, 0.5 * imgHeight);
      g2D.scale(imgScale, imgScale);
      g2D.drawRenderedImage(biShow, null);
      g2D.dispose();
      Dimension dim = new Dimension(bi.getWidth(), bi.getHeight());
      setPreferredSize(dim);
      setSize(dim);

      prepareMask();
    }
  }

  //greice
  public void prepareMask() {
    if (isImageLoaded && isMaskLoaded) {
      double sin = Math.abs(Math.sin(imgAngle));
      double cos = Math.abs(Math.cos(imgAngle));
      int newImgWidth = (int) Math
        .floor(imgWidth * cos + imgHeight * sin);
      int newImgHeight = (int) Math.floor(imgHeight * cos + imgWidth
                                          * sin);
      if(biM!=null) biM.flush();
      biM = new BufferedImage(newImgWidth, newImgHeight,
                              BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2D = biM.createGraphics();
      if (imgInvX && imgInvY) {
        g2D.scale(-1, -1);
        g2D.translate(-newImgWidth, -newImgHeight);
      } else if (imgInvX) {
        g2D.scale(-1, 1);
        g2D.translate(-newImgWidth, 0);
      } else if (imgInvY) {
        g2D.scale(1, -1);
        g2D.translate(0, -newImgHeight);
      }
      g2D.translate(0.5 * (newImgWidth - imgWidth),
                    0.5 * (newImgHeight - imgHeight));
      g2D.rotate(imgAngle, 0.5 * imgWidth, 0.5 * imgHeight);
      g2D.scale(imgScale, imgScale);
      g2D.drawRenderedImage(biMask, null);
      g2D.dispose();
      Dimension dim = new Dimension(biM.getWidth(), biM.getHeight());
      setPreferredSize(dim);
      setSize(dim);
    }
  }

  //

  public void filter() {
    if (isImageLoaded) {
      // greice
      if (isMaskLoaded) {
        Raster raster = biM.getRaster();
        for (int i = 0; i < biM.getWidth(); i++)
          for (int j = 0; j < biM.getHeight(); j++) {
          int rgb = bi.getRGB(i, j);
          int r = raster.getSample(i, j, 0);
          int g = raster.getSample(i, j, 1);
          int b = raster.getSample(i, j, 2);
          if ((r != 255) & (g != 255) & (b != 255))
            bi.setRGB(i, j, 0x22FFFFFF & rgb);
          else
            bi.setRGB(i, j, 0xFFFFFFFF & rgb);
        }
      }//

      if ((redHistogram.getFilterBin() != -1)
          || (greenHistogram.getFilterBin() != -1)
          || (blueHistogram.getFilterBin() != -1)) {
        int rLo = (int) redHistogram.getLo();
        int rHi = (int) redHistogram.getHi();
        int gLo = (int) greenHistogram.getLo();
        int gHi = (int) greenHistogram.getHi();
        int bLo = (int) blueHistogram.getLo();
        int bHi = (int) blueHistogram.getHi();

        for (int i = 0; i < bi.getWidth(); i++)
          for (int j = 0; j < bi.getHeight(); j++) {
          int rgb = bi.getRGB(i, j);
          int r = (rgb & 0xFF0000) >> 16;
          int g = (rgb & 0xFF00) >> 8;
          int b = (rgb & 0xFF);
          if (colorModel.equals("RGB")) {
            if ((r < rLo) || (r > rHi) || (g < gLo)
                || (g > gHi) || (b < bLo) || (b > bHi))
              bi.setRGB(i, j, 0x22FFFFFF & rgb);
            else
              bi.setRGB(i, j, 0xFFFFFFFF & rgb);
          } else if (colorModel.equals("HSB")) {
            float[] hsv = Color.RGBtoHSB(r, g, b, null);
            int h = (int) (255.0 * hsv[0]);
            int s = (int) (255.0 * hsv[1]);
            int v = (int) (255.0 * hsv[2]);
            if ((h < rLo) || (h > rHi) || (s < gLo)
                || (s > gHi) || (v < bLo) || (v > bHi))
              bi.setRGB(i, j, 0x22FFFFFF & rgb);
            else
              bi.setRGB(i, j, 0xFFFFFFFF & rgb);
          }
        }
      }
    }
  }

  public void reset() {
    System.gc();
    if(biShow!=null) biShow.flush();
    if (isImageLoaded) {
      biShow = biSrc;
      computeColorHistograms();
    }
  }

  public void update(Graphics g) {
    g.clearRect(0, 0, getWidth(), getHeight());
    paintComponent(g);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2D = (Graphics2D) g;
    if (isImageLoaded) {
      int xoffset, yoffset;
      if (bi.getWidth() < getWidth())
        xoffset = (getWidth() - bi.getWidth()) / 2;
      else
        xoffset = 0;
      if (bi.getHeight() < getHeight())
        yoffset = (getHeight() - bi.getHeight()) / 2;
      else
        yoffset = 0;
      g2D.drawImage(bi, xoffset, yoffset, bi.getWidth(), bi.getHeight(),
                    this);
    }
  }


  //greice
  public void resetMask() {
    if(biMask!=null) biMask.flush();
    if (isMaskLoaded) {
      biMask = biShow;
      resetSeries();
      isMaskLoaded = false;
    }
  }

  public void resetSeries() {
    redMean.clear();
    greenMean.clear();
    blueMean.clear();
    hMean.clear();
    totalMean.clear();
    visualRhythm.clear();
    excG.clear();
  }

  public void processingMask() {
    maskPixels.clear();

    int width = displayImageMask.getWidth(this);
    int height = displayImageMask.getHeight(this);

    biMask = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    Graphics2D g2D = biMask.createGraphics();
    g2D.drawImage(displayImageMask, 0, 0, width, height, this);
    g2D.dispose();

    isMaskLoaded = true;

    Raster maskRaster = biMask.getRaster();

    width = biMask.getWidth();
    height = biMask.getHeight();

    //identifica pixels da imagem original que sao brancos e representam a mascara.
    for (int i = 0; i < width; i++)
      for (int j = 0; j < height; j++) {
      Pixel pixel = new Pixel();
      int r = maskRaster.getSample(i, j, 0);
      int g = maskRaster.getSample(i, j, 1);
      int b = maskRaster.getSample(i, j, 2);
      if ((r == 255) && (g == 255) && (b == 255)) {
        pixel.setX(i);
        pixel.setY(j);
        maskPixels.add(pixel); //cria lista de pixels da mascara- brancos.
      }
    }

    indexingMask();
  }

  public void indexingMask() {
    maskIndex.clear();
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

  @SuppressWarnings("deprecation")
  public void loadFileMask(FileNode node) {
    //reset();
    String fileName = "";
    if(node != null){
      File f = node.getFile();
      if(!f.isDirectory()){
        fileName = f.getPath();
      }
    }

    if(!fileName.equals("")){
      if(fileName.toLowerCase().endsWith(".bmp")){

        try {
          displayImageMask =ImageIO.read(new URL(node.getFile().toURL(), node.getFile().getName()));
        } catch (IOException e) {
          //
          e.printStackTrace();
        }
      }
      else{

        displayImageMask = Toolkit.getDefaultToolkit().getImage(fileName);
      }

      MediaTracker mt = new MediaTracker(this);
      mt.addImage(displayImageMask, 1);
      try {
        mt.waitForAll();
      } catch (Exception e) {
        System.out.println("Exception while loading.");
      }

      processingMask();
    }
  }

  public void calcAvgRGB(File f) {
    try {
      BufferedImage biCalc = ImageIO.read(f);
      int sumR = 0;
      int sumG = 0;
      int sumB = 0;
      int sumH = 0;
      int sumExG = 0;
      for (Pixel p : maskPixels) {
        int i = p.getX();
        int j = p.getY();
        int rgb = biCalc.getRGB(i, j);
        int r = (rgb & 0xFF0000) >> 16;
        int g = (rgb & 0xFF00) >> 8;
        int b = (rgb & 0xFF);
        float[] hsv = Color.RGBtoHSB(r, g, b, null);
        int h = (int) (255.0 * hsv[0]);

        sumR = sumR + r;
        sumG = sumG + g;
        sumB = sumB + b;
        sumH = sumH + h;
        sumExG = sumExG + g + g -b -r;

      }
      float meanR = (float) sumR/maskPixels.size();
      float meanG = (float) sumG/maskPixels.size();
      float meanB = (float) sumB/maskPixels.size();
      float meanH = (float) sumH/maskPixels.size();
      float total = meanR + meanG + meanB;
      float excessGreen = (float) sumExG/(maskPixels.size());

      redMean.add(meanR);
      greenMean.add(meanG);
      blueMean.add(meanB);
      hMean.add(meanH);
      totalMean.add(total);
      excG.add(excessGreen);
      biCalc.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
}

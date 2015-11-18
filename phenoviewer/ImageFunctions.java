package phenoviewer;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class ImageFunctions {

  ImageFunctions() {
  }

  public BufferedImage load(File f) {
    BufferedImage image = null;
    try
    {
      image = ImageIO.read(f);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally {
      return image;
    }
  }

  public BufferedImage load(FileNode f) {
    return load((File)f.getFile());
  }

  public BufferedImage load(String path) {
    return load((File)new File(path));
  }

  //Name Place-holders
  public BufferedImage read(File f) {
    return load(f);
  }
  public BufferedImage read(FileNode f) {
    return load(f);
  }
  public BufferedImage read(String f) {
    return load(f);
  }

  public ImageIcon toIcon(BufferedImage img) {
    return new ImageIcon(img);
  }
}

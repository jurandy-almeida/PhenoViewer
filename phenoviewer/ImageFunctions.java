package phenoviewer;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public final class ImageFunctions {

  ImageFunctions() {
  }

  public static BufferedImage load(File f) {
    BufferedImage image = null;
    try
    {
      if (f!=null)
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

  public static BufferedImage load(FileNode f) {
    return load(f!=null ? (File)f.getFile() : null);
  }

  public static BufferedImage load(String path) {
    return load((File)new File(path));
  }

  //Name Place-holders
  public static BufferedImage read(File f) {
    return load(f);
  }
  public static BufferedImage read(FileNode f) {
    return load(f);
  }
  public static BufferedImage read(String f) {
    return load(f);
  }

  public static ImageIcon toIcon(BufferedImage img) {
    return new ImageIcon(img);
  }
}

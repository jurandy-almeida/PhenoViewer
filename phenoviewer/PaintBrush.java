package phenoviewer;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PaintBrush extends JPanel {
  private static final long serialVersionUID = -7022531643310133501L;
  private int xvalue = -1000000, yvalue = -1000000, h, w;
  private int brushSize = 8, currentZoom = 0, offsetX = 0, offsetY = 0;
  private int[] zoomW, zoomH;
  private int opacity = 160;
  private int polygonX[], polygonY[], polyPos;
  private boolean first, erase, creatingPolygon, auxPolygon, generatingSquares;
  BufferedImage mask, original, scaledOriginal, maskor;
  Color maskColor;
  Graphics2D maskG;

  public PaintBrush( BufferedImage ori, BufferedImage mskr, boolean edit) {
    erase = false;
    first = true;
    maskColor = new Color(255, 255, 255, opacity);
    creatingPolygon = false;
    generatingSquares = false;
    original = ori; //imageDisplay.biSrc;
    maskor = mskr;
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    w = original.getWidth();
    h = original.getHeight();

    scaledOriginal = original;

    zoomW = new int[] { w, (int) (w * 1.20), (int) (w * 1.20 * 1.20),
                       (int) (w * 1.20 * 1.20 * 1.20),
                         (int) (w * 1.20 * 1.20 * 1.20 * 1.20) };
    zoomH = new int[] { h, (int) (h * 1.20), (int) (h * 1.20 * 1.20),
                       (int) (h * 1.20 * 1.20 * 1.20),
                         (int) (h * 1.20 * 1.20 * 1.20 * 1.20) };

    this.setPreferredSize(new Dimension());
    mask = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    maskG = mask.createGraphics();
    maskG.setColor(Color.white);
    // se for edit mask, mostra a mascara atual
    if (edit) {
      for (int j = 0; j < h; j++)
        for (int i = 0; i < w; i++){
        if (maskor.getRGB(i, j) == -1) {
          maskG.drawLine(i, j, i, j);
        }
      }
    }

    repaint();

    addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent arg0) {
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
      }

      @Override
      public void mouseExited(MouseEvent arg0) {
      }

      @Override
      public void mousePressed(MouseEvent arg0) {
      }

      @Override
      public void mouseReleased(MouseEvent arg0) {
        if (arg0.getButton() == 1) {
          auxPolygon = true;
          xvalue = arg0.getX();
          yvalue = arg0.getY();
        } else { // right click

        }

      }
    });
    addMouseMotionListener

      (new MouseMotionAdapter() {
        public void mouseDragged(MouseEvent event) {
          xvalue = event.getX();
          yvalue = event.getY();
        }

      }

      );
  }

  public static BufferedImage getScaledImage(BufferedImage image, int width,
                                             int height) throws IOException {
    int imageWidth = image.getWidth();
    int imageHeight = image.getHeight();

    double scaleX = (double) width / imageWidth;
    double scaleY = (double) height / imageHeight;
    AffineTransform scaleTransform = AffineTransform.getScaleInstance(
      scaleX, scaleY);
    AffineTransformOp bilinearScaleOp = new AffineTransformOp(
      scaleTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

    return bilinearScaleOp.filter(image, new BufferedImage(width, height,
                                                           image.getType()));
  }

  public void paint(Graphics g) {
    if (first) {
      super.paint(g);
      first = false;
    }
    g.drawImage(scaledOriginal, 0, 0, this);
    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   (float) 0.6);
    ((Graphics2D) g).setComposite(ac);
    g.drawImage(mask, offsetX, offsetY, this);
    xvalue = xvalue - offsetX;
    yvalue = yvalue - offsetY;
    if (xvalue >= 0 && yvalue >= 0) {
      if (!creatingPolygon) {
        if (!erase) {
          maskG.fillOval(xvalue - brushSize / 2, yvalue - brushSize
                         / 2, brushSize, brushSize);
        } else {
          maskG.setComposite(AlphaComposite
                             .getInstance(AlphaComposite.CLEAR));
          maskG.fillOval(xvalue - brushSize / 2, yvalue - brushSize
                         / 2, brushSize, brushSize);
          maskG.setComposite(AlphaComposite
                             .getInstance(AlphaComposite.SRC_OVER));
        }
      } else if (auxPolygon) {
        polygonX[polyPos] = xvalue;
        polygonY[polyPos] = yvalue;
        polyPos++;
        if (polyPos > 1) {
          maskG.drawLine(polygonX[polyPos - 2],
                         polygonY[polyPos - 2], polygonX[polyPos - 1],
                         polygonY[polyPos - 1]);
        }
        auxPolygon = false;
      }
    }
    xvalue = -1000000;
    yvalue = -1000000;
    repaint();
  }

  public void moveLeft() {
    offsetX--;
  }

  public void moveRight() {
    offsetX++;
  }

  public void moveDown() {
    offsetY++;
  }

  public void moveUp() {
    offsetY--;
  }

  public void zoomIn() {
    if (currentZoom == zoomW.length - 1)
      return;
    try {
      currentZoom++;
      w = zoomW[currentZoom];
      h = zoomH[currentZoom];
      BufferedImage maskAux = getScaledImage(mask, w, h);
      maskG.dispose();
      mask.flush();
      mask = maskAux;
      maskG = mask.createGraphics();
      BufferedImage scaledAux = getScaledImage(scaledOriginal, w, h);
      scaledOriginal.flush();
      scaledOriginal = scaledAux;
      scaledAux.flush();
      maskAux.flush();
      System.gc();
    } catch (IOException e) {
      e.printStackTrace();
    }
    repaint();
    super.revalidate();
    super.repaint();
    xvalue = -1000000;
    yvalue = -1000000;
  }

  public void zoomOut() {
    if (currentZoom == 0)
      return;
    try {
      currentZoom--;
      w = zoomW[currentZoom];
      h = zoomH[currentZoom];
      BufferedImage maskAux = getScaledImage(mask, w, h);
      maskG.dispose();
      mask.flush();
      mask = maskAux;
      maskG = mask.createGraphics();
      BufferedImage scaledAux = getScaledImage(scaledOriginal, w, h);
      scaledOriginal.flush();
      scaledOriginal = scaledAux;
      scaledAux.flush();
      maskAux.flush();
      System.gc();
    } catch (IOException e) {
      e.printStackTrace();
    }
    repaint();
    super.revalidate();
    super.repaint();
    xvalue = -1000000;
    yvalue = -1000000;
  }

  public void reset() {
    maskG.dispose();
    mask.flush();
    mask = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    maskG = mask.createGraphics();
    maskG.setColor(Color.white);
    xvalue = -1000000;
    yvalue = -1000000;
    repaint();
  }

  public void createPolygon() {
    if (creatingPolygon) { // preenche polygon
      creatingPolygon = false;
      maskG.fillPolygon(polygonX, polygonY, polyPos);
      xvalue = -10;
      yvalue = -10;
      repaint();
    } else { // comeca a criar polygon
      creatingPolygon = true;
      polygonX = new int[200];
      polygonY = new int[200];
      polyPos = 0;
      auxPolygon = false;
    }
  }

  public void generateSquares() {
    generatingSquares = true;
    final JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0,2));
    final JTextField x = new JTextField("1");
    final JTextField y = new JTextField("1");

    JButton divide = new JButton("Okay");

    panel.add(new JLabel("Número de quadrados:"));
    panel.add(new JLabel(""));
    panel.add(new JLabel("Horizontais:"));
    panel.add(new JLabel("Verticais"));
    panel.add(x);
    panel.add(y);

    divide.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setSize(800,700);
        remove(panel);

        //Save Original Image, Create image with lines, when the lining process is finished, restore image without lines
        //original is the image. MaskG is where we set the final result.
        BufferedImage before = original; //Backup for manipulation

        Graphics2D g2d = original.createGraphics();
        g2d.setColor(Color.RED);
        BasicStroke bs = new BasicStroke(2);
        g2d.setStroke(bs);
        // draw the black vertical and horizontal lines
        int xint = Integer.parseInt(x.getText());
        int yint = Integer.parseInt(y.getText());

        for(int i=0;i<xint;i++){
          int x1 = (original.getWidth()+1)*i/xint;
          g2d.drawLine(x1, 0, x1,original.getHeight());
        }
        for(int i=0;i<yint;i++){
          int y1 = (original.getHeight()+1)*i/yint;
          g2d.drawLine(0, y1, original.getWidth(), y1);
        }
      }
    });

    panel.add(divide);

    panel.setVisible(true);
  }

  public void setBrush(int inc) {
    brushSize += inc;
    if (brushSize == 1)
      brushSize = 2;
  }

  public void setErase() {
    erase = !erase;
    xvalue = yvalue = -1000000;
  }

  public Dimension getPreferredSize() {
    return new Dimension(w, h);
  }

  public void close() {
    maskG.dispose();
    mask.flush();
    original.flush();
    scaledOriginal.flush();
    System.gc();
  }

  public void save() {
    try {
      BufferedImage finalMask = new BufferedImage(original.getWidth(),
                                                  original.getHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics2D finalMaskG = finalMask.createGraphics();
      BufferedImage maskAux = getScaledImage(mask, original.getWidth(),
                                             original.getHeight());
      finalMaskG.drawImage(maskAux, offsetX, offsetY, this);

      String nome = JOptionPane.showInputDialog("New mask name:");
      if (nome == null || nome == "") {
        JOptionPane.showMessageDialog(null, "Please fill in the name.",
                                      "", JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      if (ImageIO.write(finalMask, "bmp", new File("./masks/" + nome
                                                   + ".bmp"))) {
        JOptionPane.showMessageDialog(null, "Mask saved.", "",
                                      JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null,
                                      "Error trying to save the file.", "",
                                      JOptionPane.INFORMATION_MESSAGE);
      }
      finalMaskG.finalize();
      finalMask.flush();
      maskAux.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

package phenoviewer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CheckeredGenerator extends JFrame {
  public CheckeredGenerator(final BufferedImage image) {
    super("Square Tiles Generator");
    this.setSize(500,110);
    final JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0,2));
    final JTextField x = new JTextField("1");
    final JTextField y = new JTextField("1");

    JButton divide = new JButton("Okay");
    divide.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setSize(800,700);
        remove(panel);
        //Show Image with squared image on top and selector
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.PAGE_AXIS));
        imagePanel.add(new JLabel("Select the squares you want the software to analyze:"));

        BufferedImage squared = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        //http://stackoverflow.com/questions/3914265/drawing-multiple-lines-in-a-bufferedimage

        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        BasicStroke bs = new BasicStroke(2);
        g2d.setStroke(bs);
        // draw the black vertical and horizontal lines
        int xint = Integer.parseInt(x.getText());
        int yint = Integer.parseInt(y.getText());
        /*for(int i=0;i<xint;i++){
          // unless divided by some factor, these lines were being
          // drawn outside the bound of the image..
          int x1 = (image.getWidth()+1)*i/xint;
          g2d.drawLine(x1, 0, x1,image.getHeight());
        }
        for(int i=0;i<yint;i++){
          int y1 = (image.getHeight()+1)*i/yint;
          g2d.drawLine(0, y1, image.getWidth(), y1);
        }
        imagePanel.add(new JLabel(new ImageIcon(image.getScaledInstance(800, 400, Image.SCALE_SMOOTH))));

        JFrame temp = new JFrame();
        temp.add(new JLabel(new ImageIcon(image)));
        temp.setSize(image.getWidth(),image.getHeight());
        temp.setVisible(true);
        */
        JPanel miniatures = new JPanel();
        miniatures.setLayout(new GridLayout(yint, xint, 3, 3)); //3 is value of gaps between tiles
        for (int j=0;j<yint;j++) {
          for(int i=0;i<xint;i++) {
            int x1,y1,h1,w1;
            x1=i*image.getWidth()/xint;
            y1=j*image.getHeight()/yint;
            w1=image.getWidth()/xint;
            h1=image.getHeight()/yint;

            JLabel section = new JLabel(new ImageIcon(image.getSubimage(x1,y1,w1,h1).getScaledInstance(800/xint,600/xint,Image.SCALE_SMOOTH)));
            miniatures.add(section);
            //
          }
        }
        imagePanel.add(miniatures);
        imagePanel.add(new JLabel(""));
        add(imagePanel);
      }
    });


    panel.add(new JLabel("Número de quadrados:"));
    panel.add(new JLabel(""));
    panel.add(new JLabel("Horizontais:"));
    panel.add(new JLabel("Verticais"));
    panel.add(x);
    panel.add(y);
    panel.add(divide);


    this.add(panel);
  }
}

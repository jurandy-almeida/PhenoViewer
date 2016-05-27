package phenoviewer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.File;
import javax.imageio.ImageIO;
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
        final JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.PAGE_AXIS));
        imagePanel.add(new JLabel("Select the squares you want the software to analyze:"));

        final BufferedImage maskbi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY); //mask where we draw

        //http://stackoverflow.com/questions/3914265/drawing-multiple-lines-in-a-bufferedimage

        final Graphics2D mask = maskbi.createGraphics();

        int xint = Integer.parseInt(x.getText());
        int yint = Integer.parseInt(y.getText());

        /*Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        BasicStroke bs = new BasicStroke(2);
        g2d.setStroke(bs);
        // draw the black vertical and horizontal lines
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
        miniatures.setLayout(new GridLayout(yint, xint, 2, 2)); //3 is value of gaps between tiles
        for (int j=0;j<yint;j++) {
          for(int i=0;i<xint;i++) {
            final int x1,y1,h1,w1;
            x1=i*image.getWidth()/xint;
            y1=j*image.getHeight()/yint;
            w1=image.getWidth()/xint;
            h1=image.getHeight()/yint;

            JButton section = new JButton();
            section.setIcon(new ImageIcon(image.getSubimage(x1,y1,w1,h1).getScaledInstance(700/xint,525/xint,Image.SCALE_SMOOTH)));
            section.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                if (new Color(maskbi.getRGB(x1+(w1/2),y1+(h1/2))).getRed()==255) { //invert the color on each click
                  mask.setPaint(Color.black);
                } else {
                  mask.setPaint(Color.white);
                }
                mask.fill(new Rectangle2D.Double(x1, y1, w1, h1));
                /*System.out.println("Filled "+x1+","+y1+","+w1+","+h1);
                JFrame frame = new JFrame();
                frame.add(new JLabel(new ImageIcon(maskbi)));
                frame.setVisible(true);*/
              }
            });

            miniatures.add(section);
            //
          }
        }
        imagePanel.add(miniatures);
        imagePanel.add(new JLabel(""));
        JButton savebtn = new JButton("Save");
        savebtn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(imagePanel) == JFileChooser.APPROVE_OPTION) {
              File file = fileChooser.getSelectedFile();
              // save to file
              try {
              ImageIO.write(maskbi, "BMP", file);
              } catch (IOException ex) {
                ex.printStackTrace();
              }
            }
          }
        });
        imagePanel.add(savebtn);

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

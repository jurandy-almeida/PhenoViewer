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
  public CheckeredGenerator(BufferedImage image, BufferedImage mask) {
    super("Square Tiles Generator");
    this.setSize(500,110);
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0,2));
    JTextField x = new JTextField("1");
    JTextField y = new JTextField("1");

    JButton divide = new JButton("Okay");
    divide.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setSize(800,800);
        remove(panel);
        //Show Image with squared image on top and selector
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(0,1));
        imagePanel.add(new JLabel("Select the squares you want the software to analyze:"));

        BufferedImage squared = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        //http://stackoverflow.com/questions/3914265/drawing-multiple-lines-in-a-bufferedimage

        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        BasicStroke bs = new BasicStroke(2);
        g2d.setStroke(bs);
        // draw the black vertical and horizontal lines
        for(int i=0;i<Integer.parseInt(x.getText());i++){
            // unless divided by some factor, these lines were being
            // drawn outside the bound of the image..
            g2d.drawLine((image.getWidth()+2)/20*i, 0, (image.getWidth()+2)/20*i,image.getHeight()-1);
            g2d.drawLine(0, (image.getHeight()+2)/20*i, image.getWidth()-1, (image.getHeight()+2)/20*i);
        }
        imagePanel.add(new JLabel(new ImageIcon(image.getScaledInstance(800, 400, Image.SCALE_DEFAULT))));


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


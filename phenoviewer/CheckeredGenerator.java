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


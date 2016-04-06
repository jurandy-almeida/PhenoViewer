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

    panel.add(new JLabel("Número de quadrados:"));
    panel.add(new JLabel(""));
    panel.add(new JLabel("Horizontais:"));
    panel.add(new JLabel("Verticais"));
    panel.add(x);
    panel.add(y);
    this.add(panel);
  }
}


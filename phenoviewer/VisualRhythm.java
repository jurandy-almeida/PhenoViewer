package phenoviewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class VisualRhythm extends JFrame {

  VisualRhythm() {
    super("Visual Rhythm");
    setSize(800,150);
    setVisible(true);
  }

  VisualRhythm(BufferedImage vrimage) {
    super("Visual Rhythm");
    setSize(800,150);
    addImage(vrimage);
    setVisible(true);
  }

  public void addImage(BufferedImage vrimage) {
    add(new JScrollPane(new JLabel(new ImageIcon(vrimage))));
  }
}

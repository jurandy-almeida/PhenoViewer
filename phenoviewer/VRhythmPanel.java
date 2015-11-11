package phenoviewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class VRhythmPanel extends JFrame {

  VRhythmPanel() {
    super("Visual Rhythm");
    setSize(800,150);
    setVisible(true);
  }

  VRhythmPanel(BufferedImage vrimage, String name) {
    super("Visual Rhythm - "+name);
    setSize(800,150);
    addImage(vrimage);
    setVisible(true);
  }

  public void addImage(BufferedImage vrimage) {
    add(new JScrollPane(new JLabel(new ImageIcon(vrimage))));
  }
}

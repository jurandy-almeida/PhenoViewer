package imageviewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

@SuppressWarnings("serial")
public class DrawGraph extends JPanel {
  private static int MAX_SCORE = 15;
  private static final int PREF_W = 800;
  private static final int PREF_H = 650;
  private static final int BORDER_GAP = 5;
  private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
  private static final int GRAPH_POINT_WIDTH = 6;
  private static final int Y_HATCH_CNT = 10;
  private List<Float> r, g, b, t;
  private int mode;

  public DrawGraph(List<Float> r, List<Float> g, List<Float> b, List<Float> t, int mode) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.t = t;
    this.mode = mode;
  }

  @Override
  protected void paintComponent(Graphics g) {
    // Set panel background color.
    setBackground (Color.WHITE);

    super.paintComponent(g);
    plotLine(g, this.r, this.t, Color.red, new Color(139, 0, 0, 180));
    plotLine(g, this.g, this.t, Color.green, new Color(0, 100, 0, 180));
    plotLine(g, this.b, this.t, Color.blue, new Color(0, 0, 139, 180));
  }

  protected void plotLine(Graphics g, List<Float> scores, List<Float> totals, Color GRAPH_COLOR, Color GRAPH_POINT_COLOR) {
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    if (mode==1) scores = fix(scores);
    else MAX_SCORE = 15;

    double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (scores.size() - 1);
    double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (MAX_SCORE - 1);

    List<Point> graphPoints = new ArrayList<Point>();
    for (int i = 0; i < scores.size(); i++) {
      int x1 = (int) (i * xScale + BORDER_GAP);
      int y1;
      if(mode == 0) y1 = (int) ((MAX_SCORE - (100 * scores.get(i) / totals.get(i)) + 25) * yScale + BORDER_GAP);
      else y1 = (int) ((MAX_SCORE - scores.get(i)) * yScale + BORDER_GAP);
      graphPoints.add(new Point(x1, y1));
    }

    // create x and y axes
    g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
    g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

    // create hatch marks for y axis.
    for (int i = 0; i < Y_HATCH_CNT; i++) {
      int x0 = BORDER_GAP;
      int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
      int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
      int y1 = y0;
      g2.drawLine(x0, y0, x1, y1);
    }

    // and for x axis
    for (int i = 0; i < scores.size() - 1; i++) {
      int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (scores.size() - 1) + BORDER_GAP;
      int x1 = x0;
      int y0 = getHeight() - BORDER_GAP;
      int y1 = y0 - GRAPH_POINT_WIDTH;
      g2.drawLine(x0, y0, x1, y1);
    }

    Stroke oldStroke = g2.getStroke();
    g2.setColor(GRAPH_COLOR);
    g2.setStroke(GRAPH_STROKE);
    for (int i = 0; i < graphPoints.size() - 1; i++) {
      int x1 = graphPoints.get(i).x;
      int y1 = graphPoints.get(i).y;
      int x2 = graphPoints.get(i + 1).x;
      int y2 = graphPoints.get(i + 1).y;
      g2.drawLine(x1, y1, x2, y2);
    }

    g2.setStroke(oldStroke);
    g2.setColor(GRAPH_POINT_COLOR);
    for (int i = 0; i < graphPoints.size(); i++) {
      int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
      int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
      int ovalW = GRAPH_POINT_WIDTH;
      int ovalH = GRAPH_POINT_WIDTH;
      g2.fillOval(x, y, ovalW, ovalH);
    }
  }
  private List<Float> fix(List<Float> scores) { // fix min and max heights
    float min = 999, max = -999;
    for (int i = 0; i < scores.size(); i++) {
      if(scores.get(i) < min) min = scores.get(i);
    }
    for (int i = 0; i < scores.size(); i++) {
      scores.set(i, scores.get(i) - min+5);
    }
    for (int i = 0; i < scores.size(); i++) {
      if(scores.get(i) > max) max = scores.get(i);
    }
    MAX_SCORE = (int)max+5;
    return scores;
  }
}

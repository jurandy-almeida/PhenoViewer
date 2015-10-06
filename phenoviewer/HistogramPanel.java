// "Java Tech"
//  Code provided with book for educational purposes only.
//  No warranty or guarantee implied.
//  This code freely available. No copyright claimed.
//  2003

package phenoviewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**  Display histogram data on a PlotPanel subclass. **/
public class HistogramPanel extends PlotPanel {

  // Histogram reference
  protected Histogram fHistogram;

  // Histogram bar parameters.
  protected Color fBarLineColor = Color.DARK_GRAY;
  protected Color fBarFillColor = Color.PINK;
  int fGap = 2;  // 2 pixels between bars

  // Fractional margin between highest bar and top frame
  double fTopMargin  = 0.05;

  // Fractional margnin between side bars and frame
  double fSideMargin = 0.01;

  // Arrays to hold numbers for axes scale values
  double [] fXScaleValue;
  double [] fYScaleValue;

  // Number of values on each axis
  int fNumYScaleValues = 2;
  int fNumXScaleValues = 5;

  /** Create the panel with the histogram. **/
  public HistogramPanel (Histogram histogram) {
    fHistogram = histogram;
    getScaling ();
    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        // Get the histogram max value and bin data
        int max_data_value = fHistogram.getMax ();
        int [] bins = fHistogram.getBins ();

        if (max_data_value == 0) return;

        // Dimensions of the drawing area for the bars
        int side_space =  (int) (fFrameWidth*fSideMargin);
        int draw_width= fFrameWidth - 2 * side_space- (bins.length-1)*fGap;
        int draw_height=  (int)(fFrameHeight* (1.0 - fTopMargin));

        // To avoid build up of round off errors, the bar
        // positions will be calculated from a FP value.
        float step_width= (float)draw_width/(float)bins.length;
        step_width += fGap;

        int start_x = fFrameX + side_space;
        int bar_width = (int)(bins.length * step_width);

        if ((e.getX() >= start_x) && (e.getX() <= start_x + bar_width)) {
          int bin = (int)((float)(e.getX() - start_x) / step_width);

          // Scale the bars to the maximum bin value.
          float scale_factor = (float)draw_height/max_data_value;

          int start_y = fFrameY + fFrameHeight;
          int bar_height = (int)(bins[bin] * scale_factor);

          if ((e.getY() >= start_y - bar_height) && (e.getY() <= start_y)) {
            if (bin == fHistogram.getFilterBin())
              fHistogram.setFilterBin(-1);
            else
              fHistogram.setFilterBin(bin);
            repaint();
          }
        }
      }
    });
  }

  /** Switch to a new histogram. **/
  public void setHistogram (Histogram histogram){
    fHistogram = histogram;
    getScaling ();
    repaint ();
  }

  /**
    * Get the values for the scale values on
    * the plot axes.
   **/
  void getScaling () {

    fYScaleValue = new double[fNumYScaleValues];
    // Use lowest value of 0;
    fYScaleValue[0] = 0.0;

    fXScaleValue = new double[fNumXScaleValues];
    // First get the low and high values;
    fXScaleValue[0] = fHistogram.getLo ();
    fXScaleValue[fNumXScaleValues-1] = fHistogram.getHi ();

    // Then calculate the difference between the values
    //  (assumes linear scale)
    double range = fXScaleValue[fNumXScaleValues-1] -
      fXScaleValue[0];
    double dx = range/(fNumXScaleValues-1);

    // Now set the intermediate scale values.
    for (int i=1; i < (fNumXScaleValues-1); i++) {
      fXScaleValue[i] = i*dx + fXScaleValue[0];
    }

  } // getScaling

  /** Optional bar color settings. **/
  public void setBarColors (Color line, Color fill) {
    if ( line != null) fBarLineColor = line;
    if ( fill != null) fBarFillColor = fill;
  }


  /**
    * Overrides the abstract method in PlotPanel superclass.
    * Draw the vertical bars that represent the bin contents.
    * Draw also the numbers for the scales on the axes.
   **/
  void paintContents (Graphics g) {

    // Get the histogram max value and bin data
    int max_data_value = fHistogram.getMax ();
    int [] bins = fHistogram.getBins ();

    if (max_data_value == 0) return;

    Color old_color=g.getColor ();  // remember color for later

    // Dimensions of the drawing area for the bars
    int side_space =  (int) (fFrameWidth*fSideMargin);
    int draw_width= fFrameWidth - 2 * side_space- (bins.length-1)*fGap;

    int draw_height=  (int)(fFrameHeight* (1.0 - fTopMargin));

    // To avoid build up of round off errors, the bar
    // positions will be calculated from a FP value.
    float step_width= (float)draw_width/(float)bins.length;
    int bar_width = Math.round (step_width);
    step_width += fGap;

    // Scale the bars to the maximum bin value.
    float scale_factor= (float)draw_height/max_data_value;

    int start_x = fFrameX + side_space;
    int start_y = fFrameY + fFrameHeight;

    for (int i=0; i < bins.length; i++) {
      int bar_height =  (int)(bins[i] * scale_factor);

      int bar_x =  (int)(i * step_width) + start_x;

      // Bar drawn from top left corner
      int bar_y= start_y-bar_height;

      g.setColor (fBarLineColor);
      g.drawRect (bar_x,bar_y, bar_width ,bar_height);

      if (i == fHistogram.getFilterBin())
        g.setColor (Color.BLACK);
      else
        g.setColor (fBarFillColor);
      g.fillRect (bar_x+1, bar_y+1, bar_width-2, bar_height-1);
    }

    // Find the scale value of the full frame height.
    fYScaleValue[1] =  (double) (fFrameHeight/scale_factor);

    // Draw the numbers along the axes.
    drawAxesNumbers (g, fXScaleValue, fYScaleValue);

    g.setColor (old_color); //reset original color

  } // paintContents

  // Methods overriding those in PlotPanel
  String getTitle () {
    return fHistogram.getTitle ();
  }

  String getXLabel () {
    return fHistogram.getXLabel ();
  }

} // class HistogramPanel


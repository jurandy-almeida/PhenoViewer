/**
This class loads an CSV file so that the user can select dynamic graphing options for data visualization
@author Héctor Castelli Zacharias
*/
package phenoviewer;

import java.io.*;
import java.net.URL;
import java.util.Scanner;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JRadioButton;
import javax.swing.event.*;
import java.util.Arrays;
import java.awt.Color;
import java.awt.event.*;
import com.opencsv.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CSVAnalyzer extends JFrame {


  /**
   Return the graphic panel that should be added on the JFrame
   @param title The title of the Graph
   @param axisX The title of the X axis
   @param axisY The title of the Y axis
   @param data A XYSeriesCollection containing the data to be plotted
   @param showLine  Show line between points
   @param showShape Show shapes on points
   @param fillShape Fill shapes
   @param autoSort Sort data automatically by X values
   @param allowDuplicateXValues Allow/Disallow duplicated X values
   @param bgColor The default background color of the graph
   @param orientation The default orientation of the graph (0-HORIZONTAL;1-VERTICAL)
   @param showLegend Show legend
   @param tooltip Create tooltip
   @param url Create URL
   @param mode (0-exact;1-Rounded Curve;2-Area)
   @return CharPanel component with the specified proprierties
   */
  public static ChartPanel createGraphPanel(String title, String axisX, String axisY, XYSeriesCollection data, int orientation, boolean legend, boolean tooltip, boolean url, int mode, boolean showShape, boolean showLine, boolean fillShape, Color bgColor) {
    //if orientation = 0 -> PlotOrientation.VERTICAL
    //if mode = 0 -> exact; if = 1 SplineRenderer; if = 2 Area
    PlotOrientation orient;
    if (orientation == 0)
      orient = PlotOrientation.VERTICAL;
    else
      orient = PlotOrientation.HORIZONTAL;

    JFreeChart chart;

    if (mode == 2) {
      chart = ChartFactory.createXYAreaChart(title, axisX, axisY, data, orient, legend, tooltip, url);
    } else {
      chart = ChartFactory.createXYLineChart(title, axisX, axisY, data, orient, legend, tooltip, url);
    }

    XYPlot plot = chart.getXYPlot();

    if (mode == 0) {
      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(showLine, showShape);
      renderer.setBaseShapesFilled(fillShape);
      plot.setBackgroundPaint(bgColor);
      plot.setRenderer(renderer);
    } else if(mode == 1) {
      XYLineAndShapeRenderer renderer = new XYSplineRenderer();
      renderer.setBaseLinesVisible(showLine);
      renderer.setBaseShapesVisible(showShape);
      renderer.setBaseShapesFilled(fillShape);
      plot.setBackgroundPaint(bgColor);
      plot.setRenderer(renderer);
    } else {
      XYAreaRenderer renderer = new XYAreaRenderer();
      plot.setBackgroundPaint(bgColor);
      plot.setRenderer(renderer);
    }

    ChartPanel chartPanel = new ChartPanel(chart);
    return (chartPanel);
  }





  boolean showLine = true;
  boolean showShape = false;
  boolean shapeFill = true;
  boolean autoSort = false;
  boolean allowDuplicateXValues = true;
  Color backcolor = Color.LIGHT_GRAY;
  int orientation = 0;
  boolean showLegend = true;
  boolean createTooltip = true;
  boolean createURL = false;
  int modeGraph = 0;
  /**
   * Takes data from a CSV file and places it into a table for display.
   * @param source Path to the CSV to be anayzed
   */
  public CSVAnalyzer(JTable table, final String[] legend, String[][] graphData) {
    super("CSV Analyzer");
    this.setSize(580, 720);

    table.setAutoCreateRowSorter(true);

    //Variables for series list
    final XYSeries[] seriesList = new XYSeries[legend.length];

    //Add table to ScrollPane for browsing
    JScrollPane listPane = new JScrollPane(table);


    //Filters
    //Must make the generation of filter options dynamic with the TITLE of the table value
    //CheckBoxes for columns to plot (meanR, meanG, meanB, relR, relG, relB, excG, meanH)
    JPanel checkPanel = new JPanel();

    //Initialize the array with flags for data plotting
    final boolean[] data = new boolean[legend.length];
    Arrays.fill(data, false);

    // Define ChangeListener
    ChangeListener changeListener = new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
        AbstractButton abstractButton =
          (AbstractButton)changeEvent.getSource();
        ButtonModel buttonModel = abstractButton.getModel();
        //boolean armed = buttonModel.isArmed();
        boolean pressed = buttonModel.isPressed();
        boolean selected = buttonModel.isSelected();
        String title = abstractButton.getText();
        if (pressed) {
          for(int i=0;i<legend.length; i++) {
            if (title == legend[i]) {
              data[i] = selected;
            }
          }
        }
        //System.out.println("Changed: " + selected + "--" + title);
      }
    };

    for (int i=4; i<legend.length; i++) {
      JCheckBox checkBox = new JCheckBox(legend[i], false);
      checkBox.addChangeListener(changeListener);
      checkPanel.add(checkBox);
    }


    // Define RadioChangeListener
    ChangeListener radiochangeListener = new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
        JRadioButton jRadioButton = (JRadioButton) changeEvent.getSource();
        boolean selected = jRadioButton.isSelected();
        String title = jRadioButton.getText();
        if (selected) {
          if (title == "Ponto-Linha") {
            modeGraph = 0;
          } else if (title == "Linha Aproximada") {
            modeGraph = 1;
          } else {
            modeGraph = 2;
          }
        }
      }
      //System.out.println("Changed: " + selected + "--" + title);
    };


    //RadioButton for graphic style (line, spline or area)
    JRadioButton lineRadio, splineRadio, areaRadio;
    ButtonGroup groupRadio = new ButtonGroup();
    lineRadio = new JRadioButton("Ponto-Linha", true);
    groupRadio.add(lineRadio);
    lineRadio.addChangeListener(radiochangeListener);
    splineRadio = new JRadioButton("Linha Aproximada", false);
    groupRadio.add(splineRadio);
    splineRadio.addChangeListener(radiochangeListener);
    areaRadio = new JRadioButton("Área", false);
    groupRadio.add(areaRadio);
    areaRadio.addChangeListener(radiochangeListener);

    //Button for graphic adding
    JButton plotButton = new JButton("PLOT GRAPH");
    plotButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {
        //System.out.println("pressed!");
        XYSeriesCollection dataCollection = new XYSeriesCollection();
        for (int i=4;  i<legend.length; i++) {
          if (data[i] == true) {
            dataCollection.addSeries(seriesList[i]);
          }
        }
        JFrame graphFrame = new JFrame();
        graphFrame.setSize(580, 420);
        graphFrame.add(createGraphPanel("", "Day of Year", "Y", dataCollection, orientation, true, true, true, modeGraph, showShape, showLine, shapeFill, backcolor));
        graphFrame.setVisible(true);
      }
    });

    //Add filter to ScrollPane for display
    JPanel filterPanel = new JPanel();
    JScrollPane filterPane = new JScrollPane();
    filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
    //Add checkboxes

    JPanel radioPanel = new JPanel();
    radioPanel.add(lineRadio); //Added lineRadio
    radioPanel.add(splineRadio); //Added splineRadio
    radioPanel.add(areaRadio); //Added areaRadio
    filterPanel.add(radioPanel);

    //Dynamic Added
    filterPanel.add(checkPanel);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(plotButton); //Added PlotButton
    filterPanel.add(buttonPanel);

    filterPane.getViewport().add(filterPanel, null);


    //Make Series
    for (int i=0; i<legend.length; i++) {
      seriesList[i] = new XYSeries(legend[i], autoSort, allowDuplicateXValues);
    }

    //Populate Series
    for(int i = 0; i < graphData.length; i++) {
      for(int j = 0; j < graphData[0].length; j++) {
        for(int k = 4; k < legend.length; k++) {
          seriesList[k].add(Float.valueOf(graphData[i][2]),Float.valueOf(graphData[i][k]));
        }
      }
    }

    //System.out.println("\nTEST:"+Arrays.deepToString(graphData));

    //Add to window
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(listPane);
    panel.add(filterPane);
    this.add(panel);
  }
}

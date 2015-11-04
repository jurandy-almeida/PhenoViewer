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

public class CSVHandler {

  CSVHandler() {
  }

  public void WriteCSV() {
    System.out.println("Ainda não. :/");
  }

  public JFrame AnalyzeCSV(String path) {
    CSVAnalyzer analyzer = null;
    try {
      CSVReader reader = new CSVReader(new FileReader(path),';');

      List<String[]> entriesList = reader.readAll();
      String[][] rowData = entriesList.toArray(new String[0][]);
      entriesList.remove(0);

      String[][] graphData = entriesList.toArray(new String[0][]);

      JTable table = new JTable(graphData, rowData[0]);
      String[] legend = rowData[0];

      analyzer = new CSVAnalyzer(table, legend, graphData);
    }
    catch (FileNotFoundException ex) {
      System.out.println("ERROR: File error!");
    }
    finally {
      return analyzer;
    }
  }
}

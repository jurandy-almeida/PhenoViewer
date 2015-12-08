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
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
import com.opencsv.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Calendar;
import java.util.Date;


public class CSVHandler {

  CSVHandler() {
  }

  private int calculaDia(File file) {
    Date date = new FileFunctions().readDate(file);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_YEAR);
  }

  private int calculaAno(File file) {
    Date date = new FileFunctions().readDate(file);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.YEAR);
  }

  private int calculaHora(File file) {
    Date date = new FileFunctions().readDate(file);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.HOUR_OF_DAY);
  }

  public File FileToSave() {
    JFrame parentFrame = new JFrame();
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    int result = fileChooser.showSaveDialog(parentFrame);
    if (result == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile();
    } else {
      return null;
    }
  }

  /*public void WriteCSV(ArrayList<File> imageList, File mask, File fileToSave) {
      try {
        BufferedWriter out = new BufferedWriter(new FileWriter(fileToSave.getAbsolutePath()));
        CSVWriter writer = new CSVWriter(out);

        AvgRgb avg = new AvgRgb(imageList, mask);
        MeanH meanh = new MeanH(imageList, mask);
        ExcGreen excg = new ExcGreen(imageList, mask);

        ArrayList<ColorRGB> avgArray = avg.process();
        ArrayList<Float> meanHArray = meanh.process();
        ArrayList<Float> excgArray = excg.process();

        String[] title = ("filename,year,day,hour,avgR,avgG,avgB,relR,relG,relB,meanH,excG").split(",");
        writer.writeNext(title);

        for (int i=0; i<avgArray.size(); i++) {
          String[] entries = (imageList.get(i).getName()+","+calculaAno(imageList.get(i))+","+calculaDia(imageList.get(i))+","+calculaHora(imageList.get(i))+","+avgArray.get(i).toCSV()+","+avgArray.get(i).toRelRGB().toCSV()+","+meanHArray.get(i)+","+excgArray.get(i)).split(",");
          writer.writeNext(entries);
        }
        writer.close();
        out = null;
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }*/

  public String filterCommaString(String[] data, String filter) {
    String result = "";
    for (int i = 0; i<filter.length(); i++) {
      //check if is to be removed or not
      if (filter.charAt(i) == '1') {
        result += ","+data[i];
      }
    }
    return result;
  }

  public void ExportCSV(ArrayList<File> imageList, ArrayList<File> maskList, FileNode currentMask) {
    JFrame exporter = new JFrame("CSV Exporter");
    exporter.setSize(500,320);
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    JPanel selectorPanel = new JPanel();
    selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.X_AXIS));

    String[] methodNames = {"Mean R", "Mean G", "Mean B", "Mean H", "Average R", "Average G", "Average B", "Excess Green"};
    JPanel seriesPanel = new JPanel();
    seriesPanel.setLayout(new BoxLayout(seriesPanel, BoxLayout.Y_AXIS));
    for (int i=0; i<methodNames.length; i++) {
      JCheckBox checkBox = new JCheckBox(methodNames[i], true);
      seriesPanel.add(checkBox);
    }
    JScrollPane seriesScroll = new JScrollPane(seriesPanel);
    selectorPanel.add(seriesScroll);

    JPanel maskPanel = new JPanel();
    maskPanel.setLayout(new BoxLayout(maskPanel, BoxLayout.Y_AXIS));
    if (currentMask != null) {
      for (File archive: maskList) {
        JCheckBox checkBox = new JCheckBox(archive.getName(), archive.getAbsolutePath().equals(currentMask.getFile().getAbsolutePath()));
        maskPanel.add(checkBox);
      }
    } else {
      for (File archive: maskList) {
        JCheckBox checkBox = new JCheckBox(archive.getName(),false);
        maskPanel.add(checkBox);
      }
    }
    JScrollPane maskScroll = new JScrollPane(maskPanel);
    selectorPanel.add(maskScroll);

    panel.add(selectorPanel);


    JPanel actionPanel = new JPanel();
    actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
    actionPanel.add(Box.createHorizontalGlue());
    actionPanel.setPreferredSize(new Dimension(500, 32));
    JTextField path = new JTextField("./");
    path.setEditable(false);
    actionPanel.add(path);
    actionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    JButton selectPathButton = new JButton("Select File Path");
    selectPathButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        path.setText(FileToSave().getAbsolutePath());
      }
    });
    actionPanel.add(selectPathButton);

    //Populate List of CheckBoxes
    List<JCheckBox> seriescheckboxes = new ArrayList<JCheckBox>();
    for( Component comp : seriesPanel.getComponents() ) {
      if( comp instanceof JCheckBox) seriescheckboxes.add( (JCheckBox)comp );
    }
    List<JCheckBox> maskcheckboxes = new ArrayList<JCheckBox>();
    for( Component comp : maskPanel.getComponents() ) {
      if( comp instanceof JCheckBox) maskcheckboxes.add( (JCheckBox)comp );
    }

    //Buttons for Mask Selection
    JButton selectMaskButton = new JButton("Select All Masks");
    selectMaskButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (JCheckBox chk: maskcheckboxes)
          chk.setSelected(true);
      }
    });

    JButton deselectMaskButton = new JButton("Deselect All Masks");
    deselectMaskButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (JCheckBox chk: maskcheckboxes) {
          chk.setSelected(false);
        }
      }
    });

    JButton invertMaskButton = new JButton("Invert Mask Selection");
    invertMaskButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (JCheckBox chk: maskcheckboxes) {
          chk.setSelected(!chk.isSelected());
        }
      }
    });

    maskPanel.add(selectMaskButton);
    maskPanel.add(deselectMaskButton);
    maskPanel.add(invertMaskButton);


    //Buttons for Data Selection
    JButton selectSeriesButton = new JButton("Select All Series");
    selectSeriesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (JCheckBox chk: seriescheckboxes)
          chk.setSelected(true);
      }
    });

    JButton deselectSeriesButton = new JButton("Deselect All Series");
    deselectSeriesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (JCheckBox chk: seriescheckboxes) {
          chk.setSelected(false);
        }
      }
    });

    JButton invertSeriesButton = new JButton("Invert Series Selection");
    invertSeriesButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (JCheckBox chk: seriescheckboxes) {
          chk.setSelected(!chk.isSelected());
        }
      }
    });

    seriesPanel.add(selectSeriesButton);
    seriesPanel.add(deselectSeriesButton);
    seriesPanel.add(invertSeriesButton);

    JButton plotButton = new JButton("Export Series");
    plotButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String maskFilter = "";
        for (JCheckBox chk: maskcheckboxes) {
          if (chk.isSelected())
            maskFilter += "1";
          else
            maskFilter += "0";
        }
        //System.out.println(maskFilter);
        String seriesFilter = "";
        for (JCheckBox chk: seriescheckboxes) {
          if (chk.isSelected())
            seriesFilter += "1";
          else
            seriesFilter += "0";
        }
        //System.out.println(seriesFilter);

        for (int i=0; i<maskList.size(); i++) {
          if (maskFilter.charAt(i) == '1') {
            //Export Data for mask
            File Mask = maskList.get(i);

            //Generate data
            try {
              //Create File for export
              File file = new File(path.getText()+Mask.getName().substring(0, Mask.getName().length()-3)+"csv");


              BufferedWriter out = new BufferedWriter(new FileWriter(file));
              CSVWriter writer = new CSVWriter(out);

              AvgRgb avg = new AvgRgb(imageList, Mask);
              MeanH meanh = new MeanH(imageList, Mask);
              ExcGreen excg = new ExcGreen(imageList, Mask);

              ArrayList<ColorRGB> avgArray = avg.process();
              ArrayList<Float> meanHArray = meanh.process();
              ArrayList<Float> excgArray = excg.process();


              //Check for data to export
              String[] title = ("filename,year,day,hour"+filterCommaString("avgR,avgG,avgB,relR,relG,relB,meanH,excG".split(","),seriesFilter)).split(",");

              writer.writeNext(title);
              for (int j=0; j<avgArray.size(); j++) {
                //Check each data to be inserted
                String[] data = (avgArray.get(j).toCSV()+","+avgArray.get(j).toRelRGB().toCSV()+","+meanHArray.get(j)+","+excgArray.get(j)).split(",");
                String[] entries = (imageList.get(j).getName()+","+calculaAno(imageList.get(j))+","+calculaDia(imageList.get(j))+","+calculaHora(imageList.get(j))+filterCommaString(data,seriesFilter)).split(",");
                writer.writeNext(entries);
              }

              writer.close();
              out = null;
            } catch (IOException ex) {
              ex.printStackTrace();
            }
            System.out.println(Mask.getName());
          }
        }
      }
    });

    actionPanel.add(plotButton);
    panel.add(actionPanel);

    /*try {
        BufferedWriter out = new BufferedWriter(new FileWriter(path.getText()));
        CSVWriter writer = new CSVWriter(out);

        AvgRgb avg = new AvgRgb(imageList, mask);
        MeanH meanh = new MeanH(imageList, mask);
        ExcGreen excg = new ExcGreen(imageList, mask);

        ArrayList<ColorRGB> avgArray = avg.process();
        ArrayList<Float> meanHArray = meanh.process();
        ArrayList<Float> excgArray = excg.process();
      } catch (IOException e) {
        e.printStackTrace();
      }*/

    exporter.add(panel);
    exporter.setVisible(true);
  }

  public JFrame AnalyzeCSV(String path) {
    try {
      CSVReader reader = new CSVReader(new FileReader(path),',');

      List<String[]> entriesList = reader.readAll();
      String[][] rowData = entriesList.toArray(new String[0][]);
      entriesList.remove(0);

      String[][] graphData = entriesList.toArray(new String[0][]);

      JTable table = new JTable(graphData, rowData[0]);
      String[] legend = rowData[0];

      CSVAnalyzer analyzer = new CSVAnalyzer(table, legend, graphData);
      return analyzer;
    }
    catch (FileNotFoundException ex) {
      System.out.println("ERROR: File error!");
    }
    catch (IOException ex) {
      System.out.println("ERROR: File error!");
    }
    return null;
  }
}

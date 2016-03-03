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
import java.util.Calendar;
import java.util.Date;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Iterator;

public class CSVHandler {

  CSVHandler() {
  }

  private int calculaDia(File file) {
    Date date = new FileFunctions().readDate(file);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_YEAR);
  }

  private int calculaDia(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_YEAR);
  }

  private int getDia(File file) {
    Date date = new FileFunctions().readDate(file);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_MONTH);
  }

  private int getDia(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_MONTH);
  }

  private int getMes(File file) {
    Date date = new FileFunctions().readDate(file);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.MONTH);
  }

  private int getMes(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.MONTH);
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

  public void ExportCSV(final ArrayList<File> imageListOriginal, final ArrayList<File> maskList, FileNode currentMask) {
    JFrame exporter = new JFrame("CSV Exporter");
    exporter.setSize(650,520);
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    //Clone ImageList for use
    final ArrayList<File> imageList = (ArrayList<File>) imageListOriginal.clone();

    //Adding the Masks and Data selectors
    JPanel selectorPanel = new JPanel();
    selectorPanel.setLayout(new GridLayout(0,2));
    //Adding the data selector
    String[] methodNames = {"Mean R", "Mean G", "Mean B", "Mean H", "Average R", "Average G", "Average B", "Excess Green"};
    JPanel seriesPanel = new JPanel();
    seriesPanel.setLayout(new GridLayout(0,1));
    for (int i=0; i<methodNames.length; i++) {
      JCheckBox checkBox = new JCheckBox(methodNames[i], true);
      seriesPanel.add(checkBox);
    }
    JScrollPane seriesScroll = new JScrollPane(seriesPanel);
    selectorPanel.add(seriesScroll);

    //Adding the mask selector
    JPanel maskPanel = new JPanel();
    maskPanel.setLayout(new GridLayout(0,1));
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

    //Adding the selectors
    //Populate List of CheckBoxes
    final List<JCheckBox> seriescheckboxes = new ArrayList<JCheckBox>();
    for( Component comp : seriesPanel.getComponents() ) {
      if( comp instanceof JCheckBox) seriescheckboxes.add( (JCheckBox)comp );
    }
    final List<JCheckBox> maskcheckboxes = new ArrayList<JCheckBox>();
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

    //Add panel for the selector buttons
    JPanel selectorButtonPanel = new JPanel();
    selectorButtonPanel.setLayout(new GridLayout(0,2));
    selectorButtonPanel.add(selectSeriesButton);
    selectorButtonPanel.add(selectMaskButton);
    selectorButtonPanel.add(deselectSeriesButton);
    selectorButtonPanel.add(deselectMaskButton);
    selectorButtonPanel.add(invertSeriesButton);
    selectorButtonPanel.add(invertMaskButton);

    panel.add(selectorButtonPanel);


    //Adding the time interval selector.
    JPanel timePanel = new JPanel();
    final FileFunctions ff = new FileFunctions();
    //Get first date and last date.
    Date inicio  = ff.readDate(imageList.get(0));
    Date termino  = ff.readDate(imageList.get(0));
    ArrayList<Date> dateList = new ArrayList<Date>();
    for (File image: imageList) {
      Date aux = ff.readDate(image);
      dateList.add(aux);
      if (aux.before(inicio)) {
        inicio = aux;
      }
      if (aux.after(termino)) {
        termino = aux;
      }
    }
    JLabel datas = new JLabel("Período: de "+new SimpleDateFormat("dd/MM/yyyy").format(inicio)+" (Dia:"+calculaDia(inicio)+") a "+new SimpleDateFormat("dd/MM/yyyy").format(termino)+" (Dia:"+calculaDia(termino)+").");
    timePanel.add(datas);
    JPanel datePicker = new JPanel();
    datePicker.setLayout(new GridLayout(0,5));

    String[] days = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    String years2 = "";
    for (int i=0; i<=inicio.getYear()-termino.getYear() ;i++) {
      years2 += String.valueOf(inicio.getYear()+1900+i)+",";
    }
    String[] years = years2.split(",");
    String[] hours = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};

    datePicker.add(new JLabel(""));
    datePicker.add(new JLabel("Dia"));
    datePicker.add(new JLabel("Mês"));
    datePicker.add(new JLabel("Ano"));
    datePicker.add(new JLabel("Hora"));

    //Add ComboBox
    final JComboBox firstDateD = new JComboBox(days);
    final JComboBox firstDateM = new JComboBox(months);
    final JComboBox firstDateY = new JComboBox(years);
    final JComboBox firstDateH = new JComboBox(hours);

    datePicker.add(new JLabel("Limite Inferior: "));
    datePicker.add(firstDateD);
    datePicker.add(firstDateM);
    datePicker.add(firstDateY);
    datePicker.add(firstDateH);


    datePicker.add(new JLabel("Limite Superior: "));
    final JComboBox lastDateD = new JComboBox(days);
    lastDateD.setSelectedItem("31");
    final JComboBox lastDateM = new JComboBox(months);
    lastDateM.setSelectedItem("12");
    final JComboBox lastDateY = new JComboBox(years);
    lastDateY.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
    final JComboBox lastDateH = new JComboBox(hours);
    lastDateH.setSelectedItem("24");

    datePicker.add(lastDateD);
    datePicker.add(lastDateM);
    datePicker.add(lastDateY);
    datePicker.add(lastDateH);

    datePicker.add(new JLabel(""));

    timePanel.add(datePicker);
    panel.add(timePanel);



    JPanel pathPanel = new JPanel();
    pathPanel.setLayout(new GridLayout(0,1));
    pathPanel.add(Box.createHorizontalGlue());
    final JLabel path = new JLabel("./");
    pathPanel.add(path);
    JButton selectPathButton = new JButton("Select File Path");
    selectPathButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        path.setText(FileToSave().getAbsolutePath());
      }
    });
    pathPanel.add(selectPathButton);



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


        //Filter ImageList according to the period selection
        Iterator<File> lit = imageList.iterator();
        while (lit.hasNext()) {
          File f = lit.next();
          //Filter Year
          //Filter Month
          //Filter Day
          //Filter Hour
          if (calculaAno(f) < Integer.parseInt((String)firstDateY.getSelectedItem()) || calculaAno(f) > Integer.parseInt((String)lastDateY.getSelectedItem())) {
            lit.remove();
            //break;
          } else  if (getMes(f) < Integer.parseInt((String)firstDateM.getSelectedItem()) || getMes(f) > Integer.parseInt((String)lastDateM.getSelectedItem())) {
            lit.remove();
            //break;
          } else if (getDia(f) < Integer.parseInt((String)firstDateD.getSelectedItem()) || getDia(f) > Integer.parseInt((String)lastDateD.getSelectedItem())) {
            lit.remove();
            //break;
          } else if (calculaHora(f) < Integer.parseInt((String)firstDateH.getSelectedItem()) || calculaHora(f) > Integer.parseInt((String)lastDateH.getSelectedItem())) {
            lit.remove();
            //break;
          }

          /*if (ff.readDate(f).after((Date)lastDate.getSelectedItem())) {
            //System.out.println("DATA:"+ff.readDate(f)+" INICIAL:"+(Date)lastDate.getSelectedItem()+" FINAL:"+(Date)firstDate.getSelectedItem()+" .");
            lit.remove();
            //break;
          }
          if (ff.readDate(f).before((Date)firstDate.getSelectedItem())) {
            //System.out.println("DATA:"+ff.readDate(f)+" INICIAL:"+(Date)lastDate.getSelectedItem()+" FINAL:"+(Date)firstDate.getSelectedItem()+" .");
            lit.remove();
            //break;
          }*/

        }

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
              String[] title = ("filename,year,day,hour"+filterCommaString("Mean R,Mean G,Mean B,Mean H,Average R,Average G,Average B,Excess Green".split(","),seriesFilter)).split(",");

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
            JOptionPane.showMessageDialog(null, "Exporting: "+Mask.getName()+". DONE.");
            System.out.println(Mask.getName()+" DONE.");
          }
        }

        //Reset ArrayList for use
        imageList.clear();
        ArrayList<File> imageList = (ArrayList<File>) imageListOriginal.clone();
      }
    });
    panel.add(pathPanel);

    //AnalyzeCSV JTable Generator
    JButton analyzeButton = new JButton("Analyze Series(no export)");
    analyzeButton.addActionListener(new ActionListener() {
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


        //Filter ImageList according to the period selection
        Iterator<File> lit = imageList.iterator();
        while (lit.hasNext()) {
          File f = lit.next();
          //Filter Year
          //Filter Month
          //Filter Day
          //Filter Hour
          if (calculaAno(f) < Integer.parseInt((String)firstDateY.getSelectedItem()) || calculaAno(f) > Integer.parseInt((String)lastDateY.getSelectedItem())) {
            lit.remove();
            //break;
          } else  if (getMes(f) < Integer.parseInt((String)firstDateM.getSelectedItem()) || getMes(f) > Integer.parseInt((String)lastDateM.getSelectedItem())) {
            lit.remove();
            //break;
          } else if (getDia(f) < Integer.parseInt((String)firstDateD.getSelectedItem()) || getDia(f) > Integer.parseInt((String)lastDateD.getSelectedItem())) {
            lit.remove();
            //break;
          } else if (calculaHora(f) < Integer.parseInt((String)firstDateH.getSelectedItem()) || calculaHora(f) > Integer.parseInt((String)lastDateH.getSelectedItem())) {
            lit.remove();
            //break;
          }
        }

        for (int i=0; i<maskList.size(); i++) {
          if (maskFilter.charAt(i) == '1') {
            //Export Data for mask
            File Mask = maskList.get(i);

            //Generate data
            try {
              AvgRgb avg = new AvgRgb(imageList, Mask);
              MeanH meanh = new MeanH(imageList, Mask);
              ExcGreen excg = new ExcGreen(imageList, Mask);

              ArrayList<ColorRGB> avgArray = avg.process();
              ArrayList<Float> meanHArray = meanh.process();
              ArrayList<Float> excgArray = excg.process();


              //Check for data to export
              String[] legend = ("filename,year,day,hour"+filterCommaString("Mean R,Mean G,Mean B,Mean H,Average R,Average G,Average B,Excess Green".split(","),seriesFilter)).split(",");
              String[] title = ("filename,year,day,hour"+filterCommaString("Mean R,Mean G,Mean B,Mean H,Average R,Average G,Average B,Excess Green".split(","),seriesFilter)).split(",");

              //writer.writeNext(title);
              JTable table;
              DefaultTableModel tableModel = new DefaultTableModel(title,0);

              List<String[]> dataList = new ArrayList<String[]>();
              for (int j=0; j<avgArray.size(); j++) {
                //Check each data to be inserted
                String[] data = (avgArray.get(j).toCSV()+","+avgArray.get(j).toRelRGB().toCSV()+","+meanHArray.get(j)+","+excgArray.get(j)).split(",");
                String[] entries = (imageList.get(j).getName()+","+calculaAno(imageList.get(j))+","+calculaDia(imageList.get(j))+","+calculaHora(imageList.get(j))+filterCommaString(data,seriesFilter)).split(",");

                dataList.add(entries);
                tableModel.addRow(entries);
              }
              table = new JTable(tableModel);

              String[][] graphData = dataList.toArray(new String[0][]);

              AnalyzeCSV(table, legend, graphData).setVisible(true);

            } catch (IOException ex) {
              ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Exporting: "+Mask.getName()+". DONE.");
            System.out.println(Mask.getName()+" DONE.");
          }
        }
        //Reset ArrayList for use
        imageList.clear();
        ArrayList<File> imageList = (ArrayList<File>) imageListOriginal.clone();
      }
    });
    panel.add(pathPanel);


    //Add the plot button panel
    JPanel plotButtonAligner =  new JPanel();
    plotButtonAligner.setLayout(new GridLayout(0,2));
    plotButtonAligner.add(plotButton);
    plotButtonAligner.add(analyzeButton);
    panel.add(plotButtonAligner);

    //Add all panels to screen
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

  public JFrame AnalyzeCSV(JTable table, String[] legend, String[][] graphData) {
    CSVAnalyzer analyzer = new CSVAnalyzer(table, legend, graphData);
    return analyzer;
  }
}

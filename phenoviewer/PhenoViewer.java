package phenoviewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.rmi.server.ExportException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class PhenoViewer extends JFrame implements ActionListener {

  JScrollPane imageScroll; //imageDisplay Placeholder

  BasicFileTree treeImage, treeMask; //The Tree Browsers for the image and the masks
  JTabbedPane tabbedPaneImage, tabbedPaneMask; //The Tabbed browser for date or tree
  BasicFileList listImage;

  ImageDisplay imageDisplay; //Where the image is loaded

  CSVAnalyzer csvanalyzer; //CSV Analyzer

  HistogramPanel redPanel, greenPanel, bluePanel; //The histograms pannels

  //Menu
  JMenu fileMenu, viewMenu, imageMenu, toolsMenu, colorMenu, analysisMenu;

  //Menu Items
  //File
  JMenuItem openImageItem, openMaskItem, saveSeriesItem, exitItem, rgbModel, hsbModel;
  //View
  JMenuItem nextImage, prevImage, slideShow, zoomOut, zoomIn, fitScreen, oriSize;
  //Image
  JMenuItem flipHor, flipVer, rotateRight, rotateLeft;
  //Color Tools
  JMenuItem redBandItem, greenBandItem, blueBandItem, inverseBandItem, averageBandItem, resetItem;
  //Histogram
  //Analysis and Series
  JMenuItem csvparser, rhythmSeries;

  //Panels
  JPanel histogramPanel;

  DrawGraph averagePanel;
  JMenuBar menuBar;

  JPopupMenu popupMenu;

  Container container;

  Timer timer;

  FileNode currentNode = null;

  FileNode currentMask = null;

  double[] zoomFactors = new double[] { 0.25, 0.5, 1.0, 2.0, 4.0, 8.0 };

  int[] rotateFactors = new int[] { -3, -2, -1, 0, 1, 2, 3 };
  int rotateIndex = 3;

  public PhenoViewer() {
    super("e-Phenology Image Viewer");
    setSize(800, 700);

    container = getContentPane();

    menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    openImageItem = new JMenuItem("Load Images");
    openImageItem.setMnemonic('I');
    openImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                                                        KeyEvent.CTRL_MASK));
    openImageItem.addActionListener(this);
    fileMenu.add(openImageItem);
    openMaskItem = new JMenuItem("Load Masks");
    openMaskItem.setMnemonic('M');
    openMaskItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
                                                       KeyEvent.CTRL_MASK));
    openMaskItem.addActionListener(this);
    fileMenu.add(openMaskItem);
    saveSeriesItem = new JMenuItem("Export Series");
    saveSeriesItem.setMnemonic('E');
    saveSeriesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                                                         KeyEvent.CTRL_MASK));
    saveSeriesItem.addActionListener(this);
    fileMenu.add(saveSeriesItem);
    exitItem = new JMenuItem("Exit");
    exitItem.setMnemonic('X');
    exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                                   KeyEvent.CTRL_MASK));
    exitItem.addActionListener(this);
    fileMenu.add(exitItem);
    menuBar.add(fileMenu);

    viewMenu = new JMenu("View");
    viewMenu.setMnemonic('V');
    nextImage = new JMenuItem("Next");
    nextImage.setMnemonic('N');
    nextImage.setAccelerator(KeyStroke.getKeyStroke('>'));
    nextImage.addActionListener(this);
    viewMenu.add(nextImage);
    prevImage = new JMenuItem("Previous");
    prevImage.setMnemonic('P');
    prevImage.setAccelerator(KeyStroke.getKeyStroke('<'));
    prevImage.addActionListener(this);
    viewMenu.add(prevImage);
    slideShow = new JMenuItem("Start SlideShow");
    slideShow.setMnemonic('S');
    slideShow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                                    KeyEvent.CTRL_MASK));
    slideShow.addActionListener(this);
    viewMenu.add(slideShow);
    viewMenu.addSeparator();
    zoomOut = new JMenuItem("Zoom Out");
    zoomOut.setMnemonic('O');
    zoomOut.setDisplayedMnemonicIndex(5);
    zoomOut.setAccelerator(KeyStroke.getKeyStroke('-'));
    zoomOut.addActionListener(this);
    viewMenu.add(zoomOut);
    zoomIn = new JMenuItem("Zoom In");
    zoomIn.setMnemonic('I');
    zoomIn.setAccelerator(KeyStroke.getKeyStroke('+'));
    zoomIn.addActionListener(this);
    viewMenu.add(zoomIn);
    fitScreen = new JMenuItem("Fit To Screen");
    fitScreen.setMnemonic('F');
    fitScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                                                    KeyEvent.CTRL_MASK));
    fitScreen.addActionListener(this);
    viewMenu.add(fitScreen);
    oriSize = new JMenuItem("Original Size");
    oriSize.setMnemonic('G');
    oriSize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
                                                  KeyEvent.CTRL_MASK));
    oriSize.addActionListener(this);
    viewMenu.add(oriSize);
    menuBar.add(viewMenu);

    imageMenu = new JMenu("Image");
    imageMenu.setMnemonic('I');
    flipHor = new JMenuItem("Flip Horizontally");
    flipHor.setMnemonic('H');
    flipHor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
                                                  KeyEvent.SHIFT_MASK));
    flipHor.addActionListener(this);
    imageMenu.add(flipHor);
    flipVer = new JMenuItem("Flip Vertically");
    flipVer.setMnemonic('V');
    flipVer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                                                  KeyEvent.SHIFT_MASK));
    flipVer.addActionListener(this);
    imageMenu.add(flipVer);
    imageMenu.addSeparator();
    rotateRight = new JMenuItem("Rotate Right");
    rotateRight.setMnemonic('R');
    rotateRight.setDisplayedMnemonicIndex(7);
    rotateRight.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                                                      KeyEvent.SHIFT_MASK));
    rotateRight.addActionListener(this);
    imageMenu.add(rotateRight);
    rotateLeft = new JMenuItem("Rotate Left");
    rotateLeft.setMnemonic('L');
    rotateLeft.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
                                                     KeyEvent.SHIFT_MASK));
    rotateLeft.addActionListener(this);
    imageMenu.add(rotateLeft);
    menuBar.add(imageMenu);

    toolsMenu = new JMenu("Color Tools");
    toolsMenu.setMnemonic('T');
    redBandItem = new JMenuItem("Red Channel");
    redBandItem.setMnemonic('R');
    redBandItem.addActionListener(this);
    toolsMenu.add(redBandItem);
    greenBandItem = new JMenuItem("Green Channel");
    greenBandItem.setMnemonic('G');
    greenBandItem.addActionListener(this);
    toolsMenu.add(greenBandItem);
    blueBandItem = new JMenuItem("Blue Channel");
    blueBandItem.setMnemonic('B');
    blueBandItem.addActionListener(this);
    toolsMenu.add(blueBandItem);
    toolsMenu.addSeparator();
    averageBandItem = new JMenuItem("Average Image");
    averageBandItem.setMnemonic('A');
    averageBandItem.addActionListener(this);
    toolsMenu.add(averageBandItem);
    inverseBandItem = new JMenuItem("Negative Image");
    inverseBandItem.setMnemonic('N');
    inverseBandItem.addActionListener(this);
    toolsMenu.add(inverseBandItem);
    resetItem = new JMenuItem("Reset Image");
    resetItem.setMnemonic('R');
    resetItem.addActionListener(this);
    toolsMenu.add(resetItem);
    menuBar.add(toolsMenu);

    colorMenu = new JMenu("Histogram");
    colorMenu.setMnemonic('C');
    ButtonGroup colorGroup = new ButtonGroup();
    rgbModel = new JRadioButtonMenuItem("RGB");
    rgbModel.setMnemonic('R');
    rgbModel.addActionListener(this);
    rgbModel.setSelected(true);
    colorGroup.add(rgbModel);
    colorMenu.add(rgbModel);
    hsbModel = new JRadioButtonMenuItem("HSB");
    hsbModel.setMnemonic('H');
    hsbModel.addActionListener(this);
    hsbModel.setSelected(false);
    colorGroup.add(hsbModel);
    colorMenu.add(hsbModel);
    menuBar.add(colorMenu);

    analysisMenu = new JMenu("Analysis");
    analysisMenu.setMnemonic('A');
    csvparser = new JMenuItem("Analyse CSV");
    csvparser.setMnemonic('C');
    csvparser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                                                    KeyEvent.CTRL_MASK));
    csvparser.addActionListener(this);
    analysisMenu.add(csvparser);
    rhythmSeries = new JMenuItem("Visual Rhythm");
    rhythmSeries.setMnemonic('V');
    rhythmSeries.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                                                       KeyEvent.CTRL_MASK));
    rhythmSeries.addActionListener(this);
    analysisMenu.add(rhythmSeries);
    menuBar.add(analysisMenu);

    JPanel filesPanel = new JPanel();
    filesPanel.setLayout(new GridLayout(2, 1));
    filesPanel.setBorder(new TitledBorder("Input Files"));

    container.add(BorderLayout.LINE_START, filesPanel);

    tabbedPaneImage = new JTabbedPane();
    tabbedPaneImage.setTabPlacement(JTabbedPane.BOTTOM);
    filesPanel.add(tabbedPaneImage);

    treeImage = new BasicFileTree(".", new java.io.FileFilter() {
      public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".jpg")
          || f.isDirectory();
      }
    });
    treeImage.getSelectionModel().setSelectionMode(
      TreeSelectionModel.SINGLE_TREE_SELECTION);
    treeImage.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        TreePath path = e.getPath();
        treeImage.scrollPathToVisible(path);
        treeMask.scrollPathToVisible(path);
        FileNode node = (FileNode) path.getLastPathComponent();
        if (node != currentNode)
          changeImage(node);
      }
    });
    treeImage.setRootDirectory(new File(".").getAbsolutePath());
    tabbedPaneImage.addTab("Image", null, treeImage.getScrollPane(),
                           "List images according to the file system.");
    tabbedPaneImage.setMnemonicAt(0, KeyEvent.VK_I);

    listImage = new BasicFileList(".", new java.io.FileFilter() {
      public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".jpg")
          || f.isDirectory();
      }
    });
    listImage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listImage.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
          if (listImage.getSelectedIndex() != -1) {
            FileNode root = (FileNode) treeImage.getModel().getRoot();
            TreePath path = root.getPath(listImage.getSelectedValue());
            treeImage.setSelectionPath(path);
          }
        }
      }
    });
    listImage.setRootDirectory(new File(".").getAbsolutePath());
    tabbedPaneImage.addTab("Date", null, listImage.getScrollPane(),
                           "List images according to the creation date.");
    tabbedPaneImage.setMnemonicAt(1, KeyEvent.VK_D);

    // greice
    tabbedPaneMask = new JTabbedPane();
    tabbedPaneMask.setTabPlacement(JTabbedPane.BOTTOM);
    filesPanel.add(tabbedPaneMask);

    treeMask = new BasicFileTree(".", new java.io.FileFilter() {
      public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".bmp")
          || f.isDirectory();
      }
    });
    treeMask.getSelectionModel().setSelectionMode(
      TreeSelectionModel.SINGLE_TREE_SELECTION);
    /*treeMask.addTreeSelectionListener(new TreeSelectionListener() {
     *	public void valueChanged(TreeSelectionEvent e) {
     *		TreePath path = e.getPath();
     *		treeMask.imageScrollPathToVisible(path);
     *		FileNode node = (FileNode) path.getLastPathComponent();
     *		if (node != currentMask)
     *			changeMask(node);
     *	}
     *});
     */
    treeMask.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        int selRow = treeMask.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = treeMask.getPathForLocation(e.getX(), e.getY());
        if(selRow != -1) {
          treeMask.scrollPathToVisible(selPath);
          FileNode node = (FileNode) selPath.getLastPathComponent();
          if (node != currentMask)
            changeMask(node);
          else
            resetMask();
        }
      }
    });
    treeMask.setRootDirectory(new File(".").getAbsolutePath());
    tabbedPaneMask.addTab("Mask", null, treeMask.getScrollPane(),
                          "List masks select for one image.");
    tabbedPaneMask.setMnemonicAt(0, KeyEvent.VK_M);
    //

    JPanel imagePanel = new JPanel();
    imagePanel.setLayout(new GridLayout(1, 1));
    imagePanel.setBorder(new TitledBorder("Digital Image"));
    imagePanel.setPreferredSize(new Dimension(640, 480));

    imageScroll = new JScrollPane();
    imageDisplay = new ImageDisplay();
    imageScroll.getViewport().add(imageDisplay);

    imagePanel.add(imageScroll);

    container.add(BorderLayout.CENTER, imagePanel);

    popupMenu = new JPopupMenu();
    popupMenu.addPopupMenuListener(new PopupMenuListener() {
      public void popupMenuCanceled(PopupMenuEvent e) {
        menuBar.add(viewMenu);
        menuBar.add(imageMenu);
        menuBar.add(toolsMenu);
        menuBar.add(colorMenu);
        menuBar.revalidate();
      }

      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      }

      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        popupMenu.add(viewMenu);
        popupMenu.add(imageMenu);
        popupMenu.add(toolsMenu);
        popupMenu.add(colorMenu);
      }
    });
    imageDisplay.setComponentPopupMenu(popupMenu);

    //graphPanel = new JPanel();
    //graphPanel.setLayout(new GridLayout(1, 1));
    //graphPanel.setBorder(new TitledBorder("Time Series"));
    //graphPanel.setPreferredSize(new Dimension(1024, 128));
    //averagePanel = new DrawGraph(imageDisplay.getRedMean(), imageDisplay.getGreenMean(), imageDisplay.getBlueMean(), imageDisplay.getTotalMean(), 0);
    //rhythmPanel = new JPanel();
    //rhythmPanel.setLayout(new GridLayout(1, 1));
    //rhythmScroll = new JScrollPane();
    //rhythmScroll.getViewport().add(//rhythmPanel);

    //container.add(BorderLayout.PAGE_END, graphPanel);

    histogramPanel = new JPanel();
    histogramPanel.setLayout(new GridLayout(3, 0));
    histogramPanel.setBorder(new TitledBorder("Color Histogram"));
    histogramPanel.setPreferredSize(new Dimension(320, -1));

    redPanel = new HistogramPanel(imageDisplay.getRedHistogram());
    redPanel.setBarColors(Color.DARK_GRAY, Color.RED);
    redPanel.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        imageDisplay.prepare();
        imageDisplay.filter();
        imageDisplay.repaint();
        container.setCursor(null);
      }
    });
    greenPanel = new HistogramPanel(imageDisplay.getGreenHistogram());
    greenPanel.setBarColors(Color.DARK_GRAY, Color.GREEN);
    greenPanel.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        imageDisplay.prepare();
        imageDisplay.filter();
        imageDisplay.repaint();
        container.setCursor(null);
      }
    });
    bluePanel = new HistogramPanel(imageDisplay.getBlueHistogram());
    bluePanel.setBarColors(Color.DARK_GRAY, Color.BLUE);
    bluePanel.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        imageDisplay.prepare();
        imageDisplay.filter();
        imageDisplay.repaint();
        container.setCursor(null);
      }
    });

    histogramPanel.add(redPanel);
    histogramPanel.add(greenPanel);
    histogramPanel.add(bluePanel);

    container.add(BorderLayout.LINE_END, histogramPanel);

    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (imageDisplay.fitToSize(imageScroll.getWidth() - 3,
                                   imageScroll.getHeight() - 3)) {
          imageDisplay.prepare();
          imageDisplay.filter();
          imageDisplay.repaint();
        }
        container.setCursor(null);
      }
    });

    timer = new Timer(50, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showNextImage();
      }
    });

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setExtendedState(Frame.MAXIMIZED_BOTH);
    disableImageOperations();
    disableMaskOperations();

    pack();
    setVisible(true);
    //treeImage.systemPrintFileVector();

  }


  //###################################################################################################

  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source == openImageItem) {
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File("."));
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
        public boolean accept(File f) {
          return f.isDirectory();
        }

        public String getDescription() {
          return "Directory";
        }
      });
      chooser.setAcceptAllFileFilterUsed(false);
      int ret = chooser.showOpenDialog(this);
      if (ret == JFileChooser.APPROVE_OPTION) {
        container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        stopSlideShow();
        treeImage.setRootDirectory(chooser.getSelectedFile().getPath());
        listImage.setRootDirectory(chooser.getSelectedFile().getPath());
        container.setCursor(null);
      }
    } else if (source == openMaskItem) {
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File("."));
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
        public boolean accept(File f) {
          return f.isDirectory();
        }

        public String getDescription() {
          return "Directory";
        }
      });
      chooser.setAcceptAllFileFilterUsed(false);
      int ret = chooser.showOpenDialog(this);
      if (ret == JFileChooser.APPROVE_OPTION) {
        container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        treeMask.setRootDirectory(chooser.getSelectedFile().getPath());
        container.setCursor(null);
      }
    } else if (source == exitItem)
      System.exit(0);
      else if (source == nextImage)
      showNextImage();
      else if (source == prevImage)
      showPreviousImage();
      else if (source == slideShow)
      slideShow();
      else if (source == zoomOut)
      zoomOut();
      else if (source == zoomIn)
      zoomIn();
      else if (source == fitScreen)
      fitToScreen();
      else if (source == oriSize)
      originalSize();
      else if (source == rotateRight)
      rotateRight();
      else if (source == rotateLeft)
      rotateLeft();
      else if (source == flipHor)
      flipHorizontally();
      else if (source == flipVer)
      flipVertically();
      else if (source == redBandItem)
      filterImage(ImageDisplay.RED_BAND_MATRIX);
      else if (source == greenBandItem)
      filterImage(ImageDisplay.GREEN_BAND_MATRIX);
      else if (source == blueBandItem)
      filterImage(ImageDisplay.BLUE_BAND_MATRIX);
      else if (source == inverseBandItem)
      filterImage(ImageDisplay.INVERSE_BAND_MATRIX);
      else if (source == averageBandItem)
      filterImage(ImageDisplay.AVERAGE_BAND_MATRIX);
      else if (source == resetItem)
      resetImage();
      else if (source == saveSeriesItem)
      writeCSVFile();
      else if ((source == rgbModel) || (source == hsbModel)) {
      imageDisplay.setColorModel(((JRadioButtonMenuItem) source)
                                 .getText());
      histogramPanel.repaint();
    } else if (source == rhythmSeries) {
      calcVisualRhythmMask();
    } else if (source == csvparser) {
      CSVParse();
    }
  }

  private void disableImageOperations() {
    zoomOut.setEnabled(false);
    zoomIn.setEnabled(false);
    fitScreen.setEnabled(false);
    oriSize.setEnabled(false);
    imageMenu.setEnabled(false);
    toolsMenu.setEnabled(false);
    colorMenu.setEnabled(false);
  }

  private void enableImageOperations() {
    zoomOut.setEnabled(true);
    zoomIn.setEnabled(true);
    fitScreen.setEnabled(true);
    oriSize.setEnabled(true);
    imageMenu.setEnabled(true);
    toolsMenu.setEnabled(true);
    colorMenu.setEnabled(true);
  }

  private void enableMaskOperations() {
    rhythmSeries.setEnabled(true);
  }

  private void disableMaskOperations() {
    rhythmSeries.setEnabled(false);
  }

  private void changeImage(FileNode node) {
    if (currentNode == null)
      enableImageOperations();
    if (node != null) {
      File f = node.getFile();
      if (!f.isDirectory()) {
        setTitle("e-Phenology Image Viewer - " + f.getAbsolutePath());
        container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        listImage.setSelectedValue(f);
        currentNode = node;
        imageDisplay.loadFile(f.getPath());
        imageDisplay.fitToSize(imageScroll.getWidth() - 3,
                               imageScroll.getHeight() - 3);
        imageDisplay.reset();
        imageDisplay.prepare();
        imageDisplay.filter();
        imageDisplay.repaint();
        histogramPanel.repaint();
        container.setCursor(null);
      }
    }
  }

  private void changeMask(FileNode node) {
    if (currentMask == null)
      enableMaskOperations();
    if (node != null) {
      File f = node.getFile();
      if (!f.isDirectory()) {
        container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        currentMask = node;
        imageDisplay.reset();
        imageDisplay.resetMask();
        imageDisplay.loadFileMask(node);
        imageDisplay.prepareMask();
        imageDisplay.prepare();
        imageDisplay.filter();
        imageDisplay.repaint();
        histogramPanel.repaint();
      }
    }
    container.setCursor(null);
  }

  private void resetMask() {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    disableMaskOperations();
    currentMask = null;
    treeMask.clearSelection();
    imageDisplay.reset();
    imageDisplay.resetMask();
    imageDisplay.prepare();
    imageDisplay.filter();
    imageDisplay.repaint();
    histogramPanel.repaint();

    container.setCursor(null);
  }

  private void showNextImage() {
    if (tabbedPaneImage.getSelectedComponent() == treeImage.getScrollPane()) {
      FileNode nextNode = treeImage.getNextLeaf();
      TreePath path = new TreePath(nextNode.getPath());
      treeImage.setSelectionPath(path);
    } else if (tabbedPaneImage.getSelectedComponent() == listImage.getScrollPane()) {
      if (listImage.getModel().getSize() > 0) {
        if (listImage.getSelectedIndex() < listImage.getModel().getSize() - 1)
          listImage.setSelectedIndex(listImage.getSelectedIndex() + 1);
        else
          listImage.setSelectedIndex(0);
      }
    }
  }

  private void showPreviousImage() {
    if (tabbedPaneImage.getSelectedComponent() == treeImage.getScrollPane()) {
      FileNode prevNode = treeImage.getPreviousLeaf();
      TreePath path = new TreePath(prevNode.getPath());
      treeImage.setSelectionPath(path);
    } else if (tabbedPaneImage.getSelectedComponent() == listImage.getScrollPane()) {
      if (listImage.getModel().getSize() > 0) {
        if (listImage.getSelectedIndex() > 0)
          listImage.setSelectedIndex(listImage.getSelectedIndex() - 1);
        else
          listImage.setSelectedIndex(listImage.getModel().getSize() - 1);
      }
    }
  }

  private void slideShow() {
    if (timer.isRunning())
      stopSlideShow();
    else
      startSlideShow();
  }

  private void startSlideShow() {
    slideShow.setText("Stop SlideShow");
    timer.start();
  }

  private void stopSlideShow() {
    timer.stop();
    slideShow.setText("Start SlideShow");
  }

  private void zoomOut() {
    int zoomIndex = zoomFactors.length - 1;
    while ((zoomIndex >= 0)
           && (zoomFactors[zoomIndex] >= imageDisplay.getScale()))
      zoomIndex--;
    if (zoomIndex >= 0) {
      container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      if (imageDisplay.setScale(zoomFactors[zoomIndex])) {
        imageDisplay.prepare();
        imageDisplay.filter();
        imageDisplay.repaint();
      }
      container.setCursor(null);
    }
  }

  private void zoomIn() {
    int zoomIndex = 0;
    while ((zoomIndex < zoomFactors.length)
           && (zoomFactors[zoomIndex] <= imageDisplay.getScale()))
      zoomIndex++;
    if (zoomIndex < zoomFactors.length) {
      container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      if (imageDisplay.setScale(zoomFactors[zoomIndex])) {
        imageDisplay.prepare();
        imageDisplay.filter();
        imageDisplay.repaint();
      }
      container.setCursor(null);
    }
  }

  private void fitToScreen() {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (imageDisplay.fitToSize(imageScroll.getWidth() - 3,
                               imageScroll.getHeight() - 3)) {
      imageDisplay.prepare();
      imageDisplay.filter();
      imageDisplay.repaint();
    }
    container.setCursor(null);
  }

  private void originalSize() {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (imageDisplay.originalSize()) {
      imageDisplay.prepare();
      imageDisplay.filter();
      imageDisplay.repaint();
    }
    container.setCursor(null);
  }

  private void rotateRight() {
    //ADD angle instead of setting to an mask.
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (++rotateIndex > 6)
      rotateIndex = 3;
    if (imageDisplay.setAngle(Math
                              .toRadians(rotateFactors[rotateIndex] * 90))) {
      if ((imageDisplay.isFlippedHorizontally() && !imageDisplay
           .isFlippedVertically())
          || (!imageDisplay.isFlippedHorizontally() && imageDisplay
              .isFlippedVertically())) {
        imageDisplay.flipHorizontally();
        imageDisplay.flipVertically();
      }
      imageDisplay.prepare();
      imageDisplay.filter();
      imageDisplay.repaint();
    }
    container.setCursor(null);
  }

  private void rotateLeft() {
    //ADD angle instead of setting to an mask.
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (--rotateIndex < 0)
      rotateIndex = 3;
    if (imageDisplay.setAngle(Math
                              .toRadians(rotateFactors[rotateIndex] * 90))) {
      if ((imageDisplay.isFlippedHorizontally() && !imageDisplay
           .isFlippedVertically())
          || (!imageDisplay.isFlippedHorizontally() && imageDisplay
              .isFlippedVertically())) {
        imageDisplay.flipHorizontally();
        imageDisplay.flipVertically();
      }
      imageDisplay.prepare();
      imageDisplay.filter();
      imageDisplay.repaint();
    }
    container.setCursor(null);
  }

  private void flipHorizontally() {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (imageDisplay.flipHorizontally()) {
      imageDisplay.prepare();
      imageDisplay.filter();
      imageDisplay.repaint();
    }
    container.setCursor(null);
  }

  private void flipVertically() {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (imageDisplay.flipVertically()) {
      imageDisplay.prepare();
      imageDisplay.filter();
      imageDisplay.repaint();
    }
    container.setCursor(null);
  }

  private void filterImage(float matrix[][]) {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    imageDisplay.bandCombine(matrix);
    imageDisplay.prepare();
    imageDisplay.filter();
    imageDisplay.repaint();
    histogramPanel.repaint();
    container.setCursor(null);
  }

  private void resetImage() {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    imageDisplay.reset();
    imageDisplay.prepare();
    imageDisplay.filter();
    imageDisplay.repaint();
    histogramPanel.repaint();
    container.setCursor(null);
  }

  public static void main(String arg[]) {
    new PhenoViewer();
  }


  //Functions that need to be reworked
  private void CSVParse() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      String path = selectedFile.getAbsolutePath();
      CSVHandler csvhandle = new CSVHandler();
      csvhandle.AnalyzeCSV(path).setVisible(true);
    }
  }
  public void calcVisualRhythmMask() {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (imageDisplay.isMaskLoaded()) {
      VisualRhythm vr = new VisualRhythm(treeImage.getFileArray(), currentMask.getFile());
      VRhythmPanel vrhythm = new VRhythmPanel(vr.process());
    }
    container.setCursor(null);
  }

  public void writeCSVFile() {
    CSVHandler handle = new CSVHandler();
    handle.WriteCSV(treeImage.getFileArray(),currentMask.getFile());
  }
}

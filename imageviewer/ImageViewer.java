package imageviewer;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;

import com.sun.imageio.plugins.bmp.BMPImageReader;

public class ImageViewer extends JFrame implements ActionListener {
  JScrollPane imageScroll, rhythmScroll;

  BasicFileTree treeImage;

  // greice
  BasicFileTree treeMask;
  //

  BasicFileList listImage;

  ImageDisplay imageDisplay;

  HistogramPanel redPanel, greenPanel, bluePanel;

  JMenuItem averageSeries, rhythmSeries; // greice

  JMenuItem redBandItem, greenBandItem, blueBandItem, inverseBandItem,
  averageBandItem, resetItem;

  JMenuItem openImageItem, openMaskItem, saveSeriesItem, exitItem, rgbModel, hsbModel;

  JMenuItem nextImage, prevImage, slideShow;

  JMenuItem zoomOut, zoomIn, fitScreen, oriSize;

  JMenuItem flipHor, flipVer, rotateRight, rotateLeft;

  JMenu fileMenu, viewMenu, imageMenu, toolsMenu, colorMenu, seriesMenu;

  JPanel histogramPanel, rhythmPanel, graphPanel;

  DrawGraph averagePanel;

  JTabbedPane tabbedPaneImage, tabbedPaneMask;

  JMenuBar menuBar;

  JPopupMenu popupMenu;

  Container container;

  Timer timer;

  FileNode currentNode = null;

  FileNode currentMask = null;

  double[] zoomFactors = new double[] { 0.25, 0.5, 1.0, 2.0, 4.0 };

  int[] rotateFactors = new int[] { -3, -2, -1, 0, 1, 2, 3 };

  int rotateIndex = 3;

  public ImageViewer() {
    super("e-Phenology Image Viewer");
    setSize(1280, 960);
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

    toolsMenu = new JMenu("Tools");
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

    colorMenu = new JMenu("Color");
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

    seriesMenu = new JMenu("Series");
    seriesMenu.setMnemonic('S');
    ButtonGroup seriesGroup = new ButtonGroup() {
      @Override
      public void setSelected(ButtonModel m, boolean b) {
        if (b && m != null && m != getSelection())
          super.setSelected(m, b);
        else if (!b && m == getSelection())
          clearSelection();
      }
    };
    averageSeries = new JRadioButtonMenuItem("Average Colors");
    averageSeries.setMnemonic('C');
    averageSeries.addActionListener(this);
    averageSeries.setSelected(false);
    seriesGroup.add(averageSeries);
    seriesMenu.add(averageSeries);
    rhythmSeries = new JRadioButtonMenuItem("Visual Rhythm");
    rhythmSeries.setMnemonic('C');
    rhythmSeries.addActionListener(this);
    rhythmSeries.setSelected(false);
    seriesGroup.add(rhythmSeries);
    seriesMenu.add(rhythmSeries);
    menuBar.add(seriesMenu);

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
    //treeMask.addTreeSelectionListener(new TreeSelectionListener() {
    //	public void valueChanged(TreeSelectionEvent e) {
    //		TreePath path = e.getPath();
    //		treeMask.imageScrollPathToVisible(path);
    //		FileNode node = (FileNode) path.getLastPathComponent();
    //		if (node != currentMask)
    //			changeMask(node);
    //	}
    //});
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
        menuBar.add(seriesMenu);
        menuBar.revalidate();
      }

      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      }

      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        popupMenu.add(viewMenu);
        popupMenu.add(imageMenu);
        popupMenu.add(toolsMenu);
        popupMenu.add(colorMenu);
        popupMenu.add(seriesMenu);
      }
    });
    imageDisplay.setComponentPopupMenu(popupMenu);

    graphPanel = new JPanel();
    graphPanel.setLayout(new GridLayout(1, 1));
    graphPanel.setBorder(new TitledBorder("Time Series"));
    graphPanel.setPreferredSize(new Dimension(1024, 128));
    averagePanel = new DrawGraph(imageDisplay.getRedMean(), imageDisplay.getGreenMean(), imageDisplay.getBlueMean(), imageDisplay.getTotalMean());
    rhythmPanel = new JPanel();
    rhythmPanel.setLayout(new GridLayout(1, 1));
    rhythmScroll = new JScrollPane();
    rhythmScroll.getViewport().add(rhythmPanel);

    container.add(BorderLayout.PAGE_END, graphPanel);

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
  }

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
      else if ((source == averageSeries) || (source == rhythmSeries)) {
      graphPanel.removeAll();
      if (averageSeries.isSelected()) {
        calcAverageRBGMask();
        graphPanel.add(averagePanel);
      }
      else if (rhythmSeries.isSelected()) {
        calcVisualRhythmMask();
        graphPanel.add(rhythmScroll);
      }
      graphPanel.revalidate();
      graphPanel.repaint();
    }
    else if ((source == rgbModel) || (source == hsbModel)) {
      imageDisplay.setColorModel(((JRadioButtonMenuItem) source)
                                 .getText());
      histogramPanel.repaint();
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
    seriesMenu.setEnabled(true);
  }

  private void disableMaskOperations() {
    seriesMenu.setEnabled(false);
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
        if (averageSeries.isSelected())
          calcAverageRBGMask();
        else if (rhythmSeries.isSelected())
          calcVisualRhythmMask();
          container.setCursor(null);
      }
    }
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
    if (averageSeries.isSelected())
      averagePanel.repaint();
    else if (rhythmSeries.isSelected()) {
      rhythmPanel.removeAll();
      rhythmPanel.revalidate();
      rhythmPanel.repaint();
    }
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
    new ImageViewer();
  }

  public void calcAverageRBGMask() {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (imageDisplay.isMaskLoaded()) {
      if (imageDisplay.getGreenMean().size() != listImage.getModel().getSize()) {
        imageDisplay.resetSeries();
        FileNode root = (FileNode) treeImage.getModel().getRoot();
        while (root != null) {
          if (root.isLeaf())
            imageDisplay.calcAvgRGB(root.getFile());
          root = (FileNode) root.getNextNode();
        }
      }
      averagePanel.repaint();
    }
    container.setCursor(null);
  }

  public void calcVisualRhythmMask() {
    container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (imageDisplay.isMaskLoaded()) {
      if (imageDisplay.getVisualRhythmPixels().size() != listImage.getModel().getSize()) {
        imageDisplay.resetSeries();
        FileNode root = (FileNode) treeImage.getModel().getRoot();
        while (root != null) {
          if (root.isLeaf())
            imageDisplay.calcVisualRhythm(root.getFile());
          root = (FileNode) root.getNextNode();
        }
      }
      rhythmPanel.removeAll();
      rhythmPanel.add(new JLabel(new ImageIcon(imageDisplay.getVisualRhythmImage())));
      rhythmPanel.revalidate();
      rhythmPanel.repaint();
    }
    container.setCursor(null);
  }
  //greice
  public void writeCSVHeader(BufferedWriter writer) {
    try {
      writer.append("filename");
      writer.append(";year");
      writer.append(";doy");
      writer.append(";hour");
      writer.append(";meanR");
      writer.append(";meanG");
      writer.append(";meanB");
      writer.append(";relR");
      writer.append(";relG");
      writer.append(";relB");
      writer.append('\n');
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeCSVMean(BufferedWriter writer, Integer line) {
    float meanR = imageDisplay.getRedMean().get(line);
    float meanG = imageDisplay.getGreenMean().get(line);
    float meanB = imageDisplay.getBlueMean().get(line);
    float total = imageDisplay.getTotalMean().get(line);
    float relR = meanR / total;
    float relG = meanG / total;
    float relB = meanB / total;
    try {
      writer.append("" + Float.toString(meanR));//.replace(".", ","));
      writer.append(";");
      writer.append("" + Float.toString(meanG));//.replace(".", ","));
      writer.append(";");
      writer.append("" + Float.toString(meanB));//.replace(".", ","));
      writer.append(";");
      writer.append("" + Float.toString(relR));//.replace(".", ","));
      writer.append(";");
      writer.append("" + Float.toString(relG));//.replace(".", ","));
      writer.append(";");
      writer.append("" + Float.toString(relB));//.replace(".", ","));
      writer.append(";");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeCSVDate(BufferedWriter writer, File file) {
    FileFunctions ff = new FileFunctions();
    Date date = ff.readDate(file);

    Calendar cal = Calendar.getInstance();
    cal.setTime(date);

    int year = cal.get(Calendar.YEAR);
    int doy = cal.get(Calendar.DAY_OF_YEAR);
    int hour = cal.get(Calendar.HOUR_OF_DAY);

    try {
      writer.append("" + Integer.toString(year));
      writer.append(";");
      writer.append("" + Integer.toString(doy));
      writer.append(";");
      writer.append("" + Integer.toString(hour));
      writer.append(";");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeCSVLine(BufferedWriter writer, File file, Integer line) {
    try {
      writer.append(file.getName());
      writer.append(";");
      writeCSVDate(writer, file);
      writeCSVMean(writer, line);
      writer.append('\n');
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeCSVFile() {
    if (imageDisplay.isMaskLoaded()) {
      String filePath ="";
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File("."));
      chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
        public boolean accept(File f) {
          return f.getName().toLowerCase().endsWith(".csv")
            || f.isDirectory();
        }

        public String getDescription() {
          return "CSV files";
        }
      });
      chooser.setAcceptAllFileFilterUsed(false);
      int ret = chooser.showSaveDialog(this);
      if (ret == JFileChooser.APPROVE_OPTION) {
        filePath = chooser.getSelectedFile().getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".csv"))
          filePath = filePath + ".csv";
      }

      container.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      if (!averageSeries.isSelected()) {
        FileNode root = (FileNode) treeImage.getModel().getRoot();
        while (root != null) {
          if (root.isLeaf())
            imageDisplay.calcAvgRGB(root.getFile());
          root = (FileNode) root.getNextNode();
        }
      }
      try {
        FileWriter fw = new FileWriter(new File(filePath), false);
        BufferedWriter writer = new BufferedWriter(fw);

        writeCSVHeader(writer);

        Integer line = 0;
        FileNode root = (FileNode) treeImage.getModel().getRoot();
        while (root != null) {
          if (root.isLeaf()) {
            writeCSVLine(writer, root.getFile(), line);
            line++;
          }
          root = (FileNode) root.getNextNode();
        }
        writer.flush();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      container.setCursor(null);
      JOptionPane.showMessageDialog(this, "Operation successfull.");
    }
  }
}

/**
 * This class has functions of processing images - view
 * @author EPHENOLOGY
 *
 */
class ImageDisplay extends JLabel {
  // red band Matrix
  static final float RED_BAND_MATRIX[][] = { { 1.0f, 0.0f, 0.0f },
  { 0.0f, 0.0f, 0.0f }, { 0.0f, 0.0f, 0.0f } };

  // green band Matrix
  static final float GREEN_BAND_MATRIX[][] = { { 0.0f, 0.0f, 0.0f },
  { 0.0f, 1.0f, 0.0f }, { 0.0f, 0.0f, 0.0f } };

  // blue band Matrix
  static final float BLUE_BAND_MATRIX[][] = { { 0.0f, 0.0f, 0.0f },
  { 0.0f, 0.0f, 0.0f }, { 0.0f, 0.0f, 1.0f } };

  // Matrix that inverts all the bands
  // the nagative of the image.
  static final float INVERSE_BAND_MATRIX[][] = { { -1.0f, 0.0f, 0.0f },
  { 0.0f, -1.0f, 0.0f }, { 0.0f, 0.0f, -1.0f } };

  // Matrix that reduces the intensities of all bands
  static final float AVERAGE_BAND_MATRIX[][] = { { 0.5f, 0.0f, 0.0f },
  { 0.0f, 0.5f, 0.0f }, { 0.0f, 0.0f, 0.5f } };

  boolean isImageLoaded;

  // greice
  boolean isMaskLoaded;
  //

  Image displayImage;

  //greice
  Image displayImageMask;
  //

  Histogram redHistogram, greenHistogram, blueHistogram;

  ArrayList<Float> redMean, greenMean, blueMean, totalMean;

  ArrayList<Integer> visualRhythm;

  String colorModel = "RGB";

  int imgWidth, imgHeight;

  double imgScale = 0.0;

  double imgAngle = 0.0;

  boolean imgInvX = false;

  boolean imgInvY = false;

  // The source and destination images
  BufferedImage biSrc;

  BufferedImage biDest;

  BufferedImage biShow;

  // greice
  BufferedImage biMask;

  // The source and destination rasters
  Raster srcRaster;

  WritableRaster dstRaster;

  BufferedImage bi;

  //greice
  BufferedImage biM;
  //

  Graphics2D big;

  //greice
  ArrayList<Pixel> maskPixels;
  //

  TreeMap<Double, Integer> maskIndex;

  ImageDisplay() {
    setBackground(Color.black);
    redHistogram = new Histogram("Red", "", 16, 0, 256);
    greenHistogram = new Histogram("Green", "", 16, 0, 256);
    blueHistogram = new Histogram("Blue", "", 16, 0, 256);
    maskPixels = new ArrayList<Pixel>();
    maskIndex = new TreeMap<Double, Integer>();
    redMean = new ArrayList<Float>();
    greenMean = new ArrayList<Float>();
    blueMean = new ArrayList<Float>();
    totalMean = new ArrayList<Float>();
    visualRhythm = new ArrayList<Integer>();
    isImageLoaded = false;
    isMaskLoaded = false;// greice
  }

  public boolean isImageLoaded() {
    return isImageLoaded;
  }

  // greice
  public boolean isMaskLoaded() {
    return isMaskLoaded;
  }
  //

  public Histogram getRedHistogram() {
    return redHistogram;
  }

  public Histogram getGreenHistogram() {
    return greenHistogram;
  }

  public Histogram getBlueHistogram() {
    return blueHistogram;
  }

  public List<Float> getRedMean() {
    return redMean;
  }

  public List<Float> getGreenMean() {
    return greenMean;
  }

  public List<Float> getBlueMean() {
    return blueMean;
  }

  public List<Float> getTotalMean() {
    return totalMean;
  }

  public List<Integer> getVisualRhythmPixels() {
    return visualRhythm;
  }

  public BufferedImage getVisualRhythmImage() {
    int width = maskPixels.size();
    int height = visualRhythm.size() / maskPixels.size();

    BufferedImage vr = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    int p = 0;
    for (int j = 0; j < vr.getHeight(); j++)
      for (int i = 0; i < vr.getWidth(); i++)	{
      vr.setRGB(i, j, visualRhythm.get(p));
      p++;
    }

    return vr;
  }

  public void setColorModel(String colorModel) {
    if (this.colorModel != colorModel) {
      this.colorModel = colorModel;
      if (colorModel.equals("RGB")) {
        redHistogram.setTitle("Red");
        greenHistogram.setTitle("Green");
        blueHistogram.setTitle("Blue");
      } else if (colorModel.equals("HSB")) {
        redHistogram.setTitle("Hue");
        greenHistogram.setTitle("Saturation");
        blueHistogram.setTitle("Brightness");
      }
      if (isImageLoaded)
        computeColorHistograms();
    }
  }

  public void loadFile(String fileName) {
    displayImage = Toolkit.getDefaultToolkit().getImage(fileName);
    MediaTracker mt = new MediaTracker(this);
    mt.addImage(displayImage, 1);
    try {
      mt.waitForAll();
    } catch (Exception e) {
      System.out.println("Exception while loading.");
    }

    isImageLoaded = true;

    originalSize();

    setAngle(0.0);

    imgInvX = false;
    imgInvY = false;

    createBufferedImages();

    biShow = biSrc;

    redHistogram.setFilterBin(-1);
    greenHistogram.setFilterBin(-1);
    blueHistogram.setFilterBin(-1);

    computeColorHistograms();
  }

  public void computeColorHistograms() {
    int width = biShow.getWidth();
    int height = biShow.getHeight();

    boolean grayLevel = (biShow.getColorModel().getNumColorComponents() == 1);

    redHistogram.clear();
    greenHistogram.clear();
    blueHistogram.clear();

    Raster raster = biShow.getRaster();

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (grayLevel) {
          int c = raster.getSample(i, j, 0);
          if (c > 0)
            redHistogram.add((double) c);
        } else {
          int r = raster.getSample(i, j, 0);
          int g = raster.getSample(i, j, 1);
          int b = raster.getSample(i, j, 2);
          if (colorModel.equals("RGB")) {
            if ((r > 0) && (r < 255))
              redHistogram.add((double) r);
            if ((g > 0) && (g < 255))
              greenHistogram.add((double) g);
            if ((b > 0) && (b < 255))
              blueHistogram.add((double) b);
          } else if (colorModel.equals("HSB")) {
            float[] hsv = Color.RGBtoHSB(r, g, b, null);
            int h = (int) (255.0 * hsv[0]);
            int s = (int) (255.0 * hsv[1]);
            int v = (int) (255.0 * hsv[2]);
            redHistogram.add((double) h);
            greenHistogram.add((double) s);
            if ((v > 0) && (v < 255))
              blueHistogram.add((double) v);
          }
        }
      }
    }
  }

  public boolean isFlippedHorizontally() {
    return imgInvX;
  }

  public boolean flipHorizontally() {
    if (isImageLoaded) {
      imgInvX = !imgInvX;
      return true;
    }
    return false;
  }

  public boolean isFlippedVertically() {
    return imgInvY;
  }

  public boolean flipVertically() {
    if (isImageLoaded) {
      imgInvY = !imgInvY;
      return true;
    }
    return false;
  }

  public double getAngle() {
    return imgAngle;
  }

  public boolean setAngle(double value) {
    if (isImageLoaded) {
      if (imgAngle != value) {
        imgAngle = value;
        return true;
      }
    }
    return false;
  }

  public double getScale() {
    return imgScale;
  }

  public boolean setScale(double value) {
    if (isImageLoaded) {
      if (imgScale != value) {
        imgScale = value;
        imgWidth = (int) (imgScale * displayImage.getWidth(this));
        imgHeight = (int) (imgScale * displayImage.getHeight(this));
        return true;
      }
    }
    return false;
  }

  public boolean originalSize() {
    return setScale(1.0);
  }

  public boolean fitToSize(int winWidth, int winHeight) {
    if (isImageLoaded) {
      int oriWidth = displayImage.getWidth(this);
      int oriHeight = displayImage.getHeight(this);

      float scaleWidth = (float) winWidth / oriWidth;
      float scaleHeight = (float) winHeight / oriHeight;

      if (scaleWidth <= scaleHeight)
        return setScale(scaleWidth);
      else
        return setScale(scaleHeight);
    }
    return false;
  }

  public void createBufferedImages() {
    int width = displayImage.getWidth(this);
    int height = displayImage.getHeight(this);

    biSrc = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    Graphics2D g2D = biSrc.createGraphics();
    g2D.drawImage(displayImage, 0, 0, width, height, this);
    g2D.dispose();

    srcRaster = biSrc.getRaster();

    biDest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    dstRaster = (WritableRaster) biDest.getRaster();

  }

  public void bandCombine(float[][] bandCombineMatrix) {
    if (isImageLoaded) {
      BandCombineOp bandCombineOp = new BandCombineOp(bandCombineMatrix,
                                                      null);
      bandCombineOp.filter(srcRaster, dstRaster);
      biShow = biDest;
      computeColorHistograms();
    }
  }

  public void prepare() {
    if (isImageLoaded) {
      double sin = Math.abs(Math.sin(imgAngle));
      double cos = Math.abs(Math.cos(imgAngle));
      int newImgWidth = (int) Math
        .floor(imgWidth * cos + imgHeight * sin);
      int newImgHeight = (int) Math.floor(imgHeight * cos + imgWidth
                                          * sin);
      bi = new BufferedImage(newImgWidth, newImgHeight,
                             BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2D = bi.createGraphics();
      if (imgInvX && imgInvY) {
        g2D.scale(-1, -1);
        g2D.translate(-newImgWidth, -newImgHeight);
      } else if (imgInvX) {
        g2D.scale(-1, 1);
        g2D.translate(-newImgWidth, 0);
      } else if (imgInvY) {
        g2D.scale(1, -1);
        g2D.translate(0, -newImgHeight);
      }
      g2D.translate(0.5 * (newImgWidth - imgWidth),
                    0.5 * (newImgHeight - imgHeight));
      g2D.rotate(imgAngle, 0.5 * imgWidth, 0.5 * imgHeight);
      g2D.scale(imgScale, imgScale);
      g2D.drawRenderedImage(biShow, null);
      g2D.dispose();
      Dimension dim = new Dimension(bi.getWidth(), bi.getHeight());
      setPreferredSize(dim);
      setSize(dim);

      prepareMask();
    }
  }

  //greice
  public void prepareMask() {
    if (isImageLoaded && isMaskLoaded) {
      double sin = Math.abs(Math.sin(imgAngle));
      double cos = Math.abs(Math.cos(imgAngle));
      int newImgWidth = (int) Math
        .floor(imgWidth * cos + imgHeight * sin);
      int newImgHeight = (int) Math.floor(imgHeight * cos + imgWidth
                                          * sin);
      biM = new BufferedImage(newImgWidth, newImgHeight,
                              BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2D = biM.createGraphics();
      if (imgInvX && imgInvY) {
        g2D.scale(-1, -1);
        g2D.translate(-newImgWidth, -newImgHeight);
      } else if (imgInvX) {
        g2D.scale(-1, 1);
        g2D.translate(-newImgWidth, 0);
      } else if (imgInvY) {
        g2D.scale(1, -1);
        g2D.translate(0, -newImgHeight);
      }
      g2D.translate(0.5 * (newImgWidth - imgWidth),
                    0.5 * (newImgHeight - imgHeight));
      g2D.rotate(imgAngle, 0.5 * imgWidth, 0.5 * imgHeight);
      g2D.scale(imgScale, imgScale);
      g2D.drawRenderedImage(biMask, null);
      g2D.dispose();
      Dimension dim = new Dimension(biM.getWidth(), biM.getHeight());
      setPreferredSize(dim);
      setSize(dim);
    }
  }

  //

  public void filter() {
    if (isImageLoaded) {
      // greice
      if (isMaskLoaded) {
        Raster raster = biM.getRaster();
        for (int i = 0; i < biM.getWidth(); i++)
          for (int j = 0; j < biM.getHeight(); j++) {
          int rgb = bi.getRGB(i, j);
          int r = raster.getSample(i, j, 0);
          int g = raster.getSample(i, j, 1);
          int b = raster.getSample(i, j, 2);
          if ((r != 255) & (g != 255) & (b != 255))
            bi.setRGB(i, j, 0x22FFFFFF & rgb);
          else
            bi.setRGB(i, j, 0xFFFFFFFF & rgb);
        }
      }//

      if ((redHistogram.getFilterBin() != -1)
          || (greenHistogram.getFilterBin() != -1)
          || (blueHistogram.getFilterBin() != -1)) {
        int rLo = (int) redHistogram.getLo();
        int rHi = (int) redHistogram.getHi();
        int gLo = (int) greenHistogram.getLo();
        int gHi = (int) greenHistogram.getHi();
        int bLo = (int) blueHistogram.getLo();
        int bHi = (int) blueHistogram.getHi();

        for (int i = 0; i < bi.getWidth(); i++)
          for (int j = 0; j < bi.getHeight(); j++) {
          int rgb = bi.getRGB(i, j);
          int r = (rgb & 0xFF0000) >> 16;
          int g = (rgb & 0xFF00) >> 8;
          int b = (rgb & 0xFF);
          if (colorModel.equals("RGB")) {
            if ((r < rLo) || (r > rHi) || (g < gLo)
                || (g > gHi) || (b < bLo) || (b > bHi))
              bi.setRGB(i, j, 0x22FFFFFF & rgb);
            else
              bi.setRGB(i, j, 0xFFFFFFFF & rgb);
          } else if (colorModel.equals("HSB")) {
            float[] hsv = Color.RGBtoHSB(r, g, b, null);
            int h = (int) (255.0 * hsv[0]);
            int s = (int) (255.0 * hsv[1]);
            int v = (int) (255.0 * hsv[2]);
            if ((h < rLo) || (h > rHi) || (s < gLo)
                || (s > gHi) || (v < bLo) || (v > bHi))
              bi.setRGB(i, j, 0x22FFFFFF & rgb);
            else
              bi.setRGB(i, j, 0xFFFFFFFF & rgb);
          }
        }
      }
    }
  }

  public void reset() {
    if (isImageLoaded) {
      biShow = biSrc;
      computeColorHistograms();
    }
  }

  public void update(Graphics g) {
    g.clearRect(0, 0, getWidth(), getHeight());
    paintComponent(g);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2D = (Graphics2D) g;
    if (isImageLoaded) {
      int xoffset, yoffset;
      if (bi.getWidth() < getWidth())
        xoffset = (getWidth() - bi.getWidth()) / 2;
      else
        xoffset = 0;
      if (bi.getHeight() < getHeight())
        yoffset = (getHeight() - bi.getHeight()) / 2;
      else
        yoffset = 0;
      g2D.drawImage(bi, xoffset, yoffset, bi.getWidth(), bi.getHeight(),
                    this);
    }
  }


  //greice
  public void resetMask() {
    if (isMaskLoaded) {
      biMask = biShow;
      resetSeries();
      isMaskLoaded = false;
    }
  }

  public void resetSeries() {
    redMean.clear();
    greenMean.clear();
    blueMean.clear();
    totalMean.clear();
    visualRhythm.clear();
  }

  public void processingMask() {
    maskPixels.clear();

    int width = displayImageMask.getWidth(this);
    int height = displayImageMask.getHeight(this);

    biMask = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    Graphics2D g2D = biMask.createGraphics();
    g2D.drawImage(displayImageMask, 0, 0, width, height, this);
    g2D.dispose();

    isMaskLoaded = true;

    Raster maskRaster = biMask.getRaster();

    width = biMask.getWidth();
    height = biMask.getHeight();

    //identifica pixels da imagem original que sao brancos e representam a mascara.
    for (int i = 0; i < width; i++)
      for (int j = 0; j < height; j++) {
      Pixel pixel = new Pixel();
      int r = maskRaster.getSample(i, j, 0);
      int g = maskRaster.getSample(i, j, 1);
      int b = maskRaster.getSample(i, j, 2);
      if ((r == 255) && (g == 255) && (b == 255)) {
        pixel.setX(i);
        pixel.setY(j);
        maskPixels.add(pixel); //cria lista de pixels da mascara- brancos.
      }
    }

    indexingMask();
  }

  public void indexingMask() {
    maskIndex.clear();
    // find the center
    int center_x = 0;
    int center_y = 0;
    for (Pixel p : maskPixels) {
      center_x += p.getX();
      center_y += p.getY();
    }
    center_x /= maskPixels.size();
    center_y /= maskPixels.size();
    // convert to polar coordinates
    ArrayList<Double> rhoList = new ArrayList<Double>();
    ArrayList<Double> thetaList = new ArrayList<Double>();
    for (Pixel p : maskPixels) {
      int dx = p.getX() - center_x;
      int dy = p.getY() - center_y;
      double rho = Math.sqrt(dx * dx + dy * dy);
      double theta = Math.atan2(dy, dx);
      rhoList.add(rho);
      thetaList.add(theta);
    }
    // compute the orientation histogram
    int OriBins = 36;
    int[] hist = new int[OriBins];
    for (int i = 0; i < OriBins; i++)
      hist[i]= 0;
    for (Double angle : thetaList) {
      int bin = (int) (OriBins * (angle + Math.PI + 0.001) / (2.0 * Math.PI));
      assert(bin >= 0 && bin <= OriBins);
      bin = Math.min(bin, OriBins - 1);
      hist[bin]++;
    }
    // find maximum value in the histogram
    double maxval = 0;
    int maxloc = 0;
    for (int i = 0; i < OriBins; i++)
      if (hist[i] > maxval) {
      maxval = hist[i];
      maxloc = i;
    }
    double angle = 2.0 * Math.PI * (maxloc + 0.5) / OriBins - Math.PI;
    for (int i = 0; i < thetaList.size(); i++)
      thetaList.set(i, thetaList.get(i) - angle);
    for (int i = 0; i < thetaList.size(); i++)
      maskIndex.put(2 * Math.PI * rhoList.get(i) + thetaList.get(i), i);
  }

  @SuppressWarnings("deprecation")
  public void loadFileMask(FileNode node) {
    //reset();
    String fileName = "";
    if(node != null){
      File f = node.getFile();
      if(!f.isDirectory()){
        fileName = f.getPath();
      }
    }

    if(!fileName.equals("")){
      if(fileName.toLowerCase().endsWith(".bmp")){

        try {
          displayImageMask =ImageIO.read(new URL(node.getFile().toURL(), node.getFile().getName()));
        } catch (IOException e) {
          //
          e.printStackTrace();
        }
      }
      else{

        displayImageMask = Toolkit.getDefaultToolkit().getImage(fileName);
      }

      MediaTracker mt = new MediaTracker(this);
      mt.addImage(displayImageMask, 1);
      try {
        mt.waitForAll();
      } catch (Exception e) {
        System.out.println("Exception while loading.");
      }

      processingMask();
    }
  }

  public void calcAvgRGB(File f) {
    try {
      BufferedImage biCalc = ImageIO.read(f);
      int sumR = 0;
      int sumG = 0;
      int sumB = 0;
      for (Pixel p : maskPixels) {
        int i = p.getX();
        int j = p.getY();
        int rgb = biCalc.getRGB(i, j);
        int r = (rgb & 0xFF0000) >> 16;
        int g = (rgb & 0xFF00) >> 8;
        int b = (rgb & 0xFF);

        sumR = sumR + r;
        sumG = sumG + g;
        sumB = sumB + b;

      }
      float meanR = (float) sumR/maskPixels.size();
      float meanG = (float) sumG/maskPixels.size();
      float meanB = (float) sumB/maskPixels.size();
      float total = meanR + meanG + meanB;

      redMean.add(meanR);
      greenMean.add(meanG);
      blueMean.add(meanB);
      totalMean.add(total);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void calcVisualRhythm(File f) {
    try {
      BufferedImage biCalc = ImageIO.read(f);
      for (Map.Entry<Double, Integer> entry : maskIndex.entrySet()) {
        Pixel p = maskPixels.get(entry.getValue());
        int i = p.getX();
        int j = p.getY();
        int rgb = biCalc.getRGB(i, j);
        visualRhythm.add(rgb);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

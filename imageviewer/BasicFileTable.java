/*
     * License
     *
     * The contents of this file are subject to the Jabber Open Source License
     * Version 1.0 (the "License").  You may not copy or use this file, in either
     * source code or executable form, except in compliance with the License.  You
     * may obtain a copy of the License at http://www.jabber.com/license/ or at
     * http://www.opensource.org/.
     *
     * Software distributed under the License is distributed on an "AS IS" basis,
     * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
     * for the specific language governing rights and limitations under the
     * License.
     *
     * Copyrights
     *
     * Portions Copyright (c) 2002-2006 Raditha Dissanayake
     *
     * Other portions copyright their respective owners.
     *
     */

package imageviewer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class BasicFileTable extends JTable
{
  final JScrollPane scrollPane = new JScrollPane(this);

  FileFilter filter = null;

  /**
         * root assumed to be top node. On windows there is more than one root.
         * Each Drive is considered a different file system root in windows
         * where as in linux it always one root '/'
         *
         */
  public BasicFileTable()
  {
    this(null);
  }

  public BasicFileTable(String topFolder)
  {
    setRootDirectory(topFolder);
    setPreferredScrollableViewportSize(new Dimension(90, 90));
  }

  public BasicFileTable(String topFolder, FileFilter fileFilter)
  {
    filter = fileFilter;
    setRootDirectory(topFolder);
    setPreferredScrollableViewportSize(new Dimension(90, 90));
  }

  public void setRootDirectory(String topFolder)
  {
    setModel(createTableModel(topFolder));
  }

  public JScrollPane getScrollPane()
  {
    return scrollPane;
  }

  private DefaultTableModel createTableModel(String topFolder) {

    DefaultTableModel tableModel = new DefaultTableModel()
    {
      public boolean isCellEditable(int row, int column)
      {
        return false;
      }
    };

    tableModel.setColumnCount(2);

    /**
             * If the topfolder is null we are working in the shit operating system
             * so now we have to find out what the drives are and add them manualy?
             * why can't people just use linux. It's free?
             */
    if(topFolder == null)
      explore(tableModel, "/");
    else
      explore(tableModel, topFolder);

    return tableModel;
  }

  private void explore(DefaultTableModel tableModel, String path)
  {
    File root = new File(path);
    File[] list = root.listFiles(filter);

    for (File f : list) {
      if (f.isDirectory())
        explore(tableModel, f.getAbsolutePath());
      else {
        Date d = readDate(f);
        int row = tableModel.getRowCount();
        tableModel.setRowCount(row + 1);
        tableModel.setValueAt(d, row, 0);
        tableModel.setValueAt(f, row, 1);
      }
    }
  }

  private Date readDate(File f)
  {
    String dat = null, tim = null;
    Date dateTime = null;

    try {
      BufferedReader br = new BufferedReader(
        new FileReader(f.getAbsolutePath()));

      String str = null;
      do {
        str = br.readLine();
      } while ((str != null) && !str.toUpperCase().
               equals("SECTION FINGERPRINT"));

      do {
        str = br.readLine();
        if (str != null) {
          if (str.substring(0, 3).toUpperCase().equals("DAT"))
            dat = str.substring(4, str.length());
          if (str.substring(0, 3).toUpperCase().equals("TIM"))
            tim = str.substring(4, str.length());
        }
      } while ((str != null) && !str.toUpperCase().
               equals("ENDSECTION FINGERPRINT"));
    } catch (IOException e) {
      System.out.println("File not found.");
    }

    if ((dat != null) && (tim != null)) {
      try {
        SimpleDateFormat format =
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateTime = format.parse(dat + " " + tim);
      } catch (ParseException e) {
        System.out.println("Unknown file format.");
      }
    }

    return dateTime;
  }
}

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

package phenoviewer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class BasicFileList extends JList
{
  final JScrollPane scrollPane = new JScrollPane(this);

  SortedMap<Date, File> files = new TreeMap<Date, File>();

  FileFilter filter = null;

  /**
    The root is assumed to be the top node.
    (On windows there is more than one root, as each driver is considered as one different file system.
    On linux there is always one root: '/'.)
    @param topFolder The path to the top folder, where the file list will begin.
    @fileFilter The file filter desired on this file list.
  */
  public BasicFileList()
  {
    this(null);
  }

  public BasicFileList(String topFolder)
  {
    setRootDirectory(topFolder);
    scrollPane.setMinimumSize(new Dimension(90, 90));
  }

  public BasicFileList(String topFolder, FileFilter fileFilter)
  {
    filter = fileFilter;
    setRootDirectory(topFolder);
    scrollPane.setMinimumSize(new Dimension(90, 90));
  }

  public File getSelectedValue()
  {
    if(super.getSelectedValue() != null)
      return files.get(super.getSelectedValue());
    else
      return null;
  }

  public void setSelectedValue(File f)
  {
    Date d = readDate(f);
    if(d != null)
    {
      if(files.get(d) != null)
        super.setSelectedValue(d, true);
      else
        clearSelection();
    }
  }

  /**
  Set a given folder as list root.
  @param topFolder the folder to be set as root.
  */
  public void setRootDirectory(String topFolder)
  {
    files.clear();
    setModel(createListModel(topFolder));
  }

  public JScrollPane getScrollPane()
  {
    return scrollPane;
  }

  /**
  @topFolder the folder root from where we create the list.
  @return ListModel.
  */
  private DefaultListModel createListModel(String topFolder) {
    if(topFolder == null)
      explore("/");
    else
      explore(topFolder);

    DefaultListModel listModel = new DefaultListModel();

    for (Object o : files.keySet().toArray())
      listModel.addElement((Date) o);

    return listModel;
  }

  /**
  Explores the filesystem and adds date to all the nonfolder entries (files)
  @param path The filesystem path to explore.
  */
  private void explore(String path)
  {
    File root = new File(path);
    File[] list = root.listFiles(filter);

    for (File f : list) {
      if (f.isDirectory())
        explore(f.getAbsolutePath());
      else
        files.put(readDate(f), f);
    }
  }

  /**
  Reads the date from a file and returns it
  @param f A file that we want to know the date of.
  @return The given file date.
  */
  private Date readDate(File f)
  {
    FileFunctions ff = new FileFunctions();
    return (ff.readDate(f));
  }
}

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
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class BasicFileTree extends JTree
{
  final JScrollPane scrollPane = new JScrollPane(this);

  Vector files = new Vector();

  FileFilter filter = null;

  String root;

  /**
     * root assumed to be top node. On windows there is more than one root.
     * Each Drive is considered a different file system root in windows
     * where as in linux it always one root '/'
     *
     */
  public BasicFileTree()
  {
    this(null);
  }

  public BasicFileTree(String topFolder)
  {
    setRootDirectory(topFolder);
    scrollPane.setMinimumSize(new Dimension(90, 90));
  }

  public BasicFileTree(String topFolder, FileFilter fileFilter)
  {
    filter = fileFilter;
    setRootDirectory(topFolder);
    scrollPane.setMinimumSize(new Dimension(90, 90));
  }

  public void setRootDirectory(String topFolder)
  {
    files.clear();
    setModel(createTreeModel(topFolder));
  }

  public JScrollPane getScrollPane()
  {
    return scrollPane;
  }

  public FileNode getNextLeaf()
  {
    FileNode node = (FileNode) getLastSelectedPathComponent();

    if (node != null)
    {
      int index = files.indexOf(node);
      if (index < files.size() - 1)
        return (FileNode) files.get(index + 1);
      else
        return (FileNode) files.firstElement();
    }
    else
      return (FileNode) files.firstElement();
  }

  public FileNode getPreviousLeaf()
  {
    FileNode node = (FileNode) getLastSelectedPathComponent();

    if (node != null)
    {
      int index = files.indexOf(node);
      if (index > 0)
        return (FileNode) files.get(index - 1);
      else
        return (FileNode) files.lastElement();
    }
    else
      return (FileNode) files.lastElement();
  }

  private DefaultTreeModel createTreeModel(String topFolder) {
    /**
         * If the topfolder is null we are working in the shit operating system
         * so now we have to find out what the drives are and add them manualy?
         * why can't people just use linux. It's free?
         */
    File f;
    if(topFolder == null)
      f = new File("/");
    else
      f = new File(topFolder);

    FileNode root = new FileNode(f);
    explore(root);
    DefaultTreeModel treeModel = new DefaultTreeModel(root);

    return treeModel;
  }

  private void explore(FileNode root)
  {
    root.explore(filter);
    Enumeration leaf = root.children();

    while(leaf.hasMoreElements())
    {
      FileNode node = (FileNode) leaf.nextElement();
      if (node.isDirectory())
        explore(node);
      else
        files.add(node);
    }
  }


}

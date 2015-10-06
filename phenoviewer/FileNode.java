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
 * Copyright (c) 2002-2006 Raditha Dissanayake
 *
 * Other portions copyright their respective owners.
 *
 */

package phenoviewer;

import javax.swing.tree.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class FileNode extends DefaultMutableTreeNode
{
  private boolean explored = false;

  public FileNode(File file)
  {
    setUserObject(file);
  }

  public boolean getAllowsChildren()
  {
    return isDirectory();
  }

  public boolean isLeaf()
  {
    boolean isLeaf = !isDirectory();

    return isLeaf;
  }

  public File getFile()
  {
    return (File)getUserObject();
  }

  public boolean isExplored()
  {
    return explored;
  }

  public boolean isDirectory()
  {
    File file = getFile();
    if(file == null)
    {
      // could be my computer
      return true;
    }
    return file.isDirectory();
  }

  public String toString()
  {
    File file = (File)getUserObject();
    if(file == null)
    {
      return "myComputer";
    }

    String filename = file.toString();
    int index = filename.lastIndexOf(File.separator);

    return (index != -1 && index != filename.length()-1)
      ? filename.substring(index+1) :  filename;
  }

  /**
     * as the name suggests we do the actual browsing through the directory
     * in this method.
     */
  public void explore()
  {
    explore(null);
  }

  public void explore(FileFilter fileFilter)
  {
    if(!isDirectory())
      return;

    if(!isExplored())
    {
      try
      {
        File file = getFile();
        File[] children = file.listFiles(fileFilter);
        Arrays.sort(children);
        for(int i=0; i < children.length; ++i)
        {
          add(new FileNode(children[i]));
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
        // we can't explore this folder so mark it
        explored = true;
      }

      explored = true;
    }
  }

  public FileNode getNode(File f)
  {
    /*
         * make sure the file exists.
         *
         * break down the file path into it's components. Then start with the
         * root component and compare with the nodes again starting from root.
         *
         * if the root component is null, use the listRoots() method to get
         * a list of all the roots (windows). Then compare with each of them
         * to find the correct root node. Next list the child nodes of the
         * matching root node, and compare with them. Repeat until you have a
         * match or have finished traversing the whole branch.
         */

    if(f.exists())
    {
      int i,size;
      ArrayList components = new ArrayList();
      File parent = f;
      FileNode root = (FileNode) getRoot();

      while(true)
      {
        components.add(parent);
        if(parent.getParent() == null)
        {
          break;
        }
        parent = parent.getParentFile();
      }
      size = components.size()-1;

      if(root.getFile() == null)
      {
        /*  windows, this has to match */
        if(matchNode(root.children(), (File) components.get(size)) ==
           null)
        {
          return null;
        }
        size--; // already matched one.
      }
      else
      {
        /* the good operating systems or we have a match for root */
        while((size >= 0) && (root.getFile().compareTo(
          (File) components.get(size)) != 0))
        {
          size--;
        }

        if(size >= 0)
        {
          size--;
        }
      }

      if(size >= 0)
      {
        FileNode node = root;
        for(i = size ; i >= 0 ; i--)
        {
          node.explore();
          node = matchNode(node.children(), (File) components.get(i));
        }

        return node;
      }
    }
    return null;
  }

  public TreePath getPath(File f)
  {

    /*
         * make sure the file exists.
         *
         * break down the file path into it's components. Then start with the
         * root component and compare with the nodes again starting from root.
         *
         * if the root component is null, use the listRoots() method to get
         * a list of all the roots (windows). Then compare with each of them
         * to find the correct root node. Next list the child nodes of the
         * matching root node, and compare with them. Repeat until you have a
         * match or have finished traversing the whole branch.
         */

    if(f.exists())
    {
      int i,size;
      ArrayList components = new ArrayList();
      File parent = f;
      FileNode root = (FileNode) getRoot();
      TreePath path;
      ArrayList nodes = new ArrayList();

      while(true)
      {
        components.add(parent);
        if(parent.getParent() == null)
        {
          break;
        }
        parent = parent.getParentFile();
      }
      size = components.size()-1;

      if(root.getFile() == null)
      {
        /*  windows, this has to match */
        if(matchNode(root.children(), (File) components.get(size)) ==
           null)
        {
          return null;
        }
        nodes.add(root);
        size--; // already matched one.
      }
      else
      {
        /* the good operating systems or we have a match for root */
        while((size >= 0) && (root.getFile().compareTo(
          (File) components.get(size)) != 0))
        {
          size--;
        }

        if(size >= 0)
        {
          nodes.add(root);
          size--;
        }
      }

      if(size >= 0)
      {
        FileNode node = root;
        for(i = size ; i >= 0 ; i--)
        {
          node.explore();
          node = matchNode(node.children(), (File) components.get(i));
          nodes.add(node);
        }

        return new TreePath(nodes.toArray());
      }
    }
    return null;
  }

  private FileNode matchNode(Enumeration nodes, File f)
  {
    while (nodes.hasMoreElements())
    {
      FileNode item = (FileNode) nodes.nextElement();
      if(item.getFile().compareTo(f) == 0)
      {
        return item;
      }
    }
    return null;
  }
}

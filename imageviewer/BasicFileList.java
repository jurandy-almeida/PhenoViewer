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
import java.text.*;
import java.util.*;

public class BasicFileList extends JList
{
    final JScrollPane scrollPane = new JScrollPane(this);

    SortedMap<Date, File> files = new TreeMap<Date, File>();
 
    FileFilter filter = null;

    /**
     * root assumed to be top node. On windows there is more than one root.
     * Each Drive is considered a different file system root in windows
     * where as in linux it always one root '/'
     *
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

    public void setRootDirectory(String topFolder) 
    {
        files.clear();
        setModel(createListModel(topFolder));
    }
    
    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }

    private DefaultListModel createListModel(String topFolder) {
        /**
         * If the topfolder is null we are working in the shit operating system
         * so now we have to find out what the drives are and add them manualy?
         * why can't people just use linux. It's free?
         */
        if(topFolder == null) 
            explore("/");
        else
            explore(topFolder);

        DefaultListModel listModel = new DefaultListModel();

        for (Object o : files.keySet().toArray()) 
          listModel.addElement((Date) o);

        return listModel;        
    }

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

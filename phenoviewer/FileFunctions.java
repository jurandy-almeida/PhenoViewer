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

public class FileFunctions

{

  public FileFunctions()
  {

  }


  public Date readDate(File f)
  {
    Date dateTime = null;

    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    System.out.println("Modified Date :- " + sdf.format(f.lastModified()));
    dateTime = new Date(f.lastModified());

    return dateTime;
  }
}

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

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;


public class FileFunctions {

  public FileFunctions()
  {

  }


  public Date readDate(File f){
    String dat = null, tim = null;
          Date dateTime = null;
        try { //Trata imagens de outras cameras
              Metadata metadata = ImageMetadataReader.readMetadata(f);
              Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
              if(directory !=null) {
                  Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  //              System.out.println(format.format(date));
                  return date;
              }
          } catch (Exception e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
          }
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
              br.close();
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

          return (dateTime!=null) ? dateTime : new Date();
  }
}

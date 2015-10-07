package phenoviewer;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;

public class BmpImage
{
  String bfName;
  boolean imageProcessed;
  boolean windowsStyle;
  ColorModel colorModel = null;
  int pix[];

  byte bfType[];
  int bfSize;
  int bfOffset;
  int biSize;
  int biWidth;
  int biHeight;
  int biPlanes;
  int biBitCount;
  int biCompression;
  int biSizeImage;
  int biXPelsPerMeter;
  int biYPelsPerMeter;
  int biClrUsed;
  int biClrImportant;

  public BmpImage(String name)
  {
    bfName = name;
    bfType = new byte[2];
    imageProcessed = false;
  }

  /**
     * A private method for extracting little endian
     * quantities from a input stream.
     * @param is contains the input stream
     * @param len is the number of bytes in the quantity
     * @returns the result as an integer
     */
  private int pullVal(DataInputStream is, int len)
    throws IOException
  {
    int value = 0;
    int temp = 0;

    for ( int x = 0; x < len; x++ )
    {
      temp = is.readUnsignedByte();
      value += (temp << (x * 8));
    }
    return value;
  }

  /**
     * A private method for extracting the file header
     * portion of a BMP file.
     * @param is contains the input stream
     */
  private void extractFileHeader(DataInputStream is)
    throws IOException, AWTException
    {
      is.read(bfType);
      if ( bfType[0] != 'B' || bfType[1] != 'M' )
        throw new AWTException("Not BMP format");
      bfSize = pullVal(is, 4);
      is.skipBytes(4);
      bfOffset = pullVal(is, 4);
    }

  /**
     * A private method for extracting the color table from
     * a BMP type file.
     * @param is contains the input stream
     * @param numColors contains the biClrUsed (for Windows) or zero
     */
  private void extractColorMap(DataInputStream is, int numColors)
    throws IOException, AWTException
    {
      byte blues[], reds[], greens[];

      // if passed count is zero, then determine the
      // number of entries from bits per pixel.
      if ( numColors == 0 )
      {
        switch ( biBitCount )
        {
          case 1:  numColors =   2; break;
          case 4:  numColors =  16; break;
          case 8:  numColors = 256; break;
          case 24: numColors =   0; break;
          default: numColors =  -1; break;
        }
      }
      if ( numColors == -1 )
        throw new AWTException("Invalid bits per pixel: " + biBitCount);
      else if ( numColors == 0 )
        colorModel = new DirectColorModel(24, 255 * 3, 255 * 2, 255);
        else
      {
        reds = new byte[numColors];
        blues = new byte[numColors];
        greens = new byte[numColors];
        for ( int x = 0; x < numColors; x++ )
        {
          blues[x] = is.readByte();
          greens[x] = is.readByte();
          reds[x] = is.readByte();
          if ( windowsStyle )
            is.skipBytes(1);
        }
        colorModel = new IndexColorModel( biBitCount, numColors,
                                         reds, greens, blues );
      }
    }

  /**
     * A private method for extracting an OS/2 style
     * bitmap header.
     * @param is contains the input stream
     */
  private void extractOS2Style(DataInputStream is)
    throws IOException, AWTException
    {
      windowsStyle = false;
      biWidth = pullVal(is, 2);
      biHeight = pullVal(is, 2);
      biPlanes = pullVal(is, 2);
      biBitCount = pullVal(is, 2);
      extractColorMap(is, 0);
    }

  /**
     * A private method for extracting a Windows style
     * bitmap header.
     * @param is contains the input stream
     */
  private void extractWindowsStyle(DataInputStream is)
    throws IOException, AWTException
    {
      windowsStyle = true;
      biWidth = pullVal(is, 4);
      biHeight = pullVal(is, 4);
      biPlanes = pullVal(is, 2);
      biBitCount = pullVal(is, 2);
      biCompression = pullVal(is, 4);
      biSizeImage = pullVal(is, 4);
      biXPelsPerMeter = pullVal(is, 4);
      biYPelsPerMeter = pullVal(is, 4);
      biClrUsed = pullVal(is, 4);
      biClrImportant = pullVal(is, 4);
      extractColorMap(is, biClrUsed);
    }

  /**
     * A private method for extracting the bitmap header.
     * This method determines the header type (OS/2 or Windows)
     * and calls the appropriate routine.
     * @param is contains the input stream
     */
  private void extractBitmapHeader(DataInputStream is)
    throws IOException, AWTException
    {
      biSize = pullVal(is, 4);
      if ( biSize == 12 )
        extractOS2Style(is);
      else
        extractWindowsStyle(is);
    }

  /**
     * A private method for extracting 4 bit per pixel
     * image data.
     * @param is contains the input stream
     */
  private void extract4BitData( DataInputStream is )
    throws IOException
  {
    int index, temp = 0;

    if ( biCompression == 0 )
    {
      int padding = 0;
      int overage = ((biWidth + 1)/ 2) % 4;
      if ( overage != 0 )
        padding = 4 - overage;
      pix = new int[biHeight * biWidth];
      for ( int y = biHeight - 1; y >= 0; y-- )
      {
        index = y * biWidth;
        for ( int x = 0; x < biWidth; x++ )
        {
          // if on an even byte, read new 8 bit quantity
          // use low nibble of previous read for odd bytes
          if ( (x % 2) == 0 )
          {
            temp = is.readUnsignedByte();
            pix[index++] = temp >> 4;
          }
          else
            pix[index++] = temp & 0x0f;
        }
        if ( padding != 0 ) is.skipBytes(padding);
      }
    }
    else
    {
      throw new IOException("Compressed images not supported");
    }
  }

  /**
     * A private method for extracting 8 bit per pixel
     * image data.
     * @param is contains the input stream
     */
  private void extract8BitData( DataInputStream is )
    throws IOException
  {
    int index;

    if ( biCompression == 0 )
    {
      int padding = 0;
      int overage = biWidth % 4;
      if ( overage != 0 )
        padding = 4 - overage;
      pix = new int[biHeight * biWidth];
      for ( int y = biHeight - 1; y >= 0; y-- )
      {
        index = y * biWidth;
        for ( int x = 0; x < biWidth; x++ )
        {
          pix[index++] = is.readUnsignedByte();
        }
        if ( padding != 0 ) is.skipBytes(padding);
      }
    }
    else
    {
      throw new IOException("Compressed images not supported");
    }
  }

  /**
     * A private method for extracting the image data from
     * a input stream.
     * @param is contains the input stream
     */
  private void extractImageData( DataInputStream is )
    throws IOException, AWTException
    {
      switch ( biBitCount )
      {
        case 1:
        throw new AWTException("Unhandled bits/pixel: " + biBitCount);
        case 4:  extract4BitData(is); break;
        case 8:  extract8BitData(is); break;
        case 24:
        throw new AWTException("Unhandled bits/pixel: " + biBitCount);
        default:
        throw new AWTException("Invalid bits per pixel: " + biBitCount);
      }
    }

  /**
     * Given an input stream, create an ImageProducer from
     * the BMP info contained in the stream.
     * @param is contains the input stream to use
     * @returns the ImageProducer
     */
  public ImageProducer extractImage( DataInputStream is )
    throws AWTException
  {
    MemoryImageSource img = null;
    try
    {
      extractFileHeader(is);
      extractBitmapHeader(is);
      extractImageData(is);
      img = new MemoryImageSource( biWidth, biHeight, colorModel,
                                  pix, 0, biWidth );
      imageProcessed = true;
    }
    catch (IOException ioe )
    {
      throw new AWTException(ioe.toString());
    }
    return img;
  }

  /**
     * Describe the image as a string
     */
  public String toString()
  {
    StringBuffer buf = new StringBuffer("");
    if ( imageProcessed )
    {
      buf.append("       name: " + bfName + "\n");
      buf.append("       size: " + bfSize + "\n");
      buf.append(" img offset: " + bfOffset + "\n");
      buf.append("header size: " + biSize + "\n");
      buf.append("      width: " + biWidth + "\n");
      buf.append("     height: " + biHeight + "\n");
      buf.append(" clr planes: " + biPlanes + "\n");
      buf.append(" bits/pixel: " + biBitCount + "\n");
      if ( windowsStyle )
      {
        buf.append("compression: " + biCompression + "\n");
        buf.append(" image size: " + biSizeImage + "\n");
        buf.append("Xpels/meter: " + biXPelsPerMeter + "\n");
        buf.append("Ypels/meter: " + biYPelsPerMeter + "\n");
        buf.append("colors used: " + biClrUsed + "\n");
        buf.append("primary clr: " + biClrImportant + "\n");
      }
    }
    else
      buf.append("Image not read yet.");
    return buf.toString();
  }

  /**
     * A method to retrieve an ImageProducer for a BMP URL.
     * @param context contains the base URL (from getCodeBase() or such)
     * @param name contains the file name.
     * @returns an ImageProducer
     * @exception AWTException on stream or bitmap data errors
     */
  public static ImageProducer getImageProducer( URL context, String name )
    throws AWTException
  {
    InputStream is = null;
    ImageProducer img = null;

    try
    {
      BmpImage im = new BmpImage(name);
      is = new URL(context, name).openStream();
      DataInputStream input = new DataInputStream( new
                                                  BufferedInputStream(is) );
      img = im.extractImage(input);
    }
    catch (MalformedURLException me)
    {
      throw new AWTException(me.toString());
    }
    catch (IOException ioe)
    {
      throw new AWTException(ioe.toString());
    }
    return img;
  }

  /**
     * A method to retrieve an ImageProducer given just a BMP URL.
     * @param context contains the base URL (from getCodeBase() or such)
     * @returns an ImageProducer
     * @exception AWTException on stream or bitmap data errors
     */
  public static ImageProducer getImageProducer( URL context)
    throws AWTException
  {
    InputStream is = null;
    ImageProducer img = null;
    String name = context.toString();
    int index; // Make last part of URL the name
    if ((index = name.lastIndexOf('/')) >= 0)
      name = name.substring(index + 1);
    try {
      BmpImage im = new BmpImage(name);
      is = context.openStream();
      DataInputStream input = new DataInputStream( new
                                                  BufferedInputStream(is) );
      img = im.extractImage(input);
    }
    catch (MalformedURLException me)
    {
      throw new AWTException(me.toString());
    }
    catch (IOException ioe)
    {
      throw new AWTException(ioe.toString());
    }
    return img;
  }
}

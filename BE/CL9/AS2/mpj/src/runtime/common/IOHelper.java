/*
 The MIT License

 Copyright (c) 2013 - 2013
   1. High Performance Computing Group, 
   School of Electrical Engineering and Computer Science (SEECS), 
   National University of Sciences and Technology (NUST)
   2. Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter (2013 - 2013)
   

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be included
 in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * File         : IOHelper.java 
 * Author       : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter
 * Created      : Oct 28, 2013
 * Revision     : $
 * Updated      : Nov 05, 2013
 *
 *
 *  Zipping and Unzipping methods are taken from this link: http://pastebin.com/ZS1p4N5P
 */

package runtime.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class IOHelper {

  public static String getUniqueID() {

    return UUID.randomUUID().toString();
  }

  public static boolean isNullOrEmpty(String s) {

    if (s == null || s.trim().equals(""))
      return true;

    return false;
  }

  public static void CreateDirectory(String folderPath) {
    File folder = new File(folderPath);
    if (!folder.isDirectory() && !folder.exists()) {
      folder.mkdir();
    }
  }

  public static String[] getFileList(String directoryPath) {

    File directory = new File(directoryPath);

    if (directory.exists()) {
      return directory.list();
    }

    return new String[] {};
  }

  public static String getFileName(String filePath) {
    return filePath.substring(filePath.lastIndexOf("/") + 1);
  }

  public static String readCharacterFile(String path) {

    FileInputStream stream = null;
    InputStreamReader streamReader = null;
    BufferedReader bufferedReader = null;
    StringBuilder buffer = new StringBuilder();

    try {

      stream = new FileInputStream(path);
      streamReader = new InputStreamReader(stream, "UTF-8");
      bufferedReader = new BufferedReader(streamReader);

      String line = null;

      while ((line = bufferedReader.readLine()) != null) {

	buffer.append(line);
      }

    }
    catch (Exception exp) {

      exp.printStackTrace();
      return null;

    }
    finally {

      try {

	bufferedReader.close();
	stream.close();

      }
      catch (Exception e) {

	e.printStackTrace();
      }
    }

    return buffer.toString();
  }

  public static boolean deleteFile(String path) {

    try {
      File f = new File(path);
      if (f.exists()) {
	return f.delete();

      } else {
	System.out.println("File cannot be deleted");
      }

    }
    catch (Exception exp) {

      exp.printStackTrace();
    }

    return false;
  }

  public static String getUniqueName() {
    return UUID.randomUUID().toString();

  }

  public static String getFileNameFromFilePath(String path) {
    String fileName = "";
    File f = new File(path);
    fileName = f.getName();
    return fileName;
  }

  public static String getFilePath(String path) {
    String fileName = "";
    File f = new File(path);
    fileName = f.getName();
    if (!(fileName.equals(""))) {
      path = path.substring(0, path.indexOf(fileName));
    }
    path = removeTag(path);
    return path;
  }

  public static String removeTag(String str) {
    if (str.startsWith("/")) {
      str = str.substring(str.indexOf("/") + 1);
    }
    if (str.endsWith("/")) {
      str = str.substring(0, str.lastIndexOf("/"));
    }
    return str;
  }

  public static String getCharacterDataFromElement(Element e) {
    if (e != null) {
      Node child = e.getFirstChild();

      if (child instanceof CharacterData) {
	CharacterData cd = (CharacterData) child;
	return cd.getData();
      }
    }
    return "?";
  }

  public static byte[] ReadBinaryFile(String filename) {
    byte[] buffer = null;
    File a_file = new File(filename);
    try {
      FileInputStream fis = new FileInputStream(filename);
      int length = (int) a_file.length();
      buffer = new byte[length];
      fis.read(buffer);
      fis.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return buffer;
  }

  public static Boolean writeFile(String path, byte[] contents) {
    Boolean success = false;
    try {
      File outputfile = new File(path);
      FileOutputStream fos = new FileOutputStream(outputfile);
      fos.write(contents);
      fos.flush();
      fos.close();

      success = true;
    }
    catch (Exception ex) {
    }
    return success;
  }

  public static boolean writeCharacterFile(String path, String contents) {

    Writer writer = null;
    FileOutputStream fileOutputStream = null;
    boolean bRet = false;
    try {
      // if(!f.exists())
      File f = new File(path);
      if (!f.exists()) {
	f.createNewFile();
      }
      fileOutputStream = new FileOutputStream(path);
      writer = new OutputStreamWriter(fileOutputStream, "UTF-8");
      writer.write(contents);
      bRet = true;

    }
    catch (Exception exp) {

      exp.printStackTrace();

    }
    finally {

      try {

	writer.close();

      }
      catch (Exception e) {

	e.printStackTrace();
      }
    }
    return bRet;

  }

  public static void zipFolder(String srcDir, String zipFile) {

    try {
      zipDirectory(srcDir, zipFile);
    }
    catch (Exception e) {
    }
  }

  private static void zipDirectory(String srcFolder, String destZipFile)
      throws Exception {
    ZipOutputStream zip = null;
    FileOutputStream fileWriter = null;
    /*
     * create the output stream to zip file result
     */
    fileWriter = new FileOutputStream(destZipFile);
    zip = new ZipOutputStream(fileWriter);
    /*
     * add the folder to the zip
     */
    addFolderToZip("", srcFolder, zip);
    /*
     * close the zip objects
     */
    zip.flush();
    zip.close();
  }

  /*
   * recursively add files to the zip files
   */
  private static void addFileToZip(String path, String srcFile,
      ZipOutputStream zip, boolean flag) throws Exception {
    /*
     * create the file object for inputs
     */
    File folder = new File(srcFile);

    /*
     * if the folder is empty add empty folder to the Zip file
     */
    if (flag == true) {
      zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
    } else { /*
	      * if the current name is directory, recursively traverse it to get
	      * the files
	      */
      if (folder.isDirectory()) {
	/*
	 * if folder is not empty
	 */
	addFolderToZip(path, srcFile, zip);
      } else {
	/*
	 * write the file to the output
	 */
	byte[] buf = new byte[1024];
	int len;
	FileInputStream in = new FileInputStream(srcFile);
	zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
	while ((len = in.read(buf)) > 0) {
	  /*
	   * Write the Result
	   */
	  zip.write(buf, 0, len);
	}
      }
    }
  }

  /*
   * add folder to the zip file
   */
  private static void addFolderToZip(String path, String srcFolder,
      ZipOutputStream zip) throws Exception {
    File folder = new File(srcFolder);

    /*
     * check the empty folder
     */
    if (folder.list().length == 0) {
      System.out.println(folder.getName());
      addFileToZip(path, srcFolder, zip, true);
    } else {
      /*
       * list the files in the folder
       */
      for (String fileName : folder.list()) {
	if (path.equals("")) {
	  addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
	} else {
	  addFileToZip(path + "/" + folder.getName(), srcFolder + "/"
	      + fileName, zip, false);
	}
      }
    }
  }

  public static void main(String args[]) {
    IOHelper.zipFolder("/home/aleem/code/test", "/home/aleem/code/test.zip");
    IOHelper.ExtractZip("/home/aleem/code/test.zip", "/home/aleem/code/test2");
  }

  public static void ExtractZip(String srcFileName, String targetFolder) {
    ZipInputStream zis = null;
    try {

      zis = new ZipInputStream(new FileInputStream(srcFileName));
      ZipEntry entry;

      while ((entry = zis.getNextEntry()) != null) {

	// Create a file on HDD in the destinationPath directory
	// destinationPath is a "root" folder, where you want to extract your
	// ZIP file
	File entryFile = new File(targetFolder, entry.getName());
	if (entry.isDirectory()) {

	  if (entryFile.exists()) {
	  } else {
	    entryFile.mkdirs();
	  }

	} else {

	  // Make sure all folders exists (they should, but the safer, the
	  // better ;-))
	  if (entryFile.getParentFile() != null
	      && !entryFile.getParentFile().exists()) {
	    entryFile.getParentFile().mkdirs();
	  }

	  // Create file on disk...
	  if (!entryFile.exists()) {
	    entryFile.createNewFile();
	  }

	  FileOutputStream fileoutputstream = new FileOutputStream(entryFile);
	  byte[] buf = new byte[1024];
	  int n;
	  while ((n = zis.read(buf, 0, 1024)) > -1) {
	    fileoutputstream.write(buf, 0, n);
	  }

	}
      }
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}

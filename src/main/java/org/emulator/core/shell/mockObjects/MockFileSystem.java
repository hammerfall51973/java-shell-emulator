package org.emulator.core.shell.mockObjects;

import org.emulator.core.shell.helpers.Directory;
import org.emulator.core.shell.helpers.File;
import org.emulator.core.shell.helpers.FileSystem;


public class MockFileSystem extends FileSystem {
  public String currPwd = "/";
  public Directory currDir = new Directory(currPwd);


  public boolean traverse(String path) {
    if (path.equals("/dir1")) {
      currPwd = "/dir1/";
      return true;
    }

    if (path.equals("../../dir1")) {
      currPwd = "/dir1/";
      return true;
    }

    if (path.equals("dir1")) {
      currPwd = "/dir1/";
      return true;
    }

    if (path.equals(".")) {
      return true;
    }

    if (path.equals("..")) {
      currPwd = "/";
      return true;
    }

    if (path.equals("/"))
      return true;

    if (path.equals("/dir2")) {
      currPwd = "/dir2/";
      return true;
    }

    return false;
  }


  public File getChildFile(String name) {
    if (name.equals("rootFile1"))
      return new File("rootFile1", "inside rootFile1");

    if (name.equals("rootFile2"))
      return new File("rootFile2", "inside rootFile2");

    if (name.equals("rootFile3"))
      return new File("rootFile3", "inside rootFile3");

    if (name.equals("dir1File1"))
      return new File("dir1File1", "inside dir1File1");

    return null;
  }


  public Directory getDir() {
    return currDir;
  }

  /**
   * Sets current directory
   */
  public void setDir(Directory dir) {
    currDir = dir;
  }
}

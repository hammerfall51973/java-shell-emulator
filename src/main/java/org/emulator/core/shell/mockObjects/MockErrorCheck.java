package org.emulator.core.shell.mockObjects;


import org.emulator.core.shell.helpers.ErrorCheck;
import org.emulator.core.shell.helpers.FileSystem;
import org.emulator.core.shell.helpers.StandardError;

public class MockErrorCheck extends ErrorCheck {


  public String getPath(String path) {
    if (path.equals("/../../dir1"))
      return "/dir1";

    if (path.equals("dir1/"))
      return "/dir1";

    if (path.equals("dir2/"))
      return "/dir2";

    if (path.equals("rootFile1") || path.equals("rootFile2")
        || path.equals("rootFile3"))
      return "/";

    if (path.equals("/rootFile1") || path.equals("/rootFile2")
        || path.equals("/rootFile3") || path.equals("/dir1/dir1File1"))
      return "/";

    if (path.contains("/"))
      return "";

    return "/";
  }

  public boolean parentExists(FileSystem fs, String path) {
    if (path.startsWith("/dir1"))
      return true;

    if (path.startsWith("/dir2"))
      return true;

    if (path.contains("/"))
      return false;

    return true;
  }


  public boolean fileExists(String path) {
    if (path.equals("/dir1/dir1File1"))
      return true;

    if (path.equals("/rootFile1") || path.equals("/rootFile2")
        || path.equals("/rootFile3"))
      return true;

    return false;
  }


  public boolean dirExists(FileSystem fs, String path) {
    if (path.equals("."))
      return true;

    if (path.equals(".."))
      return true;

    if (path.equals("dir1"))
      return true;

    if (path.equals("/dir1"))
      return true;

    if (path.equals("dir2"))
      return true;

    if (path.equals("/dir2"))
      return true;

    if (path.endsWith("/dir1"))
      return true;

    // Malhar's test cases
    if (path.equals("/"))
      return true;

    return false;
  }

  public boolean isProperString(String input) {
    if (input.equals("\"ab\"cd\"")) {
      StandardError.errors
          .add("Error: Strings cannot contain double quotes within them\n");
      return false;
    }
    if (input.equals("abcd")) {
      StandardError.errors
          .add("Error: Strings need to be wrapped in double quotes\n");
      return false;
    }
    return true;
  }
}

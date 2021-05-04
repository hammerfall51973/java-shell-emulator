package org.emulator.core.shell.commands;


import org.emulator.core.shell.helpers.ErrorCheck;
import org.emulator.core.shell.helpers.FileSystem;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.interfaces.CommandInterface;

public class Cd implements CommandInterface {

  private static FileSystem fileSystem = FileSystem.getInstanceOfFileSystem();


  private static ErrorCheck errorCheck = new ErrorCheck();


  protected void runCd(String path) {
    fileSystem.traverse(path);
  }

  public String check(String[] arg) {
    String path = arg[1];

    if (!errorCheck.dirExists(fileSystem, path)) {
      if (!path.contains("//") && errorCheck.fileExists(fileSystem, path)) {
        StandardError.errors.add("Error: specified path is not a directory\n");
        return "";
      }
      StandardError.errors.add("Error: Invalid directory: the directory " + path
          + " does not exist\n");
      return "";
    }
    runCd(path);

    return "";
  }
}


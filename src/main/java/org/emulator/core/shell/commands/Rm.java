package org.emulator.core.shell.commands;

import org.emulator.core.shell.helpers.Directory;
import org.emulator.core.shell.helpers.ErrorCheck;
import org.emulator.core.shell.helpers.FileSystem;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.interfaces.*;



public class Rm implements CommandInterface {

  private static FileSystem fileSystem = FileSystem.getInstanceOfFileSystem();

  private static ErrorCheck errorCheck = new ErrorCheck();

  private void removeDir(String path) {
    Directory storeDir = fileSystem.getDir();

    //Directory directory = fileSystem.getDir();
    // traverse to parent of directory to be deleted
    fileSystem.traverse(errorCheck.getPath(path));
    // get directory that is to be deleted
    Directory toDel = fileSystem.getChildDir(errorCheck.getName(path));
    // remove directory from parent, garbage collector will do the rest
    fileSystem.getDir().getChildren().remove(toDel);
    // reset current directory
    fileSystem.setDir(storeDir);
  }


  public String check(String[] arg) {
    String path = arg[1];

    if (path.equals("/")) {
      StandardError.errors.add("Error: Can't remove the root directory\n");
      return "";
    }

    if (!errorCheck.dirExists(fileSystem, path)) {
      StandardError.errors.add("Error: Invalid directory: the directory " + path
          + " does not exist\n");
      return "";
    }

    removeDir(path);
    return "";
  }
}

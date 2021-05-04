package org.emulator.core.shell.commands;


import org.emulator.core.shell.helpers.Directory;
import org.emulator.core.shell.helpers.ErrorCheck;
import org.emulator.core.shell.helpers.FileSystem;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.interfaces.CommandInterface;

import java.util.Arrays;


public class Mkdir implements CommandInterface {
  /**
   * Gets an instance of fileSystem so that the class can access the fileSystem
   */
  private static FileSystem fileSystem = FileSystem.getInstanceOfFileSystem();
  /**
   * Creates an instance of errorCheck so that the class can access the
   * fileSystem
   */
  private static ErrorCheck errorCheck = new ErrorCheck();

  /**
   * Creates a directory at path
   * 
   * @param path Determines the path of the new directory
   */
  private void runMkdir(String path) {
    Directory storeDir = fileSystem.getDir();
    // Changes currDir to the directory given in the path
    fileSystem.traverse(errorCheck.getPath(path));
    // Now that we are in the required parent directory, we can add the node

    fileSystem.addChild(errorCheck.getName(path));
    // Now we come back to current directory
    fileSystem.setDir(storeDir);
  }


  public String check(String[] arg) {

    // will run for all valid paths.
    for (String path : Arrays.copyOfRange(arg, 1, arg.length)) {
      if (errorCheck.dirExists(fileSystem, path)
          || errorCheck.fileExists(fileSystem, path)) {
        StandardError.errors.add("Error: '" + path + "' already exists\n");
      } else if (!errorCheck.parentExists(fileSystem, path)) {
        StandardError.errors.add("Error: Invalid path: the parent directory "
            + errorCheck.getPath(path) + " does not exist\n");
      } else if (!errorCheck.invalidChar(errorCheck.getName(path))) {
        runMkdir(path);
      }
    }

    return "";
  }
}

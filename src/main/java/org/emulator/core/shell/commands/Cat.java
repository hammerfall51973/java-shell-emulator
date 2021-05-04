package org.emulator.core.shell.commands;

import org.emulator.core.shell.helpers.Directory;
import org.emulator.core.shell.helpers.File;
import org.emulator.core.shell.helpers.Redirection;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.interfaces.CommandInterface;

import java.util.Arrays;


public class Cat extends Redirection implements CommandInterface {


  protected static String runCat(String path) {
    String output;
    Directory storeDir = fileSystem.getDir();

    fileSystem.traverse(errorCheck.getPath(path));
    File file = fileSystem.getChildFile(errorCheck.getName(path));

    output = file.getFileContent();
    fileSystem.setDir(storeDir);

    return output;
  }


  public String check(String[] arr) {
    String[] paths = Arrays.copyOfRange(arr, 1, arr.length);
    String output = "";
    for (String path : paths) {
      if (errorCheck.fileExists(fileSystem, path)) {
        output += Cat.runCat(path) + "\n\n";
      } else {
        StandardError.errors.add("Error: Invalid filepath: the given file '"
            + path + "' does not exist\n");
      }
    }

    return output;
  }
}

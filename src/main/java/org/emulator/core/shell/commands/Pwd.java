package org.emulator.core.shell.commands;


import org.emulator.core.shell.helpers.Directory;
import org.emulator.core.shell.helpers.Redirection;
import org.emulator.core.shell.interfaces.CommandInterface;

public class Pwd extends Redirection implements CommandInterface {

  public String runPwd() {
    // Storing current directory because we need to traverse to the root
    Directory startDir = fileSystem.getDir();

    if (fileSystem.isRootDir()) {
      return "/";
    }

    // Creating string that will store the full path
    StringBuilder fullPath = new StringBuilder();

    // Go up till you hit the root
    while (!fileSystem.isRootDir()) {
      String current = fileSystem.getDir().getDirName();
      fullPath.insert(0, current + "/");
      fileSystem.cdParent();
    }

    // Adding root directory at the end
    fullPath.insert(0, "/");

    // Resetting current node
    fileSystem.setDir(startDir);

    // return the whole path
    return fullPath.substring(0, fullPath.length() - 1);
  }

  public String check(String[] arr) {
    return runPwd() + "\n";
  }
}

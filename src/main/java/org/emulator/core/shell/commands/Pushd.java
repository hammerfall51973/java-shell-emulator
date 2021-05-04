package org.emulator.core.shell.commands;


import org.emulator.core.shell.helpers.ErrorCheck;
import org.emulator.core.shell.helpers.FileSystem;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.interfaces.CommandInterface;
import java.util.Stack;


public class Pushd implements CommandInterface {
  /**
   * Creates a directory stack
   */
  public static Stack<String> directoryPathStack = new Stack<>();
  private static FileSystem fileSystem = FileSystem.getInstanceOfFileSystem();
  private static ErrorCheck errorCheck = new ErrorCheck();
  Pwd pwd;


  public Pushd(Pwd pwd) {
    this.pwd = pwd;
  }

  // normally
  public Pushd() {
    this.pwd = new Pwd();
  }

  private void runPushd(String path) {
    directoryPathStack.push(pwd.runPwd());
    fileSystem.traverse(path);
  }

  public String check(String[] arg) {
    String path = arg[1];

    if (!errorCheck.dirExists(fileSystem, path)) {
      StandardError.errors.add(
          "Error: Invalid path: the given path " + path + " does not exist\n");
      return "";
    }
    runPushd(path);
    return "";
  }
}

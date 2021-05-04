package org.emulator.core.shell.commands;


import org.emulator.core.shell.helpers.*;
import org.emulator.core.shell.interfaces.CommandInterface;

public class Find extends Redirection implements CommandInterface {
  private String list = "";


  Pwd pwd = new Pwd();

  private void find(String path, String type, String expression) {
    Directory storeDir = fileSystem.getDir();
    fileSystem.traverse(path);
    recurse(fileSystem.getDir(), type, expression);
    fileSystem.setDir(storeDir);
  }

  private void recurse(Directory currDir, String type, String expression) {
    if (type.equals("f")) {
      for (Node child : fileSystem.getDir().getChildren()) {
        if (child instanceof File) {
          File childFile = (File) child;
          if (childFile.getFileName().equals(expression)) {
            if (fileSystem.isRootDir()) {
              list += "/" + expression + " ";
            } else
              list += pwd.runPwd() + "/" + expression + " ";
          }
        } else {
          fileSystem.setDir((Directory) child);
          recurse((Directory) child, type, expression);
          fileSystem.setDir(currDir);
        }
      }
    } else {
      for (Node child : fileSystem.getDir().getChildren()) {
        if (child instanceof Directory) {
          Directory childDir = (Directory) child;
          if (childDir.getDirName().equals(expression)) {
            if (fileSystem.isRootDir()) {
              list += "/" + expression + " ";
            } else
              list += pwd.runPwd() + "/" + expression + " ";
          }
          fileSystem.setDir((Directory) child);
          recurse((Directory) child, type, expression);
          fileSystem.setDir(currDir);
        }
      }
    }
  }

  public String check(String[] arg) {
    int len = arg.length;
    if (!checkSyntax(arg, len)) {
      return "";
    }
    int i = 1;
    String output = "";
    while (!arg[i].equals("-type")) {
      if (!errorCheck.dirExists(fileSystem, arg[i])) {
        StandardError.errors
            .add("find: the path " + arg[i] + " does not exist\n");
      } else {
        find(arg[i], arg[len - 3],
            arg[len - 1].substring(1, arg[len - 1].length() - 1));
        output += arg[i] + ": " + list + "\n";
        list = "";
      }
      i++;
    }
    if (i == 1) {
      StandardError.errors.add("find: no path(s) specified\n");
      return "";
    }
    return output;
  }

  private boolean checkSyntax(String[] arg, int len) {
    if (!arg[len - 4].equals("-type")) {
      StandardError.errors
          .add("find: '-type' argument is missing/in the wrong order\n");
      return false;
    }
    if (!(arg[len - 3].equals("f") || arg[len - 3].equals("d"))) {
      StandardError.errors.add("find: please specify type of search (f/d)\n");
      return false;
    }
    if (!arg[len - 2].equals("-name")) {
      StandardError.errors
          .add("find: '-name' argument is missing/in the wrong order\n");
      return false;
    }
    if (!errorCheck.isProperString(arg[len - 1])) {
      return false;
    }
    return true;
  }
}

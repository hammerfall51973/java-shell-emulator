package org.emulator.core.shell.helpers;


import org.emulator.core.shell.commands.Pwd;


public class ErrorCheck {
  Pwd pwd = new Pwd();

  public boolean numArgCheck(String[] parsedInput) {
    String commands;
    if (parsedInput[0].equals("ls"))
      return true;
    if (parsedInput[0].equals("find") && parsedInput.length > 4)
      return true;
    if (parsedInput[0].equals("cat") && parsedInput.length > 1)
      return true;
    if (parsedInput[0].equals("mkdir") && parsedInput.length > 1)
      return true;
    if (parsedInput.length == 1) {
      commands = "exit speak tree pwd popd history";
    } else if (parsedInput.length == 2) {
      commands = "speak cd pushd history echo man rm curl save load";
    } else if (parsedInput.length == 3) {
      commands = "cp mv mkdir";
    } else {
      commands = "";
    }
    if (commands.contains(parsedInput[0])) {
      return true;
    }
    StandardError.errors.add("Error: Invalid arguments: please type 'man "
        + parsedInput[0] + "' to know more\n");
    return false;
  }


  public boolean invalidChar(String name) {
    String[] invalidChars = {"/", " ", "!", "@", "#", "$", "%", "^", "&", "*",
        "(", ")", "{", "}", "~", "|", "<", ">", "?", "\""};
    StringBuilder invalid = new StringBuilder("");

    for (String invalidChar : invalidChars) {
      if (name.contains(invalidChar)) {
        invalid.append(invalidChar).append(", ");
      }
    }

    if (invalid.length() == 0) {
      return false;
    }

    StandardError.errors
        .add("Error: Invalid character(s): A file/directory cannot have "
            + invalid.substring(0, invalid.length() - 2) + " in it's name\n");

    return true;

  }


  public boolean isProperString(String input) {
    if (input.length() < 2 || (input.charAt(0) != '"')
        || (input.charAt(input.length() - 1) != '"')) {
      StandardError.errors
          .add("Error: Strings need to be wrapped in double quotes\n");
      return false;
    }
    if (input.substring(1, input.length() - 1).contains("\"")) {
     StandardError.errors
          .add("Error: Strings cannot contain double quotes within them\n");
      return false;
    }
    return true;
  }

  public boolean dirExists(FileSystem tree, String path) {
    Directory storeDir = tree.getDir();
    boolean exists = tree.traverse(path);
    tree.setDir(storeDir);
    return exists;
  }

  public boolean fileExists(FileSystem tree, String fullPath) {
    Directory storeDir = tree.getDir();
    boolean exists = tree.traverse(getPath(fullPath));
    exists = exists && tree.getChildFile(getName(fullPath)) != null;
    tree.setDir(storeDir);
    return exists;
  }

  public boolean parentExists(FileSystem tree, String fullPath) {
    String path = this.getPath(fullPath);
    return this.dirExists(tree, path);
  }

  public String getName(String pathString) {
    String name;

    int position = pathString.lastIndexOf("/");
    if (position == -1) {
      name = pathString;
    } else if (position == 0) {
      name = pathString.substring(1);
    } else {
      name = pathString.substring(pathString.lastIndexOf("/") + 1);
    }
    return name;
  }


  public String getPath(String pathString) {
    String path;

    int position = pathString.lastIndexOf("/");
    if (position == -1) {
      path = pwd.runPwd();
    } else if (position == 0) {
      path = "/";
    } else {
      path = pathString.substring(0, pathString.lastIndexOf("/"));
    }
    return path;
  }
}

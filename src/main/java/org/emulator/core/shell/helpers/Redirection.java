package org.emulator.core.shell.helpers;



public class Redirection {


  protected static ErrorCheck errorCheck = new ErrorCheck();

  protected static FileSystem fileSystem = FileSystem.getInstanceOfFileSystem();


  private boolean checkErrors(String path, String content) {
    // check if dir exists
    if (errorCheck.dirExists(fileSystem, path)) {
      StandardError.errors
          .add("Error: a file and a directory cannot have duplicate names\n");
      return false;
    }

    // check if path is valid
    if (!errorCheck.parentExists(fileSystem, path)) {
     StandardError.errors.add(
          "Error: Invalid path: the directory " + path + " does not exist\n");
      return false;
    }

    // check if we should create a file instead
    if (!errorCheck.fileExists(fileSystem, path)) {
      if (!errorCheck.invalidChar(errorCheck.getName(path))) {
        create(path, content);
        return false;
      }
      return false;
    }

    return true;
  }


  public String append(String path, String content) {

    if (!checkErrors(path, content))
      return "";

    Directory storeDir = fileSystem.getDir();
    fileSystem.traverse(errorCheck.getPath(path));
    File file = fileSystem.getChildFile(errorCheck.getName(path));

    file.appendFileContent(content);

    fileSystem.setDir(storeDir);

    return "";
  }

  /**
   * Overwrites content to file at path
   *
   * @param path Path to a file
   * @param content Content to overwrite to a file
   */
  public String overwrite(String path, String content) {
    if (!checkErrors(path, content))
      return "";

    Directory storeDir = fileSystem.getDir();
    fileSystem.traverse(errorCheck.getPath(path));
    File file = fileSystem.getChildFile(errorCheck.getName(path));

    file.setFileContent(content);

    fileSystem.setDir(storeDir);

    return "";
  }

  private void create(String path, String content) {
    Directory storeDir = fileSystem.getDir();
    fileSystem.traverse(errorCheck.getPath(path));
    fileSystem.addChild(new File(errorCheck.getName(path), content));
    fileSystem.setDir(storeDir);
  }

}

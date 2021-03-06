package org.emulator.core.shell.commands;


import org.emulator.core.shell.helpers.*;
import org.emulator.core.shell.interfaces.CommandInterface;

public class Cp implements CommandInterface {

  protected static FileSystem fileSystem = FileSystem.getInstanceOfFileSystem();

  protected static ErrorCheck errorCheck = new ErrorCheck();

  Pwd pwd = new Pwd();
  /*
   * Get instance of Rm class to use method runRm to remove directories
   */
  Rm rm = new Rm();
  /*
   * Get instance of Mkdir class to use method runMkdir() to make directories
   */
  Mkdir mkdir = new Mkdir();

  /**
   * Copies the file objName at objParent to the file targetName at targetParent
   * 
   * @param objName name of file of OLDPATH
   * @param objParent path to objName
   * @param targetName name of file of NEWPATH
   * @param targetParent path to targetName
   */
  protected void copyFileToFile(String objName, String objParent,
      String targetName, String targetParent) {
    // Store current directory
    Directory storeDir = fileSystem.getDir();

    // Traverse to parent of object file
    fileSystem.traverse(objParent);

    // Get the objectFile
    File objectFile = fileSystem.getChildFile(objName);

    // Reset current directory
    fileSystem.setDir(storeDir);

    // Traverse to parent of target file
    fileSystem.traverse(targetParent);

    // Get the targetFile
    File targetFile = fileSystem.getChildFile(targetName);

    // Set the content of the target file to the content in the object file
    targetFile.setFileContent(objectFile.getFileContent());

    // Reset current directory
    fileSystem.setDir(storeDir);
  }


  protected void copyFileToDir(String objName, String objParent,
      String targetPath) {

    // Store current directory
    Directory storeDir = fileSystem.getDir();

    // Traverse to parent of object file
    fileSystem.traverse(objParent);

    // Get the objectFile
    File objectFile = fileSystem.getChildFile(objName);

    // Reset current directory
    fileSystem.setDir(storeDir);

    // Traverse to target directory
    fileSystem.traverse(targetPath);

    // if a file with the same name already exists in the target directory
    File possibleDup = fileSystem.getChildFile(objectFile.getFileName());
    if (possibleDup != null) {
      possibleDup.setFileContent(objectFile.getFileContent());

    } else {
      if (fileSystem.getChildDir(objectFile.getFileName()) != null ) {
        StandardError.errors.add("cp: cannot overwrite directory with file\n");
      }
      else {
     // Add file to target directory, but make a copy, not the same instance
        possibleDup =
            new File(objectFile.getFileName(), objectFile.getFileContent());
        fileSystem.addChild(possibleDup);
      }
    }

    // Reset current directory
    fileSystem.setDir(storeDir);
  }


  protected void copyDirToDir(String objectPath, String targetPath) {
    // Store current directory
    Directory storeDir = fileSystem.getDir();
    fileSystem.traverse(targetPath);
    String targetFullPath = pwd.runPwd();
    fileSystem.setDir(storeDir);
    fileSystem.traverse(objectPath);
    String objectFullPath = pwd.runPwd();
    for (Node child : fileSystem.getDir().getChildren()) {
      if (child instanceof File) {
        File objFile = (File) child;
        copyFileToDir(objFile.getFileName(), objectFullPath, targetFullPath);
      } else {
        recurse((Directory) child, targetFullPath);
      }
    }
    fileSystem.setDir(storeDir);
  }

  private void recurse(Directory objDir, String targetParent) {

    String dirName = objDir.getDirName();
    fileSystem.traverse(targetParent);
    fileSystem.addChild(dirName);
    fileSystem.setDir(objDir);

    for (Node child : objDir.getChildren()) {
      if (child instanceof File) {
        File objFile = (File) child;
        if (targetParent.equals("/")) {
          copyFileToDir(objFile.getFileName(), pwd.runPwd(),
              targetParent + dirName);
        }
        copyFileToDir(objFile.getFileName(), pwd.runPwd(),
            targetParent + "/" + dirName);
      } else {
        if (targetParent.equals("/")) {
          recurse((Directory) child, targetParent + dirName);
        } else {
          recurse((Directory) child, targetParent + "/" + dirName);
        }
        fileSystem.setDir(objDir);
      }
    }
  }


  public String check(String[] arg) {
    boolean objFileExists = errorCheck.fileExists(fileSystem, arg[1]);
    boolean objDirExists = errorCheck.dirExists(fileSystem, arg[1]);

    if (!(objFileExists || objDirExists)) {
      StandardError.errors
          .add("cp: file/directory to be copied does not exist\n");
      return "";
    }
    // checks if target path exists
    else if (!errorCheck.parentExists(fileSystem, arg[2])) {
      StandardError.errors.add("cp: Target path parent does not exist\n");
      return "";
    }

    // checks if object path exists and calls relevant methods
    if (objDirExists)
      checkDir(arg[1], arg[2]);
    else if (objFileExists)
      checkFile(arg[1], arg[2]);
    return "";
  }

  private void checkDir(String objDir, String targetPath) {
    if (errorCheck.fileExists(fileSystem, targetPath)) {
      StandardError.errors.add("cp: Cannot copy a directory into a file\n");
    } else if (errorCheck.dirExists(fileSystem, targetPath)) {
      if (!checkSubDir(objDir, targetPath)) {
        if (targetPath.equals("/") && objDir.charAt(0) != '/' && objDir.lastIndexOf('/') != -1) {
          return;
        }
        String objName = errorCheck.getName(objDir);
        String newPath;
        if (targetPath.equals("/")) {
          newPath = targetPath + objName;
        } else
          newPath = targetPath + "/" + objName;
        if (errorCheck.dirExists(fileSystem, newPath)) {
          String[] remove = {"rm", newPath};
          rm.check(remove);
        }
        makeDir(newPath);
        copyDirToDir(objDir, newPath);
      }
    } else if (!checkSubDir(objDir, errorCheck.getPath(targetPath))) {
      makeDir(targetPath);
      copyDirToDir(objDir, targetPath);
    }
  }

  private void checkFile(String objFile, String targetPath) {

    String objName = errorCheck.getName(objFile);
    String objParent = errorCheck.getPath(objFile);

    if (errorCheck.fileExists(fileSystem, targetPath)) {
      if (objParent.equals(errorCheck.getPath(targetPath))
          && objName.equals(errorCheck.getName(targetPath))) {
        StandardError.errors.add("cp: '" + objFile + "' and '" + targetPath
            + "' are identical (not copied)\n");
        return;
      } else
        copyFileToFile(objName, objParent, errorCheck.getName(targetPath),
            errorCheck.getPath(targetPath));
    } else if (errorCheck.dirExists(fileSystem, targetPath)) {
      copyFileToDir(objName, objParent, targetPath);
    } else {
      makeFile(targetPath);
      copyFileToFile(objName, objParent, errorCheck.getName(targetPath),
          errorCheck.getPath(targetPath));
    }
  }

  private boolean checkSubDir(String objDir, String targetDir) {
    Directory storeDir = fileSystem.getDir();
    fileSystem.traverse(targetDir);
    String targetFullPath = pwd.runPwd();
    fileSystem.setDir(storeDir);
    fileSystem.traverse(objDir);
    String objFullPath = pwd.runPwd();
    fileSystem.setDir(storeDir);
    if (objFullPath.equals("/") || (targetFullPath.startsWith(objFullPath)
        && targetFullPath.charAt(objFullPath.length()) == '/')) {
      StandardError.errors.add("cp: Cannot copy a directory into its child\n");
      return true;
    }
    return false;
  }


  protected void makeDir(String newDir) {
    String[] arr = {"mkdir", newDir};
    mkdir.check(arr);
  }

  protected void makeFile(String newFile) {
    Directory storeDir = fileSystem.getDir();
    fileSystem.traverse(errorCheck.getPath(newFile));
    fileSystem.addChild(new File(errorCheck.getName(newFile), ""));
    fileSystem.setDir(storeDir);
  }
}

package org.emulator.core.shell.commands;


import org.emulator.core.shell.helpers.Directory;
import org.emulator.core.shell.helpers.File;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.interfaces.CommandInterface;


public class Mv extends Cp implements CommandInterface {

  Pwd pwd = new Pwd();

  private void moveFileToDir(String objName, String objParent,
      String targetPath) {
    // Gets file and traverses to parent
    Directory storeDir = fileSystem.getDir();
    super.copyFileToDir(objName, objParent, targetPath);
    fileSystem.traverse(objParent);
    // Gets the file and removes it from original location
    File toDel = fileSystem.getChildFile(objName);
    fileSystem.getDir().getChildren().remove(toDel);
    fileSystem.setDir(storeDir);
  }

  private void moveFileToFile(String objName, String objParent,
      String targetName, String targetParent) {
    // Gets file and traverses to parent
    Directory storeDir = fileSystem.getDir();
    super.copyFileToFile(objName, objParent, targetName, targetParent);
    fileSystem.traverse(objParent);
    // Gets the file and removes it from original location
    File toDel = fileSystem.getChildFile(objName);
    fileSystem.getDir().getChildren().remove(toDel);
    fileSystem.setDir(storeDir);
  }

  private void moveDirToDir(String objectPath, String targetPath) {
    // Uses parent function to copy directories
    super.copyDirToDir(objectPath, targetPath);
    // Removes the file from original location
    String[] rmInput = {"rm", objectPath};
    rm.check(rmInput);
  }

  public String check(String[] arg) {
    // Checks if the file/dir exists
    boolean objFileExists = errorCheck.fileExists(fileSystem, arg[1]);
    boolean objDirExists = errorCheck.dirExists(fileSystem, arg[1]);

    // Error message in the case that the first argument path doesn't exist
    if (!(objFileExists || objDirExists)) {
      StandardError.errors
          .add("mv: file/directory to be moved does not exist\n");
      return "";
    }

    else if (!errorCheck.parentExists(fileSystem, arg[2])) {
      StandardError.errors.add("mv: Target path parent does not exist\n");
      return "";
    }

    // Creates directory or file if target dir/file do not exist
    if (objDirExists)
      checkDir(arg[1], arg[2]);
    else if (objFileExists)
      checkFile(arg[1], arg[2]);
    return "";
  }

  private void checkDir(String objDir, String targetPath) {
    // Checks if the file exists in the path
    if (errorCheck.fileExists(fileSystem, targetPath)) {
      StandardError.errors.add("mv: Cannot move a directory into a file\n");
    } else if (errorCheck.dirExists(fileSystem, targetPath)) {
      if (!checkSubDir(objDir, targetPath)) {
        if (targetPath.equals("/") && objDir.charAt(0) != '/') {
          String[] rmInput = {"rm", objDir};
          rm.check(rmInput);
          return;
        }
        String objName = errorCheck.getName(objDir);
        String newPath;
        // Initializes newPath to the correct path
        if (targetPath.equals("/")) {
          newPath = targetPath + objName;
        } else
          newPath = targetPath + "/" + objName;
        if (errorCheck.dirExists(fileSystem, newPath)) {
          String[] remove = {"rm", newPath};
          rm.check(remove);
        }
        // Makes the directory and moves the directory
        super.makeDir(newPath);
        moveDirToDir(objDir, newPath);
      }
    } else if (!checkSubDir(objDir, errorCheck.getPath(targetPath))) {
      super.makeDir(targetPath);
      moveDirToDir(objDir, targetPath);
    }
  }

  private void checkFile(String objFile, String targetPath) {
    String objName = errorCheck.getName(objFile);
    String objParent = errorCheck.getPath(objFile);
    // Checks if the file exists
    if (errorCheck.fileExists(fileSystem, targetPath)) {
      if (objParent.equals(errorCheck.getPath(targetPath))
          && objName.equals(errorCheck.getName(targetPath))) {
        StandardError.errors.add("mv: '" + objFile + "' and '" + targetPath
            + "' are identical (not moved)\n");
        return;
      } else
        // Calls the move file to file function if the target is a file
        moveFileToFile(objName, objParent, errorCheck.getName(targetPath),
            errorCheck.getPath(targetPath));
    } else if (errorCheck.dirExists(fileSystem, targetPath)) {
      // Calls the move file to dir if target is a dir
      moveFileToDir(objName, objParent, targetPath);
    } else {
      super.makeFile(targetPath);
      moveFileToFile(objName, objParent, errorCheck.getName(targetPath),
          errorCheck.getPath(targetPath));
    }
  }

  private boolean checkSubDir(String objDir, String targetDir) {
    // Performs traversing to the path
    Directory storeDir = fileSystem.getDir();
    fileSystem.traverse(targetDir);
    String targetFullPath = pwd.runPwd();
    fileSystem.setDir(storeDir);
    fileSystem.traverse(objDir);
    String objFullPath = pwd.runPwd();
    fileSystem.setDir(storeDir);
    // Checks if the directory is being moved into its own child
    if (objFullPath.equals("/") || (targetFullPath.startsWith(objFullPath)
        && targetFullPath.charAt(objFullPath.length()) == '/')) {
      StandardError.errors.add("mv: Cannot move a directory into its child\n");
      return true;
    }
    return false;
  }
}

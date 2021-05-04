package org.emulator.core.shell.commands;

import org.emulator.core.shell.helpers.ErrorCheck;
import org.emulator.core.shell.helpers.File;
import org.emulator.core.shell.helpers.FileSystem;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.interfaces.CommandInterface;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Save implements CommandInterface, Serializable {
  private static final long serialVersionUID = 1L;
  /*
   * Get instance of Pwd class to use method runPwd() to get the current working
   * directory
   */
  Pwd pwd = new Pwd();

  private void writeFunction(String fileToWrite) {
    String fileName = fileToWrite;
    // Serialization
    try {
      // Saving of object in a file
      FileOutputStream file = new FileOutputStream(fileName);
      ObjectOutputStream out = new ObjectOutputStream(file);

      // History Stack
      out.writeObject(History.historyList);
      // Push/Pop Stack
      out.writeObject(Pushd.directoryPathStack);
      // Current Directory
      String currWorkingDir = pwd.runPwd();
      out.writeObject(currWorkingDir);
      // Get into root directory
      Cd cd = new Cd();
      String[] input = {"cd", "/"};
      cd.check(input);
      // FileSystem Structure
      Ls ls = new Ls();
      String[] lsInput = {"ls", "-R", "/"};
      String fileSystem = ls.check(lsInput);
      out.writeObject(fileSystem);

      // Traversal through FileSystem and adding files and file content
      // to hashmap
      List<String> tree = Arrays.asList(fileSystem.split("\\s+"));
      HashMap<String, String> fileMap = new HashMap<String, String>();

      saveFileNamesAndContent(tree, fileMap, cd);

      // HashMap of Files
      out.writeObject(fileMap);
      input[1] = currWorkingDir;
      cd.check(input);

      out.close();
      file.close();
    } catch (IOException ex) {
      StandardError.errors.add("Error: Invalid Path\n");
    }
  }

  private void saveFileNamesAndContent(List<String> tree,
      HashMap<String, String> fileMap,Cd cd) {

    ErrorCheck fileCheck = new ErrorCheck();
    FileSystem fs = FileSystem.getInstanceOfFileSystem();
    String fullPath = tree.get(0).substring(0, tree.get(0).length() - 1);
    String filePath;
    for (int i = 1; i < tree.size(); i++) {
      // Check if element is directory path
      if (!tree.get(i).contains("/")) {
        // Check to avoid double backslash
        if (fullPath.equals("/")) {
          filePath = fullPath + tree.get(i);
        } else {
          filePath = fullPath + "/" + tree.get(i);
        }

        // Check if subelement is file
        if (fileCheck.fileExists(fs, filePath)) {
          File oldFile = fs.getChildFile(tree.get(i));
          // Add file path and file contents to hashmap
          fileMap.put(filePath, oldFile.getFileContent());
        }
      } else {
        // If element is a directory, cd into directory
        fullPath = tree.get(i).substring(0, tree.get(i).length() - 1);
        String[] cdInput = {"cd", fullPath};
        cd.check(cdInput);
      }
    }
  }


  public String check(String[] arg) {
    writeFunction(arg[1]);
    return "";
  }
}


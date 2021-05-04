package org.emulator.core.shell.commands;

import org.emulator.core.shell.helpers.Parser;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.interfaces.CommandInterface;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * Class Name: Load This class loads a save file and changes the state of the
 * terminal
 */
public class Load implements CommandInterface {


  public void readFunction(String fileToRead) {
    String fileName = fileToRead;
    // Serialization
    try {
      // Saving of object in a file
      FileInputStream file = new FileInputStream(fileName);
      ObjectInputStream in = new ObjectInputStream(file);

      // History Stack
      History.historyList = (ArrayList<String>) in.readObject();
      // Push/Pop Stack
      Pushd.directoryPathStack = (Stack<String>) in.readObject();
      // Current Directory
      String currWorkingDir = (String) in.readObject();
      // File System Tree
      String fileSystem = (String) in.readObject();
      List<String> tree = Arrays.asList(fileSystem.split("\\s+"));
      // HashMap for Files
      HashMap<String, String> fileMap = new HashMap<String, String>();
      fileMap = (HashMap<String, String>) in.readObject();

      makeFileAndDir(tree, fileMap);

      // Cd into the original working directory
      Cd cd = new Cd();
      String[] cdInput = {"cd", currWorkingDir};
      cd.check(cdInput);

      in.close();
      file.close();
    } catch (IOException ex) {
      StandardError.errors.add("Error: File Not Found\n");
    } catch (ClassNotFoundException ex) {
      StandardError.errors.add("Error: File Not Compatible For Loading\n");
    }
  }

  private void makeFileAndDir(List<String> tree,
      HashMap<String, String> fileMap) {
    Echo echo = new Echo();
    Mkdir mkdir = new Mkdir();
    // Create files or directories respectively
    String fullPath = tree.get(0).substring(0, tree.get(0).length() - 1);
    String filePath;
    for (int i = 1; i < tree.size(); i++) {
      // Check if element is directory path
      if (!tree.get(i).contains("/")) {
        // Avoid issues with double slash
        if (fullPath.equals("/")) {
          filePath = fullPath + tree.get(i);
        } else {
          filePath = fullPath + "/" + tree.get(i);
        }

        // Check if subelement is a file based on if key is in hashmap
        if (fileMap.containsKey(filePath)) {
          // Use echo to create a file
          echo.append(filePath, fileMap.get(filePath));
        }
      } else {
        // If element is directory path, create that directory
        fullPath = tree.get(i).substring(0, tree.get(i).length() - 1);;
        String[] mkdirInput = {"mkdir", fullPath};
        mkdir.check(mkdirInput);
      }
    }
  }

  public String check(String[] arg) {
    if (Parser.canUseLoad) {
      readFunction(arg[1]);
    } else {
      StandardError.errors
          .add("Error: Load can only be called if it is the first command\n");
    }
    return "";
  }
}

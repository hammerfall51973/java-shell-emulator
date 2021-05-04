package org.emulator.core.shell.helpers;


public class FileSystem {

  // For singleton design
  private static FileSystem fs;

  // Create a root directory '/' and set current directory to it
  private Directory currDir;

  /**
   * Default constructor
   */
  protected FileSystem() {
    // Create a root directory '/' and set current directory to it
    currDir = new Directory("/");
  }

  /**
   * Gets instance of file system to support multiple shells running on the same
   * file system
   * 
   * @return FileSystem instance of file system
   */
  public static FileSystem getInstanceOfFileSystem() {
    // Check if a file system instance already exists
    if (fs == null) {

      // Create an instance if it doesn't exist
      fs = new FileSystem();
    }

    // Else return the instance
    return fs;
  }

  // Getters and Setters

  /**
   * Gets current directory
   * 
   * @return currDir Current directory
   */
  public Directory getDir() {
    return currDir;
  }


  public void setDir(Directory newDir) {
    currDir = newDir;
  }


  public Directory getChildDir(String dirName) {
    // Loop over the list of children
    for (Node child : currDir.getChildren()) {

      // Check if name of child matches parameter and child is of Directory type
      if (child instanceof Directory
          && ((Directory) child).getDirName().equals(dirName)) {

        // Return the child
        return (Directory) child;
      }
    }

    // Else return null
    return null;
  }


  public void addChild(String name) {
    // Create a directory and set its name with the input parameter
    Directory childDir = new Directory(name);

    // Add created directory to list of children of current directory
    currDir.getChildren().add(childDir);

    // Add current directory as parent of created directory to allow two-way
    // traversal
    childDir.setParent(currDir);
  }


  public void addChild(File file) {
    currDir.getChildren().add(file);
  }

  public File getChildFile(String fileName) {
    // Loop through the children of the current directory
    for (Node child : currDir.getChildren()) {

      // Check if child is of type File and its name matches the parameter
      if (child instanceof File
          && ((File) child).getFileName().equals(fileName)) {
        // Return the child
        return (File) child;
      }

    }

    return null;
  }


  public boolean traverse(String path) {
    // Store the current directory, because we might have to traverse back if
    // path doesn't exist
    Directory startDir = currDir;

    // Create boolean to store the result
    boolean exists;

    // Trivial case, root directory always exists
    if (path.equals("/")) {
      this.cdRoot();
      return true;
    }

    if (path.contains("//")) {
      StandardError.errors
          .add("Error: Cannot have consecutive slashes in a path name\n");
      return false;
    }
    // Create an array to store the directories of the path
    String[] directories;

    // Check if path is absolute
    if (path.startsWith("/")) {

      // Traverse to the root, so we can now treat the rest of the path as
      // relative as well
      this.cdRoot();

      // Trim the path based on the backslash delimiter
      directories = path.trim().substring(1).split("/");
    } else {
      directories = path.trim().split("/");
    }

    // Call helper function to traverse to the directory
    exists = traverseHelper(directories);

    // Revert to stored directory if path doesn't exist
    if (!exists) {
      this.currDir = startDir;
    }
    // return result
    return exists;
  }


  private boolean traverseHelper(String[] directories) {
    // Loop through the array, each element being a sub directory of the
    // preceding element
    for (String directory : directories) {

      // Check for '..' case
      if (directory.equals("..")) {
        // traverse to parent
        this.cdParent();
      }

      // Else, check if child exists by calling helper method, which already
      // does the traversing if the child exists
      else if (!this.cdChild(directory)) {
        return false;
      }
    }
    return true;
  }


  public boolean isRootDir() {
    return currDir.getDirName().equals("/");
  }

  protected void cdRoot() {
    // Keep traversing to parent till you reach the root directory
    while (!this.isRootDir()) {
      this.cdParent();
    }
  }


  public void cdParent() {
    // Check if already at root directory
    if (!this.isRootDir()) {
      this.setDir(currDir.getParent());
    }
  }


  private boolean cdChild(String childName) {
    // Check for '.' case which is always true, no traversal needed
    if (childName.equals(".")) {
      return true;
    }

    // Check if a sub directory with the given parameter as a name exists
    if (this.getChildDir(childName) != null) {
      // traverse to the sub directory
      this.setDir(this.getChildDir(childName));

      return true;
    }
    return false;
  }
}

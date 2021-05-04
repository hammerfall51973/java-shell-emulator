package org.emulator.core.shell.helpers;

import java.util.ArrayList;


public class Directory extends Node {


  private Directory parent;

  private final ArrayList<Node> children = new ArrayList<>();

  public Directory(String dirName) {
    setDirName(dirName);
  }


  protected Directory getParent() {
    return parent;
  }


  protected void setParent(Directory parent) {
    this.parent = parent;
  }


  public String getDirName() {
    return super.getName();
  }


  private void setDirName(String dirName) {
    super.setName(dirName);
  }


  public ArrayList<Node> getChildren() {
    return children;
  }
}

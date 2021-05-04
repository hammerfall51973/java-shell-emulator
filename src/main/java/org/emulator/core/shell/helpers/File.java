package org.emulator.core.shell.helpers;


public class File extends Node {


  private String fileContent;


  public File(String fileName, String fileContent) {
    super.setName(fileName);
    setFileContent(fileContent);
  }


  public String getFileName() {
    return super.getName();
  }


  public void setFileName(String fileName) {
    super.setName(fileName);
  }


  public String getFileContent() {
    return fileContent;
  }


  public void setFileContent(String fileContent) {
    this.fileContent = fileContent;
  }


  public void appendFileContent(String fileContent) {
    if (this.fileContent != null) {
      this.fileContent += "\n" + fileContent;
    } else {
      setFileContent(fileContent);
    }
  }

}

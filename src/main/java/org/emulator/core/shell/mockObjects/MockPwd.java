package org.emulator.core.shell.mockObjects;

import org.emulator.core.shell.commands.Pwd;


public class MockPwd extends Pwd {
  MockFileSystem fs;


  public MockPwd(MockFileSystem fs) {
    this.fs = fs;
  }

  public String runPwd() {
    return fs.currPwd;
  }
}

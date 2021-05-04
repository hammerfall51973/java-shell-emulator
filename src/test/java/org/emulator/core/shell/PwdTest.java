package org.emulator.core.shell;

import org.emulator.core.shell.commands.Cd;
import org.emulator.core.shell.commands.Pwd;
import org.emulator.core.shell.helpers.Redirection;
import org.emulator.core.shell.mockObjects.MockErrorCheck;
import org.emulator.core.shell.mockObjects.MockFileSystem;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class PwdTest {
  String output, expected;

  Cd cd;
  Pwd pwd;
  Field field;
  MockFileSystem file;
  MockErrorCheck error;
  String[] inputPwd = {"pwd"};

  @Before
  public void setUp() throws Exception {
    cd = new Cd();
    file = new MockFileSystem();
    error = new MockErrorCheck();
    pwd = new Pwd();

    field = (Redirection.class.getDeclaredField("fileSystem"));
    field.setAccessible(true);
    field.set(file, file);

    field = (Redirection.class.getDeclaredField("errorCheck"));
    field.setAccessible(true);
    field.set(error, error);
  }

  @Test
  public void testRootDir() {
    output = pwd.runPwd();
    expected = "/";
    assertEquals(expected, output);
  }

  @Test
  public void testNormalPath() {
    boolean out = file.traverse("/dir1");
    boolean expect = true;
    assertEquals(expect, out);
  }
}

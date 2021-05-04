package org.emulator.core.shell;

import org.emulator.core.shell.commands.Cd;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.mockObjects.MockErrorCheck;
import org.emulator.core.shell.mockObjects.MockFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class CdTest {
  String output, expected;

  Cd cd;
  Field field;
  MockFileSystem file;
  MockErrorCheck error;

  @Before
  public void setUp() throws Exception {
    cd = new Cd();
    file = new MockFileSystem();
    error = new MockErrorCheck();

    /*
     * Use the mock fileSystem and errorCheck
     */

    field = (Cd.class.getDeclaredField("fileSystem"));
    field.setAccessible(true);
    field.set(file, file);

    field = (Cd.class.getDeclaredField("errorCheck"));
    field.setAccessible(true);
    field.set(error, error);
  }

  @After
  public void tearDown() throws Exception {
    StandardError.errors.clear();
  }

  @Test
  public void testCurrDir() {
    cd.check(new String[] {"cd", "doesntExist"});
    output = file.currPwd;
    expected = "/";
    assertEquals(expected, output);
  }

  @Test
  public void testDoesntExist() {
    cd.check(new String[] {"cd", "doesntExist"});
    output = file.currPwd;
    expected =
        "Error: Invalid directory: the directory doesntExist does not exist\n";
    assertEquals(expected, StandardError.errors.get(0));
  }

  @Test
  public void testToRoot() {
    cd.check(new String[] {"cd", "/"});
    output = file.currPwd;
    expected = "/";
    assertEquals(expected, output);
  }

  @Test
  public void testNormal() {
    cd.check(new String[] {"cd", "dir1"});
    output = file.currPwd;
    expected = "/dir1/";
    assertEquals(expected, output);
  }

  @Test
  public void testSimpleRootPath() {
    cd.check(new String[] {"cd", "/dir1"});
    output = file.currPwd;
    expected = "/dir1/";
    assertEquals(expected, output);
  }

  @Test
  public void testCdUp() {
    cd.check(new String[] {"cd", "dir1"});
    cd.check(new String[] {"cd", ".."});
    output = file.currPwd;
    expected = "/";
    assertEquals(expected, output);
  }

  @Test
  public void testContinousSlashes() {
    String inputCd[] = {"cd", "//////"};
    cd.check(inputCd);
    expected =
        "Error: Invalid directory: the directory ////// does not exist\n";
    assertEquals(expected, StandardError.errors.get(0));
  }

  @Test
  public void testCdUpDown() {
    cd.check(new String[] {"cd", "../../dir1"});
    output = file.currPwd;
    expected = "/dir1/";
    assertEquals(expected, output);
  }
}

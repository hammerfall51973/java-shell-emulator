package org.emulator.core.shell;

import org.emulator.core.shell.commands.Cat;
import org.emulator.core.shell.helpers.Redirection;
import org.emulator.core.shell.mockObjects.MockErrorCheck;
import org.emulator.core.shell.mockObjects.MockFileSystem;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import static org.junit.Assert.assertEquals;


public class CatTest {
  String output, expected;

  Cat cat;
  MockFileSystem file;
  MockErrorCheck error;
  String[] inputCat;

  @Before
  public void setUp() throws Exception {
    cat = new Cat();
    error = new MockErrorCheck();
    file = new MockFileSystem();
    output = "";
    expected = "";

    Field field = (Redirection.class.getDeclaredField("fileSystem"));
    field.setAccessible(true);
    field.set(file, file);

    field = (Redirection.class.getDeclaredField("errorCheck"));
    field.setAccessible(true);
    field.set(error, error);
  }

  @Test
  public void testNoFile() {
    String[] inputCat = {"cat", "notAFile"};
    output = cat.check(inputCat).trim();
    expected = "";
    assertEquals(expected, output);
  }

  @Test
  public void testExistingFile() {
    String[] inputCat = {"cat", "rootFile1"};
    output = cat.check(inputCat).trim();

    expected = "inside rootFile1";
    assertEquals(expected, output);
  }

  @Test
  public void testMultipleValidFiles() {
    // Cat a file that does exist
    String[] inputCat = {"cat", "rootFile1", "rootFile2"};
    output = cat.check(inputCat).trim();
    expected = "inside rootFile1\n\ninside rootFile2";
    assertEquals(expected, output);
  }

  @Test
  public void testMultipleMixedFiles() {
    // Cat a file that does exist
    String[] inputCat = {"cat", "rootFile2", "notAFile", "rootFile3"};
    output = cat.check(inputCat).trim();

    expected = "inside rootFile2\n\ninside rootFile3";
    assertEquals(expected, output);
  }

  @Test
  public void testMultipleMixedFilesWithFullPath() {
    // Cat a file that does exist
    String[] inputCat =
        {"cat", "/rootFile2", "/notAFile", "/rootFile3", "/dir1/dir1File1"};
    output = cat.check(inputCat).trim();

    expected = "inside rootFile2\n\ninside rootFile3\n\ninside dir1File1";
    assertEquals(expected, output);
  }

}

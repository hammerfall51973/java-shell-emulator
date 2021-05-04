package org.emulator.core.shell;

import org.emulator.core.shell.commands.Cd;
import org.emulator.core.shell.commands.Ls;
import org.emulator.core.shell.helpers.FileSystem;
import org.emulator.core.shell.helpers.Redirection;
import org.emulator.core.shell.helpers.StandardError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class LsTest {
  String output, expected;

  Ls ls;
  Cd cd;
  FileSystem file;
  Field field;

  @Before
  public void setUp() throws Exception {
    ls = new Ls();

    file = FileSystem.getInstanceOfFileSystem();

    field = (Redirection.class.getDeclaredField("fileSystem"));
    field.setAccessible(true);
    field.set(file, file);

    output = "";
    expected = "";
  }

  @After
  public void tearDown() throws Exception {
    field = (FileSystem.class.getDeclaredField("fs"));
    field.setAccessible(true);
    field.set(null, null); // setting the ref parameter to null
    StandardError.errors.clear();
  }

  @Test
  public void testEmptyDirectory() {
    output = ls.check(new String[] {"ls"}).trim();
    expected = "";
    assertEquals(expected, output);
  }

  @Test
  public void testDirectoriesInRoot() {
    file.addChild("a");
    file.addChild("c");
    output = ls.check(new String[] {"ls"}).trim();
    expected = "a\nc";
    assertEquals(expected, output);
  }

  @Test
  public void testRecursive() {
    file.addChild("a");
    file.addChild("c");
    file.traverse("a");
    file.addChild("a1");
    ls.append("c/file", "cfile");
    file.traverse("/");
    output = ls.check(new String[] {"ls", "-R"}).trim();
    expected =
        "/: a c\n" + "\n" + "/a: a1\n" + "\n" + "/a/a1:\n" + "\n" + "/c:";
    assertEquals(expected, output);
  }

  @Test
  public void testRecursiveWithPath() {
    file.addChild("a");
    file.addChild("c");
    file.traverse("a");
    file.addChild("a1");
    output = ls.check(new String[] {"ls", "-R"}).trim();
    expected = "/a: a1\n" + "\n" + "/a/a1:";
    assertEquals(expected, output);
  }

  @Test
  public void testMultiplePaths() {
    file.addChild("d");
    file.addChild("e");
    file.addChild("f");
    file.addChild("g");
    output = ls.check(new String[] {"ls", "d", "e", "f", "g"}).trim();
    expected = "/d:\n" + "\n" + "/e:\n" + "\n" + "/f:\n" + "\n" + "/g:";
    assertEquals(expected, output);
  }

  @Test
  public void testTraverseThenLs() {
    file.addChild("a");
    file.traverse("a");
    file.addChild("a1");
    file.traverse("a1");
    file.addChild("a2");
    file.traverse("/a");
    output = ls.check(new String[] {"ls"}).trim();
    expected = "a1";
    assertEquals(expected, output);
  }
}

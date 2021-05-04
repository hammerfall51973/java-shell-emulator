package org.emulator.core.shell;

import org.emulator.core.shell.commands.Mkdir;
import org.emulator.core.shell.helpers.FileSystem;
import org.emulator.core.shell.helpers.StandardError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import static org.junit.Assert.assertEquals;

public class MkdirTest {


  Mkdir mkdir;
  FileSystem fs;
  Field field;

  @Before
  public void setUp() throws Exception {
    mkdir = new Mkdir();

    fs = FileSystem.getInstanceOfFileSystem();

    field = (Mkdir.class.getDeclaredField("fileSystem"));
    field.setAccessible(true);
    field.set(fs, fs);

  }

  @After
  public void tearDown() throws Exception {
    field = (FileSystem.class.getDeclaredField("fs"));
    field.setAccessible(true);
    field.set(null, null); // setting the ref parameter to null
    StandardError.errors.clear();
  }

  @Test
  public void testCreateDirectory() {
    mkdir.check(new String[] {"mkdir", "dir"});
    // check if working directory remains the same
    assertEquals(true, fs.isRootDir());
    // check if directory was created
    assertEquals(true, fs.traverse("dir"));
  }

  @Test
  public void testMultipleInput() {
    mkdir.check(new String[] {"mkdir", "dir", "/dir/dir1", "dir/dir1/dir2"});
    assertEquals(true, fs.traverse("dir/dir1/dir2"));
  }

  @Test
  public void testCreateDuplicateDirectories() {
    mkdir.check(new String[] {"mkdir", "dir"});
    mkdir.check(new String[] {"mkdir", "dir", "dir1", "dir/ko", "dir/ko/bop"});
    assertEquals(StandardError.errors.get(0), "Error: 'dir' already exists\n");
  }

  @Test
  public void testCreateDirectoryWithInvaalidParent() {
    mkdir.check(new String[] {"mkdir", "dir/dir1"});
    assertEquals(StandardError.errors.get(0),
        "Error: Invalid path: the parent directory dir does not exist\n");
  }

  @Test
  public void testInvalidCharacter() {
    mkdir.check(new String[] {"mkdir", "dir@"});
    assertEquals(StandardError.errors.get(0),
        "Error: Invalid character(s): A file/directory cannot have @ in it's name\n");
  }


}

package org.emulator.core.shell.helpers;

import java.util.ArrayList;


public class StandardError {

  /**
   * Creates an array list for errors that go to stdErr
   */
  public static ArrayList<String> errors = new ArrayList<String>();

  /**
   * Print out all the error messages to stdErr
   */
  public static void printErrors() {
    for (int i = 0; i < errors.size(); i++) {
      // Does not use .err b/c of spacing problems
      System.out.print(errors.get(i));
    }
    errors.clear();
  }
}

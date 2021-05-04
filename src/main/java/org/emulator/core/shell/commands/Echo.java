package org.emulator.core.shell.commands;


import org.emulator.core.shell.helpers.Redirection;
import org.emulator.core.shell.interfaces.CommandInterface;

public class Echo extends Redirection implements CommandInterface {

  // Parse the quotes
  private String runEcho(String output) {
    return output.substring(1, output.length() - 1);
  }


  public String check(String[] arg) {
    if (!errorCheck.isProperString(arg[1])) {
      return "";
    }

    if (arg.length == 2) {
      return runEcho(arg[1]) + "\n";
    }

    return "";

  }
}

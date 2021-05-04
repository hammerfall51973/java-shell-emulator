package org.emulator.core.shell.commands;


import org.emulator.core.shell.helpers.Redirection;
import org.emulator.core.shell.helpers.StandardError;
import org.emulator.core.shell.interfaces.CommandInterface;
import java.util.ArrayList;


public class History extends Redirection implements CommandInterface {

  public static ArrayList<String> historyList = new ArrayList<>();

  /**
   * Outputs the list of previous commands in the history stack
   */
  private String runHistory() {
    String output = "";
    for (int i = 0; i < historyList.size(); i++) {
      output += (i + 1) + ". " + historyList.get(i) + "\n";
    }

    return output;
  }

  /**
   * Outputs the n-last commands to print from the history stack
   * 
   * @param n Determines the n-last commands
   */
  private String runHistory(int n) {
    String output = "";
    if (n <= historyList.size()) {

      for (int i = historyList.size() - n; i < historyList.size(); i++) {
        output += (i + 1) + ". " + historyList.get(i) + "\n";
      }

    } else {
      return runHistory();
    }

    return output;
  }


  public String check(String[] arg) {
    if (arg.length == 2) {
      try {
        return runHistory(Integer.parseInt(arg[1]));
      } catch (NumberFormatException nfe) {
        StandardError.errors
            .add("Error: Invalid argument: you did not enter an integer\n");
        return "";
      }
    }
    return runHistory() + "\n";
  }
}

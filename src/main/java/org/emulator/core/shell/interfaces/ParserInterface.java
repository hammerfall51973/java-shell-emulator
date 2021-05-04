package org.emulator.core.shell.interfaces;

import org.emulator.core.shell.commands.*;
import org.emulator.core.shell.helpers.Redirection;

import java.util.HashMap;
import java.util.Map;


public interface ParserInterface {


  Speak speak = new Speak();

  Map<String, CommandInterface> commandsTable =
      new HashMap<String, CommandInterface>() {
        {
          put("speak", speak);
          put("mkdir", new Mkdir());
          put("cd", new Cd());
          put("ls", new Ls());
          put("history", new History());
          put("cat", new Cat());
          put("echo", new Echo());
          put("man", new Man());
          put("cp", new Cp());
          put("rm", new Rm());
          put("save", new Save());
          put("load", new Load());
          put("pwd", new Pwd());
          put("find", new Find());
          put("mv", new Mv());
        }
      };


  Map<String, Redirection> stdOutCommandsTable =
      new HashMap<String, Redirection>() {
        {
          put("ls", new Ls());
          put("history", new History());
          put("cat", new Cat());
          put("echo", new Echo());
          put("man", new Man());
          put("pwd", new Pwd());
          put("find", new Find());
        }
      };

  public void parseCommand(String input);
}

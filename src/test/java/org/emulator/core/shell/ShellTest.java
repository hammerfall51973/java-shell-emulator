package org.emulator.core.shell;

import org.junit.Before;
import org.junit.Test;


public class ShellTest {

    private String[] commands;

    @Before
    public void setUp() {
        if (ShellConstants.IS_OS_WINDOWS) {
            commands = new String[]{"echo \"This is windows Shell Test\"",
                    "md temp",
                    "cd temp",
                    "dir",
                    "cd ..",
                    "rmdir /fs temp"
            };
        } else {
            commands = new String[]{"echo \"This is Shell test for Linux based systems\"",
                    "mkdir temp",
                    "cd temp",
                    "ls -lrt",
                    "cd ..",
                    "rm -rf temp"
            };
        }
    }

    @Test
    public void testSequenceOfCommands() {
        Shell shell = Shell.open();
        for (String command : commands) {
            System.out.printf("[%s]: Executing command: %s%n", "ShellTest", command);
            int exitCode = shell.executeCommand(command);
            if (exitCode == 0) {
                System.out.printf("[%s]: Command '%s' executed successfully%n", "ShellTest", command);
            } else {
                System.out.printf("[%s]: Command '%s' failed with exitCode %s%n", "ShellTest", command, exitCode);
            }
        }
        shell.close();
    }
}

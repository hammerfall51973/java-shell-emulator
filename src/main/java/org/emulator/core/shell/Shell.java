package org.emulator.core.shell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Shell {

    private final Process process;
    private final BufferedWriter cmdWriter;
    private final ExitCodeMonitor exitCodeMonitor;
    private final Thread processOutReaderThread;

    private Shell(String dir) throws IOException {
        // If it is not Windows OS, then we assume we are working with Linux based OS, so we start a shell
        // The SHELL variable may be set to /bin/bas, csh or other shell
        exitCodeMonitor = new ExitCodeMonitor();
        String shell = ShellConstants.IS_OS_WINDOWS ? ShellConstants.WINDOWS_COMMAND_PROGRAM : ShellConstants.LINUX_SHELL;
        ProcessBuilder processBuilder = new ProcessBuilder(shell);
        processBuilder.directory(new File(dir));
        System.getenv().forEach(processBuilder.environment()::put);
        process = processBuilder.start();
        ShellOutputHandler shellOutputHandler = new ShellOutputHandler(process.getInputStream());
        cmdWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        processOutReaderThread = new Thread(shellOutputHandler, "ShellOutputHandler");
        processOutReaderThread.start();
    }

    /**
     * Opens a shell for executing script or sequence of commands.
     */
    public static Shell open(String dir) throws IOException {
        return new Shell(dir);
    }

    public static Shell open() {
        Shell shell = null;
        try {
            shell = open(ShellConstants.USER_DIR);
        } catch (IOException e) {
            System.out.println("Error! " + e.getMessage());
            System.exit(1);
        } finally {
            System.out.println("Invoking the new shell");
        }
        return shell;
    }

    public void close() {
        if (isShellOpen()) {
            try {
                cmdWriter.write("exit");
                cmdWriter.newLine();
                cmdWriter.flush();
            } catch (IOException ie) {
                throw new RuntimeException(ie);
            }
        }
    }

    public int executeCommand(String command) {
        checkShellOpen();
        int exitCode = -1;
        try {
            cmdWriter.write(command);
            cmdWriter.newLine();
            cmdWriter.flush();
            exitCode = getExitCode();
        } catch (IOException | InterruptedException e) {
            return exitCode;

        } finally {
            System.out.println("Please wait for the command execution to complete...");
        }
        return 0;
    }

    private int getExitCode() throws IOException, InterruptedException {
        cmdWriter.write(ShellConstants.EXIT_STATUS_COMMAND);
        cmdWriter.newLine();
        cmdWriter.flush();
        while (isShellOpen() && !exitCodeMonitor.isExitCodeFlagSet() && processOutReaderThread.isAlive()) {
            //System.out.println(processOutReaderThread.getState());

        }
        return exitCodeMonitor.clearExitCodeFlag();
    }

    private void checkShellOpen() {
        if (!isShellOpen()) {
            throw new IllegalStateException("Shell is closed");
        }
    }

    private boolean isShellOpen() {
        return process != null && process.isAlive() && cmdWriter != null;
    }

    private class ShellOutputHandler implements Runnable {
        private final InputStream is;
        private ShellOutputHandler(InputStream is) {
            this.is = is;
        }

        @Override
        public void run() {
            String exitStatusCommandPrefix = ShellConstants.EXIT_STATUS_COMMAND_PREFIX;
            try (Scanner scanner = new Scanner(new InputStreamReader(is))) {
                String line;
                while (Thread.currentThread().isAlive() && scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if (line.contains(exitStatusCommandPrefix) && !line.contains("echo")) {
                        int exitCode = Integer.parseInt(line.substring(exitStatusCommandPrefix.length()));
                        exitCodeMonitor.setExitCode(exitCode);
                    } else {
                        System.out.printf("[%s]: %s%n", Thread.currentThread().getName(), line);
                    }
                }
            }
        }
    }

    private static class ExitCodeMonitor {

        private volatile boolean exitCodeFlagSet;
        private volatile int exitCode;

        boolean isExitCodeFlagSet() {
            return exitCodeFlagSet;
        }

        void setExitCode(int exitCode) {
            this.exitCode = exitCode;
            exitCodeFlagSet = true;
        }

        int clearExitCodeFlag() {
            exitCodeFlagSet = false;
            return exitCode;
        }
    }
}

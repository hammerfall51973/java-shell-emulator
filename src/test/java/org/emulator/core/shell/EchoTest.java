package org.emulator.core.shell;

import org.emulator.core.shell.commands.Echo;
import org.emulator.core.shell.helpers.StandardError;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class EchoTest {

  String output, expected;
  Echo echo;

  @Before
  public void setUp() throws Exception {
    echo = new Echo();

    output = "";
    expected = "";

    /*
     * The actual Echo command is just printing to terminal. Redirection is
     * testing in the RedirectionTes, and ParserTest
     */
  }

  @Test
  public void testEchoTerminal() {
    expected = "hello";
    output = echo.check(new String[] {"echo", "\"hello\""}).trim();
    assertEquals(expected, output);
  }

  @Test
  public void testEchoNoQuotes() {
    echo.check(new String[] {"echo", "hello"}).trim();
    expected = "Error: Strings need to be wrapped in double quotes\n";
    output = StandardError.errors.get(0);
    assertEquals(expected, output);
  }
}

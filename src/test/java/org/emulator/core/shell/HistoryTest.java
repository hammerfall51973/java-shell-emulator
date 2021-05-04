package org.emulator.core.shell;

import org.emulator.core.shell.commands.History;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HistoryTest {
  String output, expected;

  History hist;
  String[] inputHistory;

  @Before
  public void setUp() throws Exception {

    output = "";
    expected = "";

    hist = new History();
  }

  @After
  public void tearDown() throws Exception {
    History.historyList.clear();
  }

  @Test
  public void testOneCommand() {
    History.historyList.add("cat file1");

    output = hist.check(new String[] {"history"}).trim();

    expected = "1. cat file1";

    assertEquals(expected, output);
  }

  @Test
  public void testTwoCommandRequestHundred() {
    History.historyList.add("cat file1");
    History.historyList.add("cat file2");

    output = hist.check(new String[] {"history", "100"}).trim();

    expected = "1. cat file1\n2. cat file2";

    assertEquals(expected, output);
  }

  @Test
  public void testThreeCommandRequestTwo() {
    History.historyList.add("cat file1");
    History.historyList.add("cat file2");
    History.historyList.add("cat file3");

    output = hist.check(new String[] {"history", "2"}).trim();

    expected = "2. cat file2\n3. cat file3";

    assertEquals(expected, output);
  }

  @Test
  public void testThreeCommandRequestNegative() {
    History.historyList.add("cat file1");
    History.historyList.add("cat file2");
    History.historyList.add("cat file3");

    output = hist.check(new String[] {"history", "-1"});

    expected = ""; // CHECK IF THIS IS RIGHT

    assertEquals(expected, output);
  }

  @Test
  public void testThreeCommandRequestNotNumber() {
    History.historyList.add("cat file1");
    History.historyList.add("cat file2");
    History.historyList.add("cat file3");

    output = hist.check(new String[] {"history", "okok"}).trim();

    expected = "";

    // ErrorCheck stack is tested in ErrorCheck

    assertEquals(expected, output);
  }

}

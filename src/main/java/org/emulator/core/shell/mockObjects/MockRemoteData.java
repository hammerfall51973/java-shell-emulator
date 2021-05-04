package org.emulator.core.shell.mockObjects;

import org.emulator.core.shell.interfaces.RemoteDataFetcher;


public class MockRemoteData implements RemoteDataFetcher {

  @Override
  public String getHTMLFromURL(String url) {
    if (url.equals("http://sample.com/sample.txt")) {
      return "Success!";
    }
    return "Failure!";
  }

}

package org.emulator.core.shell.commands;

import org.emulator.core.shell.helpers.ErrorCheck;
import org.emulator.core.shell.interfaces.CommandInterface;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import java.util.Locale;


public class Speak implements CommandInterface {

  private ErrorCheck errorCheck = new ErrorCheck();

  private boolean speak = false;
  private String speakModeString = "";
  private javax.speech.synthesis.Synthesizer synthesizer;

  public boolean isSpeak() {
    return speak;
  }


  public void setSpeak(boolean speak) {
    this.speak = speak;
  }

  public void addSpeakString(String appendString) {
    this.speakModeString += " " + appendString;
  }


  public void runSpeakWithSpeakModeString() {
    runSpeak(speakModeString);
  }


  public void runSpeak(String input) {
    try {
      System.setProperty("freetts.voices",
          "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

      Central.registerEngineCentral(
          "com.sun.speech.freetts.jsapi." + "FreeTTSEngineCentral");

      synthesizer =
          Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));

      synthesizer.allocate();

      synthesizer.resume();

      synthesizer.speakPlainText(input, null);
      synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

      this.speakModeString = "";
      this.speak = false;

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public String check(String[] arg) {
    if (arg.length == 1) {
      speak = true;
      return "";
    }

    if (!errorCheck.isProperString(arg[1])) {
      return "";
    }

    runSpeak(arg[1].replaceAll("\"", ""));
    return "";
  }
}

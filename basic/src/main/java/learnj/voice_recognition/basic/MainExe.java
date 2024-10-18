package learnj.voice_recognition.basic;

import org.vosk.LibVosk;

public class MainExe {
  public static void main(String[] args) {
    System.out.println(
      String.format(
        "::: %s, org.vosk.LibVosk.class = %s, done\n",
        "Hi there",
        LibVosk.class
      )
    );
  }
}
package learnj.voice_recognition.basic_no_jpms;

import com.sun.tools.javac.Main;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Line;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class MainExe {

  private static final String MAGIC_PHRASE = /*"machine"*/"mirror mirror on the wall";
  private static final String LBL_CMD_IMMEDIATE_SHUTDOWN = "shut down hashtag one four three";
  private static final String LBL_CMD_ABORT = "never mind";
  private static volatile boolean isMicrophoneConnected = false;
  private static volatile boolean isOut = false;

  public static void main(String[] args) {
//    MainExe.checkFile();
    MainExe.mainV0(args);
    System.out.printf("::: %s, %s\n%s, done\n",
      "Hi there", MainExe.class, LibVosk.class
    );
  }

  //REM: [EXTERNAL, NOT_RELATED]
  public static void checkFile() {
    Path path = null;
    try {
      path = Paths.get(ClassLoader.getSystemResource(
        "learnj/voice_recognition/basic_no_jpms/rez/sample-text-001.txt"
      ).toURI());
    } catch (final URISyntaxException | NullPointerException e) {
    }

    if (path != null && Files.exists(path)) {
      if (Files.isRegularFile(path)) {
        System.out.println("The path refers to a file. " + (path));
      } else if (Files.isDirectory(path)) {
        System.out.println("The path refers to a directory." + (path));
      }
    } else {
      System.out.println("The file or directory does not exist.");
    }
  }

  public static void mainV0(String[] args) {
    System.out.println("Starting voice recognition application...");
//    while (true) {
    if (!MainExe.isMicrophoneConnected) {
      MainExe.checkMicrophoneAndStartPreListening();
    }
//    }
  }

  public static void mainV1(String[] args) {
    LibVosk.setLogLevel(LogLevel.DEBUG);

    try (Model model = new Model("../lib/model/vosk-model-en-us-0.22-lgraph/");
         InputStream ais = AudioSystem.getAudioInputStream(
           new BufferedInputStream(Files.newInputStream(
             Paths.get("../lib/test-material/sound/test.wav"))
           ));
         Recognizer recognizer = new Recognizer(model, 16000)
    ) {

      int nbytes;
      byte[] b = new byte[4096];
      while ((nbytes = ais.read(b)) >= 0) {
        if (recognizer.acceptWaveForm(b, nbytes)) {
          String result = recognizer.getResult();
          System.out.println("1.1: " + result);

          final int indexBegin = result.indexOf(':') + 1;
          result = result.substring(indexBegin, result.length() - 1).trim();
          result = result.replaceAll("^\"|\"$", "").trim();
          System.out.println("1.2: " + result);
        } else {
          System.out.println("0: " + recognizer.getPartialResult());
        }
      }
      System.out.println("1: " + recognizer.getFinalResult());
    } catch (IOException | UnsupportedAudioFileException e) {
      System.out.printf("::: ERROR: %s", e);
    } catch (final Exception e) {
      System.out.printf("::: ERROR_FALLBACK: %s", e);
    }
  }

  private static void checkMicrophoneAndStartPreListening() {
    while (!MicrophoneChecker.isMicrophoneAvailable()) {
      System.out.println("No microphone found. Waiting...");
      try {
        Thread.sleep(2500);  //REM: Wait for 5 seconds before checking again
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println("Microphone detected! Starting pre-listening mode...");
    MainExe.isMicrophoneConnected = true;
    MainExe.startPreListening();
  }

  //REM: Pre-listening mode for the magic phrase
  private static void startPreListening() {
    LibVosk.setLogLevel(LogLevel.DEBUG); //REM: Optional: Disable logging if you want

    try (Model model = new Model("../lib/model/vosk-model-en-us-0.22-lgraph")) { //REM: Load your Vosk model
      AudioFormat format = new AudioFormat(
        16000, 16, 1, true, false
      );
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
      TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);

      microphone.open(format);
      microphone.start();

      System.out.println("Listening for the magic phrase...");

      //REM: Create a thread to monitor microphone disconnection
      Thread micMonitorThread = new Thread(() -> MainExe.monitorMicrophoneDisconnection(microphone));
      micMonitorThread.start();

      try (Recognizer recognizer = new Recognizer(model, 16000)) {
        byte[] buffer = new byte[4096];
        while (MainExe.isMicrophoneConnected) {
          int bytesRead = microphone.read(buffer, 0, buffer.length);

          if (bytesRead > 0) {
            if (recognizer.acceptWaveForm(buffer, bytesRead)) {
              String result = recognizer.getResult();
              System.out.println("Recognized: " + result);
              if (result.toLowerCase().contains(MainExe.MAGIC_PHRASE)) {
                System.out.println("Magic phrase detected! Activating post-listening...");
                MainExe.startPostListening(recognizer, microphone);  //REM: Move to post-listening mode

              }
              else if(result.toLowerCase().contains(LBL_CMD_IMMEDIATE_SHUTDOWN)) {
                MainExe.isOut = true;
                micMonitorThread.join();
                break; //REM: Break out of pre-listening
              }

            } else {
              System.out.println("Partial result: " + recognizer.getPartialResult());
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //REM: Post-listening mode after magic phrase detection
  private static void startPostListening(Recognizer recognizer, TargetDataLine microphone) {
    System.out.println("Post-listening activated! Listening for further commands or actions...");

    //REM: You can add further voice recognition tasks or actions here
    //REM: This could be another phase where different commands or actions are processed.
//    try (Model model = new Model("../lib/model/vosk-model-tl-ph-generic-0.6")) {
//      AudioFormat format = new AudioFormat(
//        16000, 16, 1, true, false
//      );
//      DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
//      TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
//
//      microphone.open(format);
//      microphone.start();

//    try (Recognizer recognizer = new Recognizer(model, 16000)) {
    try {
      byte[] buffer = new byte[4096];
      while (isMicrophoneConnected) {
        int bytesRead = microphone.read(buffer, 0, buffer.length);
        if (bytesRead > 0) {
          if (recognizer.acceptWaveForm(buffer, bytesRead)) {
            String result = recognizer.getResult();
            System.out.println("Post-listening recognized: " + result);

//            final int indexBegin = result.indexOf(':') + 1;
//            result = result.substring(indexBegin, result.length() - 1).trim();
//            result = result.replaceAll("^\"|\"$", "").trim();
            System.out.println("Post-listening recognized: " + result);
            //REM: Add logic to handle post-listening commands or actions
            if (result.toLowerCase().contains(LBL_CMD_IMMEDIATE_SHUTDOWN)) {
              MainExe.isOut = true;
              Thread.sleep(1500);
              break; //REM: Break out of post-listening
            }
            else if (result.toLowerCase().contains(LBL_CMD_ABORT)) {
              break; //REM: Break out of post-listening
            }
          } else {
            String result = recognizer.getPartialResult();
            System.out.println("Post-listening Partial result: " + result);
            if (result.toLowerCase().contains(LBL_CMD_ABORT)) {
              break; //REM: Break out of post-listening
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //REM: Monitor microphone disconnection
  private static void monitorMicrophoneDisconnection(TargetDataLine microphone) {
    while (true) {
      if (!MicrophoneChecker.isMicrophoneAvailable() || MainExe.isOut) {
        System.out.println("Microphone disconnected.");
        isMicrophoneConnected = false;
        microphone.stop();
        microphone.close();
        break;
      }

      try {
        Thread.sleep(2000); //REM: Poll every 2 seconds
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}

class MicrophoneChecker {
  public static boolean isMicrophoneAvailable() {
    Mixer.Info[] mixers = AudioSystem.getMixerInfo();
    for (Mixer.Info mixerInfo : mixers) {
      Mixer mixer = AudioSystem.getMixer(mixerInfo);
      Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
      for (Line.Info lineInfo : targetLineInfos) {
        if (lineInfo.getLineClass().equals(TargetDataLine.class)) {
          return true;  //REM: Microphone found
        }
      }
    }
    return false;  //REM: No microphone found
  }
}

package learnj.voice_recognition.basic_no_jpms;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

public interface IAudioDeviceChecker {
  public abstract boolean isAvailable();
  public abstract boolean isMuted();
  public abstract void reload();

  public static boolean canAccessMicrophone(
    DataLine microphone
  ) {
    try {
      microphone.open();
      microphone.start();
      microphone.stop();
      microphone.close();
      return true;
    }
    catch( final LineUnavailableException | RuntimeException e ) {
      return false;
    }
  }
}

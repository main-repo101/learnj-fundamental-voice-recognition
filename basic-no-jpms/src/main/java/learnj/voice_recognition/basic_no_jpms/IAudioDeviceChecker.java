package learnj.voice_recognition.basic_no_jpms;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public interface IAudioDeviceChecker {
  public abstract boolean isAvailable();
  public abstract boolean isMuted();
  public abstract void reload();

  public static boolean canAccessMicrophone(
    TargetDataLine microphone
  ) {
    try {
      microphone.open();
      microphone.start();

      //REM: [TODO, IS_THERE_AN_ALTERNATIVE], checking if mic is muted or not.
      final byte[] BUFFER_SOUND = new byte[1024];
      final int BYTE_SIZE_SOUND = microphone.read(
        BUFFER_SOUND, 0, BUFFER_SOUND.length
      );

      for( int i = 0; i < BYTE_SIZE_SOUND; ++i ) {
//        System.out.println("<><> " + (BUFFER_SOUND[i]) );
        if( BUFFER_SOUND[i] < -1 || BUFFER_SOUND[i] > 1 ) {
          microphone.stop();
          microphone.close();
          return true; //REM: There's a sound.
        }
      }

    }
    catch( final LineUnavailableException | RuntimeException e ) {
      //REM: Ignore and continue
    }
    return false;
  }
}

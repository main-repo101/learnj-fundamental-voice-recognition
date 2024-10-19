package learnj.voice_recognition.basic_no_jpms;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneDevice implements IAudioDeviceChecker {

  public MicrophoneDevice() {
    this.mixerInfos = AudioSystem.getMixerInfo();
  }

  @Override
  public boolean isAvailable() {
    return this.getMicrophone() != null;
  }

  @Override
  public boolean isMuted() {
    DataLine microphone = this.getMicrophone();
    if( microphone == null ) return true;
    return !IAudioDeviceChecker.canAccessMicrophone(microphone);
  }

  @Override
  public void reload() {
    this.mixerInfos = AudioSystem.getMixerInfo();
  }

  public TargetDataLine getMicrophone() {
//    this.reload();
    for(Mixer.Info mixerInfo : this.mixerInfos ) {
      Mixer mixer = AudioSystem.getMixer(mixerInfo);
      Line.Info[] lineInfos = mixer.getTargetLineInfo();
      for( Line.Info lineInfo : lineInfos ) {
        if( TargetDataLine.class.equals(lineInfo.getLineClass()) ) {
          try {
            return (TargetDataLine) mixer.getLine(lineInfo);
          }
          catch( final LineUnavailableException | RuntimeException e ) {
            //REM: Ignore and continue...
          }
        }
      }
    }
    return null;
  }

  private Mixer.Info[] mixerInfos;
}

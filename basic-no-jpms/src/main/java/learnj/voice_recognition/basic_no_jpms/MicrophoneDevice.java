package learnj.voice_recognition.basic_no_jpms;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneDevice implements IAudioDeviceChecker {

  public MicrophoneDevice() {
    this.init();
  }

  private void init() {
    //REM: [TODO, IS_IT_PROPER]
    this.reload();
  }

  @Override
  public boolean isAvailable() {
    return this.microphone != null;
  }

  @Override
  public boolean isMuted() {
    if( this.microphone == null ) return true;
    return !IAudioDeviceChecker.canAccessMicrophone(this.microphone);
  }

  @Override
  public void reload() {
    this.mixerInfos = AudioSystem.getMixerInfo();
    this.microphone = MicrophoneDevice.findMicrophone( this.mixerInfos );
  }

  public TargetDataLine getMicrophone() {
    return this.microphone;
  }

  public Mixer.Info[] getMixerInfos() {
    return this.mixerInfos.clone(); //REM: [TODO], is this clone gonna work?
  }

  private static TargetDataLine findMicrophone(
    Mixer.Info[] mixerInfos
  ) {
    for(Mixer.Info mixerInfo : mixerInfos ) {
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
  private TargetDataLine microphone;
}

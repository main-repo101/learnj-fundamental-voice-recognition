module learnj.voice_recognition.basic {
    requires java.base;

    requires vosk;
    opens learnj.voice_recognition.basic to vosk;
}
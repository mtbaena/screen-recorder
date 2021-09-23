package com.tulco.examples;

import com.tulco.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.IOException;

public class RecordUntilStopped {
    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        ScreenRecorder sr = new ScreenRecorder(640, 480, 3); // 640x480 video AND 3 frames per second
        sr.startRecording("./my_video_3fps_IStopIt.mp4"); // recording forever to 'my_video.mp4'
        Thread.sleep(1000); // Doing something, if not, next line would immediately stop recording.
        sr.stopRecording(); // Stops recording
    }
}

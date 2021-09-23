package com.tulco.examples;

import com.tulco.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.IOException;

public class RecordThirtySeconds {
    public static void main(String[] args) throws IOException, AWTException {
        ScreenRecorder sr = new ScreenRecorder(640, 480);
        sr.startRecording(
                "./my_video",           // Path to file in current folder. Extension will be ignored/appended
                30,                 // Seconds to record before stoping.
                null,     // not set => grab main display
                -1,                  // the target FPS. -1 => maximum
                false,               // Do not inject timestamp in the recording
                0);                // Split size in minutes, if needed. 0='no split'
        // Do something, it is recording in the background...
    }
}

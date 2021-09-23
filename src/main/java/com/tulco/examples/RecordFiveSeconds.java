package com.tulco.examples;

import com.tulco.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.IOException;

public class RecordFiveSeconds {
    public static void main(String[] args) throws IOException, AWTException {
        ScreenRecorder sr = new ScreenRecorder(640, 480);
        sr.startRecording("./my_video", 5);
    }
}

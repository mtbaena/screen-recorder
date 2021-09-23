package com.tulco.examples;

import com.tulco.screenrecorder.ScreenReference;
import com.tulco.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class FullExample {
    public static void main(String[] args) throws IOException, AWTException {
        // The following initializes all artifacts. It takes around 20 seconds
        ScreenRecorder sr = new ScreenRecorder(640, 480);
        /*  NOTE:
            To avoid the initial test, if we know that the computer
            supports 5 FPS, we could do: 'new ScreenRecorder(640, 480, 5);'
         */

        // Find out the connected screens
        List<ScreenReference> screens =  sr.listScreens();

        // The path to the recording file
        String filePath = "/Users/marco/tul_pers/temp_recording/test";

        // Starts the recording in a different thread. It continues execution
        sr.startRecording(
                filePath,           // Path to base file. Extension will be ignored/appended
                screens.get(0),     // Specify what screen to record
                true,               // Inject timestamp in the recording?
                15);                // Split size in minutes, if needed. 0='no split'

        // This block mimics your software doing something else while recording...
        for (int j=0; j<24; j++) { // 24 hours
            for (int i = 0; i < 3600; i++) { // 3600 seconds ... 1 hours
                try {
                    // Waiting a little bit, as if busy
                    Thread.sleep(1000);
                    // Checking if recording
                    if (!sr.isRecording()) System.out.println("**** Not Recording");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Finishing recording
        sr.stopRecording();
    }
}

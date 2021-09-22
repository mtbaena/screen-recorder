package com.tulco;

import com.tulco.desktopscreenrecorder.ScreenReference;
import com.tulco.desktopscreenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class EntryPoint {
    public static void main(String[] args) throws IOException, AWTException {
        // The following initializes all artifacts. It takes around 20 seconds
        ScreenRecorder sr =
                new ScreenRecorder(640, 480);
        /*  NOTE:
            To avoid the initial delay, if we know the computer
            supports 5 FPS, we could do: 'new DesktopScreenRecorder(640, 480, 5);'
         */

        // Find out the connected screens
        List<ScreenReference> screens =  sr.listScreens();
        // The path to the recording file
        String filePath = "/Users/marco/tul_pers/temp_recording/test";
        // Starts the recording in a different thread. It continues execution
        sr.startRecording(
                filePath,           // Path to base file. Extension will be ignored/appended
                screens.get(0),     // Specify what desktop to record
                true,               // Inject timestamp in the recording?
                15);                // Split size in minutes, if needed. 0='no split'

//        sr.startRecording(
//                filePath,           // Path to base file. Extension will be ignored/appended
//                30,                 // Seconds to record before stoping. 0 => do not stop
//                screens.get(0),     // Specify what desktop to record
//                5,                  // the target FPS. -1 => maximum
//                true,               // Inject timestamp in the recording?
//                15);                // Split size in minutes, if needed. 0='no split'

        // Doing something...
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

package com.tulco.desktopscreenrecorder;

/**
 * This is the thread that starts recording
 */
public class RecordingTaskThread extends Thread {
    ScreenRecorder dsr = null;
    String filePath;
    int fps;
    ScreenReference desktop;
    boolean timeStamp;
    int numImages;
    int maximumRecordingSeconds = 0;
    boolean isRunning = false;
    int splitVideoInMinutes;

    /**
     * To check if the thread is technically running
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Constructor. It receives all the needed data to record.
     * @param dsr
     * @param filePath
     * @param fps
     * @param desktop
     * @param timeStamp
     * @param numImages
     * @param maximumRecordingSeconds
     * @param splitVideoInMinutes
     */
    public RecordingTaskThread(
            ScreenRecorder dsr,
            String filePath, int fps,
            ScreenReference desktop,
            boolean timeStamp,
            int numImages, int maximumRecordingSeconds,
            int splitVideoInMinutes) {
        this.dsr = dsr;
        this.filePath = filePath;
        this.fps = fps;
        this.desktop = desktop;
        this.timeStamp = timeStamp;
        this.numImages = numImages;
        this.maximumRecordingSeconds = maximumRecordingSeconds;
        this.splitVideoInMinutes = splitVideoInMinutes;
    }

    /**
     * The code for the thread
     */
    public void run() {
        try {
            isRunning = true;
            dsr.recordDesktop(
                    filePath, fps, desktop, timeStamp,
                    numImages, maximumRecordingSeconds,
                    splitVideoInMinutes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isRunning = false;
        }
    }
}

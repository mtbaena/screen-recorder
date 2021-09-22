package com.tulco.desktopscreenrecorder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcodec.api.awt.AWTSequenceEncoder;

/**
 * Main logic for recording. It uses AWT to grab the
 * images and JCodec to encode them.
 */
public class ScreenRecorder {
    private Robot robot = null;
    private long lastTimeRobotWasReset = -1;
    private int maxFPS = 10;
    private Rectangle fullScreenRectangle = null;
    private boolean stopSignal = false;
    private int width;
    private int height;
    private RecordingTaskThread rtt = null;
    private BufferedImage lastImage = null;

    /**
     * Default constructor. It tests the FPS capability of computer.
     * The thread has to wait until the test is done.
     *
     * @throws AWTException
     * @throws IOException
     */
    public ScreenRecorder(int width, int height) throws AWTException, IOException {
        this(width, height, -1);
    }

    /**
     * Main constructor. It computes FPS if not set (<1).
     *
     * @param width Final width for the video, when the images get encoded
     * @param height Final height for the video, when the images get encoded
     * @param fps The frames per second. If <1, it tests the computer capability.
     * @throws AWTException
     * @throws IOException
     */
    public ScreenRecorder(int width, int height, int fps) throws AWTException, IOException {
        this.width = width;
        this.height = height;
        robot = new Robot();
        lastTimeRobotWasReset = System.currentTimeMillis();
        if (fps<1) {
            // Testing fps capability. This makes the system wait 20-30 seconds...
            maxFPS = (int) testFPS();
        }
    }

    /**
     * Gets the image grabber. It tracks when it was initialized
     * and makes sure it is at most 1 second old.
     * @return
     * @throws AWTException
     */
    private Robot getRobot() throws AWTException {
        long now = System.currentTimeMillis();
        if (now-lastTimeRobotWasReset>1000){
            robot = new Robot();
            lastTimeRobotWasReset = now;
        }
        return robot;
    }

    /**
     * Grab an image from main desktop
     * @return
     */
    public BufferedImage createScreenCapture() {
        return createScreenCapture(null);
    }

    /**
     * Grabs an image from the provided desktop/screen
     * @param dr
     * @return
     */
    public BufferedImage createScreenCapture(ScreenReference dr) {
        if (null==dr) {
            // If rectangle not provided, get the main screen.
//            System.out.println("***** Screen not provided... seen JUST main screen!!");
            if (null==fullScreenRectangle) {
                fullScreenRectangle = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
            }
            return robot.createScreenCapture(fullScreenRectangle);
        }
        return robot.createScreenCapture(dr.getRectangle());
    }

    /**
     * It tests the FPS the system can provide.
     * It will run 2 quick rounds of test recording. This method makes
     * the default instantiation of this object delay the main thread
     * for around 5 to 20 seconds, depending on the computer capabilities.
     *
     * @return
     * @throws AWTException
     * @throws IOException
     */
    private double testFPS() throws AWTException, IOException {
        final int NUM_FRAMES = 40;
        final String TEST_FILENAME = "DELME_test.mp4";

        System.out.println("## Performing recording test. This will take few seconds...");
        // Get the biggest desktop
        ScreenReference biggestDesktop = biggestDesktop();

        // warming up.
        recordDesktop(
                TEST_FILENAME,
                10, biggestDesktop, true, 10);
        // time a recording.
        long start = System.currentTimeMillis();
        recordDesktop(
                TEST_FILENAME,
                10, biggestDesktop, true, NUM_FRAMES);
        long stop = System.currentTimeMillis();
        // compute FPS.
        double computedFPS =
                (double)((double)NUM_FRAMES/((double)(stop-start)/1000d))
                -1; // reducing by one to be more conservative

        System.out.println("### Max FPS: "+computedFPS);

        try {
            File delFile = new File(TEST_FILENAME);
            delFile.deleteOnExit();
            delFile.delete();
        } catch (Exception e) {
            // Ignore the exceptions when file cannot be deleted
            System.out.println("WARNING: file cannot be deleted: "+TEST_FILENAME);
        }
        return computedFPS;
    }

    /**
     * Returns the desktop that has the biggest area, out of
     * the available screens on this computer. It is used as a
     * pesimistic approach for computing the FPS.
     *
     * @return The reference to the biggest screen.
     * @throws AWTException
     */
    private ScreenReference biggestDesktop() throws AWTException {
        List<ScreenReference> lstDt = listScreens();
        long biggestArea = -1;
        ScreenReference biggestDesktop = null;
        for (ScreenReference dr : lstDt) {
            if (dr.computeArea()>biggestArea) biggestArea = dr.computeArea();
            biggestDesktop = dr;
        }
        return biggestDesktop;
    }

    /**
     * Records screen in this thread
     *
     * @param filePath
     * @param fps
     * @param desktop
     * @param timeStamp
     * @param numImages
     * @throws IOException
     */
    public void recordDesktop(
            String filePath, int fps,
            ScreenReference desktop,
            boolean timeStamp,
            int numImages) throws IOException, AWTException {
        recordDesktop(filePath, fps, desktop, timeStamp, numImages, -1);
    }

    /**
     * Records screen in this thread
     *
     * @param filePath
     * @param fps
     * @param desktop
     * @param timeStamp
     * @param maximumNumberOfImagesToRecord
     * @param maximumRecordingSeconds
     * @throws IOException
     * @throws AWTException
     */
    public void recordDesktop(
            String filePath, int fps,
            ScreenReference desktop,
            boolean timeStamp,
            int maximumNumberOfImagesToRecord,
            int maximumRecordingSeconds) throws IOException, AWTException {
        recordDesktop(filePath, fps, desktop, timeStamp,
                maximumNumberOfImagesToRecord, maximumRecordingSeconds,
                0);
    }

    /**
     * Records screen in this thread. Main logic.
     *
     * @param givenFilePath
     * @param fps
     * @param desktop
     * @param timeStamp
     * @param maximumNumberOfImagesToRecord
     * @param maximumRecordingSeconds
     * @param splitVideoInMinutes
     * @throws IOException
     * @throws AWTException
     */
    public void recordDesktop(
            String givenFilePath,
            int fps,
            ScreenReference desktop,
            boolean timeStamp,
            int maximumNumberOfImagesToRecord,
            int maximumRecordingSeconds,
            int splitVideoInMinutes) throws IOException, AWTException
    {
        final String EXTENSION=".mp4";
        int splitVideoInMillis = 0;
        int serialSplit = 0;

        String rootFilePath = GeneralUtils.removeFileExtension(givenFilePath);
        String serializedSuffix = "";

        // If we are going to split
        if (splitVideoInMinutes>0) {
            splitVideoInMillis=splitVideoInMinutes*60*1000;
            serializedSuffix = "_" + GeneralUtils.buildSplitStringSuffix(serialSplit);
        }

        // Setting FPS
        if (fps<1) fps = maxFPS;
        if (fps > maxFPS) {
            System.out.println(" ### FPS ("+fps+") is higher than the maximum ("+maxFPS+"). Defaulting to maximum.");
            fps = maxFPS;
        }
        // Filename
        String recordingFilePath = rootFilePath+serializedSuffix+EXTENSION;
        File file = new File(recordingFilePath);
        System.out.println("# Recording (fps: "+fps+") to file: "+recordingFilePath+"...");

        int imageCount = 0;

        // Allow both num of images and time as a stopper
        long now = System.currentTimeMillis();
        long startRecordingTime = now;
        long recordingStopTime;
        if (maximumNumberOfImagesToRecord<1) maximumNumberOfImagesToRecord = Integer.MAX_VALUE;
        if (maximumRecordingSeconds<1) {
            recordingStopTime = Long.MAX_VALUE; // Avoid overflow
        } else recordingStopTime = now + maximumRecordingSeconds*1000;

        AWTSequenceEncoder sequenceEncoder =
                AWTSequenceEncoder.createSequenceEncoder(file, fps);
        long lastFrameTime = 0;

        while (
                imageCount < maximumNumberOfImagesToRecord
                && recordingStopTime>now
                && !stopSignal) {
            // Enforce image cadence using the FPS and the start time
            boolean stillWait = true;
            while (stillWait) { // change
                now = System.currentTimeMillis();
                stillWait = (now-lastFrameTime)<(1d/(double)fps*1000d);
                if (stillWait) {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    System.out.println("## Sleeping...");
                }
            }
            lastFrameTime = now; // Setting time ahead of grabbing to compensate actual recording time.
            // Grab image
            BufferedImage image = createScreenCapture(desktop);
            // Resize image
            image = GeneralUtils.resize2(image, width, height);
            if (null==lastImage || imageCount%fps==1) {
                lastImage = GeneralUtils.deepCopyBufferedImage(image);
            }
            // Inject timestamp to image
            if (timeStamp) GeneralUtils.addTimeStamp(image);
            imageCount++;
            sequenceEncoder.encodeImage(image);

            // Split video
            if (splitVideoInMinutes>0 && now>startRecordingTime+splitVideoInMillis) {
                sequenceEncoder.finish();
                serialSplit++;
                serializedSuffix = "_" + GeneralUtils.buildSplitStringSuffix(serialSplit);
                // Filename
                recordingFilePath = rootFilePath+serializedSuffix+EXTENSION;
                file = new File(recordingFilePath);
                System.out.println("# Recording (fps: "+fps+") to file: "+recordingFilePath+"...");
                sequenceEncoder =
                        AWTSequenceEncoder.createSequenceEncoder(file, fps);
                startRecordingTime = now;
            }
        }
        sequenceEncoder.finish();
    }

    /**
     * It makes a video out of a list of files
     * @param imageList
     * @param file
     * @throws IOException
     */
    public static void makeVideoFromImages(List<BufferedImage> imageList, File file) throws IOException {
        AWTSequenceEncoder sequenceEncoder = AWTSequenceEncoder.createSequenceEncoder(file, 25);
        for (int i = 0; i < imageList.size(); i++) {
            System.out.println("list encode " + i);
            sequenceEncoder.encodeImage(imageList.get(i));
        }
        sequenceEncoder.finish();
    }

    /**
     * It checks the available screens and reports them back.
     * @return The list of desktop references.
     * @throws AWTException
     */
    public List<ScreenReference> listScreens() throws AWTException {
        List<ScreenReference> screenList = new ArrayList<>();
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            Rectangle screenRect2 = gd.getDefaultConfiguration().getBounds();
            BufferedImage image = getRobot().createScreenCapture(screenRect2);
            image = GeneralUtils.resize2(image, 400, 300);
            ScreenReference desk = new ScreenReference(screenRect2, image);
            screenList.add(desk);
        }
        return screenList;
    }

    /**
     * Records in a different thread. Main logic.
     * @param filename
     * @param secondsToRecord
     * @param dr
     * @param fps
     * @param showTimestamp
     * @param splitVideoInMinutes
     */
    public void startRecording(
            String filename,
            int secondsToRecord,
            ScreenReference dr,
            int fps,
            boolean showTimestamp,
            int splitVideoInMinutes)
    {
        if (null!= rtt) throw new IllegalStateException("Already recording");
        rtt = new RecordingTaskThread(
                this, filename, fps,
                dr, showTimestamp,
                0, secondsToRecord,
                splitVideoInMinutes);
        rtt.start();
    }

    /**
     * Records in a different thread.
     *
     * @param filename
     */
    public void startRecording(
            String filename)
    {
        startRecording(filename, 0, null, -1, false, 0);
    }

    /**
     * Records in a different thread.
     *
     * @param filename
     * @param dr
     * @param showTimestamp
     */
    public void startRecording(
            String filename,
            ScreenReference dr,
            boolean showTimestamp)
    {
        startRecording(filename, 0, dr, -1, showTimestamp, 0);
    }

    /**
     * Records in a different thread.
     *
     * @param filename
     * @param dr
     * @param showTimestamp
     * @param splitSizeInMinutes
     */
    public void startRecording(
            String filename,
            ScreenReference dr,
            boolean showTimestamp,
            int splitSizeInMinutes)
    {
        startRecording(filename, 0, dr, -1, showTimestamp, splitSizeInMinutes);
    }

    /**
     * Stops the recording that we atarted with 'startRecording()'.
     */
    public void stopRecording() {
        stopSignal = true;
    }

    /**
     * Checks if the system is recording.
     * @return
     */
    public boolean isRecording() {
        if (rtt==null) return false;
        if (!rtt.isRunning()) return false;
        if (GeneralUtils.isBlack(lastImage)) {
            System.out.println("**** Last seen image is black!!");
            return false;
        }
        return true;
    }
}


# screen-recorder

Java component for screen-recording videos.

This component allows java systems to record from the screen. It creates an mp4 file with the created video. The video is not playable until you stop recording.

As additional features, it can:

* [optional] Target any screen connected to your computer.
* [optional] Configure the final video size (width x height)
* [optional] Inject a label in each frame with the current date and time.
* [optional] Split the video in smaller ones, as it records. You can play the chunks as they are finished.
* [optional] Configure a number of seconds to record. It will stop automatically.
* [optional] Configure the FPS (frames per second).


It uses:
* jcodec, for encoding the video
* AWT, to retrieve the desktop images

## Functional Overview:

**Create the component** that will automatically perform a test of the running computer to measure its recording capabilities.

**Choose which screen** you will record from. It will record the main one if you do not specify it.

**Start recording**. When recording, a different thread will be used to manage the recording task.

**Monitor recording status**. You have the ability to check whether the recording process is working or not.
This check verifies:
* that you started the recording process.
* that the recording thread is running and healthy.
* that the last image that was used in the video was not black.

Note that black images will be created if, for example, a second screen gets suddenly disconnected while recording on it. The most common case is, a user pulling from a HDMI cable.

**Stop recording**. The video will be available as an MP4 file.

## Usage:

In order to record, one should:

#### 1. Instantiate the recording component.

    ScreenRecorder sr = new ScreenRecorder(width, height);

...where 'width' and 'height' are the final video dimensions you want.
The smaller the images, the faster the encoder gets its job done.

Example:

    ScreenRecorder sr = new ScreenRecorder(640, 480);

Upon instantiation, the component will immediately start a test to measure the computer recording capability.
This test will take somewhat between 5 and 30 seconds, depending on the power of the computer.
After the test, the component already knows the maximum FPS (frames per second) this computer can produce.

There is an option to skip the test. If you already know a healthy value for the
frames per second your computer can handle, you can specify it:

    ScreenRecorder sr = new ScreenRecorder(640, 480, 5);

here 5 is a safe value you know can work on the computer. Meaning, you know you can record 5 frames per second.

**Please note** that this value MUST be bigger than zero. Also, that if you choose a big value, your computer will not cope up with the demand. It will try to grab more images than the ones your computer can produce. This will yield a video produced with fewer images than the ones needed to maintain the FPS hence the video will play in a fast-forward manner.

#### 2. Choose screen

You can query the available screens currently connected:

    List<ScreenReference> screens =  sr.listScreens();


Each one of the returned objects in the list holds enough information to start recording.
They also contain a thumbnail with the snapshot of the screen. This image can be used in a 
graphical interface to allow the user to visually choose which screen to record from. 

#### 3. Start recording

    sr.startRecording(filePath);

This will immediately start recording the main screen, using the maximum FPS.

The parameter 'filePath' points to the file to be created. The extension you provide will be ignored and '.mp4' will be appended.

As an example: if you specify the path '/users/video/my_video.avi' the system will create the video: '/users/video/my_video.mp4'

When recording in split mode -see parameter below-, the created file names will be serialized and you will see something like this:

    /users/video/my_video_0001.mp4
    /users/video/my_video_0002.mp4
    /users/video/my_video_0003.mp4
    /users/video/my_video_0004.mp4
    ...

If you want more control, you can start recording using the complete method invocation:

        sr.startRecording(
                filePath,           // Path to base file. Extension will be ignored/appended
                30,                 // Seconds to record before stoping. 0 => do not stop
                screens.get(1),     // Specify what screen to record. Here we are using the second screen.
                5,                  // the target FPS. -1 => maximum
                true,               // Inject timestamp in the recording?
                15);                // Split size in minutes, if needed. 0='no split'

This option allows more control on the recording method.

#### 4. Stop recording

If we did not specify the recording time, the component keeps on recording until instructed. 
In order to stop recording, one must invoke the method 'stopRecording()'. Example:

    sr.stopRecording();

Once the recorder stops, the created video is ready to be used (played, moved, ...).

## Examples

Examples can be found in the package: **com.tulco.examples**.

The simplest example to record 5 seconds:

    public static void main(String[] args) throws IOException, AWTException {
        ScreenRecorder sr = new ScreenRecorder(640, 480); // 640x480 video
        sr.startRecording("./my_video.mp4", 5); // recording 5 seconds to 'my_video.mp4'
    }

The same example, but forcing the FPS limit to 3. The component does not execute the initial FPS test, so it starts recording immediately:

    public static void main(String[] args) throws IOException, AWTException {
        ScreenRecorder sr = new ScreenRecorder(640, 480, 3); // 640x480 video AND 3 frames per second
        sr.startRecording("./my_video.mp4", 5); // recording 5 seconds to 'my_video.mp4'
    }

The following example illustrates how to start recording forever and then decide when to stop it:

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        ScreenRecorder sr = new ScreenRecorder(640, 480, 3); // 640x480 video AND 3 frames per second
        sr.startRecording("./my_video_3fps_IStopIt.mp4"); // recording forever to 'my_video.mp4'
        Thread.sleep(1000); // Doing something, if not, next line would immediately stop recording.
        sr.stopRecording(); // Stops recording
    }

There is also a running example in the class:

> FullExample.java

This example shows full control of the execution and mimics parallel processing. It also checks every second that we are correctly recording.

## Limitations and Pending Features

* It does not allow configuration for the label with the timestamp.
* The frames per second achieved are low since it does not use hardware acceleration nor advanced libraries.
* When splitting video files, you could lose a small fraction of a second in between videos.
* If your system fails, or the execution abruptly stops, the rendered video is not playable.
* It always produces an mp4 video. Other formats like '.avi' are not supported yet.
* The creation of the component executes a test that holds execution for few seconds (can be avoided if manually setting FPS).
* The logs are very basic. Also, they use System.out

## Disclaimer:

This software is not intended yet to be used in production systems nor in high availability environments.



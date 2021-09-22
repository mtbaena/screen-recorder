# screen-recorder
Simple Java library for screen-recording videos.

This code was written as a help for allowing java systems to record from the screen.

It uses:
* jcodec ... for encoding the video
* AWT ... to retrieve the desktop images

## Functional Overview:


**Choose which screen** you will record from. It will record the main one if you do not specify it.

**Create the main object**: DesktopScreenRecorder. It will automatically perform a test of the running computer to measure its recording capabilities.

**Start recording**. When recording, a different thread will be used to manage the recording task.

**Monitor recording health**. You always have the ability to check that the recording process is working correctly.
This check verifies:
* that you started the recording process.
* that the recording thread is running and healthy.
* that the last image that was used in the video was not black.

Note that black images will be created if, for example, a second screen gets suddenly disconnected while recording on it. The most common case is, a user pulling from a HDMI cable.

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

here 5 is a safe value you know can work on the computer.

Note that this value MUST be bigger than zero. Also, that if you choose a big value, 
your computer will not cope up with the demand. It will try to grab more images 
than the ones your computer can produce. This will yield a video produced with fewer
images than the ones needed to maintain the FPS hence the video will play in a fast-forward
manner.

#### 2. Choose screen

You can query the available screens currently connected:

    List<ScreenReference> screens =  sr.listScreens();


Each one of the returned objects in the list holds enough information to start recording.
They also contain a thumbnail with the snapshot of the screen. This image can be used in a 
graphical interface to allow the user to visually choose which screen to record from. 

### 3. Start recording

    sr.startRecording(filePath);

This will immediatelly start recording the main screen, using the maximum FPS.

The parameter 'filePath' points to the file to be created. The extension you 
provide will be ignored and '.mp4' will be appended. 

As an example: if you specify the path 'c:\temp\my_video.avi' the system will create the video: 'c:\temp\my_video.mp4'

If you want more control, you can start recording using the complete method invocation:

        sr.startRecording(
                filePath,           // Path to base file. Extension will be ignored/appended
                30,                 // Seconds to record before stoping. 0 => do not stop
                screens.get(1),     // Specify what screen to record. Here we are using the second one.
                5,                  // the target FPS. -1 => maximum
                true,               // Inject timestamp in the recording?
                15);                // Split size in minutes, if needed. 0='no split'

This option allows more control on the recording method.

There is a running example in the class: EntryPoint.java

NOTE:
This software is not intended yet to be used in production systems nor in high availability environments.


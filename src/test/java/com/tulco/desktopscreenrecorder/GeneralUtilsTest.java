package com.tulco.desktopscreenrecorder;


import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GeneralUtilsTest {

    @org.junit.jupiter.api.Test
    void splitString() {
        assertEquals("0000",GeneralUtils.buildSplitStringSuffix(0));
        assertEquals("0001",GeneralUtils.buildSplitStringSuffix(1));
        assertEquals("0010",GeneralUtils.buildSplitStringSuffix(10));
        assertEquals("0100",GeneralUtils.buildSplitStringSuffix(100));
        assertEquals("1000",GeneralUtils.buildSplitStringSuffix(1000));
        assertEquals("9999",GeneralUtils.buildSplitStringSuffix(9999));
        assertThrows(IllegalArgumentException.class, () -> {
            GeneralUtils.buildSplitStringSuffix(10000);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            GeneralUtils.buildSplitStringSuffix(999999);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            GeneralUtils.buildSplitStringSuffix(-1);
        });
    }

    @Test
    void removeFileExtension() {
        assertEquals("pepe", GeneralUtils.removeFileExtension("pepe.mp4"));
        assertEquals("/users/johnd/temp/my_video_file.cool",
                GeneralUtils.removeFileExtension("/users/johnd/temp/my_video_file.cool.mp4"));
        assertEquals("v", GeneralUtils.removeFileExtension("v.mpeg"));
        assertEquals("c:\\data\\videos\\my_video",
                GeneralUtils.removeFileExtension("c:\\data\\videos\\my_video.avi"));
    }

    @Test
    void isBlack_01() {
        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        assertTrue(GeneralUtils.isBlack(bi));
    }
    @Test
    void isBlack_02() {
        BufferedImage bi = new BufferedImage(10000, 10000, BufferedImage.TYPE_INT_ARGB);
        assertTrue(GeneralUtils.isBlack(bi));
    }
    @Test
    void isBlack_03() throws IOException {
        BufferedImage image = ImageIO.read(new File("src/test/resources/noether.jpg"));
        assertFalse(GeneralUtils.isBlack(image));
    }
    @Test
    void isBlack_04() throws IOException {
        BufferedImage image = ImageIO.read(new File("src/test/resources/gt.jpg"));
        assertFalse(GeneralUtils.isBlack(image));
    }
}
package com.tulco.desktopscreenrecorder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralUtils {

    /**
     * Creates a string with the serialized value. E.g., 12 -> '0012'.
     *
     * @param serialSplit
     * @return
     */
    public static String buildSplitStringSuffix(int serialSplit) {
        final int MAX_VAL = 10000;
        if (serialSplit>=MAX_VAL) {
            throw new IllegalArgumentException("The maximum serializable value is "+MAX_VAL);
        }
        if (serialSplit<0) {
            throw new IllegalArgumentException("The minimum serializable value is 0. Provided: "+serialSplit);
        }
        int intSerial = MAX_VAL+serialSplit;
        return (""+intSerial).substring(1);
    }

    /**
     * Deletes the file extension at the end of a file name
     * given that it can consist of 3 or 4 chars.
     * @param filePath
     * @return
     */
    public static String removeFileExtension(String filePath) {
        if (filePath.matches(".+\\.[a-zA-Z0-9]{3,4}")) {
            int dotPos = filePath.lastIndexOf('.');
            filePath = filePath.substring(0, dotPos);
        }
        return filePath;
    }

    /**
     * Writes date and time to image.
     *
     * @param image
     */
    public static void addTimeStamp(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();
        g2d.setPaint(Color.red);
        g2d.setFont(new Font("Courier", Font.PLAIN, 25));
        String s = "DATE: "+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        FontMetrics fm = g2d.getFontMetrics();
        int x = image.getWidth() - fm.stringWidth(s) - 5;
        int y = fm.getHeight();
        g2d.drawString(s, x, y);
        g2d.dispose();
    }

    /**
     * Resizes an image to the given width and height.
     *
     * @param img
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static BufferedImage resize2(BufferedImage img, int newWidth, int newHeight) {
        if (newWidth==0 || newHeight==0) return img;
        if (newWidth<2 || newHeight<2) throw new IllegalArgumentException("Width and height must be bigger than 1");
        int w = img.getWidth();
        int h = img.getHeight();
        if (newWidth%2==1) newWidth++;
        if (newHeight%2==1) newHeight++;
        BufferedImage dimg = new BufferedImage(newWidth, newHeight, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    /**
     * Checks if an image is totally black.
     * This method is used to make sure we are
     * recording a valid screen area.
     * @param img
     * @return
     */
    public static boolean isBlack(BufferedImage img) {
        int xOffset = 0;
        int yOffset = 0;
        long okCount = 0;
        long pixelCount = 0;
        for (int x=xOffset; x<img.getWidth(); x=x+5) {
            for (int y=yOffset; y<img.getHeight(); y=y+5) {
                pixelCount++;
                int color = img.getRGB(x, y);
                int red = (color & 0x00ff0000) >> 16;
                int green = (color & 0x0000ff00) >> 8;
                int blue = color & 0x000000ff;
                if ((red+green+blue)!=0) okCount++;
                if (okCount>1000) return false;
            }
        }
        double okRatio = (double)okCount/(double)pixelCount;
        return okRatio<0.3d;
    }

    /**
     * Copies a buffered image.
     *
     * @param image
     * @return
     */
    public static BufferedImage deepCopyBufferedImage(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}

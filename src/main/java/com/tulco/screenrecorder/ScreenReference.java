package com.tulco.screenrecorder;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This holds a reference to a screen.
 */
public class ScreenReference {
    /**
     * The rectangle area of the screen
     */
    Rectangle rectangle;

    /**
     * A snapshot of the screen. It helps humans identify
     * what screen needs to be recorded.
     */
    BufferedImage snapshot = null;

    /**
     * Constructor
     * @param rectangle
     * @param snapshot
     */
    public ScreenReference(Rectangle rectangle, BufferedImage snapshot) {
        this.rectangle = rectangle;
        this.snapshot = snapshot;
    }

    /**
     * Returns the area of the screen.
     * @return
     */
    public Rectangle getRectangle() {
        return rectangle;
    }

    /**
     * Gets the snapshot of the screen
     * @return
     */
    public BufferedImage getSnapshot() {
        return snapshot;
    }

    /**
     * Computes the area in squared pixels of the screen.
     * @return
     */
    public long computeArea() {
        return rectangle.width* rectangle.height;
    }
}

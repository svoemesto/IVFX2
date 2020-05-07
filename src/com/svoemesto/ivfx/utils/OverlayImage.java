package com.svoemesto.ivfx.utils;


import javafx.geometry.Pos;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class OverlayImage {

    public static BufferedImage setOverlayFirstFrameFound(BufferedImage sourceImage) {
        String textToOverlay = "\u255A"+"\u2550"+"\u2550"+"\u2550"+"\u2550"+"\u255D";
        Color textColor = Color.RED;
        Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 24);
        Pos textPosition = Pos.TOP_CENTER;
        float opaque = 1.0f;
        return setTextOverlay(sourceImage, textToOverlay, textColor, textFont, textPosition, opaque);
    }

    public static BufferedImage setOverlayLastFrameFound(BufferedImage sourceImage) {
        String textToOverlay = "\u2554"+"\u2550"+"\u2550"+"\u2550"+"\u2550"+"\u2557";
        Color textColor = Color.RED;
        Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 24);
        Pos textPosition = Pos.BOTTOM_CENTER;
        float opaque = 1.0f;
        return setTextOverlay(sourceImage, textToOverlay, textColor, textFont, textPosition, opaque);
    }

    public static BufferedImage setOverlayFirstFrameManual(BufferedImage sourceImage) {
        String textToOverlay = "\u255A"+"\u2550"+"\u2550"+"\u2550"+"\u2550"+"\u255D";
        Color textColor = Color.GREEN;
        Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 24);
        Pos textPosition = Pos.TOP_CENTER;
        float opaque = 1.0f;
        return setTextOverlay(sourceImage, textToOverlay, textColor, textFont, textPosition, opaque);
    }

    public static BufferedImage setOverlayLastFrameManual(BufferedImage sourceImage) {
        String textToOverlay = "\u2554"+"\u2550"+"\u2550"+"\u2550"+"\u2550"+"\u2557";
        Color textColor = Color.GREEN;
        Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 24);
        Pos textPosition = Pos.BOTTOM_CENTER;
        float opaque = 1.0f;
        return setTextOverlay(sourceImage, textToOverlay, textColor, textFont, textPosition, opaque);
    }

    public static BufferedImage cancelOverlayFirstFrameManual(BufferedImage sourceImage) {
        String textToOverlay = "[";
        Color textColor = Color.ORANGE;
        Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 48);
        Pos textPosition = Pos.CENTER_LEFT;
        float opaque = 0.2f;
        return setTextOverlay(sourceImage, textToOverlay, textColor, textFont, textPosition, opaque);
    }

    public static BufferedImage cancelOverlayLastFrameManual(BufferedImage sourceImage) {
        String textToOverlay = "]";
        Color textColor = Color.ORANGE;
        Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 48);
        Pos textPosition = Pos.CENTER_RIGHT;
        float opaque = 0.2f;
        return setTextOverlay(sourceImage, textToOverlay, textColor, textFont, textPosition, opaque);
    }

    public static BufferedImage setOverlayIFrame(BufferedImage sourceImage) {
        String textToOverlay = "I";
        Color textColor = Color.BLUE;
        Font textFont = new Font(Font.MONOSPACED,Font.BOLD, 26);
        Pos textPosition = Pos.CENTER;
        float opaque = 0.9f;
        return setTextOverlay(sourceImage, textToOverlay, textColor, textFont, textPosition, opaque);
    }

    public static BufferedImage setOverlayUnderlinePlate(BufferedImage sourceImage) {
        String textToOverlay = "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588";
        Color textColor = Color.BLACK;
        Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 14);
        Pos textPosition = Pos.BOTTOM_CENTER;
        float opaque = 1.0f;
        return setTextOverlay(sourceImage, textToOverlay, textColor, textFont, textPosition, opaque);
    }

    public static BufferedImage setOverlayUnderlineText(BufferedImage sourceImage, String text) {

        String textToOverlay = text;
        Color textColor = Color.YELLOW;
        Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 14);
        Pos textPosition = Pos.BOTTOM_CENTER;
        float opaque = 1.0f;
        return setTextOverlay(setOverlayUnderlinePlate(sourceImage), textToOverlay, textColor, textFont, textPosition, opaque);
    }

    public static BufferedImage setTextOverlay(BufferedImage sourceImage, String textToOverlay, Color textColor, Font textFont, Pos textPosition, float opaque) {
        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        int imageType = BufferedImage.TYPE_INT_ARGB;
        BufferedImage resultImage = new BufferedImage(imageW, imageH, imageType);
        Graphics2D graphics2D = (Graphics2D) resultImage.getGraphics();
        graphics2D.drawImage(sourceImage,0,0,null);
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque);
        graphics2D.setComposite(alphaChannel);
        graphics2D.setColor(textColor);
        graphics2D.setFont(textFont);
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds(textToOverlay, graphics2D);
        int rectW = (int)rect.getWidth();
        int rectH = (int)rect.getHeight();
        int centerX=0, centerY=0;
        switch (textPosition) {
            case CENTER:
                centerX = (imageW - rectW) / 2;
                centerY = imageH / 2 + rectH/5;
                break;
            case CENTER_LEFT:
                centerX = 0;
                centerY = imageH / 2 + rectH/5;
                break;
            case CENTER_RIGHT:
                centerX = (imageW - rectW);
                centerY = imageH / 2 + rectH/5;
                break;
            case TOP_CENTER:
                centerX = (imageW - rectW) / 2;
                centerY = 0 + rectH/2;
                break;
            case BOTTOM_CENTER:
                centerX = (imageW - rectW) / 2;
                centerY = imageH - rectH/8;
                break;
        }


        graphics2D.drawString(textToOverlay, centerX, centerY);
        graphics2D.dispose();

        return resultImage;
    }

}

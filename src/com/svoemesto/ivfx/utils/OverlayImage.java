package com.svoemesto.ivfx.utils;


import javafx.geometry.Pos;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
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

    public static BufferedImage setOverlayIsBodyScene(BufferedImage sourceImage) {
        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        float opaque = 1.0f;
        Color color = Color.ORANGE;
        return setOverlayRectangle(sourceImage, 0,0,10,imageH, color, opaque);
    }

    public static BufferedImage setOverlayIsStartScene(BufferedImage sourceImage) {
        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        float opaque = 1.0f;
        Color color = Color.ORANGE;
        return setOverlayRectangle(sourceImage, 10,0,20,10, color, opaque);
    }

    public static BufferedImage setOverlayIsEndScene(BufferedImage sourceImage) {
        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        float opaque = 1.0f;
        Color color = Color.ORANGE;
        return setOverlayRectangle(sourceImage, 10,imageH-10,20,10, color, opaque);
    }

    public static BufferedImage setOverlayIsBodyEvent(BufferedImage sourceImage) {
        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        float opaque = 1.0f;
        Color color = Color.GREEN;
        return setOverlayRectangle(sourceImage, imageW-11,0,10,imageH, color, opaque);
    }

    public static BufferedImage setOverlayIsStartEvent(BufferedImage sourceImage) {
        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        float opaque = 1.0f;
        Color color = Color.GREEN;
        return setOverlayRectangle(sourceImage, imageW-31,0,20,10, color, opaque);
    }

    public static BufferedImage setOverlayIsEndEvent(BufferedImage sourceImage) {
        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        float opaque = 1.0f;
        Color color = Color.GREEN;
        return setOverlayRectangle(sourceImage, imageW-31,imageH-10,20,10, color, opaque);
    }


    public static BufferedImage setOverlayRectangle(BufferedImage sourceImage, int x, int y, int width, int height, Color color, float opaque) {
        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        int imageType = BufferedImage.TYPE_INT_ARGB;
        BufferedImage resultImage = new BufferedImage(imageW, imageH, imageType);
        Graphics2D graphics2D = (Graphics2D) resultImage.getGraphics();
        graphics2D.drawImage(sourceImage,0,0,null);
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque);
        graphics2D.setComposite(alphaChannel);
        graphics2D.setColor(color);
        graphics2D.fillRect ( x, y, width, height);
        graphics2D.dispose();
        return resultImage;
    }

    public static BufferedImage setOverlayTriangle(BufferedImage sourceImage, int corner, double percentOfLowerSize, Color color, float opaque) {
        // corner: 1 - верх лево, 2 - верх право, 3 - низ право, 4 - низ лево
        // percentOfLowerSize: 0-1 в частях от наиболее короткой стороны
        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        int imageType = BufferedImage.TYPE_INT_ARGB;

        int triangleSide = imageW > imageH ? (int)(imageH * percentOfLowerSize) : (int)(imageW * percentOfLowerSize);

        BufferedImage resultImage = new BufferedImage(imageW, imageH, imageType);
        Graphics2D graphics2D = (Graphics2D) resultImage.getGraphics();
        graphics2D.drawImage(sourceImage,0,0,null);
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque);
        graphics2D.setComposite(alphaChannel);

        graphics2D.setColor(color);
        graphics2D.setPaint(color);

        Path2D myPath = new Path2D.Double();
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0, x3 = 0, y3 = 0;
        switch (corner) {
            case 1:
                x1 = triangleSide;
                y1 = 0;
                x2 = 0;
                y2 = triangleSide;
                x3 = 0;
                y3 = 0;
                break;
            case 2:
                x1 = imageW - triangleSide;
                y1 = 0;
                x2 = imageW;
                y2 = triangleSide;
                x3 = imageW;
                y3 = 0;
                break;
            case 3:
                x1 = imageW;
                y1 = imageH - triangleSide;
                x2 = imageW - triangleSide;
                y2 = imageH;
                x3 = imageW;
                y3 = imageH;
                break;
            case 4:
                x1 = triangleSide;
                y1 = imageH;
                x2 = 0;
                y2 = imageH - triangleSide;
                x3 = 0;
                y3 = imageH;
                break;
            default:
        }

        myPath.moveTo(x1, y1);
        myPath.lineTo(x2, y2);
        myPath.lineTo(x3, y3);
        myPath.closePath();

        graphics2D.fill (myPath);
        graphics2D.dispose();
        return resultImage;
    }

    public static BufferedImage setOverlayUnderlineText(BufferedImage sourceImage, String text) {

        String textToOverlay = text;
        Color textColor = Color.YELLOW;
        Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN, 12);
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
                centerY = imageH - rectH/8 - 2;
                break;
        }


        graphics2D.drawString(textToOverlay, centerX, centerY);
        graphics2D.dispose();

        return resultImage;
    }

    public static BufferedImage resizeImage(BufferedImage sourceImage, int resizedW, int resizedH, Color bgColor) {

        int imageW = sourceImage.getWidth();
        int imageH = sourceImage.getHeight();
        int imageType = BufferedImage.TYPE_INT_ARGB;

        double scaleCoeff = Math.min((double)resizedW / imageW, (double)resizedH / imageH);

        BufferedImage resultImage = new BufferedImage(resizedW, resizedH, imageType);
        BufferedImage afterResize = new BufferedImage(resizedW, resizedH, imageType);

        Graphics2D graphics2D = (Graphics2D) resultImage.getGraphics();

//        graphics2D.setColor (bgColor);
//        graphics2D.fillRect ( 0, 0, resizedW, resizedH);

        AffineTransform at = new AffineTransform();
        at.scale(scaleCoeff, scaleCoeff);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        afterResize = scaleOp.filter(sourceImage, afterResize);

        int x = (int)(resizedW - imageW * scaleCoeff) / 2;
        int y = (int)(resizedH - imageH * scaleCoeff) / 2;

//        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
//        graphics2D.setComposite(alphaChannel);

        graphics2D.drawImage(afterResize,x,y,  null);
        graphics2D.dispose();

        return resultImage;
    }

}

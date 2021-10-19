package com.svoemesto.ivfx.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FFmpegTest {

    public static final String PATH_TO_FFMPEG = "D:\\ffmpeg-shared\\bin\\ffmpeg.exe";

    public static void main(String[] args) throws IOException, InterruptedException {
        String sourceFile = "D:\\NewIVFXpoject\\23.97fps.mxf";
        String outputFolder = "D:\\NewIVFXpoject\\Snapshots";
        int frameCount = 1;

        int w = 135;
        int h = 75;
        String resolution = w + "x" + h;

        int frameNumber = 6666;
        double fps = 23.976;
        int millisecs = FFmpeg.getDurationByFrameNumber(frameNumber, fps);
        String ss = FFmpeg.convertDurationToString(millisecs);

        System.out.println("Frame number: " + frameNumber);
        System.out.println("Milliseconds: " + millisecs);
        System.out.println("Duration: " + ss);

//        List<String> param = new ArrayList<String>();
//        param.add("-ss");
//        param.add(ss);
//        param.add("-i");
//        param.add(sourceFile);
//        param.add("-s");
//        param.add(resolution);
//        param.add("-vframes");
//        param.add(String.valueOf(frameCount));
//        param.add("-q:v");
//        param.add("2");
//        param.add("-y");
//        param.add(outputFolder + "\\frame.jpg");
//        String executeResult = FFmpeg.executeFFmpeg(param);

        CreateVideo.getFrame(sourceFile, frameNumber, fps, w, h, outputFolder);

    }
}

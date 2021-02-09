package com.svoemesto.ivfx.utils;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FFmpeg {

    public static void main(String[]args) throws IOException, InterruptedException {

        int frameNumber = 15322;
        double frameRate = 23.976;

        int duration = getDurationByFrameNumber(frameNumber,frameRate);
        System.out.println(duration);

        frameNumber = getFrameNumberByDuration(duration,frameRate);
        System.out.println(frameNumber);

        System.out.println(convertDurationToString(duration));


    }

    public static final String PATH_TO_FFMPEG = "D:\\ffmpeg-shared\\bin\\ffmpeg.exe";
    public static final String PATH_TO_FFPROBE = "D:\\ffmpeg-shared\\bin\\ffprobe.exe";
    public static final String PATH_TO_MKVMERGE = "D:\\iGOT\\bin\\mkvmerge.exe";

    public static int getFrameNumberByDuration(int duration, double fps) {
        double dur1fr = 1000 / fps;
        double doubleFrames = (duration / dur1fr) + 1;
        int frameNumber = (int)Math.round(doubleFrames);
//        System.out.println(doubleFrames);
        return frameNumber;
    }

    public static Integer getDurationByFrameNumber(int frameNumber, double fps) {
        double dur1fr = 1000 / fps;
        int countFramesBefore = frameNumber - 1;
        double durDouble = countFramesBefore * dur1fr;
        if (durDouble <0 ) durDouble = 0;
//        int duration = (int)Math.round(durDouble);
        int duration = (int)Math.ceil(durDouble);
//        System.out.println(durDouble);
        return duration;
    }

    public static Integer convertStringToDuration(String string) {
        String[] lines = string.split(":");
        int hours = Integer.parseInt(lines[0]);
        int minutes = Integer.parseInt(lines[1]);
        double seconds = Double.parseDouble(lines[2]);
        int duration = hours*3600000 + minutes*60000 + (int)(seconds*1000);
        return duration;
    }

    public static String convertDurationToString(int duration){
        int hours = duration / 3_600_000;
        int minutes = (duration - hours*3_600_000) / 60_000;
        int seconds = (duration - hours*3_600_000 - minutes*60_000) / 1000;
        int milliseconds = (duration - hours*3_600_000 - minutes*60_000 - seconds*1000);
        String out = String.valueOf(hours) + ":" +
                String.format("%02d",minutes) + ":" +
                String.format("%02d",seconds) + "."+
                String.format("%03d",milliseconds);
        return out;
    }

    public static List<Integer> getListIFrames(String mediaFile, double fps) throws IOException, InterruptedException {
        List<String> param = new ArrayList<String>();
        param.add("-skip_frame");
        param.add("nokey");
        param.add("-select_streams");
        param.add("v");
        param.add("-show_frames");
        param.add(mediaFile);
        String executeResult = executeFFprobe(param);
//        System.out.printf(executeResult);
        List<Integer> list = new ArrayList<>();
        String regExp = "(?<=\\[FRAME\\]\r\n)[\\w|\\W]+?(?=\\[/FRAME\\]\r\n)";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(executeResult);
        while (matcher.find()) {
            int startPosition = matcher.start();
            int endPosition = matcher.end();
            String result = executeResult.substring(startPosition, endPosition);
            String[] resultLines = result.split("\\r\\n");
            for (int i = 0; i < resultLines.length ; i++) {
                String[] line = resultLines[i].split("=");
                if (line.length > 0) {
                    if (line[0].equals("pkt_pts")) {
//                    if (line[0].equals("pkt_dts")) {
                        try {
                            int findedResult = Integer.parseInt(line[1]);
                            list.add(getFrameNumberByDuration(findedResult,fps));
                        } catch (NumberFormatException ex) {
//                            ex.printStackTrace();
                        }



                    }
                }
            }
//            System.out.println("начало: " + startPosition + ", конец: " + endPosition);
//            System.out.println(result);
//            System.out.println("----------------------------------------------");
        }
//        for (int i : list) {
//            System.out.println(i);
//        }

        return list;
    }

    public static void createFrames(String mediaFile, String imageFile, int w, int h) throws IOException, InterruptedException {
        int frameCount = Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(mediaFile,"Video","FrameCount"));
        String resolution = w + "x" + h;

        List<String> param = new ArrayList<String>();
        param.add("-i");
        param.add(mediaFile);
        param.add("-s");
        param.add(resolution);
        param.add("-vframes");
        param.add(String.valueOf(frameCount));
        param.add("-y");
        param.add(imageFile);
        String executeResult = executeFFmpeg(param);

    }

    public static void createFrameFromVideo(String mediaFile, String imageFile, int numberFrame, int w, int h) throws IOException, InterruptedException {
        double fps = Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(mediaFile,"Video","FrameRate"));
        int frameCount = Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(mediaFile,"Video","FrameCount"));
        int duration = (int)(((double) numberFrame / fps) * 1000);
        String strDur = convertDurationToString(duration);
        String resolution = w + "x" + h;
        if (numberFrame <= frameCount) {
            List<String> param = new ArrayList<String>();
            param.add("-i");
            param.add(mediaFile);
            param.add("-s");
            param.add(resolution);
            param.add("-ss");
            param.add(strDur);
            param.add("-vframes");
            param.add("1");
            param.add("-y");
            param.add(imageFile);
            String executeResult = executeFFmpeg(param);
        }


    }

    public static void createVideo(String inputFile,
                                   String outputFile,
                                   int firstFrameNumber,
                                   int lastFrameNumber,
                                   String videoCodec,
                                   int videoBitrate,
                                   String audioCodec,
                                   int audioBitrate,
                                   int audioFreq
    ) throws IOException, InterruptedException {
        List<String> param = new ArrayList<String>();

        double fps = Double.parseDouble(MediaInfo.getInfoBySectionAndParameter(inputFile,"Video","FrameRate"));
        int frameCount = Integer.parseInt(MediaInfo.getInfoBySectionAndParameter(inputFile,"Video","FrameCount"));
        if (lastFrameNumber <= firstFrameNumber) {
            lastFrameNumber = frameCount - firstFrameNumber + 1;
        }

        int framesToCode = lastFrameNumber - firstFrameNumber + 1;
        String start = convertDurationToString((int)(((double) (firstFrameNumber - 1) / fps) * 1000));
//        String end = ConvertDurationToString((int)(((double) lastFrameNumber / fps) * 1000));
        String end = convertDurationToString((int)(((double) (framesToCode - 1) / fps) * 1000));

        for (int i = 1; i <= 1 ; i++) {
            param.add("-y");

            param.add("-i");
            param.add(inputFile);

            param.add("-ss");
            param.add(start);

            param.add("-f");
            param.add("mp4");

            param.add("-r");
            param.add(String.valueOf(fps));

            param.add("-vframes");
            param.add(String.valueOf(framesToCode));

            param.add("-q");
            param.add("1");

            param.add("-c:v");
            param.add(videoCodec);

            param.add("-b:v");
            param.add(String.valueOf(videoBitrate));

            param.add("-c:a");
            param.add(audioCodec);

            param.add("-b:a");
            param.add(String.valueOf(audioBitrate));

            param.add("-ar");
            param.add(String.valueOf(audioFreq));


//            param.add("-t");
//            param.add(end);



//            param.add("-pass");
//            param.add(String.valueOf(i));

//            if (i == 1) {
//                param.add("tmp.mp4");
//            } else {
            param.add(outputFile);
//            }


            String executeResult = executeFFmpeg(param);
//            System.out.println(executeResult);
        }

    }

    public static void createLosslessVideo(String mediaFile, String losslessFile) throws IOException, InterruptedException {
        List<String> param = new ArrayList<String>();
        param.add("-i");
        param.add(mediaFile);
        param.add("-c:v");
        param.add("ffv1");
        param.add("-c:a");
        param.add("pcm_alaw");
        param.add("-y");
        param.add(losslessFile);
        executeFFmpeg(param);

    }

    public static String getInfo(String mediaFile) throws IOException, InterruptedException {
        List<String> param = new ArrayList<String>();
        param.add("-i");
        param.add(mediaFile);
        return executeFFmpeg(param);
    }

    static String executeMKVmerge(List<String> parameters) throws IOException, InterruptedException {
        final String exePath = PATH_TO_MKVMERGE;
        List<String> param = new ArrayList<String>();
        param.add(exePath);
        if (parameters.size() > 0) {
            for (int i = 0; i < parameters.size() ; i++) {
                param.add(parameters.get(i));
            }
        }
        final ProcessBuilder builder = new ProcessBuilder(param);
        builder.redirectErrorStream(true);
        final Process process = builder.start();

        final StringBuilder buffer = new StringBuilder();
        try (Reader reader = new InputStreamReader(process.getInputStream())) {
            for (int i; (i = reader.read()) != -1; ) {
                buffer.append((char) i);
            }
        }

        final int status = process.waitFor();
//        if (status == 0) {
        String out = buffer.toString();
        return out.substring(0,out.length()-2);
//        }

//        throw new IOException("Unexpected exit status " + status);

    }

    static String executeFFmpeg(List<String> parameters) throws IOException, InterruptedException {
        final String exePath = PATH_TO_FFMPEG;
        List<String> param = new ArrayList<String>();
        param.add(exePath);
        if (parameters.size() > 0) {
            for (int i = 0; i < parameters.size() ; i++) {
                param.add(parameters.get(i));
            }
        }
        final ProcessBuilder builder = new ProcessBuilder(param);
        builder.redirectErrorStream(true);
        final Process process = builder.start();

        final StringBuilder buffer = new StringBuilder();
        try (Reader reader = new InputStreamReader(process.getInputStream())) {
            for (int i; (i = reader.read()) != -1; ) {
                buffer.append((char) i);
            }
        }

        final int status = process.waitFor();
//        if (status == 0) {
        String out = buffer.toString();
        return out.substring(0,out.length()-2);
//        }

//        throw new IOException("Unexpected exit status " + status);

    }

    static String executeFFprobe(List<String> parameters) throws IOException, InterruptedException {
        final String exePath = PATH_TO_FFPROBE;
        List<String> param = new ArrayList<String>();
        param.add(exePath);
        if (parameters.size() > 0) {
            for (int i = 0; i < parameters.size() ; i++) {
                param.add(parameters.get(i));
            }
        }
        final ProcessBuilder builder = new ProcessBuilder(param);
        builder.redirectErrorStream(true);
        final Process process = builder.start();

        final StringBuilder buffer = new StringBuilder();
        try (Reader reader = new InputStreamReader(process.getInputStream())) {
            for (int i; (i = reader.read()) != -1; ) {
                buffer.append((char) i);
            }
        }

        final int status = process.waitFor();
//        if (status == 0) {
        String out = buffer.toString();
        return out.substring(0,out.length()-2);
//        }

//        throw new IOException("Unexpected exit status " + status);

    }

}

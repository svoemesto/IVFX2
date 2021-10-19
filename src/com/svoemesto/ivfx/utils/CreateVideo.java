package com.svoemesto.ivfx.utils;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXFiles;
import com.svoemesto.ivfx.tables.IVFXFilesTracks;
import com.svoemesto.ivfx.tables.IVFXShots;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class CreateVideo {

    public static BufferedImage getFrame(String pathToFile, int frameNumber, double fps, int w, int h, String pathToTempFolder) throws IOException, InterruptedException {

        String resolution = w + "x" + h;
        int millisecs = FFmpeg.getDurationByFrameNumber(frameNumber, fps);
        String ss = FFmpeg.convertDurationToString(millisecs);
        int frameCount = 1;
        String pathToImageFile = pathToTempFolder + "\\" + frameNumber + ".jpg";

        List<String> param = new ArrayList<String>();
        param.add("-ss");
        param.add(ss);
        param.add("-i");
        param.add(pathToFile);
        param.add("-s");
        param.add(resolution);
        param.add("-vframes");
        param.add(String.valueOf(frameCount));
        param.add("-q:v");
        param.add("2");
        param.add("-y");
        param.add(pathToImageFile);
        String executeResult = FFmpeg.executeFFmpeg(param);

        File file = new File(pathToImageFile);
        if (file.exists()) {
            BufferedImage bufferedImage = ImageIO.read(file);
            FileUtils.deleteQuietly(file);
            return bufferedImage;
        }
        return null;
    }

    public static void createFramesPreview(IVFXFiles ivfxFile) {

        String line;
        List<String> cmd = new ArrayList<>();

        int w = 135;
        int h = 75;

        int fileWidth = ivfxFile.getWidth();
        int fileHeight = ivfxFile.getHeight();
        double fileAspect = (double) fileWidth / (double) fileHeight;
        double frameAspect = (double) w / (double) h;

        String filter = "";

        if (fileAspect > frameAspect) {
            int frameHeight = (int)((double) w / fileAspect);
            filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
        } else {
            int frameWidth = (int)((double) h * fileAspect);
            filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
        }

        String pathToFFmpeg = FFmpeg.PATH_TO_FFMPEG;
        String mediaFile =  ivfxFile.getSourceName();
        int framesCount = ivfxFile.getFramesCount();
        String imageFile = ivfxFile.getFolderFramesPreview()+"\\"+ ivfxFile.getShortName()+ IVFXFiles.FRAMES_PREFIX +"%%06d.jpg";

        String cmdFileName = ivfxFile.getIvfxProject().getFolder()+"\\Frames\\" + ivfxFile.getShortName()+"_CreateFramesPreview.cmd";


        String resolution = w + "x" + h;
        List<String> param = new ArrayList<>();

        cmd.add("REM ======================================================="+"\n");
        cmd.add("REM Create frames PREVIEW for " + ivfxFile.getShortName() + "\n");
        cmd.add("REM ======================================================="+"\n\n");


        param.add("\"" + pathToFFmpeg + "\"");
        param.add("-i");
        param.add("\"" + mediaFile + "\"");
        param.add("-s");
        param.add(resolution);
        param.add("-vframes");
        param.add(String.valueOf(framesCount));
        param.add("-qscale:v");
        param.add("1");
//        param.add("-vf");
//        param.add(filter);
        param.add("-y");
        param.add("\"" + imageFile + "\"");

//        try {
//            FFmpeg.executeFFmpeg(param);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }

        line = "";
        for (String parameter : param) {
            line = line + parameter + " ";
        }
        line = line.substring(0,line.length()-1)+"\n";
        cmd.add(line);

        String text = "";
        for (String parameter : cmd) {
            text = text + parameter;
        }

        try {
            File cmdFile = new File(cmdFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(cmdFile));
            writer.write(text);
            writer.flush();
            writer.close();
            Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + cmdFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void createFramesFull(IVFXFiles ivfxFile) {

        String line;
        List<String> cmd = new ArrayList<>();


        int w = 720;
        int h = 400;

        int fileWidth = ivfxFile.getWidth();
        int fileHeight = ivfxFile.getHeight();
        double fileAspect = (double) fileWidth / (double) fileHeight;
        double frameAspect = (double) w / (double) h;

        String filter = "";

        if (fileAspect > frameAspect) {
            int frameHeight = (int)((double) w / fileAspect);
            filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
        } else {
            int frameWidth = (int)((double) h * fileAspect);
            filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
        }


        String pathToFFmpeg = FFmpeg.PATH_TO_FFMPEG;
        String mediaFile = ivfxFile.getSourceName();
        int framesCount = ivfxFile.getFramesCount();
        String imageFile = ivfxFile.getFolderFramesFull()+"\\"+ ivfxFile.getShortName()+ IVFXFiles.FRAMES_PREFIX +"%%06d.jpg";

        String cmdFileName = ivfxFile.getIvfxProject().getFolder()+"\\Frames\\" + ivfxFile.getShortName()+"_CreateFramesFull.cmd";

        String resolution = w + "x" + h;
        List<String> param = new ArrayList<>();

        cmd.add("REM ======================================================="+"\n");
        cmd.add("REM Create frames FULL for " + ivfxFile.getShortName() + "\n");
        cmd.add("REM ======================================================="+"\n\n");

        param.add("\"" + pathToFFmpeg + "\"");
        param.add("-i");
        param.add("\"" + mediaFile + "\"");
        param.add("-s");
        param.add(resolution);
        param.add("-vframes");
        param.add(String.valueOf(framesCount));
        param.add("-qscale:v");
        param.add("2");
        param.add("-vf");
        param.add(filter);
        param.add("-y");
        param.add("\"" + imageFile + "\"");

//        try {
//            FFmpeg.executeFFmpeg(param);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }

        line = "";
        for (String parameter : param) {
            line = line + parameter + " ";
        }
        line = line.substring(0,line.length()-1)+"\n";
        cmd.add(line);

        String text = "";
        for (String parameter : cmd) {
            text = text + parameter;
        }

        try {
            File cmdFile = new File(cmdFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(cmdFile));
            writer.write(text);
            writer.flush();
            writer.close();
            Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + cmdFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void createPreviewMP4(IVFXFiles ivfxFile) {

        String line;
        List<String> cmd = new ArrayList<>();

        String folderPreview = ivfxFile.getFolderMp4();
        File file = new File(ivfxFile.getSourceName());
        String fileName = file.getName();
        String fileNameWOExt = fileName.substring(0,fileName.lastIndexOf("."));

        int w = 720;
        int h = 400;

        int fileWidth = ivfxFile.getWidth();
        int fileHeight = ivfxFile.getHeight();
        double fileAspect = (double) fileWidth / (double) fileHeight;
        double frameAspect = (double) w / (double) h;

        String filter = "";

        if (fileAspect > frameAspect) {
            int frameHeight = (int)((double) w / fileAspect);
            filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
        } else {
            int frameWidth = (int)((double) h * fileAspect);
            filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
        }

        String pathToFFmpeg = FFmpeg.PATH_TO_FFMPEG;
        String mediaFile = ivfxFile.getSourceName();

        String cmdFileName = ivfxFile.getIvfxProject().getFolder()+"\\Video\\" + ivfxFile.getShortName()+"_CreatePreviewMP4.cmd";

        String resolution = w + "x" + h;
        List<String> param = new ArrayList<>();

        cmd.add("REM ======================================================="+"\n");
        cmd.add("REM Create preview MP4 for " + ivfxFile.getShortName() + "\n");
        cmd.add("REM ======================================================="+"\n\n");

        param.add("\"" + pathToFFmpeg + "\"");
        param.add("-i");
        param.add("\"" + mediaFile + "\"");
        param.add("-s");
        param.add(resolution);
        param.add("-c:v");
        param.add("libx264");
        param.add("-b:v");
        param.add("500000");
        param.add("-c:a");
        param.add("aac");
        param.add("-b:a");
        param.add("196608");
        param.add("-ar");
        param.add("48000");
        param.add("-f");
        param.add("mp4");
        param.add("-vf");
        param.add(filter);
        param.add("-y");
        param.add("\"" + folderPreview + "\\" + fileNameWOExt + "_preview.mp4" + "\"");

        line = "";
        for (String parameter : param) {
            line = line + parameter + " ";
        }
        line = line.substring(0,line.length()-1)+"\n";
        cmd.add(line);

        String text = "";
        for (String parameter : cmd) {
            text = text + parameter;
        }

        try {
            File cmdFile = new File(cmdFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(cmdFile));
            writer.write(text);
            writer.flush();
            writer.close();
            Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + cmdFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // создание CMD для: 1. создания лосслесс-файла. 2. резки его на планы. 3. кодирования планов
    public static void createCmdToAllStepsConvertVideofile(List<IVFXFiles> listFiles,
                                                           boolean isCreateMP4,
                                                           boolean isCreateFramesPreview,
                                                           boolean isCreateFramesMedium,
                                                           boolean isCreateFramesFull,
                                                           boolean isCreateFaces,
                                                           boolean isCreateLossless,
                                                           boolean isCreateShots,
                                                           boolean isCreateShotsMXFaudioYES,
                                                           boolean isCreateShotsMXFaudioNO,
                                                           boolean isDeleteLossless,
                                                           boolean isRunCmd) {

        String cmdConcatAllFileName = listFiles.get(0).getIvfxProject().getFolder()+"\\Video\\_Concat.cmd";
        String cmdAllFiles = listFiles.get(0).getIvfxProject().getFolder()+"\\Video\\_AllStepsConvert.cmd";
        String textConcatAll = "";
        String textAll = "";

        for (IVFXFiles ivfxFile: listFiles) {

            List<String> cmd = new ArrayList<>();
            List<String> lstConcat = new ArrayList<>();

            String line;
            String pathToFFmpeg = FFmpeg.PATH_TO_FFMPEG;
            String pathToSourceVideofile = ivfxFile.getSourceName();

            String pathToLosslessFolder =  ivfxFile.getFolderLossless();
            File folderLossless = new File(pathToLosslessFolder);
            if (!folderLossless.exists()) {
                folderLossless.mkdirs();
            }

            String pathToLosslessFile = ivfxFile.getPathToFileLossless();

            String pathToShotsFolder = ivfxFile.getFolderShots();
            File folderShots = new File(pathToShotsFolder);
            if (!folderShots.exists()) {
                folderShots.mkdirs();
            }

            String pathToFolderFramesFull = ivfxFile.getFolderFramesFull();
            File folderFramesFull = new File(pathToFolderFramesFull);
            if (!folderFramesFull.exists()) {
                folderFramesFull.mkdirs();
            }

            String pathToFolderFramesMedium = ivfxFile.getFolderFramesMedium();
            File folderFramesMedium = new File(pathToFolderFramesMedium);
            if (!folderFramesMedium.exists()) {
                folderFramesMedium.mkdirs();
            }

            String pathToFolderFramesPreview = ivfxFile.getFolderFramesPreview();
            File folderFramesPreview = new File(pathToFolderFramesPreview);
            if (!folderFramesPreview.exists()) {
                folderFramesPreview.mkdirs();
            }

            String cmdFileName = ivfxFile.getIvfxProject().getFolder()+"\\Video\\" + ivfxFile.getShortName()+"_AllStepsConvert.cmd";
            String cmdConcatFileName = ivfxFile.getIvfxProject().getFolder()+"\\Video\\" + ivfxFile.getShortName()+"_Concat.cmd";

            String lstConcatFileName = ivfxFile.getIvfxProject().getFolder()+"\\Video\\" + ivfxFile.getShortName()+"_Concat.txt";

//            String cmdConcat = "\"" + pathToFFmpeg + "\"" + " -f concat -safe 0 -i " + lstConcatFileName + " -map 0 -c copy " + ivfxFile.getIvfxProject().getFolder()+"\\Video\\Concat\\" + ivfxFile.getShortName() +".mp4\n";

            String cmdConcat = "\"" + FFmpeg.PATH_TO_FFMPEG + "\"" + " -f concat -safe 0 -i " + "\"" + lstConcatFileName + "\"" + " -map 0:v:0 ";
            for (IVFXFilesTracks track:IVFXFilesTracks.loadList(ivfxFile)) {
                if (track.getType().equals("Audio")) {
                    if (track.isUse()) {
                        String typeorder = track.getProperty("@typeorder");
                        if (typeorder == null) typeorder = "1";
                        if (typeorder != null) {
                            cmdConcat = cmdConcat + "-map 0:a:" + (Integer.parseInt(typeorder)-1) + " ";
                        }
                    }
                }
            }
            cmdConcat = cmdConcat + " -c copy -y " +"\"" +  ivfxFile.getIvfxProject().getFolder()+"\\Video\\Concat\\" + ivfxFile.getShortName() +"." + ivfxFile.getIvfxProject().getVideoContainer() + "\"" + "\n";

            List<String> param;

            if (isCreateMP4) {

                String folderPreview = ivfxFile.getFolderMp4();
                File file = new File(ivfxFile.getSourceName());
                String fileName = file.getName();
                String fileNameWOExt = fileName.substring(0,fileName.lastIndexOf("."));

                int w = 720;
                int h = 400;

                int fileWidth = ivfxFile.getWidth();
                int fileHeight = ivfxFile.getHeight();
                double fileAspect = (double) fileWidth / (double) fileHeight;
                double frameAspect = (double) w / (double) h;

                String filter = "";

                if (fileAspect > frameAspect) {
                    int frameHeight = (int)((double) w / fileAspect);
                    filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
                } else {
                    int frameWidth = (int)((double) h * fileAspect);
                    filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
                }

                String mediaFile = ivfxFile.getSourceName();

                String resolution = w + "x" + h;
                param = new ArrayList<>();

                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Create preview MP4 for " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");

                param.add("\"" + pathToFFmpeg + "\"");
                param.add("-i");
                param.add("\"" + mediaFile + "\"");
                param.add("-s");
                param.add(resolution);
                param.add("-c:v");
                param.add("libx264");
                param.add("-b:v");
                param.add("500000");
                param.add("-c:a");
                param.add("aac");
                param.add("-b:a");
                param.add("128492");
                param.add("-ar");
                param.add("48000");
                param.add("-ac");
                param.add("2");
                param.add("-f");
                param.add("mp4");
                param.add("-vf");
                param.add(filter);
                param.add("-y");
                param.add("\"" + folderPreview + "\\" + fileNameWOExt + "_preview.mp4" + "\"");

                line = "";
                for (String parameter : param) {
                    line = line + parameter + " ";
                }
                line = line.substring(0,line.length()-1)+"\n";
                cmd.add(line);

            }

            if (isCreateFramesPreview) {

                int w = 135;
                int h = 75;

                int fileWidth = ivfxFile.getWidth();
                int fileHeight = ivfxFile.getHeight();
                double fileAspect = (double) fileWidth / (double) fileHeight;
                double frameAspect = (double) w / (double) h;

                String filter = "";

                if (fileAspect > frameAspect) {
                    int frameHeight = (int)((double) w / fileAspect);
                    filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
                } else {
                    int frameWidth = (int)((double) h * fileAspect);
                    filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
                }

                String mediaFile =  ivfxFile.getSourceName();
                int framesCount = ivfxFile.getFramesCount();
                String imageFile = ivfxFile.getFolderFramesPreview()+"\\"+ ivfxFile.getShortName()+ IVFXFiles.FRAMES_PREFIX +"%%06d.jpg";

                String resolution = w + "x" + h;
                param = new ArrayList<>();

                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Create frames PREVIEW for " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");


                param.add("\"" + pathToFFmpeg + "\"");
                param.add("-i");
                param.add("\"" + mediaFile + "\"");
                param.add("-s");
                param.add(resolution);
                param.add("-vframes");
                param.add(String.valueOf(framesCount));
                param.add("-qscale:v");
                param.add("1");
                param.add("-y");
                param.add("\"" + imageFile + "\"");

                line = "";
                for (String parameter : param) {
                    line = line + parameter + " ";
                }
                line = line.substring(0,line.length()-1)+"\n";
                cmd.add(line);

            }

            if (isCreateFramesMedium) {

                int w = 720;
                int h = 400;

                int fileWidth = ivfxFile.getWidth();
                int fileHeight = ivfxFile.getHeight();
                double fileAspect = (double) fileWidth / (double) fileHeight;
                double frameAspect = (double) w / (double) h;

                String filter = "";

                if (fileAspect > frameAspect) {
                    int frameHeight = (int)((double) w / fileAspect);
                    filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
                } else {
                    int frameWidth = (int)((double) h * fileAspect);
                    filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
                }


                String mediaFile = ivfxFile.getSourceName();
                int framesCount = ivfxFile.getFramesCount();
                String imageFile = ivfxFile.getFolderFramesMedium()+"\\"+ ivfxFile.getShortName()+ IVFXFiles.FRAMES_PREFIX +"%%06d.jpg";

                String resolution = w + "x" + h;

                param = new ArrayList<>();

                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Create frames MEDIUM for " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");

                param.add("\"" + pathToFFmpeg + "\"");
                param.add("-i");
                param.add("\"" + mediaFile + "\"");
                param.add("-s");
                param.add(resolution);
                param.add("-vframes");
                param.add(String.valueOf(framesCount));
                param.add("-qscale:v");
                param.add("2");
                param.add("-vf");
                param.add(filter);
                param.add("-y");
                param.add("\"" + imageFile + "\"");

                line = "";
                for (String parameter : param) {
                    line = line + parameter + " ";
                }
                line = line.substring(0,line.length()-1)+"\n";
                cmd.add(line);

            }

            if (isCreateFramesFull) {

                int w = 1920;
                int h = 1080;

                int fileWidth = ivfxFile.getWidth();
                int fileHeight = ivfxFile.getHeight();
                double fileAspect = (double) fileWidth / (double) fileHeight;
                double frameAspect = (double) w / (double) h;

                String filter = "";

                if (fileAspect > frameAspect) {
                    int frameHeight = (int)((double) w / fileAspect);
                    filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
                } else {
                    int frameWidth = (int)((double) h * fileAspect);
                    filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
                }


                String mediaFile = ivfxFile.getSourceName();
                int framesCount = ivfxFile.getFramesCount();
                String imageFile = ivfxFile.getFolderFramesFull()+"\\"+ ivfxFile.getShortName()+ IVFXFiles.FRAMES_PREFIX +"%%06d.jpg";

                String resolution = w + "x" + h;

                param = new ArrayList<>();

                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Create frames FULL for " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");

                param.add("\"" + pathToFFmpeg + "\"");
                param.add("-i");
                param.add("\"" + mediaFile + "\"");
                param.add("-s");
                param.add(resolution);
                param.add("-vframes");
                param.add(String.valueOf(framesCount));
                param.add("-qscale:v");
                param.add("2");
                param.add("-vf");
                param.add(filter);
                param.add("-y");
                param.add("\"" + imageFile + "\"");

                line = "";
                for (String parameter : param) {
                    line = line + parameter + " ";
                }
                line = line.substring(0,line.length()-1)+"\n";
                cmd.add(line);

            }

            String videoCodec = ivfxFile.getIvfxProject().getVideoCodec();
            int videoBitrate = ivfxFile.getIvfxProject().getVideoBitrate();
            String audioCodec = ivfxFile.getIvfxProject().getAudioCodec();
            int audioBitrate = ivfxFile.getIvfxProject().getAudioBitrate();
            int audioFreq = ivfxFile.getIvfxProject().getAudioFreq();

            int w = ivfxFile.getIvfxProject().getVideoWidth();
            int h = ivfxFile.getIvfxProject().getVideoHeight();

            int fileWidth = ivfxFile.getWidth();
            int fileHeight = ivfxFile.getHeight();
            double fileAspect = (double) fileWidth / (double) fileHeight;
            double frameAspect = (double) w / (double) h;

            String filter = "";

            if (fileAspect > frameAspect) {
                int frameHeight = (int)((double) w / fileAspect);
                filter = "\"scale="+w+":"+frameHeight+",pad="+w+":"+h+":0:" + (int)((h-frameHeight)/2.0) + ":black\"";
            } else {
                int frameWidth = (int)((double) h * fileAspect);
                filter = "\"scale="+frameWidth+":"+h+",pad="+w+":"+h+":" + (int)((w-frameWidth)/2.0) + ":0:black\"";
            }

            String resolution = w + "x" + h;

            if (isCreateFaces) {

                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Detect faces for file " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");

                cmd.add("d:" + "\n");
                cmd.add("cd " + "\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\"" + "\n");

                param = new ArrayList<String>();

                param.add("py");
                param.add("\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\\detect_faces_in_folder.py" + "\"");
//                param.add("-p");
//                param.add(String.valueOf(ivfxFile.getProjectId()));
//                param.add("-f");
//                param.add(String.valueOf(ivfxFile.getId()));
                param.add("-i");
                param.add("\"" + ivfxFile.getFolderFramesFull() + "\"");
                param.add("-d");
                param.add("\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\\face_detection_model" + "\"");
                param.add("-m");
                param.add("\"" + Main.PATH_TO_FOLDER_OPENCVPROJECT + "\\openface_nn4.small2.v1.t7" + "\"");
                param.add("-c");
                param.add(String.valueOf(0.3));

                line = "";
                for (String parameter : param) {
                    line = line + parameter + " ";
                }
                line = line.substring(0, line.length() - 1) + "\n";
                cmd.add(line);

            }


            // создание лосслесс-файла

            if (isCreateLossless) {
                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Create Lossless file for " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");

                param = new ArrayList<String>();

                param.add("\"" + pathToFFmpeg + "\"");
                param.add("-i");
                param.add("\"" + pathToSourceVideofile+ "\"");
                param.add("-map");
                param.add("0:v:0");

                for (IVFXFilesTracks track:IVFXFilesTracks.loadList(ivfxFile)) {
                    if (track.getType().equals("Audio")) {
                        if (track.isUse()) {
                            String typeorder = track.getProperty("@typeorder");
                            if (typeorder == null) typeorder = "1";
                            if (typeorder != null) {
                                param.add("-map");
                                param.add("0:a:" + (Integer.parseInt(typeorder)-1));
                            }
                        }
                    }
                }

                param.add("-s");
                param.add(resolution);

                // если выбран контейнер mxf - значит у него будет кодек dnxhd и несжатое аудио - без вариантов
                if (ivfxFile.getLosslessContainer().equals("mxf")) {
                    param.add("-c:v");
                    param.add("dnxhd");
                    param.add("-b:v");
                    param.add("36M");
                    param.add("-acodec");
                    param.add("pcm_s16le");
                    param.add("-ar");
                    param.add("48000");
                } else { // иначе - выбор кодека из списка и аудио в aac
                    if (ivfxFile.getLosslessVideoCodec().equals("rawvideo")) {
                        param.add("-vcodec");
                        param.add("rawvideo");
                        param.add("-pix_fmt");
                        param.add("yuv420p");
                    } else if(ivfxFile.getLosslessVideoCodec().equals("dnxhd")) {
                        param.add("-c:v");
                        param.add("dnxhd");
                        param.add("-b:v");
                        param.add("36M");
                    }
                    param.add("-c:a");
                    param.add("aac");
                    param.add("-b:a");
                    param.add("320000");
                    param.add("-ar");
                    param.add("48000");
                }

                param.add("-vf");
                param.add(filter);

                param.add("-y");
                param.add("\"" + pathToLosslessFile + "\"");

                line = "";
                for (String parameter : param) {
                    line = line + parameter + " ";
                }
                line = line.substring(0,line.length()-1)+"\n";
                cmd.add(line);

            }


            // создание планов из лосслесс и их кодирование

            List<IVFXShots> listShots = IVFXShots.loadList(ivfxFile,false);

            if (isCreateShots) {

                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Create Shots for " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");

                for (IVFXShots shot : listShots) {

                    String fileNameOutShot = pathToShotsFolder + "\\" + shot.getShotVideoFileNameWihoutFolderX264();
                    int firstFrame = shot.getFirstFrameNumber();
                    int lastFrame = shot.getLastFrameNumber();
                    double frameRate = ivfxFile.getIvfxProject().getVideoFPS();
                    int framesToCode = lastFrame - firstFrame + 1;
                    String start = ((Double)(((double)FFmpeg.getDurationByFrameNumber(firstFrame-1,frameRate))/1000)).toString();

                    // создание плана
                    param = new ArrayList<String>();
                    param.add("\"" + pathToFFmpeg + "\"");
                    if (firstFrame != 0) {
                        param.add("-ss");
                        param.add(start);
                    }
                    param.add("-i");
                    param.add("\"" + pathToLosslessFile + "\"");
                    param.add("-map");
                    param.add("0:v:0");

                    for (IVFXFilesTracks track:IVFXFilesTracks.loadList(ivfxFile)) {
                        if (track.getType().equals("Audio")) {
                            if (track.isUse()) {
                                String typeorder = track.getProperty("@typeorder");
                                if (typeorder == null) typeorder = "1";
                                if (typeorder != null) {
                                    param.add("-map");
                                    param.add("0:a:" + (Integer.parseInt(typeorder)-1));
                                }
                            }
                        }
                    }
                    param.add("-vframes");
                    param.add(String.valueOf(framesToCode));
                    param.add("-s");
                    param.add(resolution);
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
                    param.add("-vf");
                    param.add(filter);
                    param.add("-y");
                    param.add("\"" + fileNameOutShot + "\"");

                    line = "";
                    for (String parameter : param) {
                        line = line + parameter + " ";
                    }
                    line = line.substring(0,line.length()-1)+"\n";
                    cmd.add(line);

                    lstConcat.add("file '" + fileNameOutShot + "'\n");

                }
            }

            if (isCreateShotsMXFaudioYES) {

                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Create Shots Lossless MXF with audio for " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");

                for (IVFXShots shot : listShots) {

                    String fileNameOutMXF = pathToShotsFolder + "\\" + shot.getShotVideoFileNameWihoutFolderMXFaudioON();
                    int firstFrame = shot.getFirstFrameNumber();
                    int lastFrame = shot.getLastFrameNumber();
                    double frameRate = ivfxFile.getIvfxProject().getVideoFPS();
                    int framesToCode = lastFrame - firstFrame + 1;
                    String start = ((Double)(((double)FFmpeg.getDurationByFrameNumber(firstFrame-1,frameRate))/1000)).toString();

                    // создание плана
                    param = new ArrayList<String>();
                    param.add("\"" + pathToFFmpeg + "\"");
                    if (firstFrame != 0) {
                        param.add("-ss");
                        param.add(start);
                    }
                    param.add("-i");
                    param.add("\""+ pathToLosslessFile + "\"");
                    param.add("-map");
                    param.add("0:v:0");

                    for (IVFXFilesTracks track:IVFXFilesTracks.loadList(ivfxFile)) {
                        if (track.getType().equals("Audio")) {
                            if (track.isUse()) {
                                String typeorder = track.getProperty("@typeorder");
                                if (typeorder == null) typeorder = "1";
                                if (typeorder != null) {
                                    param.add("-map");
                                    param.add("0:a:" + (Integer.parseInt(typeorder)-1));
                                }
                            }
                        }
                    }
                    param.add("-vframes");
                    param.add(String.valueOf(framesToCode));
                    param.add("-s");
                    param.add(resolution);
                    param.add("-c:v");
                    param.add("dnxhd");
                    param.add("-b:v");
                    param.add("36M");
                    param.add("-acodec");
                    param.add("pcm_s16le");
                    param.add("-y");
                    param.add("\"" + fileNameOutMXF + "\"");

                    line = "";
                    for (String parameter : param) {
                        line = line + parameter + " ";
                    }
                    line = line.substring(0,line.length()-1)+"\n";
                    cmd.add(line);

                    lstConcat.add("file '" + fileNameOutMXF + "'\n");

                }
            }

            if (isCreateShotsMXFaudioNO) {

                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Create Shots Lossless MXF without audio for " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");

                for (IVFXShots shot : listShots) {

                    String fileNameOutMXF = pathToShotsFolder + "\\" + shot.getShotVideoFileNameWihoutFolderMXFaudioOFF();
                    int firstFrame = shot.getFirstFrameNumber();
                    int lastFrame = shot.getLastFrameNumber();
                    double frameRate = ivfxFile.getIvfxProject().getVideoFPS();
                    int framesToCode = lastFrame - firstFrame + 1;
//                String start = ((Double)(((double)FFmpeg.getDurationByFrameNumber(firstFrame-1,frameRate))/1000)).toString();
                    String start = ((Double)(((double)FFmpeg.getDurationByFrameNumber(firstFrame-1,frameRate))/1000)).toString();

                    // создание плана
                    param = new ArrayList<String>();
                    param.add("\"" + pathToFFmpeg + "\"");
                    if (firstFrame != 0) {
                        param.add("-ss");
                        param.add(start);
                    }
                    param.add("-i");
                    param.add("\"" + pathToLosslessFile + "\"");
                    param.add("-map");
                    param.add("0:v:0");
                    param.add("-vframes");
                    param.add(String.valueOf(framesToCode));
                    param.add("-s");
                    param.add(resolution);
                    param.add("-c:v");
                    param.add("dnxhd");
                    param.add("-b:v");
                    param.add("36M");
                    param.add("-y");
                    param.add("\"" + fileNameOutMXF + "\"");

                    line = "";
                    for (String parameter : param) {
                        line = line + parameter + " ";
                    }
                    line = line.substring(0,line.length()-1)+"\n";
                    cmd.add(line);

                    lstConcat.add("file '" + fileNameOutMXF + "'\n");

                }
            }


            if (isDeleteLossless) {
                cmd.add("\n\n");
                cmd.add("REM ======================================================="+"\n");
                cmd.add("REM Delete Lossless for " + ivfxFile.getShortName() + "\n");
                cmd.add("REM ======================================================="+"\n\n");
                line = "DEL /Q \"" + pathToLosslessFile + "\"\n";
                cmd.add(line);
            }

            cmd.add("\n\n");

            String text = "";
            for (String parameter : cmd) {
                text = text + parameter;
            }

            textAll = textAll + text;
            textConcatAll = textConcatAll + text;
            textConcatAll = textConcatAll + cmdConcat;

            try {
                File cmdFile = new File(cmdFileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(cmdFile));
                writer.write(text);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String textConcat = "";
                for (String parameter : lstConcat) {
                    textConcat = textConcat + parameter;
                }
                File lstConcatFile = new File(lstConcatFileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(lstConcatFile));
                writer.write(textConcat);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                File cmdConcatFile = new File(cmdConcatFileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(cmdConcatFile));
                writer.write(cmdConcat);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            File cmdConcatAllFile = new File(cmdConcatAllFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(cmdConcatAllFile));
            writer.write(textConcatAll);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File cmdFile = new File(cmdAllFiles);
            BufferedWriter writer = new BufferedWriter(new FileWriter(cmdFile));
            writer.write(textAll);
            writer.flush();
            writer.close();
            if (isRunCmd) {
                Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + cmdAllFiles);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

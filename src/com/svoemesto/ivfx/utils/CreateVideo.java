package com.svoemesto.ivfx.utils;

import com.svoemesto.ivfx.tables.IVFXFiles;
import com.svoemesto.ivfx.tables.IVFXShots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateVideo {

    // создание CMD для: 1. создания лосслесс-файла. 2. резки его на планы. 3. кодирования планов
    public static void createCmdToAllStepsConvertVideofile(List<IVFXFiles> listFiles) {

        String cmdConcatAllFileName = listFiles.get(0).getIvfxProject().getFolder()+"\\Video\\_Concat.cmd";
        String textConcatAll = "";

        for (IVFXFiles ivfxFiles: listFiles) {

            List<String> cmd = new ArrayList<>();
            List<String> lstConcat = new ArrayList<>();

            String line;
            String pathToFFmpeg = FFmpeg.PATH_TO_FFMPEG;
            String pathToSourceVideofile = ivfxFiles.getSourceName();

            String pathToLosslessFolder =  ivfxFiles.getIvfxProject().getFolder() + "\\Video\\Lossless";
            File folderLossless = new File(pathToLosslessFolder);
            if (!folderLossless.exists()) {
                folderLossless.mkdirs();
            }

            String pathToLosslessFile = pathToLosslessFolder + "\\" + ivfxFiles.getShortName() + "_lossless.mkv";
//            String pathToLosslessFile = pathToLosslessFolder + "\\" + ivfxFiles.getShortName() + "_lossless.mp4";

            String pathToX264Folder = ivfxFiles.getIvfxProject().getFolder() + "\\Video\\1920x1080";
            File folderX264 = new File(pathToX264Folder);
            if (!folderX264.exists()) {
                folderX264.mkdirs();
            }

            String cmdFileName = ivfxFiles.getIvfxProject().getFolder()+"\\Video\\" + ivfxFiles.getShortName()+"_AllStepsConvert.cmd";
            String cmdConcatFileName = ivfxFiles.getIvfxProject().getFolder()+"\\Video\\" + ivfxFiles.getShortName()+"_Concat.cmd";

            String lstConcatFileName = ivfxFiles.getIvfxProject().getFolder()+"\\Video\\" + ivfxFiles.getShortName()+"_Concat.txt";

            String cmdConcat = pathToFFmpeg + " -f concat -safe 0 -i " + lstConcatFileName + " -map 0 -c copy " + ivfxFiles.getIvfxProject().getFolder()+"\\Video\\Concat\\" + ivfxFiles.getShortName() +".mp4\n";
//            String cmdConcat = pathToFFmpeg + " -f concat -safe 0 -i " + lstConcatFileName + " -map 0 -c copy " + ivfxFiles.getIvfxProject().getFolder()+"\\Video\\Concat\\" + ivfxFiles.getShortName() +".mp4\n";

            String videoCodec = "libx264";
            int videoBitrate = 9000000;
            String audioCodec = "aac";
            int audioBitrate = 196608;
            int audioFreq = 48000;
            String resolution = "1920x1080";

            // создание лосслесс-файла
            List<String> param = new ArrayList<String>();

            param.add(pathToFFmpeg);
            param.add("-i");
            param.add(pathToSourceVideofile);
            param.add("-map");
            param.add("0:v");
            param.add("-map");
            param.add("0:a");
            param.add("-vcodec");
            param.add("rawvideo");
            param.add("-pix_fmt");
            param.add("yuv420p");
            param.add("-acodec");
            param.add("pcm_s16le");
            param.add("-y");
            param.add(pathToLosslessFile);

            line = "";
            for (String parameter : param) {
                line = line + parameter + " ";
            }
            line = line.substring(0,line.length()-1)+"\n";
            cmd.add(line);

            // создание планов из лосслесс и их кодирование
            List<IVFXShots> listShots = IVFXShots.loadList(ivfxFiles,false);

            for (IVFXShots shot : listShots) {

                String fileNameOutX264 = pathToX264Folder + "\\" + shot.getShotVideoFileNameWihoutFolderX264();
                int firstFrame = shot.getFirstFrameNumber();
                int lastFrame = shot.getLastFrameNumber();
                double frameRate = ivfxFiles.getFrameRate();
                int framesToCode = lastFrame - firstFrame + 1;
//                String start = ((Double)(((double)FFmpeg.getDurationByFrameNumber(firstFrame-1,frameRate))/1000)).toString();
                String start = ((Double)(((double)FFmpeg.getDurationByFrameNumber(firstFrame,frameRate))/1000)).toString();

                // создание плана
                param = new ArrayList<String>();
                param.add(pathToFFmpeg);
                if (firstFrame != 0) {
                    param.add("-ss");
                    param.add(start);
                }
                param.add("-i");
                param.add(pathToLosslessFile);
                param.add("-map");
                param.add("0:v");
                param.add("-map");
                param.add("0:a");
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
                param.add("-f");
//                param.add("matroska");
                param.add("mp4");
                param.add("-y");
                param.add(fileNameOutX264);

                line = "";
                for (String parameter : param) {
                    line = line + parameter + " ";
                }
                line = line.substring(0,line.length()-1)+"\n";
                cmd.add(line);

                lstConcat.add("file '" + fileNameOutX264 + "'\n");

                // удаление плана

//            line = "DEL /Q " + fileNameOutLossless + "\n";
//            cmd.add(line);

            }
            line = "DEL /Q " + pathToLosslessFile + "\n";
            cmd.add(line);

            String text = "";
            for (String parameter : cmd) {
                text = text + parameter;
            }

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

    }

}

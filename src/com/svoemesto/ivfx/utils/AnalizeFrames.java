package com.svoemesto.ivfx.utils;

import com.svoemesto.ivfx.tables.IVFXFiles;
import com.svoemesto.ivfx.tables.IVFXFrames;
import com.svoemesto.ivfx.tables.IVFXScenes;
import com.svoemesto.ivfx.tables.IVFXShots;
import javafx.application.Platform;
import org.sikuli.basics.Settings;
import org.sikuli.script.Finder;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class AnalizeFrames {
    public static void analizeFrames(IVFXFiles ivfxFile) {

        String mediaFile = ivfxFile.getSourceName();
        double fps = ivfxFile.getFrameRate();
        int framesCount = ivfxFile.getFramesCount();

        Settings.MinSimilarity = 0.0;
        double simScore;

        // получаем список IFrame-ов
        List<Integer> listIFrames = new ArrayList<>();
        try {
            listIFrames = FFmpeg.getListIFrames(mediaFile, fps);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // создаем список кадров и заполяем его номером, файлом и признаком isIFrame
        List<IVFXFrames> ivfxFramesList = new ArrayList<>();
        for (int frameNumber = 1; frameNumber <= framesCount; frameNumber++) {
            int percent = (int) (((double) frameNumber / (double) framesCount) * 100);

            IVFXFrames ivfxFrame = IVFXFrames.getNewDbInstance(ivfxFile,frameNumber);
            ivfxFrame.setIsIFrame(listIFrames.contains(frameNumber));
            ivfxFrame.save();

            if (new File(ivfxFrame.getFileNamePreview()).exists()) {
                ivfxFramesList.add(ivfxFrame);
            } else {
                ivfxFile.setFramesCount(frameNumber);
                ivfxFile.save();
                break;
            }
        }

        // заполняем simScore's
        for (int i = 0; i < ivfxFramesList.size() - 1; i++) {
            System.out.println("Analize frame #" + i + "/" + (ivfxFramesList.size() - 1));
            int percent = (int) (((double) i / (double) (ivfxFramesList.size() - 1)) * 100);

            IVFXFrames ivfxFrameCurrent = ivfxFramesList.get(i);
            IVFXFrames ivfxFrameNext1 = (i < ivfxFramesList.size() - 1) ? ivfxFramesList.get(i + 1) : null;
            IVFXFrames ivfxFrameNext2 = (i < ivfxFramesList.size() - 2) ? ivfxFramesList.get(i + 2) : null;
            IVFXFrames ivfxFrameNext3 = (i < ivfxFramesList.size() - 3) ? ivfxFramesList.get(i + 3) : null;

            simScore = 0.9999;
            if (ivfxFrameNext1 != null) {
                simScore = 0.0;
                String fileName1 = ivfxFrameCurrent.getFileNamePreview();
                String fileName2 = ivfxFrameNext1.getFileNamePreview();
                Finder f = new Finder(fileName1);
                Pattern targetImage = new Pattern(fileName2);
                f.find(targetImage);
                Match match = f.next();
                if (match != null) simScore = match.getScore();
                ivfxFrameNext1.setSimScorePrev1(simScore);
            }
            ivfxFrameCurrent.setSimScoreNext1(simScore);

            simScore = 0.9999;
            if (ivfxFrameNext2 != null) {
                simScore = 0.0;
                Finder f = new Finder(ivfxFrameCurrent.getFileNamePreview());
                Pattern targetImage = new Pattern(ivfxFrameNext2.getFileNamePreview());
                f.find(targetImage);
                Match match = f.next();
                if (match != null) simScore = match.getScore();
                ivfxFrameNext2.setSimScorePrev2(simScore);
            }
            ivfxFrameCurrent.setSimScoreNext2(simScore);

            simScore = 0.9999;
            if (ivfxFrameNext3 != null) {
                simScore = 0.0;
                Finder f = new Finder(ivfxFrameCurrent.getFileNamePreview());
                Pattern targetImage = new Pattern(ivfxFrameNext3.getFileNamePreview());
                f.find(targetImage);
                Match match = f.next();
                if (match != null) simScore = match.getScore();
                ivfxFrameNext3.setSimScorePrev3(simScore);
            }
            ivfxFrameCurrent.setSimScoreNext3(simScore);
        }

        // заполняем diff's
        for (int i = 0; i < ivfxFramesList.size() - 1; i++) {
            int percent = (int) (((double) i / (double) (ivfxFramesList.size() - 1)) * 100);

            IVFXFrames ivfxFrameCurrent = ivfxFramesList.get(i);
            IVFXFrames ivfxFramePrev1 = i > 0 ? ivfxFramesList.get(i - 1) : null;
            IVFXFrames ivfxFramePrev2 = i > 1 ? ivfxFramesList.get(i - 2) : null;
            IVFXFrames ivfxFrameNext1 = i < ivfxFramesList.size() - 1 ? ivfxFramesList.get(i + 1) : null;
            IVFXFrames ivfxFrameNext2 = i < ivfxFramesList.size() - 2 ? ivfxFramesList.get(i + 2) : null;
            double diffNext;

            diffNext = 0;
            if (ivfxFrameNext1 != null) {
                diffNext = ivfxFrameCurrent.getSimScoreNext1() - ivfxFrameNext1.getSimScoreNext1();
                if (diffNext < 0) diffNext = -diffNext;
            }
            ivfxFrameCurrent.setDiffNext1(diffNext);

            diffNext = 0;
            if (ivfxFrameNext1 != null && ivfxFrameNext2 != null) {
                diffNext = ivfxFrameNext1.getSimScoreNext1() - ivfxFrameNext2.getSimScoreNext1();
                if (diffNext < 0) diffNext = -diffNext;
            }
            ivfxFrameCurrent.setDiffNext2(diffNext);

            diffNext = 0;
            if (ivfxFramePrev1 != null) {
                diffNext = ivfxFramePrev1.getSimScoreNext1() - ivfxFrameCurrent.getSimScoreNext1();
                if (diffNext < 0) diffNext = -diffNext;
            }
            ivfxFrameCurrent.setDiffPrev1(diffNext);

            diffNext = 0;
            if (ivfxFramePrev1 != null && ivfxFramePrev2 != null) {
                diffNext = ivfxFramePrev2.getSimScoreNext1() - ivfxFramePrev1.getSimScoreNext1();
                if (diffNext < 0) diffNext = -diffNext;
            }
            ivfxFrameCurrent.setDiffPrev2(diffNext);
        }

        for (IVFXFrames ivfxFrame : ivfxFramesList) {
            ivfxFrame.save();
        }

        IVFXShots.createListShotsByFrames(ivfxFile);


    }

    public static void findTransition(IVFXFiles ivfxFile) {

        List<IVFXFrames> ivfxFramesList = IVFXFrames.loadList(ivfxFile);

        double diff1 = 0.4;     //Порог обнаружения перехода
        double diff2 = 0.42;    //Вторичный порог
//        int minumalLenghtOfSegments = 5;    //Минимальная длина сегмента
        for (IVFXFrames frame : ivfxFramesList) {
            // если simScore меньше diff1 - есть шанс что это переход кадра
//            if (frame.getSimScoreNext1() < diff1) {
            if (frame.getSimScorePrev1() < diff1) {
//                if (frame.getDiffNext1() > diff2 || frame.getDiffPrev1() > diff2) {
                if (frame.getDiffPrev1() > diff2 || frame.getDiffPrev2() > diff2) {
                    frame.setIsFind(true);
                    frame.setIsManualAdd(false);
                    frame.setIsManualCancel(false);
                    frame.setIsFinalFind(true);
                } else {
                    frame.setIsFind(false);
                    frame.setIsManualAdd(false);
                    frame.setIsManualCancel(false);
                    frame.setIsFinalFind(false);
                }
            } else if (frame.getDiffPrev1() > diff2 && frame.getDiffPrev2() > diff2 && frame.getSimScoreNext1() > diff1) {
                frame.setIsFind(true);
                frame.setIsManualAdd(false);
                frame.setIsManualCancel(false);
                frame.setIsFinalFind(true);
            } else {
                frame.setIsFind(false);
                frame.setIsManualAdd(false);
                frame.setIsManualCancel(false);
                frame.setIsFinalFind(false);
            }
            frame.save();
        }

    }

    public static void clearShots(IVFXFiles ivfxFile) {

        List<IVFXScenes> listScenes = IVFXScenes.loadList(ivfxFile);
        if (listScenes.size() > 0) {
            for (IVFXScenes ivfxScene: listScenes) {
                ivfxScene.delete();
            }
        }

        List<IVFXShots> listShots = IVFXShots.loadList(ivfxFile,false);
        if (listShots.size() > 0) {
            for (IVFXShots ivfxShot: listShots) {
                ivfxShot.delete();
            }
        }

    }

}

package com.svoemesto.ivfx.utils;

import com.google.gson.Gson;
import com.svoemesto.ivfx.tables.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class FaceRecognizer {


    public static FrameFace[] getArrayFrameFaces(IVFXFiles ivfxFile) {

        List<Integer> listFrameNumbers = getFramesToRecognize(ivfxFile);
        FrameFace[] arrayFrameFaces = new FrameFace[listFrameNumbers.size()];

        for (int i = 0; i < listFrameNumbers.size(); i++) {

            FrameFace frameFace = new FrameFace();
            frameFace.faceid=0;
            frameFace.projectid=ivfxFile.getProjectId();
            frameFace.fileid=ivfxFile.getId();
            frameFace.image_file=ivfxFile.getFolderFramesFull()+"\\"+ivfxFile.getShortName()+ivfxFile.FRAMES_PREFIX+String.format("%06d", listFrameNumbers.get(i))+".jpg";
            frameFace.face_id=0;
            frameFace.person_id=0;
            frameFace.person_id_recognized=0;
            frameFace.recognize_probability=0.0;
            frameFace.frame_id = 0;
            frameFace.face_file="";
            frameFace.start_x=0;
            frameFace.start_y=0;
            frameFace.end_x=0;
            frameFace.end_y=0;
            frameFace.vector=new double[]{0};

            arrayFrameFaces[i] = frameFace;
        }

        return arrayFrameFaces;
    }

    public static List<Integer> getFramesToRecognize(IVFXFiles ivfxFile) {

        int curr = 0;
        List<Integer> listFrames = new ArrayList<>();

        List<IVFXShots> listShots = IVFXShots.loadList(ivfxFile,false);

        for (IVFXShots shot : listShots) {
            int stepFrames = (shot.getLastFrameNumber() - shot.getFirstFrameNumber()) < 60 ? 10 : 30;
            for (int i = shot.getFirstFrameNumber(); i < shot.getLastFrameNumber(); i += stepFrames) {
                curr = i;
                if (curr <= ivfxFile.getFramesCount()) listFrames.add(curr) ;
            }
            if (curr < shot.getLastFrameNumber()) curr = shot.getLastFrameNumber();
            if (curr <= ivfxFile.getFramesCount()) listFrames.add(curr) ;
        }

        System.out.println("Add frames: " + listFrames.size());

        return listFrames;
    }

    public static void reloadFaces(String pathToFileJSON) {

        Gson gson = new Gson();

        IVFXFiles ivfxFile = null;

        try (FileReader fileReader = new FileReader(pathToFileJSON)) {
            FrameFace[] frameFacesArray = gson.fromJson(fileReader, FrameFace[].class);

            boolean needToResave = false;

            for (FrameFace frameFace : frameFacesArray) {

                IVFXFrames ivfxFrame = null;
                if (frameFace.frame_id == 0) {
                    needToResave = true;
                    int frameNumber = frameFace.getFrameNumber();
                    if (ivfxFile == null || ivfxFile.getId() != frameFace.fileid) {
                        ivfxFile = IVFXFiles.load(frameFace.fileid);
                    }
                    ivfxFrame = IVFXFrames.load(ivfxFile, frameNumber, false);
                    frameFace.frame_id = ivfxFrame.getId();
                }

                IVFXFaces ivfxFace = IVFXFaces.createOrUpdateInstance(frameFace, false);

                int tagId =ivfxFace.getTagId();
                int tagRecId = ivfxFace.getTagRecognizedId();
                String tagText = "";
                if (tagId == -2) {
                    tagText = "(массовка)";
                } else if (tagId == -1) {
                    tagText = "(не лицо)";
                } else if (tagId > 0) {
                    tagText ="«" + IVFXTags.load(tagId,false).getName() + "»";
                } else if (tagRecId != 0) {
                    tagText ="«" + IVFXTags.load(tagRecId,false).getName() + "» (" + String.format("%.02f",ivfxFace.getRecognizeProbability()*100) + "%)";
                } else {
                    tagText = "(ЕЩЕ НЕ ОБРАБОТАНО)";
                }

                System.out.println("Обработка лица: file = «" + ivfxFace.getIvfxFrame().getIvfxFile().getTitle() + "», frame № = " + frameFace.getFrameNumber() + ", " + tagText);
            }

            if (needToResave) {
                try (final FileWriter fileWriter = new FileWriter(pathToFileJSON)) {
                    gson.toJson(frameFacesArray, fileWriter);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

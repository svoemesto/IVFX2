package com.svoemesto.ivfx.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.common.reflect.TypeToken;
import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.Database;
import com.svoemesto.ivfx.tables.IVFXFiles;

public class MediaInfo {
    private static String MEDIA_INFO_CLI_PATH = (new File("MediaInfo_CLI/MediaInfo.exe")).getAbsolutePath();

    public static String getInfo(String media) throws IOException, InterruptedException {
        return executeMediaInfo(media);
    }

    public static String getInfoByParameter(String media, String parameter) throws IOException, InterruptedException {
        return executeMediaInfo(media,parameter);
    }

    public static String getInfoBySectionAndParameter(String media, String section, String parameter) throws IOException, InterruptedException {
        String param = "--Inform=\""+section+";%"+parameter+"%\"";
        return executeMediaInfo(media,param);
    }

    private static String executeMediaInfo(String media) throws IOException, InterruptedException {
        List<String> param = new ArrayList<String>();
        param.add(media);
        return executeMediaInfo(param);
    }

    public static String executeMediaInfo(String media, String parameter) throws IOException, InterruptedException {
        List<String> param = new ArrayList<String>();
        param.add(parameter);
        param.add(media);
        return executeMediaInfo(param);
    }

    private static String executeMediaInfo(List<String> parameters) throws IOException, InterruptedException {
        final String exePath = MEDIA_INFO_CLI_PATH;
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

    public static void main(String[] args) {
//        try {
//            String res = executeMediaInfo("D:\\NewIVFXpoject\\Scrubs1x01.avi", "--Output=JSON");
//            Type mapType = new TypeToken<Map<String, Map>>(){}.getType();
//            Map<String, String[]> son = new Gson().fromJson(res, mapType);
//            System.out.println(son);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        Main.mainConnection = Database.getConnection();
//        IVFXFiles ivfxFile = IVFXFiles.load(170);
//
//            Type mapType = new TypeToken<Map<String, Map>>(){}.getType();
//            Map<String, String[]> son = new Gson().fromJson(ivfxFile.getMediaInfoJson(), mapType);
//            System.out.println(son);

//        System.out.println(ivfxFile.getFromMediaInfoTrack(1));
//
//        ivfxFile.createTracksFromMediaInfo();
        System.out.println(MEDIA_INFO_CLI_PATH);
    }
}

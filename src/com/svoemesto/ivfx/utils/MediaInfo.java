package com.svoemesto.ivfx.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class MediaInfo {
    private static String MEDIA_INFO_CLI_PATH = "D:\\MediaInfo_CLI\\MediaInfo.exe";

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
        try {
            String res = executeMediaInfo("E:\\GOT\\GOT.S01\\GOT.S01E01.BDRip.1080p.mkv", "--Output=XML");
//            String res = getInfoBySectionAndParameter("E:\\GOT\\GOT.S01\\GOT.S01E01.BDRip.1080p.mkv", "Audio", "Format");
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

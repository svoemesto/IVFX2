package com.svoemesto.ivfx.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

    public static String convertCyrilic(String message){
        char[] abcCyr =   {' ','а','б','в','г','д','е', 'ё', 'ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х', 'ц', 'ч', 'ш',   'щ','ы','э', 'ю', 'я','А','Б','В','Г','Д','Е', 'Ё', 'Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х', 'Ц', 'Ч', 'Ш',   'Щ','Ы','Э', 'Ю', 'Я','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1','2','3','4','5','6','7','8','9','/','-','_'};
        String[] abcLat = {" ","a","b","v","g","d","e","yo","zh","z","i","i","k","l","m","n","o","p","r","s","t","u","f","h","ts","ch","sh","shch","i","e","yu","ya","A","B","V","G","D","E","Yo","Zh","Z","I","I","K","L","M","N","O","P","R","S","T","U","F","H","Ts","Ch","Sh","Shch","I","E","Yu","Ya","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","1","2","3","4","5","6","7","8","9","/","-","_"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++ ) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }

    public static String getHTMLtextFromUrl(String url) {

        try {
            URL url1 = new URL(url);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(url1.openStream()))) {
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {

                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTextBetween(String text, String startText, String endText) {
        String result = "";
        if (text != null) {
            String firstPart = text;
            if (startText != null && !startText.equals("")) {
                if (text.contains(startText)) {
                    int start = text.indexOf(startText);
                    firstPart = text.substring(start + startText.length());
                } else {
                    return "";
                }
            }
            if (endText != null && !endText.equals("")) {
                if (firstPart.contains(endText)) {
                    int end = firstPart.indexOf(endText);
                    result = firstPart.substring(0,end);
                } else {
                    return "";
                }
            } else {
                return firstPart;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String txt = getHTMLtextFromUrl("https://gameofthrones.fandom.com/ru/wiki/%D0%92%D0%B8%D0%BD%D1%82%D0%B5%D1%80%D1%84%D0%B5%D0%BB%D0%BB_(%D1%81%D0%B5%D1%80%D0%B8%D1%8F)");

        System.out.println(getTextBetween(txt, "<span id=\"В_ролях\"></span>", "<span id=\"Навигация_по_сериям\"></span>"));

    }

}

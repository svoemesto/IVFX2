package com.svoemesto.ivfx.utils;

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

}

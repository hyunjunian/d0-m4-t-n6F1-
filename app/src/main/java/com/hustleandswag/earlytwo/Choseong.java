package com.hustleandswag.earlytwo;

/**
 * Created by hyunjunjeon on 26/10/2016.
 */

public class Choseong {

    static public String getInitialSound(String text) {
        String[] cho = {"ㄱ","ㄲ","ㄴ","ㄷ","ㄸ","ㄹ","ㅁ","ㅂ","ㅃ","ㅅ","ㅆ","ㅇ","ㅈ","ㅉ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ"};
        String result = "";
        for(int i=0;i<text.length();i++) {
            int code = text.charAt(i)-44032;
            if(code>-1 && code<11172) result += cho[code/588];
            else result += text.charAt(i);
        }
        return result;
    }
}

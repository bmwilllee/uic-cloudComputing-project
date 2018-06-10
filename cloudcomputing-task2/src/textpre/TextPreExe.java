package textpre;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TextPreExe {
    public static String StringFilter(String str) throws PatternSyntaxException {
        //define special character
        String regEx = "[`~!@#--$%^&*()+=-_|{}'\":;',\\[\\].<>/?~£¡@#£¤%¡­¡­&*£¨£©¡ª¡ª+|{}¡¾¡¿¡®£»£º¡±¡°¡¯¡££¬¡¢£¿]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String NumerFilter(String str) throws PatternSyntaxException {
        Pattern p = Pattern.compile("[\\d]");
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String dealAbbr(String s) {
        //short write to cut from '
        if(s.matches(".*'s.*") || s.equals(".*'re.*") || s.equals(".*'d.*") ||
                s.matches(".*'m.*") || s.matches(".*'ve.*")) {
            //remove 's 're 'd 'm 've in the end
            s = s.replaceAll("'s", "");
            s = s.replaceAll("'re", "");
            s = s.replaceAll("'d", "");
            s = s.replaceAll("'m", "");
            s = s.replaceAll("'ve", "");
        }

        //short write to cut
        if(s.matches(".*n't.*")){
            if(s.equals("can't")){
                s = s.replaceAll("'t", "");
            }
            else if(s.equals("won't")) {
                //count will for won't
                s = s.replaceAll("won't", "will");
            }
            else {
                //remove n't in the end
                s = s.replaceAll("n't", "");
            }
        }
        return s;
    }
}

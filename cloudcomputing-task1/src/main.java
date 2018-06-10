import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class main {

    public static String annotatorFunction(String line1) {

        // System.out.println(line1);                                       // -May11th = verify whether String-passing works; Done;

        Properties props = new Properties();

        props.put("annotators", "tokenize,ssplit,pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(line1);
        pipeline.annotate(document);
        // from Here;
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        String outputStr = "\n";

        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {    // -May11th = verify whether 'sentence' needs 'token'; Done;
                // String word = token.get(TextAnnotation.class);            // -May11th = need not use 'word' token; Done;
                String lemma = token.get(LemmaAnnotation.class) + "\t";
                //System.out.println(lemma);                                  // -May11th = verify each lemma could be converted; Done;
                // return lemma;                                           // -May11th = -?attempt: for each lemma, get and return; Done;
                outputStr += lemma ;
            }

        }

        return outputStr ;
    }


    public   static   String StringFilter(String   str)   throws   PatternSyntaxException   {
        //define special character
        String regEx="[`~!@#--$%^&*()+=-_|{}'\":;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll(" ").trim();
    }

    static Pattern p = Pattern.compile(".*\\d+.*");

    public static String dealAbbr(String s) {
        Matcher m = p.matcher(s);
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

    public static void display(String filename)throws Exception{
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line ;

        Map<String, Integer> tm = new TreeMap<String, Integer>();


        while((line = br.readLine()) != null)
        {
            // String line1 = line.toLowerCase();                   // -May11th = verify whether CoreNLP can handle 'splitting'; Done;
            String reg1 = "\\s+";  //blank character
            String reg2 = "\\w+";  //match word including letter, number, and any characters

            // Here to Elimate
            //annotatorFunction(line1);

            //String str[] = line1.split(reg1);              // -May11th = verify whether CoreNLP can handle 'splitting'; Done;
            //for(String s: str){                           // -May11th = verify whether CoreNLP can handle 'abbreviation-reset'; Done;
            //deal the abbreviation
            //	s = dealAbbr(s);                            // -May11th = verify whether CoreNLP can handle 'abbreviation-reset'; Done;

            //replace spec and loop to avoid >=2 same character
            //for(int i=0; i<s.length(); i++) {              // -May11th = repeated functionality in "characters-validation"; Done;
            //	s = StringFilter(s);
            //}

            //split string by special character(already blankspace)
            //String[] str2 = s.split(reg1);
            //for(String s2: str2) {

            String wordCombine;                                 // -May11th = create a string to hold the lemma result;
            wordCombine = annotatorFunction(line);
            //count word
            String[] strRec = wordCombine.split(reg1);               // -May11th = loop to split '\t' from each word;
            for(String str: strRec) {
                if (!tm.containsKey(str)) {
                    tm.put(str, 1);
                } else {
                    tm.put(str, tm.get(str) + 1);            // -May11th = -?attempt: traversal in 'tm.get(keyWord)' occupies times;
                }
                //accept word with letters [a-zA-Z]+
                if (!str.matches("[a-zA-Z]+")) {     // -May11th = -?attempt: put match-condition into annotatorFunction();
                    tm.remove(str);
                }
            }
            //}
            //}
        }
        System.out.println(tm);
        //after sorted
        System.out.println(entriesSortedByValues(tm));
    }

    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e2.getValue().compareTo(e1.getValue());
                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
    public static void main(String[] args) throws Exception{
        long begintime = System.nanoTime();
        //运算代码
        display("input/NovelTexts.txt");       // -May10 -! modify here for file;
        long endtime = System.nanoTime();
        long costTime = (endtime - begintime)/1000/1000;
        //要换算为微秒，就除上1000，就可以
        System.out.println("Cost time:" + costTime*0.001 + "s");
    }
}

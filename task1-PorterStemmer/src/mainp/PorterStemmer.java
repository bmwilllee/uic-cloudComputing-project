package mainp;

import java.io.*;

/**
 * 实现波特词干提取算法，将一个单词转换为它的原型。
 */
public class PorterStemmer {

    private char[] b; /* 用来存待词干提取的单词（以char的形式） */
    private int i,     /* b中的元素位置（偏移量） */
            i_end, /* 要抽取词干单词的结束位置 */
            j, k;
    private static final int INC = 50;/* 随着b的大小增加数组要增长的长度（防止溢出） */

    public PorterStemmer(){
        b = new char[INC];
        i = 0;
        i_end = 0;
    }

    /**
     * 增加一个字符到要存放待处理的单词的数组。添加完字符时，
     * 可以调用stem(void)方法来进行抽取词干的工作。
     * @param ch 字符
     */
    public void add(char ch){
        if (i == b.length){
            char[] new_b = new char[i+INC];
            for (int c = 0; c < i; c++) new_b[c] = b[c];
            b = new_b;
        }
        b[i++] = ch;
    }

    /**
     * 增加字符数组ch到存放待处理的单词的数组b
     * @param ch 字符数组
     */
    public void add(char[] ch){
        add(ch,ch.length);
    }

    /**
     * 增加wLen长度的字符数组到存放待处理的单词的数组b。
     * @param w 字符数组
     * @param wLen 数组长度
     */
    public void add(char[] w, int wLen){
        if (i+wLen >= b.length){
            char[] new_b = new char[i+wLen+INC];
            for (int c = 0; c < i; c++) new_b[c] = b[c];
            b = new_b;
        }
        for (int c = 0; c < wLen; c++) b[i++] = w[c];
    }

    /**
     * 返回单词的词干。
     */
    public String toString() { return new String(b,0,i_end); }

    /**
     * 返回单词的词干的长度
     */
    public int getResultLength() { return i_end; }

    /**
     * 返回单词的词干char[]
     */
    public char[] getResultBuffer() { return b; }

    /**
     * cons(i)：参数i：int型；返回值bool型。当i为辅音时，返回真；否则为假
     * @param i b数组下标
     * @return cons(i) 为真 <=> b[i] 是一个辅音
     */
    private final boolean cons(int i){
        switch (b[i]){
            case 'a': case 'e': case 'i': case 'o': case 'u': return false;
            //y开头，为辅；否则看i-1位，如果i-1位为辅，y为元，反之亦然。
            case 'y': return (i==0) ? true : !cons(i-1);
            default: return true;
        }
    }

    /**
     */
    /**
     * m()：返回值：int型。表示单词b介于0和j之间辅音序列的个数。
     * 现假设c代表辅音序列，而v代表元音序列。<..>表示任意存在。于是有如下定义：
     <c><v>          结果为 0
     <c>vc<v>       结果为 1
     <c>vcvc<v>    结果为 2
     <c>vcvcvc<v> 结果为 3
     ....
     * @return 单词b介于0和j之间辅音序列的个数
     */
    private final int m(){
        int n = 0;//辅音序列的个数，初始化
        int i = 0;//偏移量
        //<c>
        while(true){
            if (i > j) return n;//如果超出最大偏移量，直接返回n
            if (!cons(i)) break; //如果是元音，中断
            i++;  //辅音移一位，直到元音的位置
        }
        i++;//移完辅音，从元音的第一个字符开始
        while(true){  //循环计算vc的个数
            //<v>
            while(true){  //循环判断v
                if (i > j) return n;
                if (cons(i)) break; //出现辅音则终止循环
                i++;
            }
            i++;
            n++;
            //<c>
            while(true){  //循环判断c
                if (i > j) return n;
                if (!cons(i)) break;
                i++;
            }
            i++;
        }
    }

    /**
     * vowelinstem()：返回值：bool型。表示单词b介于0到i之间是否存在元音。
     * @return vowelinstem() 为真 <=> 0,...j 包含一个元音
     */
    private final boolean vowelinstem(){
        int i;
        for (i = 0; i <= j; i++)
            if (!cons(i))
                return true;
        return false;
    }

    /**
     * doublec(j)：参数j：int型；返回值bool型。
     * 这个函数用来表示在j和j-1位置上的两个字符是否是相同的辅音。
     * @param j 数组b的下标
     * @return doublec(j) 为真 <=> j,(j-1) 包含两个一样的辅音
     */
    private final boolean doublec(int j){
        if (j < 1) return false;
        if (b[j] != b[j-1]) return false;
        return cons(j);
    }

    /**
     * cvc(i)：参数i：int型；返回值bool型。
     * 对于i，i-1，i-2位置上的字符，它们是“辅音-元音-辅音”的形式，
     * 并且对于第二个辅音，它不能为w、x、y中的一个。
     * 这个函数用来处理以e结尾的短单词。比如说cav(e)，lov(e)，hop(e)，crim(e)。
     * 但是像snow，box，tray就不符合条件。
     * @param i 下标
     * @return boolean
     */
    private final boolean cvc(int i){
        if (i < 2 || !cons(i) || cons(i-1) || !cons(i-2))
            return false;
        else{
            int ch = b[i];
            if (ch == 'w' || ch == 'x' || ch == 'y') return false;
        }
        return true;
    }

    /**
     * ends(s)：参数：String；返回值：bool型。用于判断b是否以s结尾。
     * @param s 字符串
     * @return 判断b是否以s结尾
     */
    private final boolean ends(String s){
        int l = s.length();
        int o = k-l+1;
        if (o < 0) return false;
        for (int i = 0; i < l; i++)
            if (b[o+i] != s.charAt(i))
                return false;
        j = k-l;
        return true;
    }

    /**
     * 把b在(j+1)...k位置上的字符设为s，同时，调整k的大小
     * @param s 字符串
     */
    private final void setto(String s){
        int l = s.length();
        int o = j+1;
        for (int i = 0; i < l; i++)
            b[o+i] = s.charAt(i);
        k = j+l;
    }

    /**
     * 在m()>0的情况下，调用setto(s)。
     * @param s 字符串
     */
    private final void r(String s) {
        if (m() > 0) setto(s);
    }

    //分六步来进行处理的过程
    /** step1() 处理复数，以及ed和ing结束的单词。比如：
     caresses  ->  caress
     ponies    ->  poni
     ties      ->  ti
     caress    ->  caress
     cats      ->  cat

     feed      ->  feed
     agreed    ->  agree
     disabled  ->  disable

     matting   ->  mat
     mating    ->  mate
     meeting   ->  meet
     milling   ->  mill
     messing   ->  mess

     meetings  ->  meet
     */
    private final void step1(){
        if (b[k] == 's'){
            if (ends("sses")) k -= 2; //以“sses结尾”
            else if (ends("ies")) setto("i"); //以ies结尾，置为i
            else if (b[k-1] != 's') k--;//两个s结尾不处理
        }
        if (ends("eed")) {
            //以“eed”结尾，当m>0时，左移一位
            if (m() > 0) k--;
        } else if ((ends("ed") || ends("ing")) && vowelinstem()){
            k = j;
            if (ends("at")) setto("ate");
            else if (ends("bl")) setto("ble");
            else if (ends("iz")) setto("ize");
            else if (doublec(k)){  //如果有两个相同辅音
                k--;
                int ch = b[k];
                if (ch == 'l' || ch == 's' || ch == 'z') k++;
            }
            else if (m() == 1 && cvc(k)) setto("e");
        }
    }

    /** step2() 如果单词中包含元音，并且以y结尾，将y改为i */
    private final void step2() {
        if (ends("y") && vowelinstem()){
            b[k] = 'i';
        }
    }

    /** step3() 将双后缀的单词映射为单后缀。
     * 所以 -ization ( = -ize 加上 -ation) 被映射到 -ize 等等。
     * 注意在去除后缀之前必须确保m()>0.
     */
    private final void step3() {
        if (k == 0) return;
        switch (b[k-1]){
            case 'a': if (ends("ational")) { r("ate"); break; }
                if (ends("tional")) { r("tion"); break; }
                break;
            case 'c': if (ends("enci")) { r("ence"); break; }
                if (ends("anci")) { r("ance"); break; }
                break;
            case 'e': if (ends("izer")) { r("ize"); break; }
                break;
            case 'l': if (ends("bli")) { r("ble"); break; }
                if (ends("alli")) { r("al"); break; }
                if (ends("entli")) { r("ent"); break; }
                if (ends("eli")) { r("e"); break; }
                if (ends("ousli")) { r("ous"); break; }
                break;
            case 'o': if (ends("ization")) { r("ize"); break; }
                if (ends("ation")) { r("ate"); break; }
                if (ends("ator")) { r("ate"); break; }
                break;
            case 's': if (ends("alism")) { r("al"); break; }
                if (ends("iveness")) { r("ive"); break; }
                if (ends("fulness")) { r("ful"); break; }
                if (ends("ousness")) { r("ous"); break; }
                break;
            case 't': if (ends("aliti")) { r("al"); break; }
                if (ends("iviti")) { r("ive"); break; }
                if (ends("biliti")) { r("ble"); break; }
                break;
            case 'g': if (ends("logi")) { r("log"); break; }
        }
    }

    /** step4() 处理-ic-，-full，-ness等等后缀。和步骤3有着类似的处理 */
    private final void step4() {
        switch (b[k]){
            case 'e': if (ends("icate")) { r("ic"); break; }
                if (ends("ative")) { r(""); break; }
                if (ends("alize")) { r("al"); break; }
                break;
            case 'i': if (ends("iciti")) { r("ic"); break; }
                break;
            case 'l': if (ends("ical")) { r("ic"); break; }
                if (ends("ful")) { r(""); break; }
                break;
            case 's': if (ends("ness")) { r(""); break; }
                break;
        }
    }

    /** step5() 在<c>vcvc<v>情形下，去除-ant，-ence等后缀。 */
    private final void step5(){
        if (k == 0) return; /* for Bug 1 */
        switch (b[k-1]){
            case 'a': if (ends("al")) break; return;
            case 'c': if (ends("ance")) break;
                if (ends("ence")) break; return;
            case 'e': if (ends("er")) break; return;
            case 'i': if (ends("ic")) break; return;
            case 'l': if (ends("able")) break;
                if (ends("ible")) break; return;
            case 'n': if (ends("ant")) break;
                if (ends("ement")) break;
                if (ends("ment")) break;
                /* element etc. not stripped before the m */
                if (ends("ent")) break; return;
            case 'o': if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
                /* j >= 0 fixes Bug 2 */
                if (ends("ou")) break; return;
            /* takes care of -ous */
            case 's': if (ends("ism")) break; return;
            case 't': if (ends("ate")) break;
                if (ends("iti")) break; return;
            case 'u': if (ends("ous")) break; return;
            case 'v': if (ends("ive")) break; return;
            case 'z': if (ends("ize")) break; return;
            default: return;
        }
        if (m() > 1) k = j;
    }

    /** step6() 在m()>1的情况下，移除末尾的“e”。*/
    private final void step6(){
        j = k;
        if (b[k] == 'e'){
            int a = m();
            if (a > 1 || a == 1 && !cvc(k-1)) k--;
        }
        if (b[k] == 'l' && doublec(k) && m() > 1) k--;
    }

    /** 通过调用add()方法来将单词放入词干器数组b中
     * 可以通过下面的方法得到结果： getResultLength()+getResultBuffer() or toString().
     */
    public void stem(){
        k = i - 1;
        if (k > 1) { step1(); step2(); step3(); step4(); step5(); step6(); }
        i_end = k+1;
        i = 0;
    }

    /**
     * Test program for demonstrating the Stemmer.  It reads text from a
     * a list of files, stems each word, and writes the result to standard
     * output. Note that the word stemmed is expected to be in lower case:
     * forcing lower case must be done outside the Stemmer class.
     * Usage: Stemmer file-name file-name ...
     * @param args file-name file-name ...
     */
    public static void main(String[] args){
        char[] w = new char[501];
        PorterStemmer s = new PorterStemmer();
        for (int i = 0; i < args.length; i++)
            try{
                FileInputStream in = new FileInputStream(args[i]);
                try{
                    while(true){
                        int ch = in.read();
                        if (Character.isLetter((char) ch)){
                            int j = 0;
                            while(true){
                                ch = Character.toLowerCase((char) ch);
                                w[j] = (char) ch;
                                if (j < 500) j++;
                                ch = in.read();
                                if (!Character.isLetter((char) ch)){
                                    //to test add(char ch)
                                    for (int c = 0; c < j; c++) s.add(w[c]);
                                    //or, to test add(char[] w, int j)
                                    // s.add(w, j);
                                    s.stem();
                                    String u;
                                    //and now, to test toString() :
                                    u = s.toString();
                                    //to test getResultBuffer(), getResultLength() :
                                    //u = new String(s.getResultBuffer(), 0, s.getResultLength());
                                    System.out.print(u);
                                    break;
                                }
                            }
                        }
                        if (ch < 0) break;
                        System.out.print((char)ch);
                    }
                }catch (IOException e){
                    System.out.println("error reading " + args[i]);
                    break;
                }
            }catch (FileNotFoundException e){
                System.out.println("file " + args[i] + " not found");
                break;
            }
    }

    /**
     * 对某个单词进行词干提取，返回词干
     * @param s 单词字符串
     * @return 词干
     */
    public String stem(String s){
        char[] ch=s.toLowerCase().toCharArray();
        add(ch);
        stem();
        return toString();
    }

    /**对一系列文本中的所有单词stemming
     * @param stemFile 待stemming的文本文件路径组成的字符串数组
     */
    /**
     *
     * @param stemFile
     * @throws IOException
     */
    public static void porterMain(String[] stemFile) throws IOException{
        char[] w = new char[501];
        PorterStemmer s = new PorterStemmer();
        for (int i = 0; i < stemFile.length; i++)
            try{
                FileInputStream in = new FileInputStream(stemFile[i]);
                FileWriter targetFileWriter = new FileWriter("output.txt");
                try{
                    while(true){
                        int ch = in.read();
                        if (Character.isLetter((char) ch)){
                            int j = 0;
                            while(true){
                                ch = Character.toLowerCase((char) ch);
                                w[j] = (char) ch;
                                if (j < 500) j++;
                                ch = in.read();
                                if (!Character.isLetter((char) ch)){
                                    /* to test add(char ch) */
                                    for (int c = 0; c < j; c++) s.add(w[c]);
                                    /* or, to test add(char[] w, int j) */
                                    /* s.add(w, j); */
                                    s.stem();
                                    String u;
                                    /* and now, to test toString() : */
                                    u = s.toString();
                                    /* to test getResultBuffer(), getResultLength() : */
                                    /* u = new String(s.getResultBuffer(), 0, s.getResultLength()); */
                                    //System.out.print(u);
                                    targetFileWriter.write(u + "\n");
                                    break;
                                }
                            }
                        }
                        if (ch < 0) break;
                        System.out.print((char)ch);
                    }
                    targetFileWriter.close();
                }catch (IOException e){
                    System.out.println("error reading " + stemFile[i]);
                    break;
                }
            }catch (FileNotFoundException e){
                System.out.println("file " + stemFile[i] + " not found");
                break;
            }
    }
}
import wordcount.WordCount;

public class Main {

    public static void main(String[] args)throws Exception{

        long startTime = System.currentTimeMillis();

        WordCount.main(new String[]{args[1], args[2]});

        long endTime = System.currentTimeMillis();
        System.out.println("Processing Time:" + (endTime - startTime) + "ms");

    }
}

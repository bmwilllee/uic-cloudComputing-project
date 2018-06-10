import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;


public class WordSearch {
    public static class TokenizerMapper extends Mapper<LongWritable, Text, Text, Text> {
        private Text wordNdURL = new Text();            // keep string := "word+fileName"
        // private Text wordOccurance = new Text();        // keep string := "word's appearance"
        private int lineNum;                            // reserved for further line-counting;
        private FileSplit fileSplitter;                    // keep the splitter of file

        private final static Text wordTime = new Text("1");  // modify  -one with line-bias from file start;


        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // 获取每行数据的值
            lineNum++;                                               // -May11th = everytime map() is called, increase line counter; Done;
            fileSplitter = (FileSplit) context.getInputSplit();          // get the file by fileSplitter splits
            // String fileName = fileSplitter.getPath().getName();          // -May11th = set String store fileName; Done;

            String lineValue = value.toString();

            // 分词：将每行的单词进行分割,按照"  \t\n\r\f"(空格、制表符、换行符、回车符、换页)进行分割
            StringTokenizer tokenizer = new StringTokenizer(lineValue);

            // for each time system call map(), automatically increase the lineCounter;

            while (tokenizer.hasMoreTokens()) {
                // for each identified word:
                // int splitIndex = fileSplitter.getPath().toString().indexOf("file");      // -?: what about other formats fileName

                // gain the Path of file;

                wordNdURL.set(StringFilter(tokenizer.nextToken()) + "/" + fileSplitter.getPath().getName() + "/" + lineNum);
                // -May11th = -?attempt: Context sentences here
                context.write(wordNdURL, wordTime);
            }
        }

    }

    public static String StringFilter(String str) throws PatternSyntaxException {
        //define special character
        String regEx = "[`~!@#--$%^&*()+=-_|{}'\":;',\\[\\].<>/?~£¡@#£¤%¡­¡­&*£¨£©¡ª¡ª+|{}¡¾¡¿¡®£»£º¡±¡°¡¯¡££¬¡¢£¿]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    public static class Reduce extends Reducer<Text, Text, Text, Text> {
        private Text result = new Text();
        // normal Reduce
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            int reducerCounter = 0;
            // using LIST to hold multiple fileName ;
            for (Text value : values) {
                reducerCounter +=  Integer.parseInt(value.toString());

            }

            result.set(String.valueOf(reducerCounter));     // -May11th = attempt: generate-able for single counters to each position; Done;

            context.write(key, result);
        }
    }



    public static void main(String[] args) throws Exception {
        // 获取配置信息
        Configuration conf = new Configuration();

        FileSystem fileSystem = FileSystem.get(conf);
        // 创建一个 Job
        Job job = Job.getInstance(conf, "Inverted Index");      // 设置 job name 为 word count
        // 1. 设置 Job 运行的类
        job.setJarByClass(WordSearch.class);

        // job.setInputFormatClass(NLineInputFormat.class);             // -May11th -?Question: whether needed; Done;


        // 设置Map输出类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        // 4. 设置Reduce输出结果 key 和 value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);


        // 3. 获取输入参数，设置输入文件目录和输出文件目录
        FileInputFormat.addInputPath(job, new Path(args[0]));
        // -May11th, FileNotFoundException
        /*if(fileSystem.exists(new Path("D:/01 - Cloud Computing/cloudcomputing-task4/input"))){
            fileSystem.delete(new Path("D:/01 - Cloud Computing/cloudcomputing-task4/input"), true);
        }*/
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        // -May11th, FileNotFoundException


        // 2. 设置Mapper类, Reducer类；
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(Reduce.class);



        // 5. 提交 job，等待运行结果，并在客户端显示运行信息，最后结束程序
        boolean isSuccess = job.waitForCompletion(true);

        // 结束程序
        System.exit(isSuccess ? 0 : 1);
    }
}
package runner;

import java.io.IOException;
import java.util.*;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import textpre.*;

public class WordCountSort {

    //定义log变量
    public static final Log LOG = LogFactory.getLog(FileInputFormat.class);

    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());

            while (itr.hasMoreTokens()) {

                String wordValue = itr.nextToken();
                // 先对部分特殊单词进行预处理
                String wordValueTemp1 = TextPreExe.dealAbbr(wordValue);
                // 对wordvalue进行过滤（特殊字符 + 数字）
                String wordValueTemp2 = TextPreExe.NumerFilter(TextPreExe.StringFilter(wordValueTemp1));
                if(wordValueTemp2.equals("") == true){ // 如果过滤之后的wordValue为空
                    continue;
                }
                wordValue = new PorterStemmer().stem(wordValueTemp2);

                word.set(wordValue);
                LOG.info("map过程 : " + word + "~~~~" + key + "@@@@" + one );
                context.write(word, one);
            }
        }
    }
    public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    private static class IntWritableDecreasingComparator extends IntWritable.Comparator {
        public int compare(WritableComparable a, WritableComparable b) {
            return -super.compare(a, b);
        }

        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return -super.compare(b1, s1, l1, b2, s2, l2);
        }
    }

    private static class IntWritableDecreasingComparator2 extends IntWritable.Comparator {
        public int compare(WritableComparable a, WritableComparable b) {
            return -super.compare(a, b);
        }

        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return super.compare(b1, s1, l1, b2, s2, l2);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        int i = 0;
        // obtain input from user, if 1 - descending, 2 - Ascending
        System.out.println("Input '1' for Descending, '2' for Ascending: ");
        Scanner in = new Scanner(System.in);
        i = in.nextInt();
        System.out.println(otherArgs.length);

        if (otherArgs.length != 3) {
            System.err.println("Usage: wordcount <intput> <outout>");
            System.exit(3);
        }
        Path tempDir = new Path("wordcount-temp-" + Integer.toString(new Random().nextInt(Integer.MAX_VALUE))); //定义一个临时目录

        Job job = new Job(conf, "word count");
        job.setJarByClass(WordCountSort.class);
        try{
            job.setMapperClass(TokenizerMapper.class);
            job.setCombinerClass(IntSumReducer.class);
            job.setReducerClass(IntSumReducer.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);

            FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
            FileOutputFormat.setOutputPath(job, tempDir);//先将词频统计任务的输出结果写到临时目
            //录中, 下一个排序任务以临时目录为输入目录。
            job.setOutputFormatClass(SequenceFileOutputFormat.class);
            if(job.waitForCompletion(true))
            {
                Job sortJob = new Job(conf, "sort");
                sortJob.setJarByClass(WordCountSort.class);

                FileInputFormat.addInputPath(sortJob, tempDir);
                sortJob.setInputFormatClass(SequenceFileInputFormat.class);

                /*InverseMapper由hadoop库提供，作用是实现map()之后的数据对的key和value交换*/
                sortJob.setMapperClass(InverseMapper.class);
                /*将 Reducer 的个数限定为1, 最终输出的结果文件就是一个。*/
                sortJob.setNumReduceTasks(1);
                FileOutputFormat.setOutputPath(sortJob, new Path(otherArgs[1]));

                sortJob.setOutputKeyClass(IntWritable.class);
                sortJob.setOutputValueClass(Text.class);
                /*Hadoop 默认对 IntWritable 按升序排序，而我们需要的是按降序排列。
                 * 因此我们实现了一个 IntWritableDecreasingComparator 类,
                 * 并指定使用这个自定义的 Comparator 类对输出结果中的 key (词频)进行排序*/
                if(i == 1) {
                    sortJob.setSortComparatorClass(IntWritableDecreasingComparator.class);
                }else{
                    sortJob.setSortComparatorClass(IntWritableDecreasingComparator2.class);
                }

                System.exit(sortJob.waitForCompletion(true) ? 0 : 1);
            }
        }finally{
            FileSystem.get(conf).deleteOnExit(tempDir);
        }
    }

}

package app;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Search {
	
	public Search() {};
	
    public static ArrayList<Merge> search (String keyword) throws IOException{
            File file = new File("/Users/bm_willlee/eclipse-workspace/cloudcomputing-task5/resources/part-r-00000");
            verifyParam(file, keyword);
            Pattern p = Pattern.compile(keyword+"/\\w+");
            Matcher m;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;

            ArrayList<Merge> resultList = new ArrayList<>();

            while((line = br.readLine()) != null){
                m = p.matcher(line);
                if(m.find()){
                    String[] strs = line.split("/");
                    Boolean flag = false;
                    for (Merge merge: resultList) {
                        if(merge.getFileName().equals(strs[1])){
                            merge.addLineRecord(strFilter(strs[2]));
                            merge.num++;
                            flag = true;
                        }
                    }
                    if(flag == false){
                        resultList.add(new Merge(strs[1]));
                        resultList.get(resultList.size()-1).num++;
                        resultList.get(resultList.size()-1).addLineRecord(strFilter(strs[2]));
                    }
                }
            }
            br.close();
        return resultList;
    }

    public static void verifyParam(File file, String keyword) {
        //对参数进行校验证
        if(file == null ){
            throw new NullPointerException("the file is null");
        }
        if(keyword == null || keyword.trim().equals("")){
            throw new NullPointerException("the keyword is null or \"\" ");
        }

        if(!file.exists()) {
            throw new RuntimeException("the file is not exists");
        }
        //非目录
        if(file.isDirectory()){
            throw new RuntimeException("the file is a directory,not a file");
        }

        //可读取
        if(!file.canRead()) {
            throw new RuntimeException("the file can't read");
        }
    }
    
    public static String strFilter(String str) {
		String[] strs = str.split("\\s+");
		return strs[0];
}
}
package app;

import java.util.ArrayList;

public class Merge {
    public int num = 0;
    private String fileName = "";
    public ArrayList<String> lineList = new ArrayList<String>();

    public Merge(String fileName){
        this.fileName = fileName;
    }

    public int getNum(){
        return num;
    }

    public String getFileName(){
        return fileName;
    }

    public void addLineRecord(String s){
        lineList.add(s);
    }
}

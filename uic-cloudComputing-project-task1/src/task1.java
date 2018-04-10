import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeMap;

public class task1{
	public static void display(String filename)throws Exception{
		File file = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		
		TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
		
		while((line = br.readLine()) != null)
		{
			line.toLowerCase();																																																																																																																	
			String reg1 = "\\s+";
			String reg2 = "\\w+";
			
			String str[] = line.split(reg1);
			for(String s: str){
				if(s.matches(reg2)){
					if(!tm.containsKey(s)){
						tm.put(s, 1);
					}else{
						tm.put(s, tm.get(s)+1);
					}
				}
			}
		}
		System.out.println(tm);
	}
	
	public static void main(String[] args) throws Exception{
		display("/Users/bm_willlee/Desktop/NovelTexts.txt");
	}
}

import java.io.File;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author bm_willlee
 * 
 */

public class NovelCrawling {
	
	static int cid = 10334840;
	private String url = "http://vip.book.sina.com.cn/weibobook/vipc.php?bid=5383675&cid=";
	
	public static void getNovel(String destFilePath) {
		try {
			File destFile = new File(destFilePath);
			if(!destFile.exists()) destFile.delete();
			destFile.createNewFile();
			
			FileWriter fWriter = new FileWriter(destFile);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void Creawling(String url) {
		Pattern contentPat = Pattern.compile("<div class=\"sr-play-box-scroll-text\" id=\"sr-js-play-box-scroll-c\">(.+?)</div>");
		Matcher matcher;
		for(int i=0; i<5; i++) {
			url = url + Integer.toString(cid);
			
			
			cid ++;
		}
	}
	
	public static void main(String[] args) {
		
	}
}

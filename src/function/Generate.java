package function;
import java.util.Random;

public class Generate {
	public static  void main(String[] args){
		int cityNum = 15;
		//int pathNum = cityNum * (cityNum - 1) / 2;
		String c = cityNum + "";
		LogRecord.logRecord(c,"C:\\Users\\Chao\\Desktop\\problem2_1.txt");
		Random random = new Random();
		for(int i = 0;i < cityNum;i++){
			for(int j = i + 1;j < cityNum;j++){
				int from = i;
				int to = j;
				int path10 = random.nextInt(100);
				path10 += 10;
				double path = path10 / 10;
				String content = from + " " + to + " " + path;
				LogRecord.logRecord(content,"C:\\Users\\Chao\\Desktop\\problem2_1.txt");
			}
		}
	}
}
package hamiltonRoad;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class HamiltonProblem {
	
	private int cityNum;
	private int pathNum;
	private double[][] pathWeight;
	
	private int[][] groupGenes;
	private int groupScale;
	private int maxGenerationNumber = HyperParameter.MAXGENERATIONNUMBER;
	private double[] fitness;
	
	private double[] pi;
	private double pc = HyperParameter.PC;
	private double pm = HyperParameter.PM;
	
	private String testFilePath = HyperParameter.PARTONEPATH;
	
	public HamiltonProblem(){
		readProblem();
	}
	
	private void readProblem(){
		File file = new File(testFilePath);
		if(file.isFile() && file.exists()){
			try {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
	            BufferedReader bufferedReader = new BufferedReader(read);
	            String firstLine = bufferedReader.readLine();
	            this.cityNum = Integer.parseInt(firstLine);
	            this.pathNum = cityNum * (cityNum - 1) / 2;
	            pathWeight = new double[cityNum][cityNum];
	            for(int i = 0;i < pathNum;i++){
	            	String pathInfoLine = bufferedReader.readLine();  
	            	String[] tempList = pathInfoLine.split(" ");
	            	int from = Integer.parseInt(tempList[0]);
	            	System.out.println(from);
	            	int to = Integer.parseInt(tempList[1]);
	            	double weight = Double.parseDouble(tempList[2]);
	            	pathWeight[from][to] = weight;
	            	pathWeight[to][from] = weight;
	            }
	            read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}    
		}else{
			System.out.println("no file---");
		}
	}
}

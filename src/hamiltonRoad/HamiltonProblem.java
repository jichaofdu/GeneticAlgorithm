package hamiltonRoad;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class HamiltonProblem {
	
	private int cityNum;
	private int pathNum;
	private double[][] pathWeight;
	
	private int[][] groupGenes;
	private int groupScale = HyperParameter.SCALE;
	private int maxGenerationNumber = HyperParameter.MAXGENERATIONNUMBER;
	private double[] fitness;
	
	private double[] pi;
	private double pc = HyperParameter.PC;
	private double pm = HyperParameter.PM;
	
	private String testFilePath = HyperParameter.PARTONEPATH;
	
	public HamiltonProblem(){
		readProblem();
		initGroupGene();
		printGeneGroup();
		for(int i = 0; i < maxGenerationNumber;i++){
			calculateFitness();
			evolution();
		}
		calculateFitness();
		printGeneGroup();
		getBestResult();
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
	
	private void initGroupGene(){
		groupGenes = new int[this.groupScale][this.cityNum];
		fitness = new double[this.groupScale];
		for(int i = 0;i < groupScale;i++){
			groupGenes[i] = generateRandomLine();
		}
	}
	
	private void calculateFitness(){
		double firstAmount = 0;
		fitness = new double[this.groupScale];
		for(int i = 0;i < groupScale;i++){
			double temp = 0;
			for(int j = 0;j < cityNum - 1;j++){
				int from = groupGenes[i][j];
				int to = groupGenes[i][j+1];
				temp += pathWeight[from][to];
			}
			temp += pathWeight[groupGenes[i][cityNum - 1]][groupGenes[i][0]];
			fitness[i] = temp;	
			firstAmount += fitness[i];
		}
		double average = firstAmount / groupScale;
		double secondAmount = 0;
		for(int i = 0;i < groupScale;i++){
			fitness[i] = average / fitness[i];
			secondAmount += fitness[i];
		}
		for(int i = 0;i < groupScale;i++){
			fitness[i] /= secondAmount;
		}
		pi = new double[this.groupScale];
		pi[0] = fitness[0];
		for(int i = 1;i < groupScale;i++){
			pi[i] = fitness[i] + pi[i-1];
			//System.out.println("p[" + i + "]" + pi[i]);
		}	
	}
	
	private void evolution(){
		select();
		Random random = new Random();
		//对解进行交叉
		 for(int i = 0;i < groupScale - 1;i++){
			 double crossProbability = random.nextDouble();
			 if(crossProbability < pc){
				 cross(groupGenes[i],groupGenes[i+1]);
			 }
		 }
		
		//对解作突变处理
		 for(int i = 0;i < groupScale;i++){
			 for(int j = 0;j < cityNum;j++){
				 double probability = random.nextDouble();
				 if(probability < pm){
					 int firstCity = random.nextInt(cityNum - 1);
					 int secondCity = random.nextInt(cityNum - 1);
					 int temp = 0;
					 temp = groupGenes[i][firstCity];
					 groupGenes[i][firstCity] = groupGenes[i][secondCity];
					 groupGenes[i][secondCity] = temp;
				 }
			}
		 }
	}
	
	private void cross(int[] firstGene,int[] secondGene){
		 Random random = new Random();
		//确定交叉上下界
		 int edgeFront = random.nextInt(cityNum);
		 int edgeBehind = random.nextInt(cityNum);
		 if(edgeFront > edgeBehind){
			 int temp = edgeFront;
			 edgeFront = edgeBehind;
			 edgeBehind = temp;
		 }
		 //对数组进行交叉
		 int crossLength = edgeBehind - edgeFront;
		 for(int i = 0;i < crossLength;i++){
			 int temp = firstGene[edgeFront + i];
			 firstGene[edgeFront + i] = secondGene[edgeFront + i];
			 secondGene[edgeFront + i] = temp;
		 }
		 int[] firstGeneNewPiece = new int[crossLength];
		 int[] secondGeneNewPiece = new int[crossLength];
		 for(int i = 0;i < crossLength;i++){
			firstGeneNewPiece[i] = firstGene[i + edgeFront]; 
			secondGeneNewPiece[i] = secondGene[i + edgeFront];
		 }
		 //去除重复元素
		 int count = 0;
		 for(int i = 0;i < cityNum;i++){
			 if(i >= edgeFront && i < edgeBehind){
				 continue;
			 }else{
				 int checkNum = firstGene[i];
				 boolean isRepeat = crossRepeatCheck(checkNum,firstGeneNewPiece);
				 if(isRepeat == true) count++; 
			 }
		 }
		 int[] firstGeneRepeatRecord = new int[count];
		 int[] secondGeneRepeatRecond = new int[count];
		 
		 for(int i = 0,record = 0;i < cityNum;i++){
			 if(i >= edgeFront && i < edgeBehind){
				 continue;
			 }else{
				 int checkNum = firstGene[i];
				 boolean isRepeat = crossRepeatCheck(checkNum,firstGeneNewPiece);
				 if(isRepeat == true){
					 firstGeneRepeatRecord[record] = i;
					 record++;
				 }
			 }
		 }
		 for(int i = 0,record = 0;i < cityNum;i++){
			 if(i >= edgeFront && i < edgeBehind){
				 continue;
			 }else{
				 int checkNum = secondGene[i];
				 boolean isRepeat = crossRepeatCheck(checkNum,secondGeneNewPiece);
				 if(isRepeat == true){
					 secondGeneRepeatRecond[record] = i;
					 record++;
				 }
			 }
		 }
		 for(int i = 0;i < count;i++){
			 int firstIndex = firstGeneRepeatRecord[i];
			 int secondIndex = secondGeneRepeatRecond[count - 1 - i];
			 int temp = firstGene[firstIndex];
			 firstGene[firstIndex] = secondGene[secondIndex];
			 secondGene[secondIndex] = temp;
		 }
	}
	
	private boolean crossRepeatCheck(int checkNum,int[] checkPiece){
		int length = checkPiece.length;
		for(int i = 0;i < length;i++){
			if(checkPiece[i] == checkNum){
				return true;
			}
		}
		return false;
	}
	
	private void select(){
		int[][] newGroupGene = new int[this.groupScale][this.cityNum];
		int maxIndex = 0;
		double max = 0;
		for(int i = 0;i < groupScale;i++){
			if(fitness[i] > max){
				max = fitness[i];
				maxIndex = i;
			}
		}
		geneTransOldToNew(groupGenes[maxIndex],newGroupGene,0);
		Random random = new Random();
		for(int i = 1;i < groupScale;i++){
			double randomProbability = random.nextDouble();
			//System.out.println("random:" + randomProbability);
			for(int j = 0;j < groupScale;j++){
				if(randomProbability < pi[j]){
					geneTransOldToNew(groupGenes[j],newGroupGene,i);
					break;
				}
			}
		}
		this.groupGenes = newGroupGene;
	}
	
	private int[] generateRandomLine(){
		int[] result = new int[cityNum];
		int[] bull = new int[cityNum];
		for(int i = 0;i < cityNum;i++){
			bull[i] = i;
		}
		Random random = new Random();
		int total = cityNum;
		for(int i = 0;i < cityNum;i++){
			int indexRandom = random.nextInt(total);
			result[i] = bull[indexRandom];
			bull[indexRandom] = bull[total - 1];
			total -= 1;
		}
		//		for(int i = 0;i < cityNum;i++){
		//			System.out.print(result[i]);
		//		}
		return result;
	}
	
	private void getBestResult(){
		calculateFitness();
		double max = 0;
		int maxIndex = 0;
		for(int i = 0;i < groupScale;i++){
			if(fitness[i] > max){
				max = fitness[i];
				maxIndex = i;
			}
		}
		double temp = 0;
		System.out.println("Result:");
		for(int k = 0;k < cityNum;k++){
			System.out.print(groupGenes[maxIndex][k] + "-");
			if(k < cityNum - 1){
				temp += pathWeight[groupGenes[maxIndex][k]][groupGenes[maxIndex][k+1]];
			}else{
				temp += pathWeight[groupGenes[maxIndex][k]][groupGenes[maxIndex][0]];
			}
		}
		System.out.print("End");
		System.out.println();
		System.out.println("Weight:" + temp);
	}
	
	private void geneTransOldToNew(int[] gene,int[][] newGeneGroup,int index){
		for(int i = 0;i < gene.length;i++){
			newGeneGroup[index][i] = gene[i];
		}
	}
	
	private void printGeneGroup(){
		for(int i = 0;i < groupScale;i++){
			for(int j = 0;j < cityNum;j++){
				System.out.print(groupGenes[i][j] + "-");
			}
			System.out.print("End");
			System.out.println();
		}
	}
}

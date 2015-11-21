package threeSATProblem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class ThreeSatProblem {
	
	private int clueGroupNum;//命题组数量
	private int clueClassNum;//命题一共有多少种
	private int[][] clueValue;//读入的命题组
	
	private int[][] groupGenes;
	private int[][] groupGenesValue;
	
	private int groupScale = HyperParameter.SCALE;
	private int maxGenerationNumber = HyperParameter.MAXGENERATIONNUMBER;
	private double[] fitness;
	
	private double[] pi;
	private double pc = HyperParameter.PC;
	private double pm = HyperParameter.PM;
	
	private String testFilePath = HyperParameter.PARTONEPATH;
	
	public ThreeSatProblem(){
		readProblem();
		initGroupGene();
		setGenesValue();
		printGeneGroup();
		for(int i = 0; i < maxGenerationNumber;i++){
			calculateFitness();
			evolution();
			setGenesValue();
		}
		calculateFitness();
		//printGeneGroup();
		getBestResult();
	}
	
	private void readProblem(){
		File file = new File(testFilePath);
		if(file.isFile() && file.exists()){
			try {
	            InputStreamReader read = new InputStreamReader(new FileInputStream(file));
	            BufferedReader bufferedReader = new BufferedReader(read);
	            String firstLine;
	            firstLine = bufferedReader.readLine();
	            String[] tempList = firstLine.split(" ");
	            this.clueClassNum = Integer.parseInt(tempList[0]);
	            this.clueGroupNum = Integer.parseInt(tempList[1]);
	            this.clueValue = new int[clueGroupNum][3];
	            for(int i = 0;i < clueGroupNum;i++){
	            	String objInfoLine = bufferedReader.readLine();  
	            	String[] innerTempList = objInfoLine.split(" ");
	            	int firstGroupClue = Integer.parseInt(innerTempList[0]);
	            	int secondGroupClue = Integer.parseInt(innerTempList[1]);
	            	int thirdGroupClue = Integer.parseInt(innerTempList[2]);
	            	this.clueValue[i][0] = firstGroupClue;
	            	this.clueValue[i][1] = secondGroupClue;
	            	this.clueValue[i][2] = thirdGroupClue;
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
		this.groupGenes = new int[this.groupScale][this.clueClassNum];
		this.groupGenesValue = new int[this.groupScale][this.clueClassNum];
		this.fitness = new double[this.groupScale];
		this.pi = new double[this.groupScale];
		Random random = new Random();
		for(int i = 0;i < groupScale;i++){
			for(int j = 0;j < clueClassNum;j++){
				 groupGenes[i][j] = random.nextInt(2);
			}
		}
	}
	
	private void setGenesValue(){
		for(int i = 0;i < groupScale;i++){
			for(int j = 0;j < clueClassNum;j++){
				 if(groupGenes[i][j] == 0){
					 groupGenesValue[i][j] = -1 * (j + 1);
				 }else{
					 groupGenesValue[i][j] = 1 * (j + 1);
				 }
			}
		}
	}
	
	private void calculateFitness(){
		int fitnessAmount = 0;
		for(int i = 0;i < groupScale;i++){
			//对于种群中的每一个基因，用这个基因检测所有的命题组，对成立的命题组计数
			int correctCount = 0;
			for(int j = 0;j < clueValue.length;j++){
				boolean status = checkEveryGroup(j,i);
				if(status == true){
					correctCount += 1;
				}
			}
			fitnessAmount += correctCount;
			fitness[i] = correctCount;	
		}
		for(int i = 0;i < groupScale;i++){
				fitness[i] = fitness[i] / fitnessAmount;
		}
		pi[0] = fitness[0];
		for(int i = 1;i < groupScale;i++){
			pi[i] = fitness[i] + pi[i-1];
		}
	}
	
	private void evolution(){
	     //自然选择
		 select();  
		 //发生交叉
		 Random random = new Random();
		 for(int i = 0;i < groupScale - 1;i++){
			 double crossProbability = random.nextDouble();
			 if(crossProbability < pc){
				 cross(groupGenes[i],groupGenes[i+1]);
			 }
		 }
		 //发生变异
		 for(int i = 0;i < groupScale;i++){
			 for(int j = 0;j < clueClassNum;j++){
				 double probability = random.nextDouble();
				 if(probability < pm){
					 groupGenes[i][j] = 1 - groupGenes[i][j];
				 }
			}
		 }
	}
	
	private void cross(int[] firstGene,int[] secondGene){
		 Random random = new Random();
		//确定交叉上下界
		 int edgeFront = random.nextInt(clueClassNum);
		 int edgeBehind = random.nextInt(clueClassNum);
		 if(edgeFront > edgeFront){
			 int temp = edgeFront;
			 edgeFront = edgeBehind;
			 edgeBehind = temp;
		 }
		 //对数组进行交叉
		 int crossLength = edgeBehind - edgeFront;
		 int temp;
		 for(int i = 0;i < crossLength;i++){
			 temp = firstGene[edgeFront + i];
			 firstGene[edgeFront + i] = secondGene[edgeFront + i];
			 secondGene[edgeFront + i] = temp;
		 }
	}
	
	private void select(){
		int[][] newGroupGene = new int[this.groupScale][this.clueClassNum];
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
			for(int j = 0;j < groupScale;j++){
				if(randomProbability < pi[j]){
					geneTransOldToNew(groupGenes[j],newGroupGene,i);
					break;
				}
			}
		}
		this.groupGenes = newGroupGene;
	}
	
	private void geneTransOldToNew(int[] gene,int[][] newGeneGroup,int index){
		for(int i = 0;i < gene.length;i++){
			newGeneGroup[index][i] = gene[i];
		}
	}
	
	private boolean checkEveryGroup(int groupIndex,int genesIndex){
		int length = clueValue[groupIndex].length;
		boolean status = false;
		for(int i = 0;i < length;i++){
			int clueIndex = Math.abs(clueValue[groupIndex][i]) - 1;
			if(clueValue[groupIndex][i] == groupGenesValue[genesIndex][clueIndex]){
				status = true;
				break;
			}	
		}
		return status;
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
		System.out.println("Result:");
		for(int j = 0;j < clueValue.length;j++){
			boolean status = checkEveryGroup(j,maxIndex);
			if(status == true){	
				System.out.println("命题" + j + "符合");
			}
		}
		for(int k = 0;k < clueClassNum;k++){
			System.out.print(groupGenes[maxIndex][k]);
			LogRecord.logRecord(groupGenes[maxIndex][k]+"", "C:\\Users\\Chao\\Desktop\\problem3_sol.txt");
		}


	}
	
	private void printGeneGroup(){
		for(int i = 0;i < groupScale;i++){
			for(int j = 0;j < clueClassNum;j++){
				System.out.print(groupGenes[i][j]);
			}
			System.out.println();
		}
	}
}

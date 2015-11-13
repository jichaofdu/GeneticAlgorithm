package knapsackProblem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class KnapsackProblem {
	
	private int objectNum;
	private double bagCapacity;
	private double[] objectWeight;
	private double[] objectValue;
	
	private int[][] groupGenes;
	private int groupScale = HyperParameter.SCALE;
	private int maxGenerationNumber = HyperParameter.MAXGENERATIONNUMBER;
	private double[] fitness;
	
	private double[] pi;
	private double pc = HyperParameter.PC;
	private double pm = HyperParameter.PM;
	
	private String testFilePath = HyperParameter.PARTONEPATH;
	
	public KnapsackProblem(){
		readProblem();
		initGroupGene();
		for(int i = 0; i < maxGenerationNumber;i++){
			calculateFitness();
			evolution();
		}
		printGeneGroup();
		
	}
	
	private void readProblem(){
		File file = new File(testFilePath);
		if(file.isFile() && file.exists()){
			try {
	            InputStreamReader read = new InputStreamReader(new FileInputStream(file));
	            BufferedReader bufferedReader = new BufferedReader(read);
	            String firstLine;
				firstLine = bufferedReader.readLine();
	            this.bagCapacity = Double.parseDouble(firstLine);
	            String secondLine = bufferedReader.readLine();  
	            this.objectNum = Integer.parseInt(secondLine);
	            this.objectWeight = new double[this.objectNum];
	            this.objectValue = new double[this.objectNum];
	            for(int i = 0;i < objectNum;i++){
	            	String objInfoLine = bufferedReader.readLine();  
	            	String[] tempList = objInfoLine.split(" ");
	            	double tempSize = Double.parseDouble(tempList[0]);
	            	double tempValue = Double.parseDouble(tempList[1]);
	            	this.objectWeight[i] = tempSize;
	            	this.objectValue[i] = tempValue;
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
		groupGenes = new int[this.groupScale][this.objectNum];
		fitness = new double[this.groupScale];
		pi = new double[this.groupScale];
		Random random = new Random();
		for(int i = 0;i < groupScale;i++){
			for(int j = 0;j < objectNum;j++){
				 groupGenes[i][j] = random.nextInt(2) % 2;  
			}
		}
	}
	
	private void calculateFitness(){
		double fitnessAmount = 0;
		for(int i = 0;i < groupScale;i++){
			double tempValueAmount = 0;
			double tempWeightAmount = 0;
			for(int j = 0;j < objectNum;j++){
				tempValueAmount += groupGenes[i][j] * objectValue[j];
				tempWeightAmount += groupGenes[i][j] * objectWeight[j];
			}
			if(tempWeightAmount > bagCapacity){
				tempValueAmount = 0;
			}
			fitnessAmount += tempValueAmount;
			fitness[i] = tempValueAmount;
		}
		for(int i = 0;i < groupScale;i++){
			if(fitnessAmount != 0){
				fitness[i] = fitness[i] / fitnessAmount;
				//System.out.println(fitness[i]);
			}
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
			 for(int j = 0;j < objectNum;j++){
				 double probability = random.nextDouble();
				 if(probability < pm){
					 groupGenes[i][j] = 1 - groupGenes[i][j];
				 }
			}
		 }
	}
	
	private void select(){
		int[][] newGroupGene = new int[this.groupScale][this.objectNum];
		Random random = new Random();
		for(int i = 0;i < groupScale;i++){
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
	
	private void cross(int[] firstGene,int[] secondGene){
		 Random random = new Random();
		//确定交叉上下界
		 int edgeFront = random.nextInt(objectNum);
		 int edgeBehind = random.nextInt(objectNum);
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
	
	private void geneTransOldToNew(int[] gene,int[][] newGeneGroup,int index){
		for(int i = 0;i < gene.length;i++){
			newGeneGroup[index][i] = gene[i];
		}
	}
	
	private void printGeneGroup(){
		for(int i = 0;i < groupScale;i++){
			for(int j = 0;j < objectNum;j++){
				System.out.print(groupGenes[i][j]);
			}
			System.out.println();
		}
	}
	
}

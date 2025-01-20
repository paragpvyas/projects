import java.util.*;
import java.io.*;
import java.math.*;
public class TestDriver
{
	//declare fields and try to avoid magic numbers
	final static int minSides = 1;
	final static int maxSides = 6;

	final static int sampleSize = 6000;
	final static int experimentSize = 4;
	final static int trialSize = 60;

	final static int sideOne = 0;
	final static int sideTwo = 1;
	final static int sideThree = 2;
	final static int sideFour = 3;
	final static int sideFive = 4;
	final static int sideSix = 5;

	final static int sideOneWeight = 1;
	final static int sideTwoWeight = 2;
	final static int sideThreeWeight = 3;
	final static int sideFourWeight = 4;
	final static int sideFiveWeight = 5;
	final static int sideSixWeight = 6;

	final static int loadedMinSides = 3;
	final static int brokenSides = 1;

	final static int experimentOne = 0;
	final static int experimentTwo = 1;
	final static int experimentThree = 2;
	final static int experimentFour = 3;

	final static MathContext myMathContext = new MathContext(6);
	final static int outcomeIndex = 0;
	final static int sampleMeanIndex = 0;
	final static int varianceIndex = 1;
	public static int greaterThan = 1;

	public static int[][] matlabSamples = new int[trialSize][sampleSize];
	public static int[][] javaRandomSamples = new int[trialSize][sampleSize];
	public static int[][] loadedDiceSamples = new int[trialSize][sampleSize];
	public static int[][] brokenDiceSamples = new int[trialSize][sampleSize];
	public static int[][] frequencyCountSummary = new int[experimentSize][maxSides];
	public static int[][][] allfrequencyCounts = new int[experimentSize][trialSize][maxSides];
	public static BigDecimal[][][] probabilitiesSummary = new BigDecimal[experimentSize][trialSize][maxSides];
	public static BigDecimal[][][] expectedOutcomesSummary = new BigDecimal[experimentSize][trialSize][1];
	public static BigDecimal[] computedSampleMeanAndVariance = new BigDecimal[2];

	//DICE GAME METHODS
	public static void playDice(){
		System.out.println("Welcome to Pepper's Dice Roll Game!");
		System.out.println("I will now roll a virtual six sided dice and tell you the result.");
		System.out.println("You rolled a " + getRandomInteger(minSides, maxSides) + "!");
	}

	public static int getRandomInteger(int minBound, int MaxBound){
		//NOTE: openjdk may by default seed with automatically updated AtomicLong value
		Random randNumGenerator= new Random();
		return(minBound + randNumGenerator.nextInt(MaxBound - minBound + 1));
	}


	//Run dice fairness analysis.
	public static void testRandomness(){
		//create data structures
		//extract 60 expected outcome results from each experiment
		//create sample sets
		System.out.println("Create Samples Sets");
		try{
			File matlabResults = new File("matlabtrialsthree.txt");
			Scanner fileReader = new Scanner(matlabResults);
			int lineCounter = 0;
			int colmCounter = 0;
			int trialID = 0;
			while(fileReader.hasNext()){
				matlabSamples[trialID][colmCounter] = fileReader.nextInt();
				lineCounter++;
				colmCounter++;
				if(lineCounter % 6000 == 0){
					colmCounter = 0;
					trialID++;
				}
			}
			fileReader.close();

		} catch (Exception e){
			System.out.println(e);
		}

		for(int trialID = 0; trialID < trialSize; trialID++){
			for(int sampleID = 0; sampleID < sampleSize; sampleID++){
				javaRandomSamples[trialID][sampleID] = getRandomInteger(minSides, maxSides);
			}
		}
		//never roll a 1 or 2.
		for(int trialID = 0; trialID < trialSize; trialID++){
			for(int sampleID = 0; sampleID < sampleSize; sampleID++){
				loadedDiceSamples[trialID][sampleID] = (getRandomInteger(loadedMinSides, maxSides));
			}
		}
		//only ever roll a 1.
		for(int trialID = 0; trialID < trialSize; trialID++){
			for(int sampleID = 0; sampleID < sampleSize; sampleID++){
				brokenDiceSamples[trialID][sampleID] = brokenSides;
			}
		}

		System.out.println("Create Samples Sets");
		System.out.println("Calculate Summary Statistics");
		System.out.println("Frequency Count Summary");
		countAllFrequencies(allfrequencyCounts);
		printArray(allfrequencyCounts);
		System.out.println("Probabilities Summary");
		calculateAllProbabilities(allfrequencyCounts, probabilitiesSummary);
		printArray(probabilitiesSummary);
		System.out.println("Expected Outcomes Summary");
		calculateAllExpectedOutcomes(probabilitiesSummary, expectedOutcomesSummary);
		printArray(expectedOutcomesSummary);
		computedSampleMeanAndVariance = computeSampleMeanAndVarianceWithMatlabData(expectedOutcomesSummary);
		System.out.println("Conduct Testing of RNG generators");
		System.out.println("sample mean: " + computedSampleMeanAndVariance[sampleMeanIndex]);
		System.out.println("sample variance: " + computedSampleMeanAndVariance[varianceIndex]);
		conductFairDiceTest(computedSampleMeanAndVariance[sampleMeanIndex], 
						    computedSampleMeanAndVariance[varianceIndex],
							expectedOutcomesSummary);

	}//endtestrandomness

	public static void conductFairDiceTest(BigDecimal variance, BigDecimal sampleMean, BigDecimal[][][] expectedOutcomesSummary){
		BigDecimal matlabSampleExpectedOutcome = expectedOutcomesSummary[experimentOne][0][0];
		BigDecimal javaRandomSampleExpectedOutcome = expectedOutcomesSummary[experimentTwo][0][0];
		BigDecimal loadedDiceExpectedOutcome = expectedOutcomesSummary[experimentThree][0][0];
		BigDecimal brokenDiceExpectedOutcome = expectedOutcomesSummary[experimentFour][0][0];
		BigDecimal tolerance = variance.multiply(new BigDecimal(0.1), myMathContext);
		
		for(int experimentID = 0; experimentID < experimentSize; experimentID++){
			if(experimentID == experimentOne){
				BigDecimal testStatisticOne = (matlabSampleExpectedOutcome.subtract(sampleMean, myMathContext)).setScale(6, RoundingMode.HALF_EVEN);
				BigDecimal testStatisticTwo = testStatisticOne.abs(myMathContext);
				BigDecimal testStatisticThree = testStatisticTwo.divide(variance, 6, RoundingMode.HALF_EVEN);
				String testResult = "FAIL";
				if(testStatisticThree.compareTo(tolerance) == greaterThan)
					testResult = "PASS";
					
				System.out.println("Matlab RNG test result: " + testResult);
			}
			if(experimentID == experimentTwo){
				BigDecimal testStatisticOne = (javaRandomSampleExpectedOutcome.subtract(sampleMean, myMathContext)).setScale(6, RoundingMode.HALF_EVEN);
				BigDecimal testStatisticTwo = testStatisticOne.abs(myMathContext);
				BigDecimal testStatisticThree = testStatisticTwo.divide(variance, 6, RoundingMode.HALF_EVEN);
				String testResult = "FAIL";
				if(testStatisticThree.compareTo(tolerance) == greaterThan)
					testResult = "PASS";
					
				System.out.println("Java 11 RNG test result: " + testResult);
			}	
			if(experimentID == experimentThree){
				BigDecimal testStatisticOne = (loadedDiceExpectedOutcome.subtract(sampleMean, myMathContext)).setScale(6, RoundingMode.HALF_EVEN);
				BigDecimal testStatisticTwo = testStatisticOne.abs(myMathContext);
				BigDecimal testStatisticThree = testStatisticTwo.divide(variance, 6, RoundingMode.HALF_EVEN);
				String testResult = "FAIL";
				if(testStatisticThree.compareTo(tolerance) == greaterThan)
					testResult = "PASS";
					
				System.out.println("Loaded Dice RNG test result: " + testResult);
			}
			if(experimentID == experimentFour){
				BigDecimal testStatisticOne = (brokenDiceExpectedOutcome.subtract(sampleMean, myMathContext)).setScale(6, RoundingMode.HALF_EVEN);
				BigDecimal testStatisticTwo = testStatisticOne.abs(myMathContext);
				BigDecimal testStatisticThree = testStatisticTwo.divide(variance, 6, RoundingMode.HALF_EVEN);
				String testResult = "FAIL";
				if(testStatisticThree.compareTo(tolerance) == greaterThan)
					testResult = "PASS";
					
				System.out.println("Broken Dice RNG test result: " + testResult);
			}
			
			
		}
	}
	
	public static BigDecimal[] computeSampleMeanAndVarianceWithMatlabData(BigDecimal[][][] expectedOutcomesSummary){
		BigDecimal[] results = new BigDecimal[2];
		
		BigDecimal accumulator = new BigDecimal(0);
		for(int trialID = 0; trialID < trialSize; trialID++){
			accumulator = accumulator.add(expectedOutcomesSummary[experimentOne][trialID][outcomeIndex], myMathContext);
		}
		BigDecimal sampleMean = accumulator.divide(new BigDecimal(trialSize), myMathContext);
		
		BigDecimal accumulatorTwo = new BigDecimal(0);
		for(int trialID = 0; trialID < trialSize; trialID++){
			BigDecimal distanceFromMean = expectedOutcomesSummary[experimentOne][trialID][outcomeIndex].subtract(sampleMean,myMathContext);
			accumulatorTwo = accumulatorTwo.add((distanceFromMean).pow(2, myMathContext));
		}
		BigDecimal varianceSquared = accumulatorTwo.divide(new BigDecimal(trialSize, myMathContext), 6, RoundingMode.HALF_EVEN);
		BigDecimal variance = varianceSquared.sqrt(myMathContext);

		results[sampleMeanIndex] = sampleMean;
		results[varianceIndex] = variance;
		return results; 		
	}
		
	public static void calculateAllExpectedOutcomes(BigDecimal[][][] probabilitiesSummary, BigDecimal[][][] expectedOutcomesSummary){
		for(int experimentID = 0; experimentID < experimentSize; experimentID++){
			for(int trialID = 0; trialID < trialSize; trialID++){
				calculateExpectedOutcomes(experimentID, trialID, probabilitiesSummary, expectedOutcomesSummary);
			}
		}
		
		
	}//endallcalcEx		
	public static void calculateExpectedOutcomes(int experimentID,
												 int trialID,
												 BigDecimal[][][] probOf,
												 BigDecimal[][][] expectedOutcome){

			BigDecimal resultOne = probOf[experimentID][trialID][sideOne].multiply(new BigDecimal(sideOneWeight), myMathContext);
			BigDecimal resultTwo = probOf[experimentID][trialID][sideTwo].multiply(new BigDecimal(sideTwoWeight), myMathContext);
			BigDecimal resultThree = probOf[experimentID][trialID][sideThree].multiply(new BigDecimal(sideThreeWeight), myMathContext);
			BigDecimal resultFour = probOf[experimentID][trialID][sideFour].multiply(new BigDecimal(sideFourWeight), myMathContext);
			BigDecimal resultFive = probOf[experimentID][trialID][sideFive].multiply(new BigDecimal(sideFiveWeight), myMathContext);
			BigDecimal resultSix = probOf[experimentID][trialID][sideSix].multiply(new BigDecimal(sideSixWeight), myMathContext);

			BigDecimal resultSeven = resultOne.add(resultTwo, myMathContext);
			BigDecimal resultEight = resultThree.add(resultFour, myMathContext);
			BigDecimal resultNine = resultFive.add(resultSix, myMathContext);
			BigDecimal resultTen = resultSeven.add(resultEight, myMathContext);

			expectedOutcome[experimentID][trialID][0] = resultTen.add(resultNine, myMathContext);
	}//endcalcExpOut



	public static void calculateProbabilities(int experimentID, int trialID, int[][][] frequencyCountSummary, BigDecimal[][][] probabilitiesSummary){
		BigDecimal sampleSizeDecimal = new BigDecimal(sampleSize, myMathContext);
			for(int sideNumber = 0; sideNumber < maxSides; sideNumber++){
				BigDecimal frequencyCount = new BigDecimal(frequencyCountSummary[experimentID][trialID][sideNumber], myMathContext);
				probabilitiesSummary[experimentID][trialID][sideNumber] = frequencyCount.divide(sampleSizeDecimal, myMathContext);
			}
	}//endcalcprob

	public static void calculateAllProbabilities(int[][][] frequencyCountSummary, BigDecimal[][][] probabilitiesSummary){
		for(int experimentID = 0; experimentID < experimentSize; experimentID++){
			for(int trialID = 0; trialID < trialSize; trialID++){
				calculateProbabilities(experimentID, trialID, frequencyCountSummary, probabilitiesSummary);
			}
		}
	}//endcalcallprobs
	
	public static void frequencyCount(int experimentID, int trialID, int[] sampleArray, int[][][] countSummary){
		int bucketToIncrement = 0;
		for(int sampleID = 0; sampleID < sampleSize; sampleID++){
			int currentSample = sampleArray[sampleID];
			if(currentSample == sideOneWeight)
				bucketToIncrement = sideOne;
			else if(currentSample == sideTwoWeight)
				bucketToIncrement = sideTwo;
			else if(currentSample == sideThreeWeight)
				bucketToIncrement = sideThree;
			else if(currentSample == sideFourWeight)
				bucketToIncrement = sideFour;
			else if(currentSample == sideFiveWeight)
				bucketToIncrement = sideFive;
			else if(currentSample == sideSixWeight)
				bucketToIncrement = sideSix;
			else
				System.out.println("FREQUENCY COUNT ERROR");
			//System.out.println(experimentID + " " + trialID + " " + currentSample);
			countSummary[experimentID][trialID][bucketToIncrement]++;
		}
	}//endfreqcount

	public static void countAllFrequencies(int[][][] frequencyCountSummary){

		for(int experimentID = 0; experimentID < experimentSize; experimentID++){
			if(experimentID == experimentOne)
				countAllFrequenciesHelper(experimentID, frequencyCountSummary, matlabSamples);
			else if(experimentID == experimentTwo)
				countAllFrequenciesHelper(experimentID, frequencyCountSummary, javaRandomSamples);
			else if(experimentID == experimentThree)
				countAllFrequenciesHelper(experimentID, frequencyCountSummary, loadedDiceSamples);
			else if(experimentID == experimentFour)
				countAllFrequenciesHelper(experimentID, frequencyCountSummary, brokenDiceSamples);
			else
				System.out.println("COUNT ALL FREQ ERROR");
		}

	}//endcountallfreq

	public static void countAllFrequenciesHelper(int experimentID, int[][][] freqCountSummary, int[][] sampleArray){
		for(int trialID = 0; trialID < trialSize; trialID++){
			//expID, samples array, countsummary
			frequencyCount(experimentID, trialID, sampleArray[trialID], freqCountSummary);
		}
	}//end countAllFrequenciesHelper

	//utility methods
	public static void printArray(int[] targetArray){
		for(int i = 0; i < targetArray.length; i++)
			System.out.println(targetArray[i]);
	}

	public static void printArray(int[][] targetArray){
		for(int i = 0; i < targetArray.length; i++){
			for(int j = 0; j < targetArray[i].length; j++){
				System.out.print(targetArray[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static void printArray(BigDecimal[] targetArray){
		for(int i = 0; i < targetArray.length; i++)
			System.out.println(targetArray[i]);
	}

	public static void printArray(BigDecimal[][] targetArray){
		for(int i = 0; i < targetArray.length; i++){
			for(int j = 0; j < targetArray[i].length; j++){
				System.out.print(targetArray[i][j] + " ");
			}//endinnerloop
			System.out.println();
		}

	}

	public static void printArray(int[][][] targetArray){
		for(int i = 0; i < targetArray.length; i++){
			for(int j = 0; j < targetArray[i].length; j++){
				for(int w = 0; w < targetArray[i][j].length; w++){
					System.out.print(targetArray[i][j][w] + " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}//end printarray
	
	public static void printArray(BigDecimal[][][] targetArray){
		for(int i = 0; i < targetArray.length; i++){
			for(int j = 0; j < targetArray[i].length; j++){
				for(int w = 0; w < targetArray[i][j].length; w++){
					System.out.print(targetArray[i][j][w] + " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}//end printarray	

	// main class
	public static void main(String[] args){
		//System.out.println("Welcome Porby");
		//playDice();
		/*for(int i = 0; i < 50; i++){
			System.out.println(getRandomInteger(minSides, maxSides));
		}
		*/
		testRandomness();
	}//endmainmethod
}//endclass



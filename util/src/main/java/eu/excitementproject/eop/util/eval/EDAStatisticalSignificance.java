package eu.excitementproject.eop.util.eval;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.JCas;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lap.PlatformCASProber;

/**
 * When there are two or more reasonable models for an EDA and one has to select one of them,
 * one would need to know if their performances are significantly different. 
 * 
 * This class allows to answer this question by calculating confidence intervals. Given a certain 
 * probability value (generally 90%, 95%), the class calculates a confidence intervals
 * providing a range of values that the EDA model error can assume with the specified confidence value.
 * 
 * More specifically, let errD be a model error computed on data set D. Then the
 * error confidence interval is defined as the probability p that the model error errD lies
 * between some lower bound lower bound lb and some upper bound ub: Pr(lb ≤ errD ≤ ub) = p.
 * 
 * For example suppose producing the 95% error confidence interval for two models you have to select one from. Then if the confidence
 * intervals do not overlap, the model performances are considered to be significantly different, and
 * as a result the best model should be used. In contrast, if the two intervals do overlap, the model
 * performances are considered not significantly different and other criteria can be taken into account
 * to select the model to be used (e.g. the less complex model).
 * 
 * To calculate the confidence interval the class uses the bootstrap method proposed by Efron (1979). It generates the bootstrap
 * samples from D and computes the model error on each sample. During this process the parameters of the EDA are the
 * same for all bootstrap samples. Basically they are the optimized parameters found during the development phase for that EDA and that
 * are in the EDA's configuration file.
 * After that the array containing the error values produced by testing the EDA on the
 * bootstrap samples is saved in ascending order. Testing here is obtained by the hold-out method:
 * each bootstrap sample is split into two parts: one that is used for training the EDA and one that
 * is used for testing.
 * Finally the algorithm uses the number of the bootstrap samples and probability value for extracting the upper and lower 
 * bounds. Basically, the larger the number of bootstrap samples, the more accurate the approximation
 * of the true confidence interval.
 * 
 * 
 * Pseudo code:
 * 
 * given data set D
 * probability <- 95 //95 of probability represents the 97.5 percentile and 2.5 percentile
 * number_of_samples <- 200
 * 
 * for i = 1 to number_of_samples do
 * 		B[i] <- sample D with replacement
 * 		err[i] <- compute model error using parameter set
 * end for
 * 
 * Sort err in ascending order
 * lb ← err[5] //2.5 * number_of_samples(i.e. 200) = 5
 * ub ← err[195] //97.5 * number_of_samples(i.e. 200) = 195
 * 
 * return (lb, ub)"Misha"
 * 
 * 
 * @author Roberto Zanoli
 * 
 */
public class EDAStatisticalSignificance {

	/*
	 * the logger
	 */
	static Logger logger = 
			Logger.getLogger(EDAStatisticalSignificance.class.getName());
	
	/* 
	 * the probabilities values to calculate the confidential interval
	 */
	private static int PROBABILITY_95 = 95;
	private static int PROBABILITY_90 = 90;
	/*
	 * the configuration file to be used with the selected EDA
	 */
	private CommonConfig config = null; 
	/*
	 * the directory where the training files (i.e. preprocessed cas files) are.
	 */
	private String trainDirSource = null;
	/*
	 * the directory where the bootstrap samples for training have to be stored; this is the same directory
	 * reported in the configuration file of the EDA. In fact the EDA will use this data set
	 * for training.
	 */
	private String trainDirTarget = null;
	/*
	 * the number of bootstrap samples"Misha"
	 */
	private int samples = 200;
	/*
	 * the EDA to be evaluated
	 */
	private EDABasic<?> eda = null;
	/*
	 * the temporary directory used for storing temporary files
	 */
	private String tmpDirectory = null;
	/*
	 * each bootstrap sample is split in two parts: one for training and the other one for testing.
	 * percentageOfExamplesInTest means the percentage of the examples in samples to be used for
	 * testing.
	 */
	private int percentageOfExamplesInTest = 0;
	/*
	 * the temporary directory of the test files
	 */
	private String tmpTestDir = null;
	
	
	/**
	 * Construct an object StatisticalSignificance
	 * 
	 * @param edaClassName the EDA class name
	 * @param config the configuration file to be used with the selected EDA
	 * @param trainDirSource the directory where the training files (i.e. cas files) are.
	 * @param trainDirTarget the directory where the sampled training files are put; it corresponds
	 * to the training directory where the EDA reads the files from (i.e. the directory reported
	 * in the configuration file of the EDA).
	 * @param samples the number of bootstrap samples
	 * 
	 */
	public EDAStatisticalSignificance(String edaClassName, CommonConfig config,
			String trainDirSource, String trainDirTarget, int samples ) {
		
		try {
			
			String className = edaClassName;
			this.eda = makeEDAObject(className);
			this.config = config;
			this.trainDirSource = trainDirSource;
			this.trainDirTarget = trainDirTarget;
			this.samples = samples;
			this.percentageOfExamplesInTest = 10;
			this.tmpDirectory = System.getProperty("java.io.tmpdir");
			this.tmpTestDir = tmpDirectory + "/" + "tmpTestDir";
			
		} catch (Exception e){
			
			logger.error(e.getMessage());
			
		}
		
	}
	
	
	/*
	 * In sampling with replacement the examples are selected from D without
	 * deleting them from D to create copies of D. 
	 * 
	 * @param fileList the list of examples in the data set
	 * 
	 * @result the bootstrap samples
	 */
	private String[] sampleWithReplacement(String[] fileList) {
		
		int listSize = fileList.length;
		
		String[] result = new String[listSize];
		
		for (int i = 0; i < listSize; i++) {
			
			int index = (int)(Math.random() * listSize-1);
			result[i] = fileList[index];
			
		}
		
		return result;
		
	}
	
	
	/*
	 * Split the given data set in train and test according to the percentage
	 * of examples (i.e. percentageOfExamplesInTest) to be used for training.
	 * Training files are saved in trainDirSource whereas the test files in tmpTestDir.
	 * 
	 * @param fileList the list of examples for the current bootstrap sample
	 * 
	 */
	private void splitTrainAndTest(String[] fileList) throws IOException {
		
		/*
		 *  creating the temporary directory for the training files
		 */
		createDir(trainDirTarget);
		/*
		 *  creating the temporary directory for the test files
		 */
		createDir(tmpTestDir);
		/*
		 *  sampling the data set
		 */
		String[] sampledList = sampleWithReplacement(fileList);
		/*
		 *  number of examples to be used for testing
		 */
		int numberOfTestExample = (int)percentageOfExamplesInTest * sampledList.length/100;
		
		for (int i = 0; i < sampledList.length; i++) {
			
			String fileName = trainDirSource + "/" + sampledList[i];
			/*
			 * loading the content of the file
			 */
			String content = loadFile(fileName);
			/*
			 * saving files
			 */
			if (i <  sampledList.length - numberOfTestExample) {
				// training file
				String[] contentList = new String[1];
				contentList[0] = content;
				save(new File(trainDirTarget + "/" + i + "_" +sampledList[i]), contentList);
			}
			else {
				// test file
				String[] contentList = new String[1];
				contentList[0] = content;
				save(new File(tmpTestDir + "/" + i + "_" + sampledList[i]), contentList);
			}
			
		}
		
	}
	
	
	/*
	 * Create new directories. If the directory does not exist it creates a new one. Otherwise
	 * it removes all the files in the directory.
	 * 
	 * @param dirName the directory to be created
	 * 
	 */
	private void createDir(String dirName) {
		
		try {
			
			File file = new File(dirName);
			
			if (file.exists()) {
				
				File[] files = file.listFiles();
				for(File f: files) {
					f.delete();
		        }
				
			}
			else {
				
				file.mkdir();
				
			}
			
		} catch (Exception e){
			
			logger.error(e.getMessage());
			
		}
		
	}
	
	
	/*
	 * Train the selected EDA
	 */
	private void trainEDA() {
		
		try {
			
			eda.startTraining(config);
			
		} catch(Exception e) {
			
			logger.error(e.getMessage());
			
		} finally {
    		
			eda.shutdown();
    		
    	}
		
	}
	
	
	/*
	 * Test the selected EDA and returns the annotated data set.
	 * 
	 * @result the annotated data set
	 */
	private String[] testEDA() {
		
		String[] list = null;
		
		try {
		
			/*
			 * EDA initialization
			 */
			eda.initialize(config);
			
			File file = new File(tmpTestDir);
			/*
			 * the list of the test files
			 */
			list = new String[file.listFiles().length];
			
			int i = 0;
			
			/*
			 * reading and annotating the test files
			 */
			for (File xmi : (file.listFiles())) {
				
				if (!xmi.getName().endsWith(".xmi")) {
					
					continue;
					
				}
				
				JCas cas = PlatformCASProber.probeXmi(xmi, null);
				TEDecision teDecision1 = eda.process(cas);
				// the annotated test set
				list[i] = getPairID(cas) + "\t" + getGoldLabel(cas) + "\t"  + teDecision1.getDecision().toString() + "\t" + teDecision1.getConfidence();
				i++;
				
			}
			
		} catch(Exception e) {
			
			logger.error(e.getMessage());
			
		} finally {
    		
			eda.shutdown();
    		
    	}
		
		return list;
		
	}
	
	
	/*
	 * Calculate the confidential interval.
	 * 
	 * @throws IOException
	 * @return the interval 
	 */
	public double[] computeModelError() throws IOException {
		
		/*
		 * The list of the errors produced by the EDA on the bootstrap data sets 
		 */
		ArrayList<Double> errorList = new ArrayList<Double>();
		
		/*
		 * The data set D
		 */
		File file = new File(trainDirSource);
		/*
		 * getting the list of the file of the data set D
		 */
		String[] fileList = file.list();
		
		/*
		 * creating samples bootstrap samples of the data set D where
		 * training and testing the EDA
		 */
		for (int i = 0; i < this.samples; i++) {
			
			logger.info("sample: " + i);
			
			/*
			 * sampling the data set D
			 */
			String[] sampledList = sampleWithReplacement(fileList);
			/*
			 * splitting the sampled data set in training and test
			 */
	        splitTrainAndTest(sampledList);
	        /*
	         * training the EDA on the created training data set
	         */
			trainEDA();
			 /*
	         * testing the EDA on the created test data set
	         */
			String[] list = testEDA();
			/*
			 * saving the annotated data set
			 */
			File annotatedFileName = new File(this.tmpDirectory + "/" + "results.xml");
			annotatedFileName.deleteOnExit();
			save(annotatedFileName, list);
			/*
			 * evaluating the results
			 */
			String evaluationFileName = this.tmpDirectory + "/" + "evaluation.txt";
			EDAScorer.score(annotatedFileName, evaluationFileName);
			/*
			 * getting the accuracy
			 */
			double accuracy = getAccuracy(evaluationFileName, "Accuracy");
			/*
			 * getting the error
			 */
			double error = 1 - accuracy;
			/*
			 * adding the calculated error in the list of the calculated errors
			 */
			errorList.add(error);
			
		}
		
		/*
		 * sorting the list of the calculated errors in ascendenting order
		 */
		Object[] errorArray = errorList.toArray();
		sortArray(errorArray);
		/*
		 * getting the confidential interval
		 */
		double[] interval = getConfidentialInterval(errorArray, samples, PROBABILITY_95);
		
		return interval;
			
	}
	
	
	/*
	 * Getting the accuracy. It reads the accuracy from the file containing the results.
	 * 
	 * @param fileName the file with the results
	 * @param measure the accuracy measure
	 * 
	 * @result the accuracy value
	 */
	private double getAccuracy(String fileName, String measure) {
		
		double result = 0;
		
		try {
		
			String resultFile = loadFile(fileName);
			String[] splitText = resultFile.split("\n");
			
			for (int i = 0; i < splitText.length; i++) {
				
				String line = splitText[i];
				if (line.indexOf(measure) != -1) {
					
					result = Double.parseDouble(line.replaceAll("<" + measure + ">", "").replaceAll("</" + measure + ">", ""));
					break;
					
				}
				
			}
			
		} catch(Exception e) {
			
			logger.error(e.getMessage());
			
		}
		
		return result;
		
	}
	
	
	/*
	 * Given the calculated list of errors it returns the confidential interval
	 * 
	 * @param errorList the list of the calculated errors
	 * @param samples the number of samples
	 * @param probability the probability to calculate the confidential interval
	 * 
	 * @results the confidential interval
	 * 
	 */
	private double[] getConfidentialInterval(Object[] errorList, int samples, int probability) {
		
		/*
		 * the confidential interval
		 * result[0] contains the lower bound
		 * result[1] contains the upper bound
		 */
		double[] result = new double[2];
		
		int lower = 0;
		int upper = 0;
		
		if (probability == PROBABILITY_95) {
			
			/*
			 * 95 of probability represents the 97.5 percentile and 2.5 percentile
			 */
			lower = (int)(samples * 2.5 / 100);
			upper = (int)(samples * 97.5 / 100);
			
		}
		else if (probability == PROBABILITY_90) {
			
			/*
			 * 90 of probability represents the 95.0 percentile and 5.0 percentile
			 */
			lower = (int)(samples * 5.0 / 100);
			upper = (int)(samples * 95.0 / 100);
			
		}
		
		result[0] = (double)errorList[lower];
		result[1] = (double)errorList[upper];
		
		return result;
		
	}
	
	
	/*
	 * Sort an array in ascendenting order
	 */
	private void sortArray(Object[] array) {
		
	    Arrays.sort(array);
		
	}
	
	
	/*
	 * Create and EDA object from an EDA class name
	 * 
	 * @param edaClassName the EDA class name
	 * 
	 * @result the created object
	 */
	private EDABasic<?> makeEDAObject(String edaClassName) throws EDAException{
		
		EDABasic<?> eda = null;
		
		try {
		
			Class<?> edaClass = Class.forName(edaClassName);
			Constructor<?> edaClassConstructor = edaClass.getConstructor();
			eda = (EDABasic<?>) edaClassConstructor.newInstance();
			
		} catch (Exception e){
			
			logger.error(e.getMessage());
			
		}

		return eda;
	}
	
	
	/**
	 * Save a file with its content
	 * 
	 * @param fileName the file name
	 * @param content the content of the file
	 * 
	 */
    private void save(File fileName, String[] content) throws IOException {
    	
    	BufferedWriter writer = null;
    	
    	try {
    		
	    	writer = new BufferedWriter(new OutputStreamWriter(
	                  new FileOutputStream(fileName), "UTF-8"));
	    	
	    	PrintWriter printout = new PrintWriter(writer, false);
    	
	    	for (int i = 0; i < content.length; i++) {
	    		
	    		printout.println(content[i]);
	    		
	    	}
	    	
	    	printout.close();
	    	
    	} catch (Exception e) {
    		
    		logger.error(e.getMessage());
    		 
    	} finally {
    		
    		if (writer != null)
    			writer.close();
    		
    	}

    }
    
    
    /**
	 * Load file
	 * 
	 * @param fileName the file name
	 * 
	 * @results the content of the loaded file
	 */
    private String loadFile(String fileName) throws IOException {
		
    	String result = null;
    	
		BufferedReader reader = null;
		
		try {
			
			reader = new BufferedReader(
	                   new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			
			StringBuffer buffer = new StringBuffer();
			String line = reader.readLine();
			
			while (line != null) {
				
				buffer.append(line);
				line = reader.readLine();
				if (line != null)
					buffer.append("\n");
				
			}
			
			result = buffer.toString();
			
		} catch (Exception e) {
			
			logger.error(e.getMessage());
			
		} finally { 
			if (reader != null)
				reader.close();
		}
		
		return result;
		
	}
    
    
    /**
	 * @param aCas the <code>JCas</code> object
	 * 
	 * @return return the pairID of the T-H pair
	 */
	private String getPairID(JCas aCas) {
		
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository().getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		
		return p.getPairID();
		
	}
	
	
	/**
	 * @param aCas the <code>JCas</code> object
	 * 
	 * @return if the T-H pair contains the gold answer, return it; otherwise return null
	 */
	private String getGoldLabel(JCas aCas) {
		
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository()
				.getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		
		if (null == p.getGoldAnswer() || p.getGoldAnswer().equals("")
				|| p.getGoldAnswer().equals("ABSTAIN")) {
			return null;
		} else {
			return p.getGoldAnswer();
		}
		
	}
	
	
	/* 
	 * main method for testing the class
	 * 
	 * usage: EDAStatisticSignificance edaClassName edaConfigFileName trainDirSource trainDirTarget samples
	 * 
	 * e.g. 
	 * 
	 * To test Edit Distance
	 * EDAStatisticSignificance eu.excitementproject.eop.core.EditDistanceEDA ./src/main/resources/configuration-file/EditDistanceEDA_IT.xml /tmp/IT/dev/ /tmp/IT/dev2/ 200 
	 * 
	 * To test MaxEntClassification
	 * EDAStatisticSignificance eu.excitementproject.eop.core.MaxEntClassificationEDA ./src/main/resources/configuration-file/MaxEntClassificationEDA_Base_EN.xml /tmp/IT/dev/ /tmp/IT/dev2/ 200
	 * 
	 * @param args array of command-line arguments passed to this method 
	 */ 
    public static void main(String[] args) {
    	
    	try {
    	
    		BasicConfigurator.configure();
    		
    		// the class name of the EDA to be tested, e.g. eu.excitementproject.eop.core.EditDistanceEDA
    		String edaClassName = args[0];
    		// the configuration file of the selected EDA, e.g. ./src/main/resources/configuration-file/EditDistanceEDA_IT.xml
    		String edaConfigFileName = args[1];
    		/*
    		 * the directory where the training files (i.e. preprocessed cas files) are.
    		 */
    		String trainDirSource = args[2];
    		/*
    		 * the directory where the bootstrap samples for training have to be stored; this is the same directory
    		 * reported in the configuration file of the EDA. In fact the EDA will use this data set
    		 * for training.
    		 */
    		String trainDirTarget = args[3];
    		/*
    		 * the number of bootstrap samples
    		 */
    		int samples = Integer.parseInt(args[4]);
    		
	    	File configFile = new File(edaConfigFileName);
	    	
			CommonConfig config = new ImplCommonConfig(configFile);
			
	    	EDAStatisticalSignificance statisticalSignificance = 
	    			new EDAStatisticalSignificance(edaClassName, config, trainDirSource, trainDirTarget, samples);
	    	
	    	double[] interval = statisticalSignificance.computeModelError();
	    	
	    	logger.info("[" + interval[0] + "," + interval[1] + "]");
	    	
    	} catch (Exception e) {
    		
    		logger.error(e.getMessage());
    		
    	}
    	
    }
	
}

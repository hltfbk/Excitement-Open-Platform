package eu.excitementproject.eop.common.utilities.datasets.rtesum.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswersFileReader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultAnswersFileReader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.common.utilities.stats.McnemarTest;


/**
 * Runs the {@link McnemarTest} for statistical significance on two given RTE6/7 answer files, and a given gold standard file. 
 * @author Amnon Lotan
 *
 * @since 2 Jan 2012
 */
public class Rte6McNemarTester {
	
	private final String answerFile2;
	private final String answerFile1;
	private final String goldStandardFile;
	private double chiSquare = 0;
	private double confidence = 0;
	private MisclassifiedExamplesCounter misclassifiedExamples;

	/**
	 * Ctor
	 * @param rte6AnswerFile1	the first answer file to compare 
	 * @param rte6AnswerFile2	the second answer file to compare
	 * @param goldStandardFile	the gold standard answers file
	 */
	public Rte6McNemarTester(String rte6AnswerFile1,
			String rte6AnswerFile2, String goldStandardFile) {
		super();
		this.answerFile2 = rte6AnswerFile2;
		this.answerFile1 = rte6AnswerFile1;
		this.goldStandardFile = goldStandardFile;
	}
	
	public void compute() throws Rte6mainIOException
	{
		AnswersFileReader answerReader1 = new DefaultAnswersFileReader();
		answerReader1.setXml(answerFile1);
		answerReader1.read();
		Map<String,Map<String,Set<SentenceIdentifier>>> answers1 = answerReader1.getAnswers();

		AnswersFileReader answerReader2 = new DefaultAnswersFileReader();
		answerReader2.setXml(answerFile2);
		answerReader2.read();
		Map<String,Map<String,Set<SentenceIdentifier>>> answers2 = answerReader2.getAnswers();

		AnswersFileReader answerReaderGs = new DefaultAnswersFileReader();
		answerReaderGs.setXml(goldStandardFile);
		answerReaderGs.read();
		Map<String,Map<String,Set<SentenceIdentifier>>> gsAnswers = answerReaderGs.getAnswers();

		misclassifiedExamples = countDifferenceOfAnswers(gsAnswers, answers1, answers2);
		//		System.out.println("McNemar Test: n10=="+differencesBetweenAnswers.getN10() + "\t n01=="+differencesBetweenAnswers.getN01());
		chiSquare = McnemarTest.chi_sqaure(misclassifiedExamples.getN01(), misclassifiedExamples.getN10());
		confidence = McnemarTest.confidenceLevel(chiSquare);
	}
	
	/**
	 * @return the chiSquare
	 */
	public double getChiSquare() {
		return chiSquare;
	}
	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return confidence;
	}
	/**
	 * @return the N01 number of examples misclassified by answers1 but not by answers2
	 */
	public double getN01() {
		return misclassifiedExamples.getN01();
	}
	/**
	 * @return the N10, number of examples misclassified by answers2 but not by answers1
	 */
	public double getN10() {
		return misclassifiedExamples.getN10();
	}
	
	/**
	 * Count n10 number of examples misclassified by answers2 but not by answers1, and n01 number of examples misclassified by answers1 but not by answers2. 
	 * For hypothesis, and its set of answers, n10 is the  number of true true-positives in {answers1 - answers2}, plus the number of flase negatives in 
	 * {answers2 - answers1}. n01 is computed symmetrically. 
	 * 
	 * @param gsAnswers
	 * @param answers1
	 * @param answers2
	 * @return
	 */
	private MisclassifiedExamplesCounter countDifferenceOfAnswers(
			Map<String, Map<String, Set<SentenceIdentifier>>> gsAnswers, 
			Map<String, Map<String, Set<SentenceIdentifier>>> answers1,
			Map<String, Map<String, Set<SentenceIdentifier>>> answers2) {
		
		int misclassifiedOnlyBy2 = 0;
		int misclassifiedOnlyBy1 = 0;
		
		// iterate over all topics 
		for (String topic : gsAnswers.keySet())
		{
			Map<String, Set<SentenceIdentifier>> topicGsAnswers = gsAnswers.get(topic);
			Map<String, Set<SentenceIdentifier>> topicAnswers1 = answers1.get(topic);
			Map<String, Set<SentenceIdentifier>> topicAnswers2 = answers2.get(topic);
			
			// iterate over all Hs
			for (String hypoKey : topicGsAnswers.keySet())
			{
				Set<SentenceIdentifier> hypoGsAnswers = topicGsAnswers.get(hypoKey);
				Set<SentenceIdentifier> hypoAnswers1 = topicAnswers1.get(hypoKey);
				Set<SentenceIdentifier> hypoAnswers2 = topicAnswers2.get(hypoKey);
				
				Collection<SentenceIdentifier> hypoAnswers1minus2 = Utils.minus(hypoAnswers1, hypoAnswers2, new HashSet<SentenceIdentifier>());
				Collection<SentenceIdentifier> hypoAnswers2minus1 = Utils.minus(hypoAnswers2, hypoAnswers1, new HashSet<SentenceIdentifier>());

				for (SentenceIdentifier candidate : hypoAnswers1minus2)
					if (hypoGsAnswers.contains(candidate))
						// TP for 1, FN for 2
						misclassifiedOnlyBy2++;
					else
						// FP for 1, TN for 2
						misclassifiedOnlyBy1++;
						
				for (SentenceIdentifier candidate : hypoAnswers2minus1)
					if (hypoGsAnswers.contains(candidate))
						// FN for 1, TP for 2
						misclassifiedOnlyBy1++;
					else
						// TN for 1, FP for 2
						misclassifiedOnlyBy2++;
			}
		}

		MisclassifiedExamplesCounter misclassifiedPairs = new MisclassifiedExamplesCounter(misclassifiedOnlyBy2, misclassifiedOnlyBy1);
		return misclassifiedPairs;
	}

	private class MisclassifiedExamplesCounter {
		private final int n10;
		private final int n01;
		/**
		 * Ctor
		 * @param n10 number of examples misclassified by answers2 but not by answers1
		 * @param n01 number of examples misclassified by answers1 but not by answers2
		 */
		public MisclassifiedExamplesCounter(int n10, int n01) {
			super();
			this.n10 = n10;
			this.n01 = n01;
		}
		/**
		 * number of examples misclassified by answers1 but not by answers2
		 * @return the n01
		 */
		public int getN01() {
			return n01;
		}
		/**
		 * number of examples misclassified by answers2 but not by answers1
		 * @return the n10
		 */
		public int getN10() {
			return n10;
		}
	}
}
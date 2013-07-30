package eu.excitementproject.eop.biutee.small_unit_tests;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.linearimplementations.LogisticRegressionClassifier;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;



/**
 * Gets two files with samples in SvmLight format (see http://svmlight.joachims.org/)
 * @author Asher Stern
 * @since Mar 8, 2011
 *
 */
public class DemoLogisticRegression
{
	
	public static final double LEARNING_RATE = 0.005;
	
	public static void main(String[] args)
	{
		try
		{
			DemoLogisticRegression app = new DemoLogisticRegression(args[0],args[1]);
			app.run();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
		
	}
	
	public DemoLogisticRegression(String trainFileName, String testFileName)
	{
		this.trainFileName = trainFileName;
		this.testFileName = testFileName;
	}


	public void run() throws IOException, TeEngineMlException, ClassifierException
	{
		LogisticRegressionClassifier classifier = new LogisticRegressionClassifier(LEARNING_RATE,0.0);
		
		System.out.println("reading...");
		Vector<LabeledSample> train = readFile(trainFileName);
		Vector<LabeledSample> test = readFile(testFileName);
		
		System.out.println("training...");
		classifier.train(train);
		
		System.out.println("Making normalization...");
		classifier.setToZeroNegativeParametersBut(new LinkedHashSet<Integer>());
		//RTEPairsTrainerUtils.normalizeClassifierForSearch(classifier);
		
		System.out.println("testing...");
		System.out.println(String.format("Accuracy = %-3.6f", ClassifierUtils.accuracyOf(classifier, test)));
	}
	
	public Vector<LabeledSample> readFile(String filename) throws IOException, TeEngineMlException
	{
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		try
		{
			List<String> lines = new LinkedList<String>();
			String line = null;
			line = reader.readLine();
			while(line!=null)
			{
				if (line.length()>0)
				{
					lines.add(line);
				}

				line = reader.readLine();
			}
			
			Vector<LabeledSample> samples = ClassifierUtils.fromSvmLightFormatToLabeledSamples(lines);
			return samples;
		}
		finally
		{
			reader.close();
		}
	}
	
	private String trainFileName;
	private String testFileName;
}

package ac.biu.nlp.nlp.engineml.small_unit_tests;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierUtils;
import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.classifiers.linearimplementations.LogisticRegressionClassifier;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;


/**
 * Gets two files with samples in SvmLight format (see http://svmlight.joachims.org/)
 * @author Asher Stern
 * @since Mar 8, 2011
 *
 */
@SuppressWarnings("unused")
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
		classifier.setToZeroNegativeParametersBut(new HashSet<Integer>());
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

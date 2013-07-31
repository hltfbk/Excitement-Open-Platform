package eu.excitementproject.eop.biutee.small_unit_tests;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.linearimplementations.LogisticRegressionClassifier;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.StringUtil;


/**
 * Tests the {@link LogisticRegressionClassifier}.
 * 
 * A line in the sample file looks like: "0,1.0,1.0,1.0". The first number is label, and
 * should be either 0 or 1.
 * @author Asher Stern
 * @since Jan 6, 2011
 *
 */
public class DemoLogisticRegressionClassifier
{
	public static Vector<LabeledSample> readDataFile(File file) throws IOException
	{
		Vector<LabeledSample> ret = new Vector<LabeledSample>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try
		{
			String line = reader.readLine();
			while (line !=null)
			{
				String[] sa = line.split(",");
				int labelInt = Integer.parseInt(sa[0]);
				boolean label;
				if (labelInt==0)
					label = false;
				else if (labelInt==1)
					label = true;
				else throw new RuntimeException("bad label");
				
				Map<Integer, Double> features = new LinkedHashMap<Integer, Double>();
				int index=1;
				for (int saIndex=1;saIndex<sa.length;++saIndex)
				{
					double val = Double.parseDouble(sa[saIndex]);
					features.put(index, val);
					++index;
				}
				
				ret.add(new LabeledSample(features, label));
				
				line = reader.readLine();
			}
		}
		finally
		{
			if (reader!=null)
				reader.close();
		}
		
		return ret;
	}
	
	public static void printSamples(Vector<LabeledSample> samples)
	{
		for (LabeledSample sample : samples)
		{
			System.out.print(sample.getLabel()+" ");
			for (Integer index : sample.getFeatures().keySet())
			{
				System.out.print(index.toString()+":"+String.format("%3.3f", sample.getFeatures().get(index))+", ");
			}
			System.out.println();
		}
	}

	public static void f(String[] args) throws IOException, ClassifierException
	{
		
		Vector<LabeledSample> samples = readDataFile(new File(args[0]));
		printSamples(samples);
		
		LogisticRegressionClassifier classifier = new LogisticRegressionClassifier(LogisticRegressionClassifier.DEFAULT_LEARNING_RATE,1.0);
		classifier.train(samples);
		
		System.out.println("Trained with "+classifier.getNumberOfIterations()+" iterations.");
		System.out.println(classifier.getFeatures());
		

		int all = 0;
		int good = 0;
		for (LabeledSample sample : samples)
		{
			double classification = classifier.classify(sample.getFeatures());
			if (
				( (classification<0.5) && (sample.getLabel()==false) )
				||
				( (classification>=0.5) && (sample.getLabel()==true) )
				)
				good++;
			
			all++;
		}
		System.out.println("correct: "+good+". All="+all);
		System.out.println("accuracy: "+String.format("%4.4f", ((double) good) / ((double) all) ));
		
		
		System.out.println(StringUtil.generateStringOfCharacter('-', 50));
		System.out.println(classifier.descriptionOfTraining());
		
		System.out.println(StringUtil.generateStringOfCharacter('-', 50));
		Set<Integer> dontChange = new LinkedHashSet<Integer>();
		dontChange.add(2);
		classifier.setToZeroNegativeParametersBut(dontChange);
		System.out.println(classifier.descriptionOfTraining());

		
		
		
		
		
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			f(args);
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}

	}

}

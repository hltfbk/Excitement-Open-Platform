package eu.excitementproject.eop.biutee.small_unit_tests.old_small_tests;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;

public class DemoSigmoidForClassifier
{
	public static void f(String[] args)
	{
		double exp = 0.5;
		System.out.println(ClassifierUtils.relief(0.9, exp));
		System.out.println(ClassifierUtils.relief(0.1, exp));
		System.out.println(ClassifierUtils.relief(0.0, exp));
		System.out.println(ClassifierUtils.relief(1.0, exp));
		System.out.println(ClassifierUtils.relief(0.5, exp));
		System.out.println(ClassifierUtils.relief(0.99, exp));
		System.out.println(ClassifierUtils.relief(0.01, exp));
		System.out.println("------------------------");
		System.out.println(ClassifierUtils.relief(0.6, exp));
		System.out.println(ClassifierUtils.relief(0.4, exp));
		System.out.println(ClassifierUtils.relief(0.51, exp));
		System.out.println(ClassifierUtils.relief(0.49, exp));
		
		System.out.println("------------------------");
		System.out.println(ClassifierUtils.sigmoid(-1000.0));
		
		System.out.println("------------------------");
		System.out.println(String.format("%-6.6f", 11.05));
		
		
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

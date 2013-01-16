package eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression;


/**
 * An encapsulation of recall, precision and F1 (just the values), as well
 * as true-positive/false-positive/true-negative/false-negative values. 
 * 
 * @author Asher Stern
 * @since Mar 20, 2012
 *
 */
public final class RecallPrecisionF1
{
	public RecallPrecisionF1(double recall, double precision, double f1,
			int truepositive, int falsepositive, int truenegative,
			int falsenegative)
	{
		super();
		this.recall = recall;
		this.precision = precision;
		this.f1 = f1;
		this.truepositive = truepositive;
		this.falsepositive = falsepositive;
		this.truenegative = truenegative;
		this.falsenegative = falsenegative;
	}
	public double getRecall()
	{
		return recall;
	}
	public double getPrecision()
	{
		return precision;
	}
	public double getF1()
	{
		return f1;
	}
	
	
	
	
	
	
	
	







	public int getTruepositive()
	{
		return truepositive;
	}
	public int getFalsepositive()
	{
		return falsepositive;
	}
	public int getTruenegative()
	{
		return truenegative;
	}
	public int getFalsenegative()
	{
		return falsenegative;
	}
	
	
	@Override
	public String toString()
	{
		return String.format("Recall: %-4.4f, Precision: %-4.4f, F1: %-4.4f", recall,precision,f1);
		
	}
	
	public String getAllValues()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(toString());
		sb.append(String.format("\ntruepositive = %d, falsepositive = %d, truenegative = %d, falsenegative = %d", truepositive,falsepositive,truenegative,falsenegative));
		return sb.toString();
	}





	private final double recall;
	private final double precision;
	private final double f1;
	
	private final int truepositive;
	private final int falsepositive;
	private final int truenegative;
	private final int falsenegative;

}

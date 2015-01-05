package eu.excitementproject.eop.alignmentedas.subs;

//import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;

public class ValueTest {

	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		
		
		FeatureValue v1 = new FeatureValue(MyNominalValues.NOMINAL1);
		
		try 
		{
			Enum<?> e = v1.getNominalValue();
			// we can access the enum value it self.. 
			System.out.println(e.toString()); 
			
			// and also all the other values permitted in that enum. 
			// this is essential for training data (of nominal values) 
			Enum<?>[] elist = e.getClass().getEnumConstants(); 
			for (Enum<?> x : elist)
			{
				System.out.println(x); 
			}
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage()); 
		}
	}

	public enum MyNominalValues {
		NOMINAL1,
		NOMINAL2,
		NOMINAL3			
	}

}

package eu.excitementproject.eop.alignmentedas;

import java.util.HashMap;
import java.util.Map;

import eu.excitementproject.eop.common.DecisionLabel;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	DecisionLabel l1 = DecisionLabel.Entailment;
    	DecisionLabel l2 = DecisionLabel.Entailment; 
    	DecisionLabel l3 = DecisionLabel.NonEntailment; 
    	
    	Map<DecisionLabel, Integer> map = new HashMap<DecisionLabel, Integer>(); 
    	map.put(l1, 1); 
    	map.put(l2, 1); 
    	map.put(l3, 1); 
    	
    	for (DecisionLabel l : map.keySet())
    	{
    		System.out.println(l.toString()); 
    	}
    	
    	System.out.println( "Hello World!" );
    }
}

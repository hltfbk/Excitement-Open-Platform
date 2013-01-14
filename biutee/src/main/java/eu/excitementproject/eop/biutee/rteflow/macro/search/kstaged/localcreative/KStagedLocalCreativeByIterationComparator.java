package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged.localcreative;
import eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged.ByIterationComparator;

/**
 * 
 * @author Asher Stern
 * @since Oct 31, 2011
 *
 */
public class KStagedLocalCreativeByIterationComparator implements ByIterationComparator<KStagedLocalCreativeElement>
{
	public int compare(KStagedLocalCreativeElement o1,
			KStagedLocalCreativeElement o2)
	{
		Double e1 = eval(o1);
		Double e2 = eval(o2);
		if (e1==e2)return 0;
		else if (e1==null)return 1;
		else if (e2==null)return -1;
		else return Double.compare(e1,e2);
	}

	public void setIteration(int iteration)
	{
		// Do nothing
	}
	
	private Double eval(KStagedLocalCreativeElement element)
	{
		if (null==element.getBase())
			return new Double(0.0);
		else
		{
			double deltaCost = element.getCost()-element.getBase().getCost();
			double deltaGap = element.getBase().getGap() - element.getGap();
			
			if (deltaGap<=0)
				return null; // infinity
			else
			{
				if (deltaCost<0)throw new RuntimeException("BUG");
				return deltaCost/deltaGap;
			}
		}
		
		
	}

}

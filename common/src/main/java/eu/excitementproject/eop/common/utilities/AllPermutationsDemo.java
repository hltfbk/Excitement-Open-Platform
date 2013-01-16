package eu.excitementproject.eop.common.utilities;


/**
 * Demo for {@link AllPermutations} class.
 * @author Asher Stern
 *
 */
public class AllPermutationsDemo
{
	public static void main(String[] args)
	{
		try
		{
			AllPermutations p = new AllPermutations(4,10);

			do
			{
				int[] result = p.getResult();
				for (int index=0;index<result.length;index++)
				{
					System.out.print(result[index]+", ");
				}
				System.out.println();
				
			}while (p.next()==true);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

package eu.excitementproject.eop.common.datastructures;

public class TableDemo
{
	
	public void f()
	{
		Table<Integer, String> table = new MapsBasedTable<Integer, String>();
		table.put(1, 1, "hello world");
		table.put(1, 2, "hello computer");
		table.put(2, 1, "bye bye");
		table.put(2, 2, "nice to see you.");
		
		for (Integer col : table.allCols())
		{
			for (Integer row : table.rowsOfCol(col))
			{
				System.out.println("in: "+row+" / "+col+": "+table.get(row, col));
			}
		}
		table.put(3, 2, "non symetric.");
		System.out.println("-------------");
		for (Integer col : table.allCols())
		{
			for (Integer row : table.rowsOfCol(col))
			{
				System.out.println("in: "+row+" / "+col+": "+table.get(row, col));
			}
		}

	}
	
	public static void main(String[] args)
	{
		try
		{
			new TableDemo().f();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

}

package eu.excitementproject.eop.transformations.datastructures;
import java.io.PrintWriter;

import eu.excitementproject.eop.common.datastructures.Table;


/**
 * 
 * Writes a given table to a csv (Comma Separated Values) file. 
 * 
 * @author Asher Stern
 * @since Jun 19, 2011
 *
 * @param <K> type of keys.
 * @param <V> type of values.
 */
public class TablePrinter<K,V>
{
	/**
	 * A constructor with the file into which that table will be printed
	 * and the table.
	 * @param writer where the table will be printed to.
	 * @param table the table.
	 */
	public TablePrinter(PrintWriter writer, Table<K, V> table)
	{
		super();
		this.writer = writer;
		this.table = table;
	}
	
	/**
	 * Writes the table into the writer, in a CSV style.
	 */
	public void print()
	{
		for (K col : table.allCols())
		{
			writer.print(",");
			writer.print(col.toString());
		}
		writer.println();
		
		for (K row : table.allRows())
		{
			writer.print(row.toString());
			for (K col : table.colsOfRow(row))
			{
				writer.print(",");
				V v = table.get(row,col);
				writer.print(v.toString());
			}
			writer.println();
		}
	}
	
	private PrintWriter writer;
	private Table<K, V> table;
}

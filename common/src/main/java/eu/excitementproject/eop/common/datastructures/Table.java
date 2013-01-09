package eu.excitementproject.eop.common.datastructures;

import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;



/**
 * Represents 2D table.
 * 
 * @author Asher Stern
 * @since Aug 12, 2010
 *
 * @param <K>
 * @param <V>
 */
public interface Table<K,V> extends Serializable
{
	/**
	 * Puts the given value into the specified cell.
	 * @param rowKey
	 * @param colKey
	 * @param value
	 */
	public void put (K rowKey, K colKey, V value);
	
	/**
	 * Gets the value at the specified cell.
	 * Return null if no value has been assigned to that cell.
	 * 
	 * @param rowKey
	 * @param colKey
	 * @return
	 */
	public V get(K rowKey, K colKey);
	
	/**
	 * Returns all rows in the table - i.e. any row such that there exist a cell in the
	 * table that its row index is the that row.
	 * 
	 * @return
	 */
	public ImmutableSet<K> allRows();
	
	/**
	 * Returns all columns in the table - i.e. any column such that there exist a cell in the
	 * table that its column index is the that column.
	 * 
	 * @return
	 */
	public ImmutableSet<K> allCols();
	
	/**
	 * Returns all columns in the table such that there exist a cell that its row index
	 * is the given row, and its column index is that column. 
	 * @param rowKey
	 * @return
	 */
	public ImmutableSet<K> colsOfRow(K rowKey);

	/**
	 * Returns all rows in the table such that there exist a cell that its column index
	 * is the given column, and its row index is that row. 
	 * 
	 * @param colKey
	 * @return
	 */
	public ImmutableSet<K> rowsOfCol(K colKey);
}

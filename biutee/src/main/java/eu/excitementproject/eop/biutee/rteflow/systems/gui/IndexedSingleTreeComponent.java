package eu.excitementproject.eop.biutee.rteflow.systems.gui;


/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and
 * should be improved. Need to re-design, make it more modular,
 * adding documentation and improve code.
 * 
 * @author Asher Stern
 * @since May 25, 2011
 *
 */
public class IndexedSingleTreeComponent
{
	public IndexedSingleTreeComponent(int index, SingleTreeComponent component)
	{
		super();
		this.index = index;
		this.component = component;
	}
	
	public int getIndex()
	{
		return index;
	}
	public SingleTreeComponent getComponent()
	{
		return component;
	}



	private final int index;
	private final SingleTreeComponent component;
}

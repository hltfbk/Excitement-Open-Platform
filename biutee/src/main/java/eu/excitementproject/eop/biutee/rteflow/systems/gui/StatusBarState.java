package eu.excitementproject.eop.biutee.rteflow.systems.gui;


/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and
 * should be improved. Need to re-design, make it more modular,
 * adding documentation and improve code.
 * 
 * @author Asher Stern
 * @since Nov 22, 2011
 *
 */
public enum StatusBarState
{
	PAIRS_MODE("pairs dataset mode"),
	SUM_MODE("summarization based dataset mode"),
	OLD_BEAM("old beam search");
	
	private StatusBarState(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	private String description;
}

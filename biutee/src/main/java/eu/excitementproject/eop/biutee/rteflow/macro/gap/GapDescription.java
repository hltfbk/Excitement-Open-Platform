package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.io.Serializable;

/**
 * 
 * @author Asher Stern
 * @since Aug 18, 2013
 *
 */
public class GapDescription implements Serializable
{
	private static final long serialVersionUID = -8684455904716609300L;

	public GapDescription(String description)
	{
		super();
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	protected final String description;
}

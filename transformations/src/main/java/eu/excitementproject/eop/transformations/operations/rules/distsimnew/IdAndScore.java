package eu.excitementproject.eop.transformations.operations.rules.distsimnew;
import java.io.Serializable;

/**
 * Combines and ID, represented as <code>int</code>,
 * and a score, represented by <code>double</code>.
 * <BR>
 * Used by {@link DirtDBRuleBase}.
 * 
 * @author Asher Stern
 * @since Dec 6, 2011
 *
 */
public final class IdAndScore implements Serializable
{
	private static final long serialVersionUID = 5465658666198568335L;
	
	public IdAndScore(int id, double score)
	{
		super();
		this.id = id;
		this.score = score;
	}
	public int getId()
	{
		return id;
	}
	public double getScore()
	{
		return score;
	}

	private final int id;
	private final double score;
}

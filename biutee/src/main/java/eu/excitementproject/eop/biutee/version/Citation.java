package eu.excitementproject.eop.biutee.version;

/**
 * Contains string that describe what
 * must be cited by users of the system.
 * 
 * @author Asher Stern
 * @since Aug 15, 2011
 *
 */
public class Citation
{
	public static final String INSTRUCTION =
			"We kindly ask that every research work that uses BIUTEE, or any of " +
			"its components, will include the following citation:\n";
//		"A paper that describes either theoretical or empirical results, " +
//		"based completely or partially on the BIUTEE system or some of its components, " +
//		"either on its theoretical ideas, its code or its binary-implementation, " +
//		"must contain a citation of the following paper:\n";
	
	public static final String CITATION =
		"Asher Stern and Ido Dagan: " +
		"\"A Confidence Model for Syntactically-Motivated Entailment Proofs.\" " +
		"in Proceedings of RANLP 2011";
	
	
	public static String citationInsturction()
	{
		return INSTRUCTION+CITATION;
	}
}

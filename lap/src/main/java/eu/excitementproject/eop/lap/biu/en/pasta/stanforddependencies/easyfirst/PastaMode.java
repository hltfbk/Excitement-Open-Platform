package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst;

/**
 * 
 * @author Asher Stern
 * @since Aug 6, 2013
 *
 */
public enum PastaMode
{
	LEGACY,
	BASIC,
	EXPANDED;
	
	public boolean isEqualOrGreater(PastaMode other)
	{
		return (ordinal()>=other.ordinal());
	}
}

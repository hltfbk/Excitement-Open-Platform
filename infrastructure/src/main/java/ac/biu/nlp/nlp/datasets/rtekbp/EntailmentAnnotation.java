package ac.biu.nlp.nlp.datasets.rtekbp;

/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public enum EntailmentAnnotation
{
	YES,
	NO;
	
	public boolean getBooleanAnnotation()
	{
		boolean ret = false;
		if (this.equals(EntailmentAnnotation.YES))
			ret = true;
		return ret;
	}

}

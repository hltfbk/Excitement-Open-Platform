package eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ExtendedPairData;
import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;


/**
 * A T-H pair instance, which is mainly an {@link ExtendedPairData}.
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 */
public class THPairInstance extends Instance
{
	private static final long serialVersionUID = 6692406254486067536L;
	
	public THPairInstance(ExtendedPairData pairData)
	{
		super();
		this.pairData = pairData;
		hypothesisInformation = new HypothesisInformation(pairData.getPair().getHypothesis(), pairData.getHypothesisTree());
	}

	@Override
	public HypothesisInformation getHypothesisInformation() throws BiuteeException
	{
		return this.hypothesisInformation;
	}

	@Override
	public String toString()
	{
		Integer id = pairData.getPair().getId();
		if (id!=null)
		{
			return "T-H pair #"+String.valueOf(id);
		}
		else
		{
			return "T-H pair. Unknown Id";
		}
	}

	@Override
	public Boolean getBinaryLabel() throws BiuteeException
	{
		return pairData.getPair().getBooleanClassificationType();
	}
	
	

	public ExtendedPairData getPairData()
	{
		return pairData;
	}



	private final ExtendedPairData pairData;
	private final HypothesisInformation hypothesisInformation;
}

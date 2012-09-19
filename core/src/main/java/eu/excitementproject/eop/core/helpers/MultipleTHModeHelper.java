package eu.excitementproject.eop.core.helpers;

import java.util.List;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.core.EDABasic;
import eu.excitementproject.eop.core.TEDecision;
import eu.excitementproject.eop.core.exceptions.ComponentException;
import eu.excitementproject.eop.core.exceptions.EDAException;

public abstract class MultipleTHModeHelper<T extends TEDecision> {

	abstract public void setEDA(EDABasic<T> eda);
	abstract public List<T> processMultiT(JCas aCas) throws EDAException, ComponentException;
	abstract public List<T> processMultiH(JCas aCas) throws EDAException, ComponentException;
	
}

package eu.excitementproject.eop.core.helpers;

import java.util.List;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.exception.ComponentException;

public abstract class MultipleTHModeHelper<T extends TEDecision> {

	abstract public void setEDA(EDABasic<T> eda);
	abstract public List<T> processMultiT(JCas aCas) throws EDAException, ComponentException;
	abstract public List<T> processMultiH(JCas aCas) throws EDAException, ComponentException;
	
}

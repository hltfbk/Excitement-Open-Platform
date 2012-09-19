package eu.excitementproject.eop.core.helpers;

import java.io.File;

import eu.excitementproject.eop.core.EDABasic;
import eu.excitementproject.eop.core.EDAMultiH;
import eu.excitementproject.eop.core.EDAMultiT;
import eu.excitementproject.eop.core.EDAMultiTH;
import eu.excitementproject.eop.core.TEDecision;
import eu.excitementproject.eop.core.exceptions.ComponentException;
import eu.excitementproject.eop.core.exceptions.ConfigurationException;
import eu.excitementproject.eop.core.exceptions.EDAException;

public abstract class InitializationHelper<T extends TEDecision> {
	
	abstract public EDABasic<T> startEngineBasic(File f) throws ConfigurationException, EDAException, ComponentException;
	abstract public EDAMultiT<T> startEngineMultiT(File f) throws ConfigurationException, EDAException, ComponentException; 
	abstract public EDAMultiH<T> startEngineMultiH(File f) throws ConfigurationException, EDAException, ComponentException;
	abstract public EDAMultiTH<T> startEngineMultiTH(File f) throws ConfigurationException, EDAException, ComponentException;

}

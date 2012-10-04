package eu.excitementproject.eop.core.helpers;

import java.io.File;

import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.EDABasic;
import eu.excitementproject.eop.core.EDAException;
import eu.excitementproject.eop.core.EDAMultiH;
import eu.excitementproject.eop.core.EDAMultiT;
import eu.excitementproject.eop.core.EDAMultiTH;
import eu.excitementproject.eop.core.TEDecision;

public abstract class InitializationHelper<T extends TEDecision> {
	
	abstract public EDABasic<T> startEngineBasic(File f) throws ConfigurationException, EDAException, ComponentException;
	abstract public EDAMultiT<T> startEngineMultiT(File f) throws ConfigurationException, EDAException, ComponentException; 
	abstract public EDAMultiH<T> startEngineMultiH(File f) throws ConfigurationException, EDAException, ComponentException;
	abstract public EDAMultiTH<T> startEngineMultiTH(File f) throws ConfigurationException, EDAException, ComponentException;

}

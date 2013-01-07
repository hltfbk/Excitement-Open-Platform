package eu.excitementproject.eop.core.helpers;

import java.io.File;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.EDAMultiH;
import eu.excitementproject.eop.common.EDAMultiT;
import eu.excitementproject.eop.common.EDAMultiTH;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;

public abstract class InitializationHelper<T extends TEDecision> {
	
	abstract public EDABasic<T> startEngineBasic(File f) throws ConfigurationException, EDAException, ComponentException;
	abstract public EDAMultiT<T> startEngineMultiT(File f) throws ConfigurationException, EDAException, ComponentException; 
	abstract public EDAMultiH<T> startEngineMultiH(File f) throws ConfigurationException, EDAException, ComponentException;
	abstract public EDAMultiTH<T> startEngineMultiTH(File f) throws ConfigurationException, EDAException, ComponentException;

}

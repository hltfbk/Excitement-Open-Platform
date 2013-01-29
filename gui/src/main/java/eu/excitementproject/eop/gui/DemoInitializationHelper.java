package eu.excitementproject.eop.gui;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.EDAMultiH;
import eu.excitementproject.eop.common.EDAMultiT;
import eu.excitementproject.eop.common.EDAMultiTH;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.helpers.InitializationHelper;

public class DemoInitializationHelper<T extends TEDecision> extends InitializationHelper<T>{

	String configTag = "PlatformConfiguration";
	String edaConfig = "activatedEDA";
	String edaAttr = "class";
	File configFile = null;
	
	private String getClassName(){
		
		return ConfigFileUtils.getAttribute(configFile, configTag, edaConfig, edaAttr);
		
//		String className = "eu.excitementproject.eop.core.EditDistanceEDA";
//		return className;
	}
	
	
	@SuppressWarnings("unchecked")
	private EDABasic<T> makeEDAObject() throws EDAException{
		String edaClassStr = getClassName();
		EDABasic<T> eda = null;
		
		try {
			Class<?> edaClass = Class.forName(edaClassStr);
			Constructor<?> edaClassConstructor = edaClass.getConstructor();
			eda = (EDABasic<T>) edaClassConstructor.newInstance();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
			System.out.println("Error generating EDA instance from class " + edaClassStr);
			e.printStackTrace();
		}

		return eda;
	}
	
	@Override
	public EDABasic<T> startEngineBasic(File f) throws ConfigurationException,
			EDAException, ComponentException {
		configFile = f;
		return makeEDAObject();
	}

	@Override
	public EDAMultiT<T> startEngineMultiT(File f) throws ConfigurationException,
			EDAException, ComponentException {
		configFile = f;
		return (EDAMultiT<T>) makeEDAObject();
	}

	@Override
	public EDAMultiH<T> startEngineMultiH(File f) throws ConfigurationException,
			EDAException, ComponentException {
		configFile = f;
		return (EDAMultiH<T>) makeEDAObject();
	}

	@Override
	public EDAMultiTH<T> startEngineMultiTH(File f) throws ConfigurationException,
			EDAException, ComponentException {
		configFile = f;
		return (EDAMultiTH<T>) makeEDAObject();
	}
	

}

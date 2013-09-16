package eu.excitementproject.eop.lexicalminer.definition.classifier;

import java.util.LinkedList;

import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCounts.SyntacticOfflinePosCountClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCounts.SyntacticOfflinePosRelationCountClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCounts.SyntacticOfflineRelationCountClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCountsRoot.SyntacticOfflinePosCountRootClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCountsRoot.SyntacticOfflinePosRelationCountRootClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCountsRoot.SyntacticOfflineRelationCountRootClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocations.SyntacticOfflinePosLocationClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocations.SyntacticOfflinePosRelationLocationClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocations.SyntacticOfflineRelationLocationClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocationsSquare.SyntacticOfflinePosLocationSquareClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocationsSquare.SyntacticOfflinePosRelationLocationSquareClassifier;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocationsSquare.SyntacticOfflineRelationLocationSquareClassifier;

public class BuildClassifiers {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
			
		org.apache.log4j.Logger m_logger = org.apache.log4j.Logger.getLogger(RetrievalTool.class.getName());

		
		if (args.length==0)
		{
			System.out.println("Missing configuration file path on first argument");
			return;
		}
		
		//open config file
		ConfigurationFile conf;
		try 
		{
			conf= new ConfigurationFile(args[0]);	
		} 
		catch (Exception e)
		{
			System.out.println("Error in configuration:");
			throw e;
		}
		
		ConfigurationParams DBConf;
		RetrievalTool retrivalTool;
		try {
			DBConf = conf.getModuleConfiguration("Database");
			retrivalTool = new RetrievalTool(DBConf);			
		} catch (ConfigurationException e1) {
			throw e1;
		} 		
				
		//find all classifiers
		List<OfflineClassifier> classifiers = getClassifiersFromConfig( conf, retrivalTool);

		if (classifiers == null)
		{
			return;
		}
		
		//Run all setAllRank
		for (Classifier classifier : classifiers) 
		{
			try 
			{
				System.out.println("start setAllRank of classifier: " + classifier.getClassifierUniqueName() +", at " + System.currentTimeMillis()/100);
				classifier.setAllRank();
				System.out.println("end setAllRank of classifier: " + classifier.getClassifierUniqueName() +", at " + System.currentTimeMillis()/100);
			} 
				catch (LexicalResourceException e) {
				e.printStackTrace();
				m_logger.error(String.format("Classfier: %s, had a exception while trying to set all i's ranks.",classifier.toString()),e);
			}				
		}		
		
	}

	private static List<OfflineClassifier> getClassifiersFromConfig(ConfigurationFile conf, RetrievalTool retrivalTool) throws ConfigurationException {
		List<OfflineClassifier> classifiers = new LinkedList<OfflineClassifier>();
		
		ConfigurationParams extractorsConf = conf.getModuleConfiguration("ClassidiersToRunOffline"); 
		
		if(extractorsConf.getBoolean("SyntacticOfflinePosCountClassifier"))
		{
			classifiers.add(new SyntacticOfflinePosCountClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflineRelationCountClassifier"))
		{
			classifiers.add(new SyntacticOfflineRelationCountClassifier(retrivalTool, 0.0005));
		}

		if(extractorsConf.getBoolean("SyntacticOfflinePosRelationCountClassifier"))
		{
			classifiers.add(new SyntacticOfflinePosRelationCountClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflinePosCountRootClassifier"))
		{
			classifiers.add(new SyntacticOfflinePosCountRootClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflineRelationCountRootClassifier"))
		{
			classifiers.add(new SyntacticOfflineRelationCountRootClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflinePosRelationCountRootClassifier"))
		{
			classifiers.add(new SyntacticOfflinePosRelationCountRootClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflinePosLocationClassifier"))
		{
			classifiers.add(new SyntacticOfflinePosLocationClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflineRelationLocationClassifier"))
		{
			classifiers.add(new SyntacticOfflineRelationLocationClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflinePosRelationLocationClassifier"))
		{
			classifiers.add(new SyntacticOfflinePosRelationLocationClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflinePosLocationSquareClassifier"))
		{
			classifiers.add(new SyntacticOfflinePosLocationSquareClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflineRelationLocationSquareClassifier"))
		{
			classifiers.add(new SyntacticOfflineRelationLocationSquareClassifier(retrivalTool, 0.0005));
		}
		
		if(extractorsConf.getBoolean("SyntacticOfflinePosRelationLocationSquareClassifier"))
		{
			classifiers.add(new SyntacticOfflinePosRelationLocationSquareClassifier(retrivalTool, 0.0005));
		}			
		return classifiers;
	}

	
	
}

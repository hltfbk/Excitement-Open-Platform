package ac.biu.nlp.nlp.engineml.utilities.safemodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.rteflow.systems.FeatureVectorStructureOrganizer;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;

/**
 * 
 * Input and Output of {@link SafeSamples}, as well as some other methods.
 * <BR>
 * Actually, this class just calls the methods of {@link SafeModelUtils}.
 * 
 * @see SafeModelUtils
 * @see SafeSamples
 * 
 * @author Asher Stern
 * @since Aug 24, 2011
 *
 */
public class SafeSamplesUtils
{
	/**
	 * Creates a new {@link SafeSamples} object.
	 */
	public static SafeSamples create(Vector<LabeledSample> samples, FeatureVectorStructureOrganizer featureVectorStructure) throws TeEngineMlException
	{
		return new SafeSamples(featureVectorStructure,samples);
	}
	
	/**
	 * Stores the given {@link SafeSamples} in a serialization file.
	 */
	public static void store(File serFile, SafeSamples safeSamples) throws TeEngineMlException, FileNotFoundException, IOException
	{
		SafeModelUtils.store(serFile, safeSamples);
	}

	/**
	 * Loads a previously stored {@link SafeSamples} object.
	 * @param serFile The file in which the {@link SafeSamples} object is stored.
	 * @return The stored {@link SafeSamples}.
	 */
	public static SafeSamples load(File serFile, FeatureVectorStructureOrganizer featureVectorStructure) throws TeEngineMlException, FileNotFoundException, IOException, ClassNotFoundException
	{
		return SafeModelUtils.load(serFile, featureVectorStructure);
	}
}

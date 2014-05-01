package eu.excitementproject.eop.biutee.utilities.safemodel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.transformations.datastructures.BooleanAndString;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Dec 18, 2012
 *
 */
public class SafeModelUtils
{
	/**
	 * Stores the given {@link SafeModel} in a serialization file.
	 * 
	 * @param serFile
	 * @param safeModel
	 * @throws TeEngineMlException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static <S extends SafeModel<?>> void store(File serFile, S safeModel) throws TeEngineMlException, FileNotFoundException, IOException
	{
		if (null==serFile) throw new TeEngineMlException("null");
		if (null==safeModel) throw new TeEngineMlException("null");
		ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(serFile));
		try
		{
			outputStream.writeObject(safeModel);
		}
		finally
		{
			outputStream.close();
		}
	}

	/**
	 * Loads a previously stored {@link SafeModel} object.
	 * 
	 * @param serFile The file in which the {@link SafeSamples} object is stored.
	 * 
	 * @return The stored {@link SafeSamples}.
	 * @throws TeEngineMlException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <S extends SafeModel<?>> S load(File serFile, FeatureVectorStructureOrganizer featureVectorStructure) throws TeEngineMlException, FileNotFoundException, IOException, ClassNotFoundException
	{
		if (null==serFile) throw new TeEngineMlException("The given serialization file is null");
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(serFile));
		try
		{
			@SuppressWarnings("unchecked")
			S safeModel = (S) inputStream.readObject();
			BooleanAndString compatible =
					SafeModel.isCompatible(safeModel, featureVectorStructure,true);
			if (true==compatible.getBooleanValue())
			{
				// Do nothing.
			}
			else
			{
				throw new TeEngineMlException("Safe samples are incompatible.\nIt seems that the current system configuration is incompatible with model learned in the training phase.\n"+compatible.getString());
			}
			return safeModel;
		}
		finally
		{
			inputStream.close();
		}
		
	}
}

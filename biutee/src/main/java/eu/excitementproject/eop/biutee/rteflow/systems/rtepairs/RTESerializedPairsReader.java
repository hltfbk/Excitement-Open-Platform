package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;



/**
 * Reads a serialization file as created by {@link RTEPairsPreProcessor}
 * @author Asher Stern
 * @since Feb 3, 2011
 *
 */
public class RTESerializedPairsReader
{
	public RTESerializedPairsReader(String pairsSerializationFileName)
	{
		this.pairsSerializationFileName = pairsSerializationFileName;
	}

	/**
	 * Read the file in the same format saved by {@link RTEPairsPreProcessor#writeToSerializationFile}
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void read() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		pairsData = new ArrayList<PairData>();
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(pairsSerializationFileName)));
		try
		{
			boolean hasNext = true;
			while(hasNext)
			{
				PairData pairData = (PairData) inputStream.readObject();
				hasNext = inputStream.readBoolean();
				pairsData.add(pairData);
			}
		}
		finally
		{
			if (inputStream!=null)
				inputStream.close();
		}
		pairsData.trimToSize();
	}
	
	public ArrayList<PairData> getPairsData()
	{
		return pairsData;
	}



	private String pairsSerializationFileName;
	
	private ArrayList<PairData> pairsData;
}

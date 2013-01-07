package ac.biu.nlp.nlp.engineml.rteflow.systems.gui;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;

import ac.biu.nlp.nlp.datasets.DefaultRTEMainReader;
import ac.biu.nlp.nlp.datasets.DefaultRTEMainWriter;
import ac.biu.nlp.nlp.datasets.RTEMainReader;
import ac.biu.nlp.nlp.datasets.RTEMainReaderException;
import ac.biu.nlp.nlp.datasets.RTEMainWriter;
import ac.biu.nlp.nlp.datasets.RTEMainWriterException;
import ac.biu.nlp.nlp.datasets.TextHypothesisPair;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;

/**
 * A class to save the pairs that were processed by the "visual tracing tool".
 * It is actually a cache of last pairs that have been processed.
 * <P>
 * These pairs are stored in the XML file {@value Constants#FILENAME_RECENT_PAIRS_IN_GUI}
 * in the working-directory. 
 * 
 * @author Asher Stern
 * @since Apr 17, 2012
 *
 */
public class RecentPairsSaver
{
	public static final int SIZE = Constants.SIZE_QUEUE_RECENT_PAIRS_IN_GUI;
	
	public RecentPairsSaver() throws RTEMainReaderException
	{
		load();
	}
	
	public void add(String text, String hypothesis, String taskName) throws RTEMainWriterException
	{
		TextHypothesisPair thPair = new TextHypothesisPair(text,hypothesis,lastId,taskName);
		++lastId;
		add(thPair,false);
	}
	
	protected void add(TextHypothesisPair thPair) throws RTEMainWriterException
	{
		add(thPair,true);
	}
	
	protected void add(TextHypothesisPair thPair, boolean doSetId) throws RTEMainWriterException
	{
		if (doSetId)
		{
			thPair = setId(lastId, thPair);
			++lastId;
		}
		
		queue.addFirst(thPair);
		
		if (queue.size()>=SIZE)
		{
			queue.removeLast();
		}
		
		store();
	}
	
	protected void store() throws RTEMainWriterException
	{
		RTEMainWriter writer = new DefaultRTEMainWriter(file,queue);
		writer.write();
	}

	protected void load() throws RTEMainReaderException
	{
		queue = new LinkedList<TextHypothesisPair>();
		if (file.exists())
		{
			RTEMainReader reader = new DefaultRTEMainReader();
			reader.setXmlFile(file);
			reader.read();
			Map<Integer,TextHypothesisPair> pairs = reader.getMapIdToPair();
			lastId = 1;
			for (Map.Entry<Integer,TextHypothesisPair> pair : pairs.entrySet())
			{
				if (queue.size()<=SIZE)
				{
					queue.add(setId(lastId, pair.getValue()));
				}
				++lastId;
			}
		}
	}
	
	
	private static TextHypothesisPair setId(int id, TextHypothesisPair pair)
	{
		TextHypothesisPair ret = new TextHypothesisPair(pair.getText(),pair.getHypothesis(),id,pair.getClassificationType(),pair.getAdditionalInfo());
		return ret;
		
	}

	private int lastId = 1;
	private LinkedList<TextHypothesisPair> queue = null;
	private File file = new File(Constants.FILENAME_RECENT_PAIRS_IN_GUI);
}

package eu.excitementproject.eop.distsim.builders.mapred;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.builders.reader.FileBasedSentenceReader;
import eu.excitementproject.eop.distsim.builders.reader.SentenceReaderException;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.CreationException;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;

public class SentenceCountingRecordReader<T> extends RecordReader<T,LongWritable> {
	
	@SuppressWarnings({ "unchecked"})
	public SentenceCountingRecordReader(ConfigurationParams confParams) throws CreationException, ConfigurationException {
		this.sentenceReader = (FileBasedSentenceReader<T>)Factory.create(confParams.get(Configuration.SENTENCE_READER_CLASS), confParams);		
	}
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.RecordReader#initialize(org.apache.hadoop.mapreduce.InputSplit, org.apache.hadoop.mapreduce.TaskAttemptContext)
	 */
	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context) throws IOException {
		try {
			FileSplit split = (FileSplit) genericSplit;
			start = split.getStart();
			end = start + split.getLength();
			sentenceReader.setSource(new File(split.getPath().toUri()));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	 
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.RecordReader#nextKeyValue()
	 */
	@Override
	public boolean nextKeyValue() throws IOException {
		
		Pair<T, Long> senntenceAndCount;
		try {
			senntenceAndCount = sentenceReader.nextSentence();
			pos = sentenceReader.getPosition();
		} catch (SentenceReaderException e) {
			//throw new IOException(e);
			pos = sentenceReader.getPosition();
			return nextKeyValue();
		}
		if (senntenceAndCount == null) {
			key = null;
			value = null;
			return false; 
		} else {
			key = senntenceAndCount.getFirst();
			value = senntenceAndCount.getSecond();
			return true;
		}
	}
	 
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.RecordReader#getCurrentKey()
	 */
	@Override
	public T getCurrentKey() {
		return key; 
	}
	 
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.RecordReader#getCurrentValue()
	 */
	@Override
	public LongWritable getCurrentValue() {
		return new LongWritable(value);
	}
	 
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.RecordReader#getProgress()
	 */
	@Override
	public float getProgress() {
		if (start == end) {
			return 0.0f;
		} else {
			return Math.min(1.0f, (pos - start) / (float) (end - start));
		}
	}
	 
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.RecordReader#close()
	 */
	@Override
	public synchronized void close() throws IOException {
		if (sentenceReader != null) {
			try {
				sentenceReader.closeSource();
			} catch (SentenceReaderException e) {
				throw new IOException(e);
			}
		}
	}
	 
	private long start;
	private long pos;
	private long end;
	private FileBasedSentenceReader<T> sentenceReader;
	private T key = null;
	private Long value = null;
	protected String sentenceReaderClass;

}

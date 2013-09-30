package eu.excitementproject.eop.distsim.builders.mapred;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;

public class BasicNodeInputFormat extends FileInputFormat<BasicNode, LongWritable> {

	  @Override
	  public RecordReader<BasicNode, LongWritable> createRecordReader(InputSplit split, TaskAttemptContext context)  {
		  try {
			  ConfigurationFile conf = new ConfigurationFile(context.getConfiguration().get(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_FILE));
			  return new SentenceCountingRecordReader<BasicNode>(conf.getModuleConfiguration(context.getConfiguration().get(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_MODULE)));
		  } catch (Exception e) {
			  throw new RuntimeException(e);
		  }
	  }
	 
	  @Override
	  protected boolean isSplitable(JobContext context, Path file) {
		  return false;
	  }
}

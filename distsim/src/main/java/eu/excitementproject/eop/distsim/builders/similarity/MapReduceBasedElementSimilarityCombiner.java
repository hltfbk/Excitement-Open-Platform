/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.similarity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.scoring.combine.IlegalScoresException;
import eu.excitementproject.eop.distsim.scoring.combine.SimilarityCombination;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;

/**
 * A program which combined a given set of huge similarity scoring devices, which are not sorted.
 * The characteristics of the combination is determined by a given combination method.
 * The program is based on Hadoop's Map-Reduce pattern.  
 * 
 * @author Meni Adler
 * @since 11/09/2012
 *
 */
public class MapReduceBasedElementSimilarityCombiner { //implements ElementSimilarityCombiner {

	private final static Logger logger = Logger.getLogger(OrderedBasedElementSimilarityCombiner.class);
    private final static String RERQUIRED_SCORES_NUMBER = "required-scores-number";
	 
	public static class MapClass extends Mapper<LongWritable, Text, IntWritable, Text > {
	    @Override
	    public void map(LongWritable key, Text value, Context context) throws IOException,  InterruptedException {
	    	String line = value.toString().trim();
	    	int pos = line.indexOf("\t");
	    	IntWritable outkey = new IntWritable(Integer.parseInt(line.substring(0,pos)));
	    	Text outValue = new Text(line.substring(pos+1));
	        context.write(outkey, outValue);
	    }
	  }
	 
	  public static class ReduceClass extends Reducer<IntWritable, Text, IntWritable, Text> {
		  
		  private SimilarityCombination similarityCombination;
		  private int requiredScoresNumForCombination;
		    
		  @Override
		  public void setup(Context context) {
			 try {
				similarityCombination = (SimilarityCombination)Factory.create(context.getConfiguration().get(eu.excitementproject.eop.distsim.util.Configuration.SIMILARITY_COMBINATION_CLASS));
				requiredScoresNumForCombination = Integer.parseInt(context.getConfiguration().get(RERQUIRED_SCORES_NUMBER));
			 } catch (Exception e) {
				throw new RuntimeException(e);
			 } 
		  }
		  
		    @Override
		    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException,  InterruptedException {
		    	
		    	Map<Integer, List<Double>> entialmentScores = new HashMap<Integer, List<Double>>();
		    	for (Text value : values) {
		    	  	String[] toks = value.toString().split("\t");
		  			for (int i=0; i< toks.length; i+=2) {
		  				int id = Integer.parseInt(toks[i]);
		  				double score = Double.parseDouble(toks[i+1]);
		  				List<Double> scores = entialmentScores.get(id);
		  				if (scores == null) {
		  					scores = new LinkedList<Double>();
		  					entialmentScores.put(id,scores);
		  				}
		  				scores.add(score);
		  			}
		    	}
		    	TIntDoubleMap combinedEntailmentScores = new TIntDoubleHashMap();
				for (Entry<Integer, List<Double>> entialmentScore : entialmentScores.entrySet()) {
					double combinedScore;
					try {
						
						//debug
						System.out.println("combined scores of element " + entialmentScore.getKey());
						
						combinedScore = similarityCombination.combine(entialmentScore.getValue(), requiredScoresNumForCombination);
						
						//debug
						System.out.println("combined " + entialmentScore.getValue() + " to " + combinedScore);
						
					} catch (IlegalScoresException e) {
						throw new RuntimeException (e);
					}
					if (combinedScore > 0)
						combinedEntailmentScores.put(entialmentScore.getKey(), combinedScore);
				}
				//debug
				System.out.println(combinedEntailmentScores);
				try {
					LinkedHashMap<Integer, Double> map = SortUtil.sortMapByValue(combinedEntailmentScores, true);
					if (!map.isEmpty()) {
						StringBuilder sb = new StringBuilder();
						for (Entry<Integer,Double> entry : map.entrySet()) {			
							sb.append("\t");
							sb.append(entry.getKey());
							sb.append("\t");
							sb.append(entry.getValue());
						}
						context.write(key, new Text(sb.toString()));
					}
				} catch (Exception e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}	    	
		    }
	  }
	 	 
	 public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Usage: MapReduceBasedElementSimilarityCombiner <configuration file>");
			System.exit(0);
		}
			

		 try {
			 Configuration conf = new Configuration();
			 
			 //ConfigurationFile confFile = new ConfigurationFile(args[0]);
			 ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
		    
			 ConfigurationParams loggingParams = confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.LOGGING);
			 PropertyConfigurator.configure(loggingParams.get(eu.excitementproject.eop.distsim.util.Configuration.PROPERTIES_FILE));
						
			 ConfigurationParams similarityCombinerParams = confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.ELEMENT_SIMILARITY_COMBINER);
			
			 conf.set(eu.excitementproject.eop.distsim.util.Configuration.SIMILARITY_COMBINATION_CLASS, similarityCombinerParams.get(eu.excitementproject.eop.distsim.util.Configuration.SIMILARITY_COMBINATION_CLASS));
			 String[] infiles = similarityCombinerParams.getStringArray(eu.excitementproject.eop.distsim.util.Configuration.IN_FILES);
			 //String outfile = similarityCombinerParams.getString(eu.excitementproject.eop.distsim.util.Configuration.OUT_COMBINED_FILE);
			 String tmpOutDir = similarityCombinerParams.getString(eu.excitementproject.eop.distsim.util.Configuration.NEW_TMP_DIR);
			
			 conf.set(RERQUIRED_SCORES_NUMBER,Integer.toString(infiles.length));
			 
			 Job job = new Job(conf, "element similarity combiner");
			 job.setJarByClass(MapReduceBasedElementSimilarityCombiner.class);
			 job.setMapperClass(MapClass.class);
			 job.setReducerClass(ReduceClass.class);
			 job.setOutputKeyClass(IntWritable.class);
			 job.setOutputValueClass(Text.class);
			 for (String infile : infiles)
				 FileInputFormat.addInputPath(job, new Path(infile));
			 FileOutputFormat.setOutputPath(job, new Path(tmpOutDir));
			 System.exit(job.waitForCompletion(true) ? 0 : 1);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	  }	 
}
package eu.excitementproject.eop.distsim.builders.mapred;

import java.io.File;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.builders.cooccurrence.CooccurrenceExtraction;
import eu.excitementproject.eop.distsim.builders.elementfeature.ElementFeatureExtraction;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * @author Meni Adler
 * @since May 26, 2013
 * 
 * Extracts elements and features from a given corpus, based on map-reduce scheme.
 * At the first stage, the map-reduce procedure extracts counts (and filters by minimal count)the elements and the features.
 * At the second stage the extracted elements and features, are organized into the 'traditional' distsim format, the output files: elements,features, element-feature-counts, feature-elements  
 *
 */
public class ExtractAndCountBasicNodeBasedElementsFeatures { 

	@SuppressWarnings("rawtypes")
	public static class MapClass extends Mapper<BasicNode, LongWritable, Text, LongWritable> {
    
		
		protected CooccurrenceExtraction cooccurrenceExtraction;
		protected ElementFeatureExtraction elementFeatureExtraction;
		
		@Override
		public void setup(Context context)  {
			try {
				//ConfigurationFile confFile = new ConfigurationFile(context.getConfiguration().get(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_FILE));
				ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(context.getConfiguration().get(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_FILE))));
				
			    ConfigurationParams confParams = confFile.getModuleConfiguration(context.getConfiguration().get(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_MODULE));
	
			    this.cooccurrenceExtraction = (CooccurrenceExtraction)Factory.create(confParams.get(eu.excitementproject.eop.distsim.util.Configuration.COOCCURENCE_EXTRACTION_CLASS), confParams);
	
				this.elementFeatureExtraction = (ElementFeatureExtraction)Factory.create(confParams.get(eu.excitementproject.eop.distsim.util.Configuration.ELEMENT_FEATURE_EXTRACTION_CLASS),confParams);
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	    @SuppressWarnings("unchecked")
		@Override
	    public void map(BasicNode root, LongWritable count, Context context) throws IOException,  InterruptedException {
    		try {
	    		Pair<? extends List<? extends TextUnit>, ? extends List<? extends Cooccurrence>> cooccurrences = cooccurrenceExtraction.extractCooccurrences(root);	    		
		    	for (Cooccurrence cooccurrence : cooccurrences.getSecond()) {
		    		
		    		for (Pair<Element,Feature> elementFeaturePair :  elementFeatureExtraction.extractElementsFeature(cooccurrence)) {
		    			String element = elementFeaturePair.getFirst().toKey();
		    			String feature = elementFeaturePair.getSecond().toKey();
		    			if (!element.contains(Defs.ELEMENT_TAG) && !feature.contains(Defs.FEATURE_TAG) &&
		    					!element.contains(Defs.ELEMENTFEATURE_TAG) && !feature.contains(Defs.ELEMENTFEATURE_TAG) &&
		    					!element.contains(Defs.ELEMENTFEATURE_SEPARATOR) && !feature.contains(Defs.ELEMENTFEATURE_SEPARATOR)) {
		    				context.write(new Text(Defs.ELEMENT_TAG + element), count);
		    				context.write(new Text(Defs.FEATURE_TAG + feature), count);
		    				context.write(new Text(Defs.ELEMENTFEATURE_TAG + element + Defs.ELEMENTFEATURE_SEPARATOR + feature), count);
		    			}
		    		}
		    	}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	 }

  public static class ReduceClass extends Reducer<Text,LongWritable,Text,LongWritable> {
	  
	  long minCount;
	  
	  @Override
		public void setup(Context context)  {
			try {
				//ConfigurationFile confFile = new ConfigurationFile(context.getConfiguration().get(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_FILE));
				ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig  (new File(context.getConfiguration().get(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_FILE))));
			    ConfigurationParams confParams = confFile.getModuleConfiguration(context.getConfiguration().get(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_MODULE));
			    try {
			    	minCount = confParams.getLong(eu.excitementproject.eop.distsim.util.Configuration.MIN_COUNT);
			    } catch (ConfigurationException ce) {
			    	minCount = 0;
			    }
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	@Override
    public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException,  InterruptedException {
      long sum = 0;
      for (LongWritable value : values) 
        sum += value.get();
      if (sum >= minCount)
    	  context.write(key, new LongWritable(sum)); 
	}
  }
  
  
  public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	    
	    // tmp for my linux account
	    /*conf.set("hadoop.tmp.dir","/home/ir/adlerm6/tmp");
	    conf.set("dfs.name.dir", "/home/ir/adlerm6/tmp");
	    conf.set("dfs.data.dir", "/home/ir/adlerm6/tmp");
	    conf.set("dfs.client.buffer.dir", "/home/ir/adlerm6/tmp");
	    conf.set("mapred.local.dir", "/home/ir/adlerm6/tmp");*/
	    
	    conf.set(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_FILE, args[0]);
	    conf.set(eu.excitementproject.eop.distsim.util.Configuration.CONFIGURATION_MODULE,eu.excitementproject.eop.distsim.util.Configuration.MAPRED_COOCCURRENCE_COUNTING);
	    Job job = new Job(conf, "Count cooccurrences");
	    
	    //ConfigurationFile confFile = new ConfigurationFile(args[0]);
	    ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
	    ConfigurationParams confParams = confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_COOCCURRENCE_COUNTING);
	    job.setJarByClass(ExtractAndCountBasicNodeBasedElementsFeatures.class);
	    job.setMapperClass(MapClass.class);
	    job.setCombinerClass(ReduceClass.class);
	    job.setReducerClass(ReduceClass.class);
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(LongWritable.class);
	    job.setOutputKeyClass(Text.class);    
	    job.setOutputValueClass(LongWritable.class);
	    job.setInputFormatClass(BasicNodeInputFormat.class);
	    for (File f : eu.excitementproject.eop.distsim.util.FileUtils.getFiles(new File(confParams.get(eu.excitementproject.eop.distsim.util.Configuration.INDIR))))	    	
	    	FileInputFormat.addInputPath(job, new Path(f.getPath()));
	    
	    String sOutdir = confParams.get(eu.excitementproject.eop.distsim.util.Configuration.OUTDIR);
	    File outdir = new File(sOutdir);
	    
	    if (outdir.exists()) {
		    if (outdir.isFile())
		    	throw new ConfigurationException("The " + eu.excitementproject.eop.distsim.util.Configuration.OUTDIR + " configuration property must be a directory");
	    	for (File f : outdir.listFiles())
	    		f.delete();
	    	outdir.delete();	    	
	    }
	    
	    FileOutputFormat.setOutputPath(job, new Path(sOutdir));
	    
	    // Apply the first map-reduce stage, which extracts counts (and filters by minimal count)the elements and features 
	    job.waitForCompletion(true);
	    
	    // Apply the second stage, in order to organize the extracted elements and the features into the 'traditional' distsim format.
	    // 1. Generate 'elements' and 'features' output files
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures1(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_1));
	    // 2. Generate the 'element-feature-counts' and 'feature-elements' output files
	    SeparateFilterAndIndexElementsFeatures.separateFilterAndIndexElementsFeatures2(confFile.getModuleConfiguration(eu.excitementproject.eop.distsim.util.Configuration.MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_2));

	 }
 }
